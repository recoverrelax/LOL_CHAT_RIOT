package com.recoverrelax.pt.riotxmppchat.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.edgelabs.pt.mybaseapp.R;
import com.recoverrelax.pt.riotxmppchat.Adapter.PersonalMessageAdapter;
import com.recoverrelax.pt.riotxmppchat.Database.MessageDirection;
import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.Globals;
import com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils;
import com.recoverrelax.pt.riotxmppchat.Network.Helper.PersonalMessageHelper;
import com.recoverrelax.pt.riotxmppchat.Network.Helper.PersonalMessageImpl;

import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import LolChatRiotDb.MessageDb;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observer;

import static com.recoverrelax.pt.riotxmppchat.Network.RiotXmppService.NewMessageObserver;
import static com.recoverrelax.pt.riotxmppchat.ui.activity.FriendListActivity.INTENT_FRIEND_NAME;
import static com.recoverrelax.pt.riotxmppchat.ui.activity.FriendListActivity.INTENT_FRIEND_XMPPNAME;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalMessageFragment extends BaseFragment implements Observer<List<MessageDb>>, NewMessageObserver {

    @InjectView(R.id.messageRecyclerView)
    RecyclerView messageRecyclerView;

    @InjectView(R.id.chatEditText)
    EditText chatEditText;

    @InjectView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private static final String TAG = PersonalMessageFragment.class.getSimpleName();

    private RecyclerView.LayoutManager layoutManager;

    /**
     * Adapter
     */
    private PersonalMessageAdapter adapter;
    private PersonalMessageHelper personalMessageHelper;

    private String friendXmppName;
    private String friendUsername;
    private SwipeRefreshLayout.OnRefreshListener swipeRefreshListener;

    private int defaultMessageNrReturned = Globals.Message.DEFAULT_MESSAGES_RETURNED;


    public PersonalMessageFragment() {
        // Required empty public constructor
    }

    public static PersonalMessageFragment newInstance() {
        return new PersonalMessageFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personal_message, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle extras = getArguments();
        if(extras==null || !extras.containsKey(INTENT_FRIEND_NAME) || !extras.containsKey(INTENT_FRIEND_XMPPNAME)){
            getActivity().finish();
            LogUtils.LOGE(TAG, "Something went wrong, we haven't got a xmppName");
        }
        else {
            friendUsername = extras.getString(INTENT_FRIEND_NAME);
            friendXmppName = extras.getString(INTENT_FRIEND_XMPPNAME);
        }

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        messageRecyclerView.setLayoutManager(layoutManager);

        adapter = new PersonalMessageAdapter(getActivity(), new ArrayList<MessageDb>(), R.layout.personal_message_from, R.layout.personal_message_to, messageRecyclerView);
        messageRecyclerView.setAdapter(adapter);

        personalMessageHelper = new PersonalMessageImpl(this);
        personalMessageHelper.getLastXPersonalMessageList(defaultMessageNrReturned,
                MainApplication.getInstance().getRiotXmppService().getConnectedXmppUser(), friendXmppName);

        setToolbarTitle(getResources().getString(R.string.chatting_with) + " " + friendUsername);

        swipeRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                personalMessageHelper.getLastXPersonalMessageList(doubleLoadedItems(),
                        MainApplication.getInstance().getRiotXmppService().getConnectedXmppUser(), friendXmppName);
            }
        };

        swipeRefreshLayout.setOnRefreshListener(swipeRefreshListener);
    }

    public int doubleLoadedItems(){
        this.defaultMessageNrReturned = defaultMessageNrReturned*2;
        return this.defaultMessageNrReturned;
    }

    @OnClick(R.id.expandButton)
    public void sendMessageButton(View view){
        String message = chatEditText.getText().toString();
        Presence rosterPresence = MainApplication.getInstance().getRiotXmppService().getRosterPresence(friendXmppName);

        if (!message.equals("") && rosterPresence.isAvailable()) {

            MainApplication.getInstance().getRiotXmppService().sendMessage(message, friendXmppName);
            RiotXmppDBRepository.insertMessage(new MessageDb(null, MainApplication.getInstance().getRiotXmppService().getConnectedXmppUser(),
                    friendXmppName, MessageDirection.TO.getId(), new Date(), message, false));
            OnNewMessageNotification(null, null);
            // clear text

            chatEditText.setText("");
        }
    }

    @Override
    public void onCompleted() {}

    @Override
    public void onError(Throwable e) {
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onNext(List<MessageDb> messageDbs) {

        if(messageDbs != null)
            setAllMessagesReaded();

        adapter.setItems(messageDbs, swipeRefreshLayout.isRefreshing() ? null : PersonalMessageAdapter.ScrollTo.LAST_ITEM);

        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);

    }

    private void setAllMessagesReaded() {
        List<MessageDb> allMessages = adapter.getAllMessages();
        for(MessageDb message: allMessages)
            message.setWasRead(true);
        RiotXmppDBRepository.updateMessages(allMessages);
    }

    @Override
    public void onResume() {
        super.onResume();
        MainApplication.getInstance().getRiotXmppService().addNewMessageObserver(this);
        personalMessageHelper.getLastXPersonalMessageList(Globals.Message.DEFAULT_MESSAGES_RETURNED,
                MainApplication.getInstance().getRiotXmppService().getConnectedXmppUser(), friendXmppName);
    }

    @Override
    public void onPause() {
        super.onPause();
        MainApplication.getInstance().getRiotXmppService().removeNewMessageObserver(this);
    }

    @Override
    public void OnNewMessageNotification(Message message, String messageFrom) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /**
                 * Change this to update only the corresponding item, not the whole list.
                 */
                personalMessageHelper.getLastXPersonalMessageList(Globals.Message.DEFAULT_MESSAGES_RETURNED,
                        MainApplication.getInstance().getRiotXmppService().getConnectedXmppUser(), friendXmppName);
            }
        });
    }
}

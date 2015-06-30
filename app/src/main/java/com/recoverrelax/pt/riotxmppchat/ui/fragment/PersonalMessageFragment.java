package com.recoverrelax.pt.riotxmppchat.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.OnNewLogEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.OnNewMessageReceivedEventEvent;
import com.recoverrelax.pt.riotxmppchat.Network.Helper.GlobalImpl;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Adapter.PersonalMessageAdapter;
import com.recoverrelax.pt.riotxmppchat.Database.MessageDirection;
import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppGlobals;
import com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils;
import com.recoverrelax.pt.riotxmppchat.Network.Helper.PersonalMessageHelper;
import com.recoverrelax.pt.riotxmppchat.Network.Helper.PersonalMessageImpl;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.InAppLogIds;
import com.squareup.otto.Subscribe;

import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import LolChatRiotDb.InAppLogDb;
import LolChatRiotDb.MessageDb;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGE;
import static com.recoverrelax.pt.riotxmppchat.Network.Helper.PersonalMessageImpl.*;
import static com.recoverrelax.pt.riotxmppchat.ui.activity.PersonalMessageActivity.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalMessageFragment extends RiotXmppCommunicationFragment implements PersonalMessageImplCallbacks {

    @Bind(R.id.messageRecyclerView)
    RecyclerView messageRecyclerView;

    @Bind(R.id.chatEditText)
    EditText chatEditText;

    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.expandButton)
    ImageView expandButton;

    @Bind(R.id.message_layout)
    RelativeLayout message_layout;

    @Bind(R.id.uselessShape)
    FrameLayout uselessShape;

    private static final String TAG = PersonalMessageFragment.class.getSimpleName();

    /**
     * Adapter
     */
    private PersonalMessageAdapter adapter;
    private PersonalMessageHelper personalMessageHelper;

    private String friendXmppName;
    private String friendUsername;
    private int bgColor;

    private SwipeRefreshLayout.OnRefreshListener swipeRefreshListener;

    private int defaultMessageNrReturned = AppGlobals.Message.DEFAULT_MESSAGES_RETURNED;


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
        ButterKnife.bind(this, view);
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
        if (extras == null || !extras.containsKey(INTENT_FRIEND_NAME) || !extras.containsKey(INTENT_FRIEND_XMPPNAME)
                || !extras.containsKey(INTENT_BGCOLOR)) {
            LogUtils.LOGE(TAG, "Something went wrong, we haven't got a xmppName");
        } else {
            friendUsername = extras.getString(INTENT_FRIEND_NAME);
            friendXmppName = extras.getString(INTENT_FRIEND_XMPPNAME);
            bgColor = extras.getInt(INTENT_BGCOLOR);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true);
            messageRecyclerView.setLayoutManager(layoutManager);

            adapter = new PersonalMessageAdapter(getActivity(), new ArrayList<>(), R.layout.personal_message_from, R.layout.personal_message_to, messageRecyclerView);
            messageRecyclerView.setAdapter(adapter);

            personalMessageHelper = new PersonalMessageImpl(this);
            personalMessageHelper.getLastXPersonalMessageList(defaultMessageNrReturned, friendXmppName);

            swipeRefreshListener = () -> personalMessageHelper.getLastXPersonalMessageList(doubleLoadedItems(), friendXmppName);

            swipeRefreshLayout.setOnRefreshListener(swipeRefreshListener);

            uselessShape.setTranslationY(convertDIPToPixels(getActivity(), (70 / 2)));
            swipeRefreshLayout.setBackgroundColor(bgColor);
//            ((BaseActivity)getActivity()).getToolbar().getBackground().setAlpha(0);
        }
    }

    public static int convertDIPToPixels(Context context, int dip) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, displayMetrics);
    }

    public int doubleLoadedItems() {
        this.defaultMessageNrReturned = defaultMessageNrReturned * 2;
        return this.defaultMessageNrReturned;
    }

    @OnClick(R.id.expandButton)
    public void sendMessageButton(View view) {
        String message = chatEditText.getText().toString();
        Presence rosterPresence = MainApplication.getInstance().getRiotXmppService().getRiotRosterManager().getRosterPresence(friendXmppName);

        if (!message.equals("") && rosterPresence.isAvailable()) {

            MainApplication.getInstance().getRiotXmppService().getRiotChatManager().sendMessage(message, friendXmppName);

            MessageDb messageDb = new MessageDb(null, MainApplication.getInstance().getConnectedUserString(),
                    friendXmppName, MessageDirection.TO.getId(), new Date(), message, false);

            new GlobalImpl(MainApplication.getInstance().getConnection()).insertMessage(messageDb)
                    .subscribe(new Subscriber<Long>() {
                        @Override public void onCompleted() { }
                        @Override public void onError(Throwable e) { }
                        @Override public void onNext(Long aLong) { }
                    });

            OnNewMessageReceived(null);
            new GlobalImpl(MainApplication.getInstance().getConnection()).updateInappLog(new InAppLogDb(null, InAppLogIds.FRIEND_PM.getOperationId(), new Date(),
                    "You said to  " + friendUsername + ": " + message,
                    MainApplication.getInstance().getConnectedUserString(), friendXmppName))
                    .subscribe(new Subscriber<Long>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                        }

                        @Override
                        public void onNext(Long aLong) {
                        }
                    });

            MainApplication.getInstance().getBusInstance().post(new OnNewLogEvent());
            // clear text
            chatEditText.setText("");
        }
    }

    private void setAllMessagesRead() {
        List<MessageDb> allMessages = adapter.getAllMessages();
        for (MessageDb message : allMessages)
            message.setWasRead(true);
        RiotXmppDBRepository.updateMessages(allMessages);
    }

    @Override
    public void onResume() {
        super.onResume();
//        MainApplication.getInstance().getBusInstance().register(this);
        personalMessageHelper.getLastXPersonalMessageList(defaultMessageNrReturned, friendXmppName);
    }

    @Subscribe
    public void OnNewMessageReceived(OnNewMessageReceivedEventEvent messageReceived) {
        if (this.friendXmppName != null) {
            getActivity().runOnUiThread(() -> {

                // this means that i have sent a message
                if (messageReceived == null) {
                    personalMessageHelper.getLastPersonalMessage(friendXmppName);
                } else {
                    // if its not for me, don't update
                    if (this.friendXmppName.equals(messageReceived.getMessageFrom()))
                        personalMessageHelper.getLastPersonalMessage(friendXmppName);
                }
            });
        }
    }

        @Override
        public void onLastXPersonalMessageListReceived (List < MessageDb > messageDbs) {
            if (messageDbs != null)
                setAllMessagesRead();

            adapter.setItems(messageDbs, swipeRefreshLayout.isRefreshing() ? null : PersonalMessageAdapter.ScrollTo.FIRST_ITEM);

            if (swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onLastXPersonalMessageListFailedReception (Throwable e){
            onGeneralThrowableEvent(e);
        }

        @Override
        public void onLastPersonalMessageReceived (MessageDb message){
            if (message != null)
                setAllMessagesRead();

            adapter.addItem(message);

            if (swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onLastPersonalMessageFailedReception (Throwable e){
            onGeneralThrowableEvent(e);
        }

    public void onGeneralThrowableEvent(Throwable e) {
        LOGE(TAG, "", e);
    }
}

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

import com.recoverrelax.pt.riotxmppchat.Adapter.PersonalMessageAdapter;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.NewMessageReceivedNotifyEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.EventHandler;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppGlobals;
import com.recoverrelax.pt.riotxmppchat.MyUtil.LogUtils;
import com.recoverrelax.pt.riotxmppchat.Network.Manager.RiotRosterManager;
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.PersonalMessageImpl;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.InAppLogIds;
import com.recoverrelax.pt.riotxmppchat.Storage.MessageDirection;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import LolChatRiotDb.InAppLogDb;
import LolChatRiotDb.MessageDb;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.LogUtils.LOGE;
import static com.recoverrelax.pt.riotxmppchat.ui.activity.ChatActivity.INTENT_BGCOLOR;
import static com.recoverrelax.pt.riotxmppchat.ui.activity.ChatActivity.INTENT_FRIEND_NAME;
import static com.recoverrelax.pt.riotxmppchat.ui.activity.ChatActivity.INTENT_FRIEND_XMPPNAME;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends BaseFragment implements NewMessageReceivedNotifyEvent {

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

    private static final String TAG = ChatFragment.class.getSimpleName();

    /**
     * Adapter
     */
    private PersonalMessageAdapter adapter;
    @Inject PersonalMessageImpl personalMessageImpl;
    @Inject RiotRosterManager riotRosterManager;

    @Inject Bus bus;
    @Inject
    EventHandler handler;

    private String friendXmppName;
    private String friendUsername;
    private int bgColor;

    private SwipeRefreshLayout.OnRefreshListener swipeRefreshListener;

    private int defaultMessageNrReturned = AppGlobals.Message.DEFAULT_MESSAGES_RETURNED;
    private final CompositeSubscription subscriptions = new CompositeSubscription();


    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personal_message, container, false);
        ButterKnife.bind(this, view);
        MainApplication.getInstance().getAppComponent().inject(this);
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

            adapter = new PersonalMessageAdapter(getActivity(), new ArrayList<>(), R.layout.personal_message_from_layout, R.layout.personal_message_to_layout, messageRecyclerView);
            messageRecyclerView.setAdapter(adapter);

            getLastXPersonalMessageList(defaultMessageNrReturned, friendXmppName);

            swipeRefreshListener = () -> getLastXPersonalMessageList(doubleLoadedItems(), friendXmppName);

            swipeRefreshLayout.setOnRefreshListener(swipeRefreshListener);

            uselessShape.setTranslationY(convertDIPToPixels(getActivity(), (70 / 2)));
            swipeRefreshLayout.setBackgroundColor(bgColor);
//            ((BaseActivity)getActivity()).getToolbar().getBackground().setAlpha(0);
        }
    }

    public void getLastXPersonalMessageList(int itemQtt, String friendXmppName){
        Subscription subscribe = personalMessageImpl.getLastXPersonalMessageList(itemQtt, friendXmppName)
                .subscribe(new Subscriber<List<MessageDb>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        onGeneralThrowableEvent(e);
                    }

                    @Override
                    public void onNext(List<MessageDb> messageDbs) {
                        if (messageDbs != null)
                            setAllMessagesRead();

                        adapter.setItems(messageDbs, swipeRefreshLayout.isRefreshing() ? null : PersonalMessageAdapter.ScrollTo.FIRST_ITEM);

                        if (swipeRefreshLayout.isRefreshing())
                            swipeRefreshLayout.setRefreshing(false);
                    }
                });
        subscriptions.add(subscribe);
    }

    @Override
    public void onResume() {
        super.onResume();
//        MainApplication.getInstance().getBusInstance().register(this);
        getLastXPersonalMessageList(defaultMessageNrReturned, friendXmppName);
        handler.registerForNewMessageNotifyEvent(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        subscriptions.clear();

        if(adapter != null)
            adapter.removeSubscriptions();
        handler.unregisterForNewMessageNotifyEvent(this);
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

        riotRosterManager.getRosterPresence(friendXmppName)
                .flatMap(rosterPresence -> Observable.just(rosterPresence.isAvailable()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override public void onCompleted() { }
                    @Override public void onError(Throwable e) { }

                    @Override
                    public void onNext(Boolean isAvailable) {
                        if(isAvailable && !message.equals(""))
                            sendMessage(message);
                    }
                });
    }

    public void sendMessage(String message){
        MainApplication.getInstance().getRiotXmppService().getRiotChatManager().sendMessage(message, friendXmppName)
                .flatMap(sentMessageId -> MainApplication.getInstance().getRiotXmppService().getRiotConnectionManager().getConnectedUser())
                .flatMap(connectedUser -> riotXmppDBRepository.insertMessage(new MessageDb(null,
                                connectedUser,
                                friendXmppName,
                                MessageDirection.TO.getId(),
                                new Date(),
                                message,
                                false))
                        .map(aLong -> connectedUser)
                )
                .flatMap(connectedUser -> riotXmppDBRepository.insertOrReplaceInappLog(new InAppLogDb(null,
                        InAppLogIds.FRIEND_PM.getOperationId(),
                        new Date(),
                        "You said to  " + friendUsername + ": " + message,
                        connectedUser, friendXmppName)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override public void onCompleted() { }
                    @Override public void onError(Throwable e) { }

                    @Override
                    public void onNext(Long aLong) {
                        if (aLong != null)
                            onNewMessageNotifyReceived(null, null, null, null);
                        // clearFriendList text
                        chatEditText.setText("");
                    }
                });
    }

    private void setAllMessagesRead() {
        List<MessageDb> allMessages = adapter.getAllMessages();
        for (MessageDb message : allMessages)
            message.setWasRead(true);
        riotXmppDBRepository.updateMessages(allMessages)
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                    }
                });
    }



    public void getLastPersonalMessage(String friendXmppName){
        Subscription subscribe = personalMessageImpl.getLastPersonalMessage(friendXmppName)
                .subscribe(new Subscriber<MessageDb>() {
                    @Override public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        onGeneralThrowableEvent(e);
                    }

                    @Override
                    public void onNext(MessageDb messageDb) {
                        if (messageDb != null)
                            setAllMessagesRead();

                        adapter.addItem(messageDb);

                        if (swipeRefreshLayout.isRefreshing())
                            swipeRefreshLayout.setRefreshing(false);
                    }
                });
        subscriptions.add(subscribe);
    }

    public void onGeneralThrowableEvent(Throwable e) {
        LOGE(TAG, "", e);
    }

    @Override
    public void onNewMessageNotifyReceived(String userXmppAddress, String username, String message, String buttonLabel) {
        if (this.friendXmppName != null) {
            getActivity().runOnUiThread(() -> {

                // this means that i have sent a message
                if (userXmppAddress == null && username == null && message == null && buttonLabel == null) {
                    getLastPersonalMessage(friendXmppName);
                } else {
                    // if its not for me, don't update
                    if (this.friendXmppName.equals(userXmppAddress))
                        getLastPersonalMessage(friendXmppName);
                }
            });
        }
    }
}

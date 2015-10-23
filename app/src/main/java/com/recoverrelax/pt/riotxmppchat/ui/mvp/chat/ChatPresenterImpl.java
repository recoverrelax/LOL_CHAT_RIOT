package com.recoverrelax.pt.riotxmppchat.ui.mvp.chat;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.NewMessageReceivedNotifyEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.EventHandler;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppMVPHelper;
import com.recoverrelax.pt.riotxmppchat.Network.Manager.RiotRosterManager;
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.PersonalMessageImpl;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.InAppLogIds;
import com.recoverrelax.pt.riotxmppchat.Storage.MessageDirection;
import com.recoverrelax.pt.riotxmppchat.Storage.RiotXmppDBRepository;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import LolChatRiotDb.InAppLogDb;
import LolChatRiotDb.MessageDb;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.LogUtils.LOGE;

public class ChatPresenterImpl extends AppMVPHelper.BasePresenterImpl<ChatPresenterCallbacks, ChatAdapter>
        implements
        ChatPresenter,
        NewMessageReceivedNotifyEvent {

    private final String TAG = ChatPresenterImpl.this.getClass().getSimpleName();

    @Inject PersonalMessageImpl personalMessageImpl;
    @Inject RiotRosterManager riotRosterManager;
    @Inject EventHandler eventHandler;
    @Inject RiotXmppDBRepository riotXmppDBRepository;

    public ChatPresenterImpl(ChatPresenterCallbacks model, Context context) {
        super(model, context);
        MainApplication.getInstance().getAppComponent().inject(this);
    }

    @Override
    public void getLastXPersonalMessageList(String friendXmppName) {
        subscriptions.add(

                personalMessageImpl.getLastXPersonalMessageList(model.getMessageSize(), friendXmppName)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<List<MessageDb>>() {
                            @Override public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                onGeneralThrowableEvent(e);
                            }

                            @Override
                            public void onNext(List<MessageDb> messageDbs) {
                                if (messageDbs != null)
                                    setAllMessagesRead();

                                adapter.setItems(messageDbs, model.isListRefreshing() ? null : ChatAdapter.ScrollTo.FIRST_ITEM);
                                model.setListRefreshing(false);
                            }
                        })
        );
    }

    private void getLastPersonalMessage(String friendXmppName) {
        subscriptions.add(
                personalMessageImpl.getLastPersonalMessage(friendXmppName)
                        .subscribe(new Subscriber<MessageDb>() {
                            @Override public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                onGeneralThrowableEvent(e);
                            }

                            @Override
                            public void onNext(MessageDb messageDb) {
                                if (messageDb != null)
                                    setAllMessagesRead();

                                adapter.addItem(messageDb);
                                model.setListRefreshing(false);
                            }
                        })
        );
    }

    @Override
    public void sendMessage(String message, String friendXmppName) {
        // First we need to get the Presence

        riotRosterManager.getRosterPresence(friendXmppName) // get roster presence
                .flatMap(rosterPresence -> {
                    if (rosterPresence.isAvailable())
                        return Observable.just(true);
                    else
                        return Observable.error(new ConnectException("Cannot send a message to an offline user!"));
                })
                .doOnError(throwable -> model.sendFriendOfflineSnack())
                .flatMap( // send message
                        ignoreThis ->
                                MainApplication.getInstance().getRiotXmppService().getRiotChatManager().sendMessage(message, model.getFriendXmppName())
                )
                .flatMap(
                        sentMessageId ->
                                MainApplication.getInstance().getRiotXmppService().getRiotConnectionManager().getConnectedUser()
                )
                .flatMap(
                        connectedUser ->
                                riotXmppDBRepository.insertMessage(new MessageDb(null,
                                        connectedUser,
                                        model.getFriendXmppName(),
                                        MessageDirection.TO.getId(),
                                        new Date(),
                                        message,
                                        false))
                                        .map(aLong -> connectedUser)
                )
                .flatMap(
                        connectedUser ->
                                riotXmppDBRepository.insertOrReplaceInappLog(new InAppLogDb(null,
                                        InAppLogIds.FRIEND_PM.getOperationId(),
                                        new Date(),
                                        "You said to  " + model.getFriendUsername() + ": " + message,
                                        connectedUser, model.getFriendXmppName()))
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override public void onCompleted() {
                    }

                    @Override public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (aLong != null)
                            onNewMessageNotifyReceived(null, null, null, null);
                        model.clearChatText();
                    }
                });
    }

    private void setAllMessagesRead() {
        List<MessageDb> allMessages = adapter.getAllMessages();
        for (MessageDb message : allMessages)
            message.setWasRead(true);

        Observable.from(adapter.getAllMessages())
                .doOnNext(messageDb -> messageDb.setWasRead(true))
                .toList()
                .flatMap(ignoreThis -> riotXmppDBRepository.updateMessages(allMessages))
                .subscribe();

    }

    public void onGeneralThrowableEvent(Throwable e) {
        LOGE(TAG, "", e);
    }

    @Override
    public void onResume() {
        eventHandler.registerForNewMessageNotifyEvent(this);
        getLastXPersonalMessageList(model.getFriendXmppName());
    }

    @Override
    public void onPause() {

        subscriptions.clear();

        if (adapter != null)
            adapter.removeSubscriptions();

        eventHandler.unregisterForNewMessageNotifyEvent(this);
        subscriptions.clear();
    }

    @Override public void configRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true);
        model.setRecyclerViewLayoutParams(layoutManager);
    }

    @Override public void configAdapter(RecyclerView recyclerView) {
        adapter = new ChatAdapter(
                context,
                new ArrayList<>(),
                R.layout.personal_message_from_layout,
                R.layout.personal_message_to_layout,
                recyclerView);

        model.setRecyclerViewAdapter(adapter);
    }

    @Override public void onNewMessageNotifyReceived(String userXmppAddress, String username, String message, String buttonLabel) {
        if (this.model.getFriendXmppName() != null) {

            // this means that i have sent a message
            if (userXmppAddress == null && username == null && message == null && buttonLabel == null) {
                getLastPersonalMessage(model.getFriendXmppName());
            } else {
                // if its not for me, don't update
                if (this.model.getFriendXmppName().equals(userXmppAddress))
                    getLastPersonalMessage(model.getFriendXmppName());
            }
        }
    }
}

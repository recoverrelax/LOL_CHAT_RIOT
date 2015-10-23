package com.recoverrelax.pt.riotxmppchat.ui.mvp.messagelist;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.NewMessageReceivedNotifyEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.EventHandler;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppContextUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppMVPHelper.BasePresenterImpl;
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.FriendMessageListImpl;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.FriendListChat;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.LogUtils.LOGE;

public class MessageListPresenterImpl extends BasePresenterImpl<MessageListPresenterCallbacks, MessageListAdapter> implements
        MessageListPresenter, NewMessageReceivedNotifyEvent {

    private final String TAG = MessageListPresenterImpl.this.getClass().getSimpleName();

    @Inject EventHandler handler;
    @Inject FriendMessageListImpl friendMessageListHelper;

    public MessageListPresenterImpl(MessageListPresenterCallbacks model, Context context) {
        super(model, context);
        MainApplication.getInstance().getAppComponent().inject(this);
    }


    @Override public void onResume() {
        handler.registerForNewMessageNotifyEvent(this);
        getPersonalMessageList();
    }

    @Override public void onPause() {

        subscriptions.clear();

        if (adapter != null)
            adapter.removeSubscriptions();

        handler.unregisterForNewMessageNotifyEvent(this);
    }

    @Override public void configRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        model.setRecyclerViewLayoutParams(layoutManager);
    }

    @Override public void configAdapter(RecyclerView recyclerView) {
        adapter = new MessageListAdapter(context, new ArrayList<>());

        adapter.setRowClickListener(
                (view, friendName, friendXmppAddress) ->
                        AppContextUtils.startPersonalMessageActivity(context, friendName, friendXmppAddress)
        );

        model.setRecyclerViewAdapter(adapter);
    }

    private void getPersonalMessageSingleItem(String userXmppAddress) {

        subscriptions.add(

                friendMessageListHelper.getPersonalMessage(userXmppAddress)
                        .subscribe(new Subscriber<FriendListChat>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                onGeneralThrowableEvent(e);
                            }

                            @Override
                            public void onNext(FriendListChat friendListChat) {
                                adapter.setSingleItem(friendListChat);
                                model.showProgressBar(false);
                            }
                        })
        );
    }

    private void getPersonalMessageList() {
        subscriptions.add(
                friendMessageListHelper.getPersonalMessageList()
                        .subscribe(new Subscriber<List<FriendListChat>>() {
                            @Override public void onCompleted() {
                            }

                            @Override public void onError(Throwable e) {
                            }

                            @Override
                            public void onNext(List<FriendListChat> friendListChats) {
                                adapter.setItems(friendListChats);
                                model.showProgressBar(false);
                            }
                        })
        );
    }

    @Override public void onNewMessageNotifyReceived(String userXmppAddress, String username, String message, String buttonLabel) {
        if (adapter.contains(userXmppAddress)) {
            getPersonalMessageSingleItem(userXmppAddress);
        } else {
            getPersonalMessageList();
        }
    }

    public void onGeneralThrowableEvent(Throwable e) {
        LOGE(TAG, "", e);
    }
}

package com.recoverrelax.pt.riotxmppchat.Network.Manager;

import android.content.Context;

import com.recoverrelax.pt.riotxmppchat.Database.MessageDirection;
import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.NotificationCenter.MessageNotification;
import com.recoverrelax.pt.riotxmppchat.MyUtil.NotificationCenter3;
import com.squareup.otto.Bus;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import LolChatRiotDb.MessageDb;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;

public class RiotChatManager implements ChatManagerListener, ChatMessageListener {

    private AbstractXMPPConnection connection;
    private ChatManager chatManager;
    private String connectedXmppUser;
    private RiotRosterManager riotRosterManager;
    private Context context;
    private Bus busInstance;

    private RiotXmppDBRepository riotXmppDBRepository;

    private Map<String, Chat> chatList;

    public RiotChatManager(Context context, AbstractXMPPConnection connection, String connectedXmppUser, RiotRosterManager riotRosterManager){
        this.connection = connection;
        this.connectedXmppUser = connectedXmppUser;
        this.riotRosterManager = riotRosterManager;
        this.context = context;
        this.busInstance = MainApplication.getInstance().getBusInstance();
        this.riotXmppDBRepository = new RiotXmppDBRepository();
    }

    public void addChatListener() {
        if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
            this.chatManager = ChatManager.getInstanceFor(connection);
            this.chatManager.addChatListener(this);
        }
    }

    public void removeChatListener() {
        if (this.chatManager != null) {
            this.chatManager.removeChatListener(this);
        }
    }

    @Override
    public void chatCreated(Chat chat, boolean createdLocally) {
        chat.addMessageListener(this);
    }

    @Override
    public void processMessage(Chat chat, Message message) {
        String messageFrom = AppXmppUtils.parseXmppAddress(message.getFrom());
        LOGI("1212", "processMessage");
        addChat(chat, messageFrom);

        if (messageFrom != null && message.getBody() != null) {
            MessageDb message1 = new MessageDb(null, connectedXmppUser, messageFrom, MessageDirection.FROM.getId(), new Date(), message.getBody(), false);

            new RiotXmppDBRepository().insertMessage(message1)
                    .subscribe(new Subscriber<Long>() {
                        @Override public void onCompleted() { }
                        @Override public void onError(Throwable e) { }
                        @Override public void onNext(Long aLong) { }
                    });
            notifyNewMessage(message, messageFrom);
        }
    }

    public void addChat(Chat chat, String messageFrom) {
        if (this.chatList == null) {
            this.chatList = new HashMap<>();
        }

        if (!this.chatList.containsKey(messageFrom)) {
            this.chatList.put(messageFrom, chat);
        }
    }

    public Chat getChat(String userXmppName) {
        /**
         * At this step means, there's no active chat for that user so it means you are starting the conversation
         * and need to start a new chat  as well.
         */
        Chat chat = this.chatManager.createChat(userXmppName, this);
        addChat(chat, userXmppName);
        return chat;
    }

    /**
     * Notify all Observers of new messages
     */
    public void notifyNewMessage(Message message, String userXmppAddress) {
        new MessageNotification(userXmppAddress, message).sendMessageNotification();
    }

    public Observable<Boolean> sendMessage(String message, String userXmppName) {
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    Chat chat = getChat(userXmppName);
                    chat.sendMessage(message);

                    subscriber.onNext(true);
                    subscriber.onCompleted();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}

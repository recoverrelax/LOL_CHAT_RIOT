package com.recoverrelax.pt.riotxmppchat.Network.Manager;

import com.recoverrelax.pt.riotxmppchat.Storage.MessageDirection;
import com.recoverrelax.pt.riotxmppchat.Storage.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.NotificationCenter.MessageNotification;
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

import javax.inject.Inject;

import LolChatRiotDb.MessageDb;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.LogUtils.LOGI;

public class RiotChatManager implements ChatManagerListener, ChatMessageListener {

    private ChatManager chatManager;
    private String connectedXmppUser = null;

    private RiotXmppDBRepository riotXmppDBRepository;
    private Map<String, Chat> chatList;
    private MessageNotification messageNotification;

    @Inject
    public RiotChatManager(RiotXmppDBRepository riotXmppDBRepository, MessageNotification messageNotification){
        this.riotXmppDBRepository = riotXmppDBRepository;
        this.messageNotification = messageNotification;
    }

    public void addChatListener(AbstractXMPPConnection connection) {
        if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
            this.chatManager = ChatManager.getInstanceFor(connection);
            this.chatManager.addChatListener(this);
        }
        connectedXmppUser = AppXmppUtils.parseXmppAddress(connection != null ? connection.getUser() : null);
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
            if(connectedXmppUser != null) {
                MessageDb message1 = new MessageDb(null, connectedXmppUser, messageFrom, MessageDirection.FROM.getId(), new Date(), message.getBody(), false);

                riotXmppDBRepository.insertMessage(message1)
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
            }
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
        messageNotification.init(userXmppAddress, message);
        messageNotification.sendMessageNotification();
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

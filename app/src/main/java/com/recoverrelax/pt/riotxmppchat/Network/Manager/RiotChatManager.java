package com.recoverrelax.pt.riotxmppchat.Network.Manager;

import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.NewMessageReceivedNotifyPublish;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.NewMessageReceivedPublish;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppMiscUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.NotificationCenter.MessageSpeechNotification;
import com.recoverrelax.pt.riotxmppchat.NotificationCenter.NotificationHelper;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.InAppLogIds;
import com.recoverrelax.pt.riotxmppchat.Storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.Storage.MessageDirection;
import com.recoverrelax.pt.riotxmppchat.Storage.RiotXmppDBRepository;
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

import javax.inject.Inject;
import javax.inject.Singleton;

import LolChatRiotDb.MessageDb;
import LolChatRiotDb.NotificationDb;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Singleton
public class RiotChatManager implements ChatManagerListener, ChatMessageListener {

    private static final int MESSAGE_NOTIFICATION_DRAWABLE = R.drawable.ic_action_question_answer_green;
    private static final int MESSAGE_NOTIFICATION_ID = 1111111;
    @Inject RiotXmppDBRepository riotXmppDBRepository;
    @Inject RiotRosterManager riotRosterManager;
    @Inject MessageSpeechNotification messageSpeechNotification;
    @Inject DataStorage dataStorageInstance;
    @Inject Bus bus;
    private ChatManager chatManager;
    private String connectedXmppUser = null;
    private Map<String, Chat> chatList;

    @Singleton
    @Inject
    public RiotChatManager() {

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
        addChat(chat, messageFrom);

        if (messageFrom != null && message.getBody() != null) {
            if (connectedXmppUser != null) {
                MessageDb message1 = new MessageDb(null, connectedXmppUser, messageFrom, MessageDirection.FROM.getId(), new Date(), message.getBody(), false);

                riotXmppDBRepository.insertMessage(message1)
                        .subscribe();
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

        if (messageHasNoProperContent(message.getBody()))
            return;

        NotificationDb notification = riotXmppDBRepository.getNotificationByUser(userXmppAddress).toBlocking().single();
        String targetUserName = riotRosterManager.getFriendNameFromXmppAddress(userXmppAddress).toBlocking().single();

        riotXmppDBRepository.insertOrReplaceInappLog(
                InAppLogIds.FRIEND_PM.getOperationId(),
                targetUserName + " says: " + message.getBody(),
                userXmppAddress
        ).subscribe();

        if (getMessageSpeechPermission(notification))
            messageSpeechNotification.sendMessageSpeechNotification(message.getBody(), targetUserName);

        if (isPausedOrClosed()) {
            NotificationHelper.sendSystemNotification(targetUserName + " says:", message.getBody(), MESSAGE_NOTIFICATION_DRAWABLE, MESSAGE_NOTIFICATION_ID,
                    getMessageBackgroundPermission(notification)).subscribe();
        } else {

            boolean permission = getMessageForegroundPermission(notification);
            String buttonLabel = "CHAT";
            String messageFinal = targetUserName + " said: " + message.getBody();

            if (permission)
                bus.post(new NewMessageReceivedPublish(userXmppAddress, targetUserName, messageFinal, buttonLabel));
            bus.post(new NewMessageReceivedNotifyPublish(userXmppAddress, targetUserName, messageFinal, buttonLabel));
        }
    }

    private boolean messageHasNoProperContent(String body) {
        return body.contains("<body>");
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

    private boolean getMessageSpeechPermission(NotificationDb notification) {
        return notification != null &&
                (isPausedOrClosed()
                        ? dataStorageInstance.getGlobalNotifBackgroundSpeech()
                        : dataStorageInstance.getGlobalNotifForegroundSpeech()
                )
                && notification.getHasSentMePm() && !AppMiscUtils.isPhoneSilenced();
    }

    private boolean getMessageForegroundPermission(NotificationDb notification) {
        return notification != null &&
                !isPausedOrClosed() && dataStorageInstance.getGlobalNotifForegroundText() && notification.getHasSentMePm();
    }

    private boolean getMessageBackgroundPermission(NotificationDb notification) {
        return notification != null &&
                isPausedOrClosed() &&
                dataStorageInstance.getGlobalNotifBackgroundText() &&
                notification.getHasSentMePm();
    }

    protected boolean isPausedOrClosed() {
        return MainApplication.getInstance().isApplicationPausedOrClosed();
    }
}

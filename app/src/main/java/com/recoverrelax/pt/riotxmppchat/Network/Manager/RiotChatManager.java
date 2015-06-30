package com.recoverrelax.pt.riotxmppchat.Network.Manager;

import android.content.Context;

import com.recoverrelax.pt.riotxmppchat.Database.MessageDirection;
import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.OnNewMessageReceivedEventEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.NotificationCenter;
import com.recoverrelax.pt.riotxmppchat.Network.Helper.GlobalImpl;
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

public class RiotChatManager implements ChatManagerListener, ChatMessageListener {

    private AbstractXMPPConnection connection;
    private ChatManager chatManager;
    private String connectedXmppUser;
    private RiotRosterManager riotRosterManager;
    private Context context;
    private Bus busInstance;
    private GlobalImpl globalImpl;

    private RiotXmppDBRepository riotXmppDBRepository;

    private Map<String, Chat> chatList;

    public RiotChatManager(Context context, AbstractXMPPConnection connection, String connectedXmppUser, RiotRosterManager riotRosterManager){
        this.connection = connection;
        this.connectedXmppUser = connectedXmppUser;
        this.riotRosterManager = riotRosterManager;
        this.context = context;
        this.busInstance = MainApplication.getInstance().getBusInstance();
        this.riotXmppDBRepository = new RiotXmppDBRepository();
        this.globalImpl = new GlobalImpl(connection);
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

        addChat(chat, messageFrom);

        if (messageFrom != null && message.getBody() != null) {
            MessageDb message1 = new MessageDb(null, connectedXmppUser, messageFrom, MessageDirection.FROM.getId(), new Date(), message.getBody(), false);

            globalImpl.insertMessage(message1)
                .subscribe(new Subscriber<Long>() {
                    @Override public void onCompleted() { }
                    @Override public void onError(Throwable e) { }
                    @Override public void onNext(Long aLong) { } });
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

        new NotificationCenter(userXmppAddress)
                .sendMessageNotification(message.getBody());

        String name = MainApplication.getInstance().getRiotXmppService().getRiotRosterManager().getRosterEntry(userXmppAddress).getName();
        MainApplication.getInstance().getBusInstance().post(new OnNewMessageReceivedEventEvent(message, userXmppAddress, name));
    }

    public void sendMessage(String message, String userXmppName) {
        try {
            Chat chat = getChat(userXmppName);
            chat.sendMessage(message);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }
}

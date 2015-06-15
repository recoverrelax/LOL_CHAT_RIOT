package com.recoverrelax.pt.riotxmppchat.Network.Manager;

import android.content.Context;

import com.recoverrelax.pt.riotxmppchat.Database.MessageDirection;
import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.OnNewMessageReceivedEventEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.NewMessageSpeechNotification;
import com.recoverrelax.pt.riotxmppchat.ui.activity.FriendListActivity;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.FriendMessageListFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.PersonalMessageFragment;
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

public class RiotChatManager implements ChatManagerListener, ChatMessageListener {

    private AbstractXMPPConnection connection;
    private ChatManager chatManager;
    private String connectedXmppUser;
    private RiotRosterManager riotRosterManager;
    private Context context;
    private Bus busInstance;

    private Map<String, Chat> chatList;

    public RiotChatManager(Context context, AbstractXMPPConnection connection, String connectedXmppUser, RiotRosterManager riotRosterManager){
        this.connection = connection;
        this.connectedXmppUser = connectedXmppUser;
        this.riotRosterManager = riotRosterManager;
        this.context = context;
        this.busInstance = MainApplication.getInstance().getBusInstance();
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
            RiotXmppDBRepository.insertMessage(message1);

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

        String username = MainApplication.getInstance().getRiotXmppService().getRiotRosterManager().getRoster().getEntry(userXmppAddress).getName();

//        Log.i("TAG123", "Passed Here-1");
//        if (applicationClosed) {
//            Log.i("TAG123", "Passed Here0");
//
////            new SystemNotification(context, message.getBody(), username + " says: ");
//        }

//        new SoundNotification(context, R.raw.teemo_new_message, applicationClosed
//                ? SoundNotification.NotificationType.OFFLINE
//                : SoundNotification.NotificationType.ONLINE);

        NewMessageSpeechNotification.getInstance()
            .sendSpeechNotification(message.getBody(), username);


        /**
         * Deliver the new message to all the observers
         *
         * 1st: {@link FriendListActivity#OnNewMessageReceived(OnNewMessageReceivedEventEvent)}  }
         * 2nd: {@link PersonalMessageFragment#OnNewMessageReceived(OnNewMessageReceivedEventEvent)}  }
         * 3rd: {@link FriendMessageListFragment#OnNewMessageReceived(OnNewMessageReceivedEventEvent)}  }
         */


        busInstance.post(new OnNewMessageReceivedEventEvent(message, userXmppAddress, username));
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

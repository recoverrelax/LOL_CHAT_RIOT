package com.recoverrelax.pt.riotxmppchat.Network;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.edgelabs.pt.mybaseapp.R;
import com.recoverrelax.pt.riotxmppchat.Database.MessageDirection;
import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.MessageNotification;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.SoundNotification;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.XmppUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.Network.Helper.RiotXmppConnectionImpl;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotGlobals;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotServer;
import com.recoverrelax.pt.riotxmppchat.Riot.Interface.RiotXmppConnectionHelper;
import com.recoverrelax.pt.riotxmppchat.Riot.Interface.RiotXmppDataLoaderCallback;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

import LolChatRiotDb.MessageDb;
import rx.Observer;

import static junit.framework.Assert.assertTrue;

public class RiotXmppService extends Service implements Observer<RiotXmppConnectionImpl.RiotXmppOperations>, RosterListener, ChatMessageListener {

    private static final String TAG = RiotXmppService.class.getSimpleName();
    private static final int ONGOING_SERVICE_NOTIFICATION_ID = 12345;

    private final IBinder mBinder = new MyBinder();
    private DataStorage dataStorage;
    private MainApplication mainApplication;

    public static final String INTENT_SERVER_HOST_CONST = "server";
    public static final String INTENT_SERVER_USERNAME = "username";
    public static final String INTENT_SERVER_PASSWORD = "password";

    private static final long DELAY_BEFORE_ROSTER_LISTENER = 5000;

    public static RiotXmppDataLoaderCallback<RiotXmppConnectionImpl.RiotXmppOperations> loginActilivyCallback;

    /**
     * Server Info
     */
    private String serverHost;

    private int serverPort = RiotGlobals.RIOT_PORT;
    private String serverDomain = RiotGlobals.RIOT_DOMAIN;
    private String username;
    private String password;

    private XMPPTCPConnectionConfiguration connectionConfig;
    private AbstractXMPPConnection connection;
    private RiotXmppConnectionHelper connectionHelper;
    private Roster roster;
    private ChatManager chatManager;
    private ChatManagerListener chatManagerListener;
    private Map<String, Chat> chatList;

    /**
     * Callbacks
     */

    /**
     * New Message Notification
     */

    private List<NewMessageObserver> newMessageObserver;
    public class MyBinder extends Binder {
        public RiotXmppService getService() {
            return RiotXmppService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dataStorage = DataStorage.getInstance();

        /**
         * Get credentials from intent
         */
        if (intent != null) {
            Bundle extras = intent.getExtras();
            username = extras.getString(INTENT_SERVER_USERNAME, null);
            password = extras.getString(INTENT_SERVER_PASSWORD, null);
            serverHost = (RiotServer.getRiotServerByName(extras.getString(INTENT_SERVER_HOST_CONST, null))).getServerHost();
        } else {
            /**
             * Get credentials from SharedPreferences
             */
            username = dataStorage.getUsername();
            password = dataStorage.getPassword();
            serverHost = (RiotServer.getRiotServerByName((dataStorage.getServer())).getServerHost());
        }

        if (intent != null && isUserAlreadyLogged(username, password, serverHost)) {
            /**
             * USER LOGGED IN, JUST START THE APP
             */
            onNext(RiotXmppConnectionImpl.RiotXmppOperations.LOGGED_IN);
        } else {
            /**
             * USER NOT LOGGED IN
             * ---- DO LOGGED_IN
             */
            connectToRiotXmppServer(serverHost, serverPort, serverDomain, username, password);
        }

        NotificationCompat.Builder mNotificationBuilder =
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.profile_icon_example)
                .setContentTitle("Lol Friend's Alerter")
                .setContentText("LOL Friend Alerter is now running.");


        Notification notification = mNotificationBuilder.build();
        startForeground(ONGOING_SERVICE_NOTIFICATION_ID, notification);

        return Service.START_STICKY;
    }

    private boolean isUserAlreadyLogged(String username, String password, String serverHost) {
        if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
            if (dataStorage.getUsername().equals(username) && dataStorage.getPassword().equals(password) && dataStorage.getServer().equals(RiotServer.getRiotServerHost(serverHost).getServerName())) {
                /**
                 * USER ALREADY LOGGED IN
                 */
                return true;
            } else {
                /**
                 * USER NOT LOGGED IN OR ANOTHER USER LOGGED IN
                 */
                return false;
            }
        } else
            return false;
    }

    public void connectToRiotXmppServer(String serverHost, int serverPort, String serverDomain,
                                        String username, String password) {

        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.serverDomain = serverDomain;

        this.username = username;
        this.password = password;

        prepareConnectionConfig(serverDomain, serverHost, serverPort);
        connection = new XMPPTCPConnection(connectionConfig);
        connectionHelper = new RiotXmppConnectionImpl(this);

        connect();
    }

    public void prepareConnectionConfig(String serverDomain, String serverHost, int serverPort) {
        this.connectionConfig = XMPPTCPConnectionConfiguration.builder()
                .setSocketFactory(SSLSocketFactory.getDefault())
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setServiceName(serverDomain)
                .setHost(serverHost)
                .setUsernameAndPassword(username, "AIR_" + password)
                .setPort(serverPort)
                .build();
    }

    /**
     * Attempt to connect to the Riot Server. Success/Fail are reported back to
     * {@link #onNext(RiotXmppConnectionImpl.RiotXmppOperations) onNext},
     * {@link #onError(Throwable) onError};
     */
    public void connect() {
        assertTrue("To start a connection to the server, you must first call init() method!",
                this.connectionConfig != null);

        connectionHelper.connect(connection);
    }

    public void login() {

        assertTrue("To start a connection to the server, you must first call init() method!",
                this.connectionConfig != null || this.connection != null);

        connectionHelper.login(connection);
    }

    /**
     * Observer callback for both connection and authentication to Riot Servers.
     *
     * @param result The parameter will tell which operation was successfull. Connection or Authentication.
     */
    @Override
    public void onNext(RiotXmppConnectionImpl.RiotXmppOperations result) {
        switch (result) {
            case CONNECTED:
                login();
                break;
            case LOGGED_IN:
                if (loginActilivyCallback != null) {
                    loginActilivyCallback.onSuccess(result);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addRosterListener(RiotXmppService.this);
                        addChatListener();
                    }
                }, DELAY_BEFORE_ROSTER_LISTENER);

                break;
        }
    }

    @Override public void onCompleted() {}

    @Override
    public void onError(Throwable e) {
        if (loginActilivyCallback != null) {
            loginActilivyCallback.onFailure(e);
        }
    }

    public void addRosterListener(RosterListener rosterListener) {
        if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
            this.roster = Roster.getInstanceFor(connection);
            this.roster.addRosterListener(rosterListener);
        }
    }

    public void addChatListener() {
        if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
            this.chatManager = ChatManager.getInstanceFor(connection);
            this.chatManagerListener = new ChatManagerListener() {
                @Override
                public void chatCreated(Chat chat, boolean createdLocally) {
                    chat.addMessageListener(RiotXmppService.this);
                }
            };
            this.chatManager.addChatListener(this.chatManagerListener);
        }
    }

    /**
     * Chat Listener callback receiver
     * @param chat
     * @param message
     */
    @Override
    public void processMessage(Chat chat, Message message) {
        String messageFrom = XmppUtils.parseXmppAddress(message.getFrom());

        addChat(chat, messageFrom);

        if(message != null && messageFrom != null && message.getBody() != null){
            MessageDb message1 = new MessageDb(null, getConnectedXmppUser(), messageFrom, MessageDirection.FROM.getId(), new Date(), message.getBody(), false);
            RiotXmppDBRepository.insertMessage(message1);
            LogUtils.LOGI(TAG, "Iserted message in the db:\n + " + message1.toString());

//            MainApplication.getInstance().getBus().post(new OnNewMessageReceived(messageFrom));
            notifyNewMessage(message, messageFrom);
        }
    }

    public void addChat(Chat chat, String messageFrom){
        if(this.chatList == null){
            this.chatList = new HashMap<>();
        }

        if(!this.chatList.containsKey(messageFrom)){
            this.chatList.put(messageFrom, chat);
        }
    }

    public Chat getChat(String userXmppName){
            /**
             * At this step means, there's no active chat for that user so it means you are starting the conversation
             * and need to start a new chat  as well.
             */
            Chat chat = this.chatManager.createChat(userXmppName, RiotXmppService.this);
            addChat(chat, userXmppName);
            return chat;
    }

    public Roster getRoster() {
        return roster;
    }

    public Presence getRosterPresence(String xmppAddress){
        if(roster != null){
            return roster.getPresence(xmppAddress);
        }
        return null;
    }

    public void sendMessage(String message, String userXmppName){
        try {
            Chat chat = getChat(userXmppName);
            chat.sendMessage(message);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    public void removeChatListener(){
        if (this.chatManager != null && this.chatManagerListener != null) {
            this.chatManager.removeChatListener(this.chatManagerListener);
        }
    }

    public void removeRosterListener(RosterListener rosterListener) {
        if (roster != null && rosterListener != null) {
            roster.removeRosterListener(rosterListener);
        }
    }

    public AbstractXMPPConnection getConnection() {
        return connection;
    }

    public String getConnectedXmppUser(){
        return XmppUtils.parseXmppAddress(connection.getUser());
    }

    @Override
    public void onDestroy() {
        if (connection != null)
            connection.disconnect();
        connection = null;
        super.onDestroy();
    }

    /**
     * ROSTER CHANGES LISTENER
     * @param addresses
     */
    @Override public void entriesAdded(Collection<String> addresses) {}
    @Override public void entriesUpdated(Collection<String> addresses) {}
    @Override public void entriesDeleted(Collection<String> addresses) {}

    @Override
    public void presenceChanged(Presence presence) {
        LogUtils.LOGI(TAG, "Callback called on the service");
    }

    /**
     * Add an observer to the list of observers
     * @param observer
     */
    public void addNewMessageObserver(NewMessageObserver observer){
        if(newMessageObserver == null)
            newMessageObserver = new ArrayList<>();

        newMessageObserver.add(observer);
    }

    /**
     * Remove an observers from the list of observers
     * @param observer
     */
    public void removeNewMessageObserver(NewMessageObserver observer){
        if(newMessageObserver != null)
            newMessageObserver.remove(observer);
    }

    /**
     * Notify all Observers of new messages
     * @param message
     * @param messageFrom
     */
    public void notifyNewMessage(Message message, String messageFrom){

        if(MainApplication.getInstance().isApplicationClosed()){
            String username = roster.getEntry(messageFrom).getName();

            new MessageNotification(this, message.getBody(), username).sendNotification();
        }

        /**
         * Play a sound everytime!
         */
            new SoundNotification(this, R.raw.teemo_new_message);

        /**
         * Deliver the new message to all the observers
         */
        for(NewMessageObserver observer: newMessageObserver){
            observer.OnNewMessageNotification(message, messageFrom);
        }
    }

    /**
     * New Message Notification Interface
     * Used to notify observers of newly received messages
     */
    public interface NewMessageObserver {
        void OnNewMessageNotification(Message message, String messageFrom);
    }

}

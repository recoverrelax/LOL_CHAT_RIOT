package com.recoverrelax.pt.riotxmppchat.Network;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.recoverrelax.pt.riotxmppchat.Database.MessageDirection;
import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.EventHandling.FriendList.OnFriendPresenceChangedEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.FriendList.OnReconnectListener;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.FriendLeftGameNotification;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.OnNewMessageReceivedEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Login.OnConnectionOrLoginFailureEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Login.OnSuccessLoginEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.SoundNotification;
import com.recoverrelax.pt.riotxmppchat.MyUtil.SystemNotification;
import com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.Network.Helper.RiotXmppConnectionImpl;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotGlobals;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotServer;
import com.recoverrelax.pt.riotxmppchat.Riot.Interface.RiotXmppConnectionHelper;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
import com.recoverrelax.pt.riotxmppchat.ui.activity.FriendListActivity;
import com.recoverrelax.pt.riotxmppchat.ui.activity.FriendMessageListActivity;
import com.recoverrelax.pt.riotxmppchat.ui.activity.LoginActivity;
import com.recoverrelax.pt.riotxmppchat.ui.activity.PersonalMessageActivity;
import com.recoverrelax.pt.riotxmppchat.ui.activity.SettingActivity;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.FriendListFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.FriendMessageListFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.PersonalMessageFragment;
import com.squareup.otto.Bus;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.SSLSocketFactory;

import LolChatRiotDb.MessageDb;
import rx.Observer;

import static junit.framework.Assert.assertTrue;

public class RiotXmppService extends Service implements Observer<RiotXmppConnectionImpl.RiotXmppOperations>, RosterListener, ChatMessageListener, ConnectionListener {

    private static final String TAG = RiotXmppService.class.getSimpleName();
    private static final int ONGOING_SERVICE_NOTIFICATION_ID = 12345;
    private Bus busInstance;

    private final IBinder mBinder = new MyBinder();
    private DataStorage dataStorage;

    public static final String INTENT_SERVER_HOST_CONST = "server";
    public static final String INTENT_SERVER_USERNAME = "username";
    public static final String INTENT_SERVER_PASSWORD = "password";

    private static final long DELAY_BEFORE_ROSTER_LISTENER = 500;

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
    private ConnectionListener connectionListener;
    private Map<String, Chat> chatList;

    private Set<String> friendsPlaying = new HashSet<>();

    /**
     * Callbacks
     */

    /**
     * New Message Notification
     */

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
        busInstance = MainApplication.getInstance().getBusInstance();
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
        /**
         * USER ALREADY LOGGED IN
         * OR
         * USER NOT LOGGED IN OR ANOTHER USER LOGGED IN
         */
        return connection != null
                && connection.isConnected()
                && connection.isAuthenticated()
                && dataStorage.getUsername().equals(username)
                && dataStorage.getPassword().equals(password)
                && dataStorage.getServer().equals(RiotServer.getRiotServerHost(serverHost).getServerName());
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
                /**{@link LoginActivity#onSuccessLogin(OnSuccessLoginEvent)} */
                MainApplication.getInstance().getBusInstance().post(new OnSuccessLoginEvent());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addRosterListener(RiotXmppService.this);
                        addChatListener();

                        ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(connection);
                        reconnectionManager.enableAutomaticReconnection();
                        reconnectionManager.setReconnectionPolicy(ReconnectionManager.ReconnectionPolicy.RANDOM_INCREASING_DELAY);
                        addConnectionListener(RiotXmppService.this);
                }
                }, DELAY_BEFORE_ROSTER_LISTENER);

                break;
        }
    }

    @Override
    public void onCompleted() {}

    @Override
    public void onError(Throwable e) {
        /**{@link LoginActivity#onFailure(OnConnectionOrLoginFailureEvent)} */
        MainApplication.getInstance().getBusInstance().post(new OnConnectionOrLoginFailureEvent());
    }

    public void addRosterListener(RosterListener rosterListener) {
        if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
            this.roster = Roster.getInstanceFor(connection);
            this.roster.addRosterListener(rosterListener);
        }
    }

    public void addConnectionListener(ConnectionListener connectionListener){
        if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
            this.connectionListener = connectionListener;
            this.connection.addConnectionListener(this.connectionListener);
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


    @Override
    public void processMessage(Chat chat, Message message) {
        // sum1212121@riot.pt
        // but we need sum1212121 ...
        String messageFrom = AppXmppUtils.parseXmppAddress(message.getFrom());

        addChat(chat, messageFrom);

        if (messageFrom != null && message.getBody() != null) {
            MessageDb message1 = new MessageDb(null, getConnectedXmppUser(), messageFrom, MessageDirection.FROM.getId(), new Date(), message.getBody(), false);
            RiotXmppDBRepository.insertMessage(message1);
            LogUtils.LOGI(TAG, "Iserted message in the db:\n + " + message1.toString());

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
        Chat chat = this.chatManager.createChat(userXmppName, RiotXmppService.this);
        addChat(chat, userXmppName);
        return chat;
    }

    public Roster getRoster() {
        if(roster != null && roster.isLoaded())
        return roster;
        else {
            roster = Roster.getInstanceFor(connection);
            return roster;
        }
    }

    public Collection<RosterEntry> getRosterEntries(){
        return getRoster().getEntries();
    }

    public RosterEntry getRosterEntry(String user){
        return getRoster().getEntry(user);
    }

    public Presence getRosterPresence(String xmppAddress) {
        return getRoster().getPresence(xmppAddress);
    }

    public void sendMessage(String message, String userXmppName) {
        try {
            Chat chat = getChat(userXmppName);
            chat.sendMessage(message);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    public void removeChatListener() {
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

    /**
     *
     * @return eg: sum12345@pvp.net
     */
    public String getConnectedXmppUser() {
        MainApplication instance = MainApplication.getInstance();

        if (instance.getConnectedXmppUser() == null) {
            instance.setConnectedXmppUser(AppXmppUtils.parseXmppAddress(connection.getUser()));
        }
        return instance.getConnectedXmppUser();
    }

    @Override
    public void onDestroy() {
        if (connection != null)
            connection.disconnect();
        connection = null;
        super.onDestroy();
    }

    @Override
    public void entriesAdded(Collection<String> addresses) {}
    @Override
    public void entriesUpdated(Collection<String> addresses) {}
    @Override
    public void entriesDeleted(Collection<String> addresses) {}

    /** {@link FriendListFragment#OnFriendPresenceChanged(OnFriendPresenceChangedEvent)}**/
    @Override
    public void presenceChanged(Presence presence) {
        LogUtils.LOGI(TAG, "Callback called on the service");
        busInstance.post(new OnFriendPresenceChangedEvent(presence));

        RosterEntry rosterEntry = MainApplication.getInstance().getRiotXmppService().getRosterEntry(presence.getFrom());
        Presence bestPresence = MainApplication.getInstance().getRiotXmppService().getRosterPresence(presence.getFrom());
        String user = AppXmppUtils.parseXmppAddress(rosterEntry.getUser());

        Friend friend = new Friend(rosterEntry.getName(), user, bestPresence);

        if(friend.isPlaying())
            addFriendPlaying(friend.getName(), friend.getUserXmppAddress());
        else
            removeFriendPlaying(friend.getName(), friend.getUserXmppAddress());
    }

    /**
     * Notify all Observers of new messages
     *
     */
    public void notifyNewMessage(Message message, String userXmppAddress) {

        boolean applicationClosed = MainApplication.getInstance().isApplicationClosed();

        if (applicationClosed) {
            String username = roster.getEntry(userXmppAddress).getName();
            new SystemNotification(this, message.getBody(), username + " says: ");
        }

        new SoundNotification(this, R.raw.teemo_new_message, applicationClosed
                ? SoundNotification.NotificationType.OFFLINE
                : SoundNotification.NotificationType.ONLINE);


        /**
         * Deliver the new message to all the observers
         *
         * 1st: {@link FriendListFragment#OnNewMessageReceived(OnNewMessageReceivedEvent)}  }
         * 2nd: {@link PersonalMessageFragment#OnNewMessageReceived(OnNewMessageReceivedEvent)}  }
         * 3rd: {@link FriendMessageListFragment#OnNewMessageReceived(OnNewMessageReceivedEvent)}  }
         */
        busInstance.post(new OnNewMessageReceivedEvent(message, userXmppAddress));
    }

    public void addFriendPlaying(String friendName, String userXmppAddress){
        friendsPlaying.add(friendName);
        Log.i("TAGF", "Added: " + friendName + " to friendsPlaying!");
    }

    public void removeFriendPlaying(String friendName, String userXmppAddress){
        boolean removed = friendsPlaying.remove(friendName);
//        username + " says: \n" + message
            if(removed){
                Log.i("ASAS", "REMOVED FRIEND: " + friendName);
                if (MainApplication.getInstance().isApplicationClosed()) {
                    new SystemNotification(this, friendName, "... just left a game!");
                }else{

                    /**
                     * 1st: {@link FriendListActivity#OnFriendLeftGame(FriendLeftGameNotification)}
                     * 2nd: {@link PersonalMessageActivity#OnFriendLeftGame(FriendLeftGameNotification)}
                     * 3rd: {@link FriendMessageListActivity#OnFriendLeftGame(FriendLeftGameNotification)}
                     * 4th: {@link SettingActivity#OnFriendLeftGame(FriendLeftGameNotification)}
                     */
                    MainApplication.getInstance().getBusInstance().post(new FriendLeftGameNotification(friendName + " ... just left a game!", friendName, userXmppAddress));
                }
            }
    }

    @Override public void connected(XMPPConnection connection) { }

    @Override public void authenticated(XMPPConnection connection, boolean resumed) { }

    @Override public void connectionClosed() { }

    @Override public void connectionClosedOnError(Exception e) { }

    @Override
    public void reconnectionSuccessful() {
        Log.i("TAS", "Recconected!");
        MainApplication.getInstance().getBusInstance().post(new OnReconnectListener());
    }

    @Override public void reconnectingIn(int seconds) { }

    @Override public void reconnectionFailed(Exception e) { }

}

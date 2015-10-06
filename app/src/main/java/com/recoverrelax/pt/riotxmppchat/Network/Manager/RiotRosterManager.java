package com.recoverrelax.pt.riotxmppchat.Network.Manager;

import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.NewMessageReceivedPublish;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.OnFriendPresenceChangedPublish;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.OnNewFriendPlayingPublish;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppMiscUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.NotificationCenter.MessageSpeechNotification;
import com.recoverrelax.pt.riotxmppchat.NotificationCenter.NotificationHelper;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.InAppLogIds;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
import com.recoverrelax.pt.riotxmppchat.Storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.Storage.RiotXmppDBRepository;
import com.squareup.otto.Bus;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import LolChatRiotDb.MessageDb;
import LolChatRiotDb.MessageDbDao;
import LolChatRiotDb.NotificationDb;
import de.greenrobot.dao.query.QueryBuilder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.LogUtils.LOGI;
import static junit.framework.Assert.assertTrue;

@Singleton
public class RiotRosterManager implements RosterListener {

    private Roster roster;
    private AbstractXMPPConnection connection;
    private Map<String, Presence> friendList; // friendXmppAddress, Presence
    private boolean enabled = false;
    private boolean notificationsEnabled = true;

    private static final int ONLINE_NOTIFICATION_DRAWABLE = R.drawable.ic_online;
    private static final int OFFLINE_NOTIFICATION_DRAWABLE = R.drawable.ic_offline;
    private static final int START_GAME_NOTIFICATION_DRAWABLE = R.drawable.ic_online;
    private static final int LEFT_GAME_NOTIFICATION_DRAWABLE = R.drawable.ic_offline;

    private static final int STATUS_NOTIFICATION_ID = 2222222;

    @Inject
    Bus busInstance;
    @Inject
    RiotXmppDBRepository riotXmppDBRepository;
    @Inject
    Bus bus;
    @Inject
    DataStorage dataStorageInstance;
    @Inject
    MessageSpeechNotification messageSpeechNotification;

    @Singleton
    @Inject
    public RiotRosterManager() {
    }

    public void enableNotifications() {
        this.notificationsEnabled = true;
    }

    public void disableNotifications() {
        this.notificationsEnabled = false;
    }

    public void init(AbstractXMPPConnection connection) {
        this.connection = connection;
        this.friendList = new HashMap<>();
        LOGI("123", "HERE");
    }

    public void addRosterListener() {
        checkConnectionInit();

        if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
            this.roster = Roster.getInstanceFor(connection);
            this.roster.addRosterListener(this);
            LOGI("123", "HERE2");
        }
    }

    public void checkConnectionInit() {
        assertTrue("Must call init first", connection != null);
    }

    public void removeRosterListener(RosterListener rosterListener) {
        if (roster != null && rosterListener != null) {
            roster.removeRosterListener(rosterListener);
        }
    }

    @Override
    public void entriesAdded(Collection<String> addresses) {
    }

    @Override
    public void entriesUpdated(Collection<String> addresses) {
    }

    @Override
    public void entriesDeleted(Collection<String> addresses) {
    }

    public Observable<RosterEntry> getRosterEntries() {
        return Observable.defer(() -> Observable.from(roster.getEntries()));
    }

    public Observable<Friend> getFriendFromRosterEntry(RosterEntry entry) {
        return getRosterPresence(entry.getUser())
                .map(rosterPresence -> {
                    String finalUserXmppAddress = AppXmppUtils.parseXmppAddress(entry.getUser());
                    return new Friend(entry.getName(), finalUserXmppAddress, rosterPresence);
                });
    }

    public Observable<MessageDb> getFriendLastMessage(String friendUser) {

        return MainApplication.getInstance().getRiotXmppService().getRiotConnectionManager().getConnectedUser()
                .map(connectedUser -> {
                    List<MessageDb> list = RiotXmppDBRepository.getMessageDao().queryBuilder()
                            .where(MessageDbDao.Properties.UserXmppId.eq(connectedUser),
                                    MessageDbDao.Properties.FromTo.eq(friendUser))
                            .orderDesc(MessageDbDao.Properties.Id)
                            .limit(1).build().list();

                    return list.size() == 0
                            ? null
                            : list.get(0);
                });
    }

    public Observable<List<MessageDb>> getLastXMessages(int x, String userToGetMessagesFrom) {
        return MainApplication.getInstance().getRiotXmppService().getRiotConnectionManager().getConnectedUser()
                .map(connectedUser -> {
                    QueryBuilder qb = RiotXmppDBRepository.getMessageDao().queryBuilder();
                    qb.where(MessageDbDao.Properties.UserXmppId.eq(connectedUser),
                            MessageDbDao.Properties.FromTo.eq(userToGetMessagesFrom))
                            .orderDesc(MessageDbDao.Properties.Id)
                            .limit(x).build();
                    return qb.list();
                });
    }

    public Observable<String> getFriendNameFromXmppAddress(String friendXmppAddress) {
        return getRosterEntry(friendXmppAddress)
                .map(RosterEntry::getName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Friend> getFriendFromXmppAddress(String userXmppAddress) {
        return getRosterEntry(userXmppAddress)
                .flatMap(rosterEntry -> getRosterPresence(rosterEntry.getUser())
                        .map(presence -> {
                            String finalUserXmppAddress = AppXmppUtils.parseXmppAddress(rosterEntry.getUser());
                            return new Friend(rosterEntry.getName(), finalUserXmppAddress, presence);
                        }));
    }

    public Observable<RosterEntry> getRosterEntry(String user) {
        return Observable.defer(() -> Observable.just(roster.getEntry(user)));
    }

    /**
     * sum12345678@pvp.net
     */
    public Observable<String> getSummonerIdByXmppName(String xmppName) {
        return Observable.defer(
                () -> Observable.just(roster.getEntry(xmppName).getUser())
                        .map(jid -> {
                            String firstPart = "sum";
                            String secondPart = "@pvp.net";
                            return jid.replace(firstPart, "").replace(secondPart, "");
                        })
        )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Presence> getRosterPresence(String xmppAddress) {
        return Observable.defer(() -> Observable.just(roster.getPresence(xmppAddress)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public void presenceChanged(Presence presence) {
        if (connection != null && connection.isConnected() && notificationsEnabled)
            checkForFriendNotificationToSend(presence);
        busInstance.post(new OnFriendPresenceChangedPublish(presence));
    }

    public void updateFriend(Presence presence) {
        String from = presence.getFrom();
        String friendXmppAddress = AppXmppUtils.parseXmppAddress(from);

        friendList.put(friendXmppAddress, presence);
    }

    public void clearFriendList() {
        this.friendList.clear();
    }

    public FriendStates getFriendState(Friend friend) {
        FriendStates state;

        if (friend == null || friend.isOffline())
            state = FriendStates.OFFLINE;
        else if (friend.isPlaying())
            state = FriendStates.PLAYINNG;
        else
            state = FriendStates.IDLE;
        return state;
    }

    public void checkForFriendNotificationToSend(Presence newPresence) {
        String xmppAddress = AppXmppUtils.parseXmppAddress(newPresence.getFrom());
        String friendName = getFriendNameFromXmppAddress(xmppAddress).toBlocking().single();

        Presence oldPresence = friendList.containsKey(xmppAddress) ? friendList.get(xmppAddress) : null;

        FriendStates oldState = getFriendState(new Friend(friendName, xmppAddress, oldPresence));
        FriendStates newState = getFriendState(new Friend(friendName, xmppAddress, newPresence));

        if (enabled) {
            if (oldState.isOffline() && !newState.isOffline()) {
                sendStatusNotification(xmppAddress, friendName, Status.ONLINE);
            } else if (!oldState.isOffline() && newState.isOffline()) {
                sendStatusNotification(xmppAddress, friendName, Status.OFFLINE);
            } else if (!oldState.isPlaying() && newState.isPlaying()) {
                sendStatusNotification(xmppAddress, friendName, Status.STARTED_GAME);
            } else if (oldState.isPlaying() && !newState.isPlaying()) {
                sendStatusNotification(xmppAddress, friendName, Status.LEFT_GAME);
            }
        }
        updateFriend(newPresence);
    }

    public void sendStatusNotification(String xmppAddress, String username, Status status) {

        NotificationDb notification = riotXmppDBRepository.getNotificationByUser(xmppAddress).toBlocking().single();

        if (status.isStartedGame() || status.isLeftGame())
            bus.post(new OnNewFriendPlayingPublish());

        int logId = getLogIdFromStatus(status);
        String logMessage = getLogMessageFromStatus(status, username);
        boolean speechPermission = getSpeechPermissionFromStatus(status, notification);

        riotXmppDBRepository.insertOrReplaceInappLog(logId, logMessage, xmppAddress).subscribe();

        if (speechPermission)
            messageSpeechNotification.sendStatusSpeechNotification(logMessage);

        if (isPausedOrClosed()) {
            String systemNotificationMessage = getMessageFromStatus(status);
            int notificationDrawable = getNotificationDrawableFromStatus(status);

            boolean statusPermission = getStatusPermissionFromStatus(status, notification);

            NotificationHelper.sendSystemNotification(username, systemNotificationMessage, notificationDrawable, STATUS_NOTIFICATION_ID,
                    statusPermission).subscribe();
        } else {

            boolean permission = getStatusPermissionFromStatus(status, notification);

            if (permission) {
                String buttonLabel = "CHAT";
                String message = username + " " + getMessageFromStatus(status);

                bus.post(new NewMessageReceivedPublish(xmppAddress, username, message, buttonLabel));
            }
        }
    }

    public void setEnabled(boolean state) {
        this.enabled = state;
    }


    public enum FriendStates {
        OFFLINE,
        PLAYINNG,
        IDLE;

        public boolean isOffline() {
            return this.equals(FriendStates.OFFLINE);
        }

        public boolean isPlaying() {
            return this.equals(FriendStates.PLAYINNG);
        }

        public boolean isIdle() {
            return this.equals(FriendStates.IDLE);
        }
    }

    public enum Status {
        ONLINE,
        OFFLINE,
        STARTED_GAME,
        LEFT_GAME;

        public boolean isOnline() {
            return this.equals(Status.ONLINE);
        }

        public boolean isOffline() {
            return this.equals(Status.OFFLINE);
        }

        public boolean isStartedGame() {
            return this.equals(Status.STARTED_GAME);
        }

        public boolean isLeftGame() {
            return this.equals(Status.LEFT_GAME);
        }
    }

    private int getLogIdFromStatus(Status status) {
        switch (status) {
            case ONLINE:
                return InAppLogIds.FRIEND_ONLINE.getOperationId();
            case OFFLINE:
                return InAppLogIds.FRIEND_OFFLINE.getOperationId();
            case STARTED_GAME:
                return InAppLogIds.FRIEND_STARTED_GAME.getOperationId();
            case LEFT_GAME:
                return InAppLogIds.FRIEND_ENDED_GAME.getOperationId();
            default:
                return InAppLogIds.FRIEND_OFFLINE.getOperationId();
        }
    }

    private String getLogMessageFromStatus(Status status, String username) {
        return username + " " + getMessageFromStatus(status);
    }

    private String getMessageFromStatus(Status status) {
        switch (status) {
            case ONLINE:
                return "is now Online";
            case OFFLINE:
                return "has went Offline";
            case STARTED_GAME:
                return "has started a game";
            case LEFT_GAME:
                return "has left a game";
            default:
                return "has went Offline";
        }
    }

    private boolean getSpeechPermissionFromStatus(Status status, NotificationDb notificationDb) {

        if (notificationDb == null)
            return false;

        switch (status) {
            case ONLINE:
                return (isPausedOrClosed() ? dataStorageInstance.getGlobalNotifBackgroundSpeech() : dataStorageInstance.getGlobalNotifForegroundSpeech())
                        && notificationDb.getIsOnline()
                        && !AppMiscUtils.isPhoneSilenced();

            case OFFLINE:
                return (isPausedOrClosed() ? dataStorageInstance.getGlobalNotifBackgroundSpeech() : dataStorageInstance.getGlobalNotifForegroundSpeech())
                        && notificationDb.getIsOffline()
                        && !AppMiscUtils.isPhoneSilenced();

            case STARTED_GAME:
                return (isPausedOrClosed() ? dataStorageInstance.getGlobalNotifBackgroundSpeech() : dataStorageInstance.getGlobalNotifForegroundSpeech())
                        && notificationDb.getHasStartedGame()
                        && !AppMiscUtils.isPhoneSilenced();

            case LEFT_GAME:
                return (isPausedOrClosed() ? dataStorageInstance.getGlobalNotifBackgroundSpeech() : dataStorageInstance.getGlobalNotifForegroundSpeech())
                        && notificationDb.getHasLefGame()
                        && !AppMiscUtils.isPhoneSilenced();
            default:
                return (isPausedOrClosed() ? dataStorageInstance.getGlobalNotifBackgroundSpeech() : dataStorageInstance.getGlobalNotifForegroundSpeech())
                        && notificationDb.getIsOffline()
                        && !AppMiscUtils.isPhoneSilenced();
        }
    }

    protected boolean isPausedOrClosed() {
        return MainApplication.getInstance().isApplicationPausedOrClosed();
    }

    private int getNotificationDrawableFromStatus(Status status) {
        switch (status) {
            case ONLINE:
                return ONLINE_NOTIFICATION_DRAWABLE;
            case OFFLINE:
                return OFFLINE_NOTIFICATION_DRAWABLE;
            case STARTED_GAME:
                return START_GAME_NOTIFICATION_DRAWABLE;
            case LEFT_GAME:
                return LEFT_GAME_NOTIFICATION_DRAWABLE;
            default:
                return ONLINE_NOTIFICATION_DRAWABLE;
        }
    }

    private boolean getStatusPermissionFromStatus(Status status, NotificationDb notificationDb) {
        switch (status) {
            case ONLINE:
            case OFFLINE:
                return (isPausedOrClosed() ? dataStorageInstance.getGlobalNotifBackgroundText() : dataStorageInstance.getGlobalNotifForegroundText()) &&
                        (status.isOnline()
                                ? notificationDb.getIsOnline()
                                : notificationDb.getIsOffline()
                        );
            case STARTED_GAME:
            case LEFT_GAME:
                return (isPausedOrClosed()) ? dataStorageInstance.getGlobalNotifBackgroundText() : dataStorageInstance.getGlobalNotifForegroundText() &&
                        (status.isStartedGame()
                                ? notificationDb.getHasStartedGame()
                                : notificationDb.getHasLefGame()
                        );
            default:
                return (isPausedOrClosed() ? dataStorageInstance.getGlobalNotifBackgroundText() : dataStorageInstance.getGlobalNotifForegroundText()) &&
                        (status.isOnline()
                                ? notificationDb.getIsOnline()
                                : notificationDb.getIsOffline()
                        );
        }
    }
}

package com.recoverrelax.pt.riotxmppchat.Network.Manager;

import com.recoverrelax.pt.riotxmppchat.NotificationCenter.StatusNotification;
import com.recoverrelax.pt.riotxmppchat.Storage.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.EventHandling.OnFriendPresenceChangedEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.FriendListFragment;
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
import javax.inject.Provider;
import javax.inject.Singleton;

import LolChatRiotDb.MessageDb;
import LolChatRiotDb.MessageDbDao;
import de.greenrobot.dao.query.QueryBuilder;
import rx.Observable;
import rx.Subscriber;
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

    @Inject Bus busInstance;
    @Inject Provider<StatusNotification> statusNotificationProvider;

    @Singleton
    @Inject
    public RiotRosterManager() {}

    public void enableNotifications(){
        this.notificationsEnabled = true;
    }

    public void disableNotifications(){
        this.notificationsEnabled = false;
    }

    public void init(AbstractXMPPConnection connection){
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

    public void checkConnectionInit(){
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

    public Observable<Presence> getRosterPresence(String xmppAddress) {
        return Observable.defer(() -> Observable.just(roster.getPresence(xmppAddress)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }



    @Override
    public void presenceChanged(Presence presence) {
        if(connection != null && connection.isConnected() && notificationsEnabled)
            checkForFriendNotificationToSend(presence);

        LOGI("123", "HERE3");
        /** {@link FriendListFragment#OnFriendPresenceChanged(OnFriendPresenceChangedEvent)} */
        busInstance.post(new OnFriendPresenceChangedEvent(presence));
    }

    public void updateFriend(Presence presence){
        String from = presence.getFrom();
        String friendXmppAddress = AppXmppUtils.parseXmppAddress(from);

        friendList.put(friendXmppAddress, presence);
    }

    public void clearFriendList(){
        this.friendList.clear();
    }

    public FriendStates getFriendState(Friend friend){
        FriendStates state;

        if(friend == null ||friend.isOffline())
            state = FriendStates.OFFLINE;
        else if(friend.isPlaying())
            state = FriendStates.PLAYINNG;
        else
            state = FriendStates.IDLE;
        return state;
    }

    public void checkForFriendNotificationToSend(Presence newPresence) {
        String xmppAddress = AppXmppUtils.parseXmppAddress(newPresence.getFrom());
        LOGI("123", "HERE4");
        getFriendNameFromXmppAddress(xmppAddress)
                .subscribe(new Subscriber<String>() {
                    @Override public void onCompleted() { }
                    @Override public void onError(Throwable e) { }

                    @Override
                    public void onNext(String friendName) {
                        Presence oldPresence = friendList.containsKey(xmppAddress) ? friendList.get(xmppAddress) : null;

                        Friend friendOldStatus = new Friend(friendName, xmppAddress, oldPresence);
                        Friend friendNewStatus = new Friend(friendName, xmppAddress, newPresence);

                        FriendStates oldState = getFriendState(friendOldStatus);
                        FriendStates newState = getFriendState(friendNewStatus);

                        friendOldStatus = null;
                        friendNewStatus = null;

                        StatusNotification.Status statusNotificationStatus;

                        if (enabled) {
                            if (oldState.isOffline() && !newState.isOffline()) {
                                LOGI("1212", "friend has went online");
                                statusNotificationStatus = StatusNotification.Status.ONLINE;
                            } else if (!oldState.isOffline() && newState.isOffline()) {
                                LOGI("1212", "friend has went offline");
                                statusNotificationStatus = StatusNotification.Status.OFFLINE;
                            } else if (!oldState.isPlaying() && newState.isPlaying()) {
                                LOGI("1212", "friend started a game");
                                statusNotificationStatus = StatusNotification.Status.STARTED_GAME;
                            } else if (oldState.isPlaying() && !newState.isPlaying()) {
                                LOGI("1212", "friend left a game");
                                statusNotificationStatus = StatusNotification.Status.LEFT_GAME;
                            } else {
                                LOGI("1212", "friend in the last position");
                                statusNotificationStatus = StatusNotification.Status.OFFLINE;
                            }
                            StatusNotification sn = statusNotificationProvider.get();
                            sn.init(xmppAddress, friendName, statusNotificationStatus);
                            sn.sendStatusNotification();
                        }
                        updateFriend(newPresence);
                    }
                });
    }

    public void setEnabled(boolean state){
        this.enabled = state;
    }


    public enum FriendStates {
        OFFLINE,
        PLAYINNG,
        IDLE;

        public boolean isOffline(){
            return this.equals(FriendStates.OFFLINE);
        }

        public boolean isPlaying(){
            return this.equals(FriendStates.PLAYINNG);
        }

        public boolean isIdle(){
            return this.equals(FriendStates.IDLE);
        }
    }

}

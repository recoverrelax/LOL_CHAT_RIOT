package com.recoverrelax.pt.riotxmppchat.Network.Manager;

import android.content.Context;

import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.EventHandling.FriendList.OnFriendPresenceChangedEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.FriendListFragment;
import com.squareup.otto.Bus;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;

import java.util.Collection;
import java.util.List;

import LolChatRiotDb.MessageDb;
import LolChatRiotDb.MessageDbDao;
import de.greenrobot.dao.query.QueryBuilder;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func2;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;

public class RiotRosterManager implements RosterListener{

    private AbstractXMPPConnection connection;
    private Roster roster;
    private Context context;
    private FriendStatusTracker friendStatusTracker;

    private Bus busInstance;

    public RiotRosterManager(Context context, AbstractXMPPConnection connection) {
        this.context = context;
        this.connection = connection;
        this.busInstance = MainApplication.getInstance().getBusInstance();
    }

    public void addRosterListener() {
        if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
            this.roster = Roster.getInstanceFor(connection);
            this.roster.addRosterListener(this);
            this.friendStatusTracker = new FriendStatusTracker(context);
        }
    }

    public void removeRosterListener(RosterListener rosterListener) {
        if (roster != null && rosterListener != null) {
            roster.removeRosterListener(rosterListener);
        }
    }

    public FriendStatusTracker getFriendStatusTracker() {
        return friendStatusTracker;
    }

    @Override public void entriesAdded(Collection<String> addresses) { }
    @Override public void entriesUpdated(Collection<String> addresses) { }
    @Override public void entriesDeleted(Collection<String> addresses) { }

    public boolean isValidRoster(){
        return this.roster != null && this.roster.isLoaded();
    }

    public Roster getRoster(){
        if(!isValidRoster())
            this.roster = Roster.getInstanceFor(this.connection);
        return this.roster;
    }

    public Collection<RosterEntry> getRosterEntries2() {
        return getRoster().getEntries();
    }

    public Observable<RosterEntry> getRosterEntries(){
        return Observable.from(roster.getEntries());
    }

    public Observable<Friend> getFriendFromRosterEntry(RosterEntry entry){
        return getRosterPresence(entry.getUser())
                .flatMap(rosterPresence -> {
                    String finalUserXmppAddress = AppXmppUtils.parseXmppAddress(entry.getUser());
                    return Observable.just(new Friend(entry.getName(), finalUserXmppAddress, rosterPresence));
                });
    }

    public Observable<MessageDb> getFriendLastMessage(Observable<Friend> friend){
        return friend.flatMap(friend1 -> getLastMessage(friend1.getUserXmppAddress()));
    }

    public Observable<MessageDb> getLastMessage(String friendUser){

        return MainApplication.getInstance().getRiotXmppService().getConnectedUser()
                .flatMap(connectedUser -> {
                    List<MessageDb> list = RiotXmppDBRepository.getMessageDao().queryBuilder()
                            .where(MessageDbDao.Properties.UserXmppId.eq(connectedUser),
                                    MessageDbDao.Properties.FromTo.eq(friendUser))
                            .orderDesc(MessageDbDao.Properties.Id)
                            .limit(1).build().list();

                    return list.size() == 0
                            ? Observable.just(null)
                            : Observable.just(list.get(0));
                });
    }

    public Observable<List<MessageDb>> getLastXMessages(int x, String userToGetMessagesFrom){
        return MainApplication.getInstance().getRiotXmppService().getConnectedUser()
                .flatMap(connectedUser -> {
                    QueryBuilder qb = RiotXmppDBRepository.getMessageDao().queryBuilder();
                    qb.where(MessageDbDao.Properties.UserXmppId.eq(connectedUser),
                            MessageDbDao.Properties.FromTo.eq(userToGetMessagesFrom))
                            .orderDesc(MessageDbDao.Properties.Id)
                            .limit(x).build();
                    QueryBuilder.LOG_SQL = true;
                    List<MessageDb> messageList = qb.list();
                    return Observable.just(messageList);
                });
    }

    public Observable<Friend> getFriendFromXmppAddress(String userXmppAddress){
//        return Observable.just(getRiotRosterManager())
//                .flatMap(rosterManager -> {
//                    RosterEntry entry = rosterManager.getRosterEntry2(userXmppAddress);
//                    Presence presence = rosterManager.getRosterPresence2(entry.getUser());
//                    String finalUserXmppAddress = AppXmppUtils.parseXmppAddress(entry.getUser());
//
//                    return Observable.just(new Friend(entry.getName(), finalUserXmppAddress, presence));
//                });

//        getRosterEntry(userXmppAddress)
//                .flatMap(rosterEntry -> {
//
//                })

        return Observable.zip(
                getRosterEntry(userXmppAddress).flatMap(rosterEntry -> getRosterPresence(rosterEntry.getUser()))
                ,
                getRosterEntry(userXmppAddress)
                , new Func2<Presence, RosterEntry, Friend>() {
                    @Override
                    public Friend call(Presence presence, RosterEntry rosterEntry) {
                        String finalUserXmppAddress = AppXmppUtils.parseXmppAddress(rosterEntry.getUser());
                        return new Friend(rosterEntry.getName(), finalUserXmppAddress, presence);
                    }
                }).flatMap(Observable::just);
    }

    public RosterEntry getRosterEntry2(String user) {
        return getRoster().getEntry(user);
    }

    public Observable<RosterEntry> getRosterEntry(String user) {
        return Observable.just(roster.getEntry(user));
    }

    public Presence getRosterPresence2(String xmppAddress) {
        return getRoster().getPresence(xmppAddress);
    }

    public Observable<Presence> getRosterPresence(String xmppAddress) {
        return Observable.just(roster.getPresence(xmppAddress));
    }

    @Override
    public void presenceChanged(Presence presence) {

        if(!MainApplication.getInstance().getConnection().isConnected())
            return;

        String xmppAddress = AppXmppUtils.parseXmppAddress(presence.getFrom());

        if(friendStatusTracker != null){
            getFriendStatusTracker().checkForFriendNotificationToSend(xmppAddress, presence);
        }
        /** {@link FriendListFragment#OnFriendPresenceChanged(OnFriendPresenceChangedEvent)} */
        busInstance.post(new OnFriendPresenceChangedEvent(presence));

        Presence bestPresence = getRosterPresence2(presence.getFrom());
        String name = getRosterEntry2(xmppAddress).getName();

        Friend friend = new Friend(name, xmppAddress, bestPresence);
        getFriendStatusTracker().updateFriend(friend);
    }

    public Friend getFriendFromPresence(Presence presence){
        RosterEntry rosterEntry = getRosterEntry2(presence.getFrom());
        Presence bestPresence = getRosterPresence2(presence.getFrom());
        String user = AppXmppUtils.parseXmppAddress(rosterEntry.getUser());

        return new Friend(rosterEntry.getName(), user, bestPresence);
    }
}

package com.recoverrelax.pt.riotxmppchat.Network.Manager;

import android.content.Context;

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
import java.util.List;

import LolChatRiotDb.MessageDb;
import LolChatRiotDb.MessageDbDao;
import de.greenrobot.dao.query.QueryBuilder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.LogUtils.LOGI;

public class RiotRosterManager implements RosterListener {

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
            this.friendStatusTracker = new FriendStatusTracker();
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
                    List<MessageDb> messageList = qb.list();
                    return messageList;
                });
    }

    public Observable<String> getFriendNameFromXmppAddress(String friendXmppAddress) {
        return MainApplication.getInstance().getRiotXmppService().getRiotRosterManager().getRosterEntry(friendXmppAddress)
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
        if (friendStatusTracker != null) {
            getFriendStatusTracker().checkForFriendNotificationToSend(presence);
        }

        /** {@link FriendListFragment#OnFriendPresenceChanged(OnFriendPresenceChangedEvent)} */
        busInstance.post(new OnFriendPresenceChangedEvent(presence));
    }

}

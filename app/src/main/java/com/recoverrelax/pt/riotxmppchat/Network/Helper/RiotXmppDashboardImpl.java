package com.recoverrelax.pt.riotxmppchat.Network.Helper;

import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.Network.RiotXmppService;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.Presence;

import java.util.List;

import LolChatRiotDb.InAppLogDb;
import LolChatRiotDb.InAppLogDbDao;
import de.greenrobot.dao.query.QueryBuilder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RiotXmppDashboardImpl {

    private AbstractXMPPConnection connection;
    private RiotXmppService riotXmppService;
    private RiotXmppDBRepository riotXmppDBRepository;

    private Integer messageCount;

    public RiotXmppDashboardImpl() {
        this.connection = MainApplication.getInstance().getConnection();
        this.riotXmppService = MainApplication.getInstance().getRiotXmppService();
        this.riotXmppDBRepository = new RiotXmppDBRepository();
    }

    public Observable<String> getUnreadedMessagesCount() {
        if (messageCount == null)
            messageCount = 0;
        else
            messageCount = messageCount++;

        return Observable.zip(Observable.just(messageCount), new RiotXmppDBRepository().getUnreadedMessages(),
                (integer, integer2) -> {
                    int max = Math.max(integer, integer2);
                    return String.valueOf(max);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<FriendStatusInfo> getFriendStatusInfo() {
        final FriendStatusInfo friendStatusInfo = new FriendStatusInfo();

        return Observable.from(riotXmppService.getRiotRosterManager().getRosterEntries2()) // Observable<Collection<RosterEntry>>
                .flatMap(rosterEntry -> {
                    Presence rosterPresence = riotXmppService.getRiotRosterManager().getRosterPresence2(rosterEntry.getUser());
                    String userXmppAddress = AppXmppUtils.parseXmppAddress(rosterEntry.getUser());

                    Friend f = new Friend(rosterEntry.getName(), userXmppAddress, rosterPresence);
                    return Observable.just(f);
                })
                .doOnNext(friend -> {
                    if (friend.isPlaying())
                        friendStatusInfo.addFriendPlaying();
                    if (friend.isOnline())
                        friendStatusInfo.addFriendOnline();
                    if (friend.isOffline())
                        friendStatusInfo.addFriendOffline();
                })
                .toList()
                .flatMap(friends -> Observable.just(friendStatusInfo))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<InAppLogDb> getLogSingleItem() {
        return riotXmppService.getConnectedUser()
                .flatMap(connectedUser -> {
                    QueryBuilder qb = RiotXmppDBRepository.getInAppLogDbDao().queryBuilder();
                    qb.where(InAppLogDbDao.Properties.UserXmppId.eq(connectedUser))
                            .orderDesc(InAppLogDbDao.Properties.Id)
                            .limit(1).build();
                    QueryBuilder.LOG_SQL = true;

                    List<InAppLogDb> logList = qb.list();

                    if(logList.size() == 0)
                        return Observable.just(null);
                    else
                        return Observable.just(logList.get(0));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<InAppLogDb>> getLogLast20List() {
        return riotXmppService.getConnectedUser()
                .flatMap(connectedUser -> new RiotXmppDBRepository().getLast20List(connectedUser))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public class FriendStatusInfo {
        int friendsPlaying;
        int friendsOnline;
        int friendsOffline;

        public FriendStatusInfo() {
            friendsOffline = 0;
            friendsOnline = 0;
            friendsPlaying = 0;
        }

        public void addFriendOnline() {
            friendsOnline++;
        }

        public void addFriendOffline() {
            friendsOffline++;
        }

        public void addFriendPlaying() {
            friendsPlaying++;
        }

        public int getFriendsPlaying() {
            return friendsPlaying;
        }

        public int getFriendsOnline() {
            return friendsOnline;
        }

        public int getFriendsOffline() {
            return friendsOffline;
        }
    }

}

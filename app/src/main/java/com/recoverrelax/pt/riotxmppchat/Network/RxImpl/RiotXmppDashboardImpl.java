package com.recoverrelax.pt.riotxmppchat.Network.RxImpl;

import com.recoverrelax.pt.riotxmppchat.Network.Manager.RiotRosterManager;
import com.recoverrelax.pt.riotxmppchat.Storage.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.Network.RiotXmppService;

import java.util.List;

import javax.inject.Inject;

import LolChatRiotDb.InAppLogDb;
import LolChatRiotDb.InAppLogDbDao;
import de.greenrobot.dao.query.QueryBuilder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RiotXmppDashboardImpl {

    private RiotXmppService riotXmppService = MainApplication.getInstance().getRiotXmppService();

    private RiotXmppDBRepository riotXmppDBRepository;
    private RiotRosterManager riotRosterManager;

    @Inject
    public RiotXmppDashboardImpl(RiotXmppDBRepository riotXmppDBRepository, RiotRosterManager riotRosterManager) {

        this.riotXmppDBRepository = riotXmppDBRepository;
        this.riotRosterManager = riotRosterManager;
    }

    public Observable<String> getUnreadedMessagesCount() {
        return riotXmppDBRepository.getUnreadedMessages()
                .map(String::valueOf)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<FriendStatusInfo> getFriendStatusInfo() {
        final FriendStatusInfo friendStatusInfo = new FriendStatusInfo();

            return riotRosterManager.getRosterEntries()
                    .flatMap(rosterEntry -> riotRosterManager.getFriendFromRosterEntry(rosterEntry))
                   .doOnNext(friend -> {
                       if (friend.isPlaying())
                           friendStatusInfo.addFriendPlaying();
                       if (friend.isOnline())
                           friendStatusInfo.addFriendOnline();
                       if (friend.isOffline())
                           friendStatusInfo.addFriendOffline();
                   })
                    .toList()
                    .map(friends -> friendStatusInfo)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());


    }

    public Observable<InAppLogDb> getLogSingleItem() {
        return riotXmppService.getRiotConnectionManager().getConnectedUser()
                .map(connectedUser -> {
                    QueryBuilder qb = RiotXmppDBRepository.getInAppLogDbDao().queryBuilder();
                    qb.where(InAppLogDbDao.Properties.UserXmppId.eq(connectedUser))
                            .orderDesc(InAppLogDbDao.Properties.Id)
                            .limit(1).build();
                    List<InAppLogDb> logList = qb.list();

                    if (logList.size() == 0)
                        return null;
                    else
                        return logList.get(0);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<InAppLogDb>> getLogLast20List() {
        return riotXmppService.getRiotConnectionManager().getConnectedUser()
                .flatMap(riotXmppDBRepository::getLast20List)
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

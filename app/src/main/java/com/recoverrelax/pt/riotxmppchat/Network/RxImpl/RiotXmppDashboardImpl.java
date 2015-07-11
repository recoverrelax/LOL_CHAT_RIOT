package com.recoverrelax.pt.riotxmppchat.Network.RxImpl;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.Network.RiotXmppService;
import com.recoverrelax.pt.riotxmppchat.Storage.RiotXmppDBRepository;

import java.util.List;

import LolChatRiotDb.InAppLogDb;
import LolChatRiotDb.InAppLogDbDao;
import de.greenrobot.dao.query.QueryBuilder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RiotXmppDashboardImpl {

    private RiotXmppService riotXmppService = MainApplication.getInstance().getRiotXmppService();
    RiotXmppDBRepository riotXmppDBRepository;

    public RiotXmppDashboardImpl(RiotXmppDBRepository repository) {
        this.riotXmppDBRepository = repository;
    }

    /**
     * @return the number of unreaded messages for the connected user
     */
    public Observable<String> getUnreadedMessagesCount() {
        return riotXmppDBRepository.getUnreadedMessages()
                .map(String::valueOf)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * get roster entries FROM
     * get friend for each entry
     * for each friend, update the friendStatusObject
     * converts back to a list
     * ingores this list and retuns the created friendStatusInfo
     * @return
     */
    public Observable<FriendStatusInfo> getFriendStatusInfo() {
        final FriendStatusInfo friendStatusInfo = new FriendStatusInfo();

            return riotXmppService.getRiotRosterManager().getRosterEntries()
                   .flatMap(rosterEntry -> riotXmppService.getRiotRosterManager().getFriendFromRosterEntry(rosterEntry))
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

    /**
     * get the connected user
     * attemps to get the last log for that user
     * @return the last log for that user
     */
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

    /**
     * @return Get the last 20 log messages for the connected user
     */
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

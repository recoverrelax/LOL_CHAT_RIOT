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
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class RiotXmppDashboardImpl {

    private RiotXmppService riotXmppService;
    private RiotXmppDBRepository riotXmppDBRepository;

    public RiotXmppDashboardImpl() {
        this.riotXmppService = MainApplication.getInstance().getRiotXmppService();
        this.riotXmppDBRepository = new RiotXmppDBRepository();
    }

    public Observable<String> getUnreadedMessagesCount() {
        return riotXmppDBRepository.getUnreadedMessages()
                .map(String::valueOf)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

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
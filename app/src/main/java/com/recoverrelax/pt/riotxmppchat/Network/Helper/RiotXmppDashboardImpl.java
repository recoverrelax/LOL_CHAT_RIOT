package com.recoverrelax.pt.riotxmppchat.Network.Helper;

import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.Network.RiotXmppService;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.FriendListChat;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.Presence;

import java.util.Collections;
import java.util.List;

import LolChatRiotDb.InAppLogDb;
import LolChatRiotDb.InAppLogDbDao;
import LolChatRiotDb.MessageDb;
import de.greenrobot.dao.query.QueryBuilder;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;

public class RiotXmppDashboardImpl {

    private RiotXmppDashboardImplCallbacks callback;
    private AbstractXMPPConnection connection;
    private RiotXmppService riotXmppService;
    private RiotXmppDBRepository riotXmppDBRepository;
    private GlobalImpl globalImpl;

    private Integer messageCount;

    public RiotXmppDashboardImpl(RiotXmppDashboardImplCallbacks callback) {
        this.callback = callback;
        this.connection = MainApplication.getInstance().getRiotXmppService().getConnection();
        this.riotXmppService = MainApplication.getInstance().getRiotXmppService();
        this.riotXmppDBRepository = new RiotXmppDBRepository();
        this.globalImpl = new GlobalImpl(connection);
    }

    public void getUnreadedMessagesCount() {
        if (messageCount == null)
            messageCount = 0;
        else
            messageCount = messageCount++;

        Observable.zip(Observable.just(messageCount), globalImpl.getUnreadedMessages(),
                (integer, integer2) -> {
                    int max = Math.max(integer, integer2);
                    return String.valueOf(max);
                })
                .observeOn(Schedulers.newThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (callback != null)
                            callback.onUnreadedMessageCountFailedReception();
                    }

                    @Override
                    public void onNext(String s) {
                        if (callback != null) {
                            callback.onUnreadedMessageCountRetrieved(s);
                        }
                    }
                });
    }

    public void getFriendStatusInfo() {
        final FriendStatusInfo friendStatusInfo = new FriendStatusInfo();

        Observable.from(riotXmppService.getRiotRosterManager().getRosterEntries()) // Observable<Collection<RosterEntry>>
                .flatMap(rosterEntry -> {
                    Presence rosterPresence = riotXmppService.getRiotRosterManager().getRosterPresence(rosterEntry.getUser());
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
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FriendStatusInfo>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (callback != null)
                            callback.onFriendsStatusInfoFailedReception();
                    }

                    @Override
                    public void onNext(FriendStatusInfo friendStatusInfo) {
                        if (callback != null) {
                            callback.onFriendsStatusInfoRetrieved(friendStatusInfo);
                        }
                    }
                });
    }

    public void getLogSingleItem() {
        MainApplication.getInstance().getConnectedUser()
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
                }).observeOn(Schedulers.newThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<InAppLogDb>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (callback != null)
                            callback.onLogSingleItemFailedReception();
                    }

                    @Override
                    public void onNext(InAppLogDb inAppLogDb) {
                        if (callback != null)
                            callback.onLogSingleItemReceived(inAppLogDb);
                    }
                });
    }

    public void getLogLast20List() {
        LOGI("AAA", "aaaaaaaaaaaaaa");
        MainApplication.getInstance().getConnectedUser()
                .flatMap(connectedUser -> {
                    QueryBuilder qb = RiotXmppDBRepository.getInAppLogDbDao().queryBuilder();
                    qb.where(InAppLogDbDao.Properties.UserXmppId.eq(connectedUser))
                            .orderDesc(InAppLogDbDao.Properties.Id)
                            .limit(20).build();
                    QueryBuilder.LOG_SQL = true;

                    List<InAppLogDb> logList = qb.list();

                    if(logList.size() == 0)
                        return Observable.just(null);
                    else
                        return Observable.just(logList);
                })
                .observeOn(Schedulers.newThread())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<InAppLogDb>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (callback != null)
                            callback.onLogListFailedReception();
                    }

                    @Override
                    public void onNext(List<InAppLogDb> inAppLogDbs) {
                        if (callback != null)
                            callback.onLogListReceived(inAppLogDbs);
                    }
                });
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


    public interface RiotXmppDashboardImplCallbacks {
        void onUnreadedMessageCountRetrieved(String messageCount);
        void onUnreadedMessageCountFailedReception();
        void onFriendsStatusInfoRetrieved(FriendStatusInfo friendStatusInfo);
        void onFriendsStatusInfoFailedReception();

        void onLogListReceived(List<InAppLogDb> logList);
        void onLogListFailedReception();

        void onLogSingleItemReceived(InAppLogDb log);
        void onLogSingleItemFailedReception();
    }
}

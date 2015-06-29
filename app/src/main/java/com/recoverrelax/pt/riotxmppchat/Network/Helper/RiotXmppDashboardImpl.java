package com.recoverrelax.pt.riotxmppchat.Network.Helper;

import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.Network.RiotXmppService;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.Presence;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;

public class RiotXmppDashboardImpl implements RiotXmppDashboardHelper {

    private RiotXmppDashboardImplCallbacks callback;
    private AbstractXMPPConnection connection;
    private RiotXmppService riotXmppService;
    private RiotXmppDBRepository riotXmppDBRepository;

    private Integer messageCount;

    public RiotXmppDashboardImpl(RiotXmppDashboardImplCallbacks callback) {
        this.callback = callback;
        this.connection = MainApplication.getInstance().getRiotXmppService().getConnection();
        this.riotXmppService = MainApplication.getInstance().getRiotXmppService();
        this.riotXmppDBRepository = new RiotXmppDBRepository();
    }

    @Override
    public void getUnreadedMessagesCount() {
        if (messageCount == null)
            messageCount = 0;
        else
            messageCount = messageCount++;

        Observable.zip(Observable.just(messageCount), riotXmppDBRepository.getUnreadedMessages(),
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

    @Override
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
    }
}

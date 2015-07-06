package com.recoverrelax.pt.riotxmppchat.Network.Manager;

import android.content.Context;
import android.support.v4.util.Pair;

import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.OnNewFriendPlayingEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.OnNewMessageReceivedEventEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.NotificationCenter;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;

import org.jivesoftware.smack.packet.Presence;

import java.util.HashMap;
import java.util.Map;

import LolChatRiotDb.NotificationDb;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;

public class FriendStatusTracker {

    private Map<String, FriendStates> friendList;
    private Context context;

    private boolean enabled = false;

    public FriendStatusTracker(Context context){
        this.context = context;
        this.friendList = new HashMap<>();
    }

    public void updateFriend(Friend friend){
        FriendStates state = getFriendState(friend);
        friendList.put(friend.getName(), state);
    }

    public void clear(){
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

    public void checkForFriendNotificationToSend(String xmppAddress, Presence newPresence) {

        Observable.zip
                (
                        new RiotXmppDBRepository().getNotificationByUser(xmppAddress),
                        MainApplication.getInstance().getRiotXmppService().getRiotRosterManager().getRosterEntry(xmppAddress)
                                .flatMap(rosterEntry -> Observable.just(rosterEntry.getName())),
                        (notificationDb, friendName) -> {
                            NotificationCenter notificationCenter = new NotificationCenter(xmppAddress, friendName, notificationDb);
                            return new Pair<>(friendName, notificationCenter);
                        }
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Pair<String, NotificationCenter>>() {
                    @Override public void onCompleted() { }
                    @Override public void onError(Throwable e) { }

                    @Override
                    public void onNext(Pair<String, NotificationCenter> stringNotificationCenterPair) {
                        String friendName = stringNotificationCenterPair.first;
                        NotificationCenter notificationCenter = stringNotificationCenterPair.second;

                        FriendStates oldState = friendList.containsKey(friendName) ? friendList.get(friendName) : FriendStates.OFFLINE;
                        FriendStates newState = getFriendState(new Friend(friendName, xmppAddress, newPresence));

                        if (enabled) {
                            if (oldState.isOffline() && !newState.isOffline()) {
                                notificationCenter.sendOnlineOfflineNotification(NotificationCenter.OnlineOffline.ONLINE);
                            }else if (!oldState.isOffline() && newState.isOffline())
                                notificationCenter.sendOnlineOfflineNotification(NotificationCenter.OnlineOffline.OFFLINE);
                            else if(!oldState.isPlaying() && newState.isPlaying()) {
                                notificationCenter.sendStartedEndedGameNotification(NotificationCenter.PlayingIddle.STARTED_GAME);
                                MainApplication.getInstance().getBusInstance().post(new OnNewFriendPlayingEvent());
                            }
                            else if(oldState.isPlaying() && !newState.isPlaying()) {
                                notificationCenter.sendStartedEndedGameNotification(NotificationCenter.PlayingIddle.ENDED_GAME);
                                MainApplication.getInstance().getBusInstance().post(new OnNewFriendPlayingEvent());
                            }
                        }

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

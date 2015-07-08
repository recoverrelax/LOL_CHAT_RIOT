package com.recoverrelax.pt.riotxmppchat.Network.Manager;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.NotificationCenter.StatusNotification;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
import org.jivesoftware.smack.packet.Presence;
import java.util.HashMap;
import java.util.Map;
import rx.Subscriber;

public class FriendStatusTracker {

    private Map<String, Presence> friendList; // friendXmppAddress, Presence

    private boolean enabled = false;

    public FriendStatusTracker(){
        this.friendList = new HashMap<>();
    }

    public void updateFriend(Presence presence){
        String from = presence.getFrom();
        String friendXmppAddress = AppXmppUtils.parseXmppAddress(from);

        friendList.put(friendXmppAddress, presence);
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

    public void checkForFriendNotificationToSend(Presence newPresence) {
        String xmppAddress = AppXmppUtils.parseXmppAddress(newPresence.getFrom());

        MainApplication.getInstance().getRiotXmppService().getRiotRosterManager().getFriendNameFromXmppAddress(xmppAddress)
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

                        StatusNotification.Status statusNotification;

                        if (enabled) {
                            if (oldState.isOffline() && !newState.isOffline()) {
                                statusNotification = StatusNotification.Status.ONLINE;
                            } else if (!oldState.isOffline() && newState.isOffline()) {
                                statusNotification = StatusNotification.Status.OFFLINE;
                            } else if (!oldState.isPlaying() && newState.isPlaying()) {
                                statusNotification = StatusNotification.Status.STARTED_GAME;
                            } else if (oldState.isPlaying() && !newState.isPlaying()) {
                                statusNotification = StatusNotification.Status.LEFT_GAME;
                            } else {
                                statusNotification = StatusNotification.Status.OFFLINE;
                            }
                            new StatusNotification(xmppAddress, friendName, statusNotification).sendStatusNotification();
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

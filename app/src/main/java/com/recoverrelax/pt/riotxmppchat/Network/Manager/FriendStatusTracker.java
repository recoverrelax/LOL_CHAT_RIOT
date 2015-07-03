package com.recoverrelax.pt.riotxmppchat.Network.Manager;

import android.content.Context;

import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.OnNewFriendPlayingEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.NotificationCenter;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;

import org.jivesoftware.smack.packet.Presence;

import java.util.HashMap;
import java.util.Map;

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
        String friendName = MainApplication.getInstance().getRiotXmppService().getRiotRosterManager().getRosterEntry2(xmppAddress).getName();

        FriendStates oldState = friendList.containsKey(friendName) ? friendList.get(friendName) : FriendStates.OFFLINE;
        FriendStates newState = getFriendState(new Friend(friendName, xmppAddress, newPresence));

        if (enabled) {
            if (oldState.isOffline() && !newState.isOffline()) {
                new NotificationCenter(xmppAddress).sendOnlineOfflineNotification(NotificationCenter.OnlineOffline.ONLINE);
            }else if (!oldState.isOffline() && newState.isOffline())
                    new NotificationCenter(xmppAddress).sendOnlineOfflineNotification(NotificationCenter.OnlineOffline.OFFLINE);
            else if(!oldState.isPlaying() && newState.isPlaying()) {
                new NotificationCenter(xmppAddress).sendStartedEndedGameNotification(NotificationCenter.PlayingIddle.STARTED_GAME);
                MainApplication.getInstance().getBusInstance().post(new OnNewFriendPlayingEvent());
            }
            else if(oldState.isPlaying() && !newState.isPlaying()) {
                new NotificationCenter(xmppAddress).sendStartedEndedGameNotification(NotificationCenter.PlayingIddle.ENDED_GAME);
                MainApplication.getInstance().getBusInstance().post(new OnNewFriendPlayingEvent());
            }
        }
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

package com.recoverrelax.pt.riotxmppchat.Network.Manager;

import android.content.Context;
import com.recoverrelax.pt.riotxmppchat.MyUtil.NotificationCenter;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;

import org.jivesoftware.smack.packet.Presence;

import java.util.HashMap;
import java.util.Map;

public class FriendStatusTracker {

    private Map<String, Friend> friendList;
    private Context context;

    private boolean enabled = false;

    public FriendStatusTracker(Context context){
        this.context = context;
        this.friendList = new HashMap<>();
    }

    public void updateFriend(Friend friend){
        friendList.put(friend.getName(), friend);
    }

    public void checkForFriendNotificationToSend(String friendName, String xmppAddress, Presence newPresence) {
        Friend oldFriend = friendList.containsKey(friendName) ? friendList.get(friendName) : null;
        Friend newFriend = new Friend(friendName, xmppAddress, newPresence);

        if(enabled) {
            String message = null;
            String speechMessage = null;

                if(checkForFriendOnlineStatus(oldFriend, newFriend)){
                    message = friendName + " is now Online...";
                    speechMessage = friendName + " has went online";
                } else if(checkForLeftGameStatus(oldFriend, newFriend)){
                    message = friendName + " has left a game";
                    speechMessage = message;
                } else if(checkForFriendOfflineStatus(oldFriend, newFriend)){
                    message = friendName + " has went offline";
                    speechMessage = message;
                }

            if(message != null && speechMessage != null)
                new NotificationCenter(context).sendStatusGameNotification(friendName, message, speechMessage);
            }
    }

    public boolean checkForFriendOnlineStatus(Friend oldFriend, Friend newFriend){
        return newFriend.isOnline() &&
                (oldFriend == null ||
                        (oldFriend.getGameStatus().isOutOfGame())
                                && oldFriend.isOffline());
    }

    public boolean checkForFriendOfflineStatus(Friend oldFriend, Friend newFriend){
        return !newFriend.isOnline() &&
                oldFriend.isOnline();
    }

    public boolean checkForLeftGameStatus(Friend oldFriend, Friend newFriend){
        return newFriend.isOnline() && !newFriend.getGameStatus().isPlaying()
                    && oldFriend.isOnline() && oldFriend.isPlaying();
    }

    public void setEnabled(boolean state){
        this.enabled = state;
    }
}

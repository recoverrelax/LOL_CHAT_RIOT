package com.recoverrelax.pt.riotxmppchat.Network.Manager;

import android.content.Context;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.NotificationCenter;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;

import org.jivesoftware.smack.packet.Presence;

import java.util.HashMap;
import java.util.Map;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;

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

    public void checkForFriendNotificationToSend(String xmppAddress, Presence newPresence) {
        String friendName = MainApplication.getInstance().getRiotXmppService().getRiotRosterManager().getRosterEntry(xmppAddress).getName();

        Friend oldFriend = friendList.containsKey(friendName) ? friendList.get(friendName) : null;
        Friend newFriend = new Friend(friendName, xmppAddress, newPresence);

        if (enabled) {
            if (checkForFriendOnlineStatus(oldFriend, newFriend)) {
                LOGI("1212", "checkForFriendOnlineStatus");
                new NotificationCenter(newFriend.getUserXmppAddress()).sendOnlineOfflineNotification(NotificationCenter.OnlineOffline.ONLINE);
            }else if (checkForFriendOfflineStatus(oldFriend, newFriend))
                new NotificationCenter(newFriend.getUserXmppAddress()).sendOnlineOfflineNotification(NotificationCenter.OnlineOffline.OFFLINE);
        }
    }

    public boolean checkForFriendOnlineStatus(Friend oldFriend, Friend newFriend){
        return newFriend.isOnline() &&
                (oldFriend == null || oldFriend.isOffline());
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

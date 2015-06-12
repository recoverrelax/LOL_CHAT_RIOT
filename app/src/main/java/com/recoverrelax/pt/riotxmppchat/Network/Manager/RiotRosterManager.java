package com.recoverrelax.pt.riotxmppchat.Network.Manager;

import android.content.Context;
import android.util.Log;

import com.recoverrelax.pt.riotxmppchat.EventHandling.FriendList.OnFriendPresenceChangedEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.FriendLeftGameNotification;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.SystemNotification;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
import com.recoverrelax.pt.riotxmppchat.ui.activity.FriendListActivity;
import com.recoverrelax.pt.riotxmppchat.ui.activity.FriendMessageListActivity;
import com.recoverrelax.pt.riotxmppchat.ui.activity.PersonalMessageActivity;
import com.recoverrelax.pt.riotxmppchat.ui.activity.SettingActivity;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.FriendListFragment;
import com.squareup.otto.Bus;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class RiotRosterManager implements RosterListener{

    private AbstractXMPPConnection connection;
    private Roster roster;
    private Set<String> friendsPlaying;
    private Context context;

    private Bus busInstance;

    public RiotRosterManager(Context context, AbstractXMPPConnection connection) {
        this.context = context;
        this.connection = connection;
        this.friendsPlaying = new HashSet<>();

        this.busInstance = MainApplication.getInstance().getBusInstance();
    }

    public void addRosterListener() {
        if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
            this.roster = Roster.getInstanceFor(connection);
            this.roster.addRosterListener(this);
        }
    }

    public void removeRosterListener(RosterListener rosterListener) {
        if (roster != null && rosterListener != null) {
            roster.removeRosterListener(rosterListener);
        }
    }

    @Override public void entriesAdded(Collection<String> addresses) { }
    @Override public void entriesUpdated(Collection<String> addresses) { }
    @Override public void entriesDeleted(Collection<String> addresses) { }

    public boolean isValidRoster(){
        return this.roster != null && this.roster.isLoaded();
    }

    public Roster getRoster(){
        if(!isValidRoster())
            this.roster = Roster.getInstanceFor(this.connection);
        return this.roster;
    }

    public Collection<RosterEntry> getRosterEntries() {
        return getRoster().getEntries();
    }

    public RosterEntry getRosterEntry(String user) {
        return getRoster().getEntry(user);
    }

    public Presence getRosterPresence(String xmppAddress) {
        return getRoster().getPresence(xmppAddress);
    }

    @Override
    public void presenceChanged(Presence presence) {
        /** {@link FriendListFragment#OnFriendPresenceChanged(OnFriendPresenceChangedEvent)} */
        busInstance.post(new OnFriendPresenceChangedEvent(presence));

        RosterEntry rosterEntry = getRosterEntry(presence.getFrom());
        Presence bestPresence = getRosterPresence(presence.getFrom());
        String user = AppXmppUtils.parseXmppAddress(rosterEntry.getUser());

        Friend friend = new Friend(rosterEntry.getName(), user, bestPresence);

        if (friend.isPlaying())
            addFriendPlaying(friend.getName(), friend.getUserXmppAddress());
        else
            removeFriendPlaying(friend.getName(), friend.getUserXmppAddress());
    }

    public void addFriendPlaying(String friendName, String userXmppAddress) {
        friendsPlaying.add(friendName);
        Log.i("TAGF", "Added: " + friendName + " to friendsPlaying!");
    }

    public void removeFriendPlaying(String friendName, String userXmppAddress) {
        boolean removed = friendsPlaying.remove(friendName);
        if (removed) {
            Log.i("ASAS", "REMOVED FRIEND: " + friendName);
            if (MainApplication.getInstance().isApplicationClosed()) {
                new SystemNotification(context, friendName, "... just left a game!");
            } else {

                /**
                 * 1st: {@link FriendListActivity#OnFriendLeftGame(FriendLeftGameNotification)}
                 * 2nd: {@link PersonalMessageActivity#OnFriendLeftGame(FriendLeftGameNotification)}
                 * 3rd: {@link FriendMessageListActivity#OnFriendLeftGame(FriendLeftGameNotification)}
                 * 4th: {@link SettingActivity#OnFriendLeftGame(FriendLeftGameNotification)}
                 */
                MainApplication.getInstance().getBusInstance().post(new FriendLeftGameNotification(friendName + " ... just left a game!", friendName, userXmppAddress));
            }
        }
    }
}

package com.recoverrelax.pt.riotxmppchat.Network.Manager;

import android.content.Context;

import com.recoverrelax.pt.riotxmppchat.EventHandling.FriendList.OnFriendPresenceChangedEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
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

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;

public class RiotRosterManager implements RosterListener{

    private AbstractXMPPConnection connection;
    private Roster roster;
    private Context context;
    private FriendStatusTracker friendStatusTracker;

    private Bus busInstance;

    public RiotRosterManager(Context context, AbstractXMPPConnection connection) {
        this.context = context;
        this.connection = connection;
        this.busInstance = MainApplication.getInstance().getBusInstance();


    }

    public void addRosterListener() {
        if (connection != null && connection.isConnected() && connection.isAuthenticated()) {
            this.roster = Roster.getInstanceFor(connection);
            this.roster.addRosterListener(this);
            this.friendStatusTracker = new FriendStatusTracker(context);
        }
    }

    public void removeRosterListener(RosterListener rosterListener) {
        if (roster != null && rosterListener != null) {
            roster.removeRosterListener(rosterListener);
        }
    }

    public FriendStatusTracker getFriendStatusTracker() {
        return friendStatusTracker;
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

        if(!MainApplication.getInstance().getConnection().isConnected())
            return;

        String xmppAddress = AppXmppUtils.parseXmppAddress(presence.getFrom());

        if(friendStatusTracker != null){
            getFriendStatusTracker().checkForFriendNotificationToSend(xmppAddress, presence);
        }
        /** {@link FriendListFragment#OnFriendPresenceChanged(OnFriendPresenceChangedEvent)} */
        busInstance.post(new OnFriendPresenceChangedEvent(presence));

        Presence bestPresence = getRosterPresence(presence.getFrom());
        String name = getRosterEntry(xmppAddress).getName();

        Friend friend = new Friend(name, xmppAddress, bestPresence);
        getFriendStatusTracker().updateFriend(friend);
    }

    public Friend getFriendFromPresence(Presence presence){
        RosterEntry rosterEntry = getRosterEntry(presence.getFrom());
        Presence bestPresence = getRosterPresence(presence.getFrom());
        String user = AppXmppUtils.parseXmppAddress(rosterEntry.getUser());

        return new Friend(rosterEntry.getName(), user, bestPresence);
    }
}

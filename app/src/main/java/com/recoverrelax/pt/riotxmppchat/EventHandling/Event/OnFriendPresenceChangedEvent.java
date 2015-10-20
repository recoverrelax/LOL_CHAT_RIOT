package com.recoverrelax.pt.riotxmppchat.EventHandling.Event;

import org.jivesoftware.smack.packet.Presence;

public interface OnFriendPresenceChangedEvent extends Event {

    void onFriendPresenceChanged(Presence presence);
}

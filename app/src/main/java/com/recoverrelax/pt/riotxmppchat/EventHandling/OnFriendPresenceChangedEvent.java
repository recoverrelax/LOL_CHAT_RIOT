package com.recoverrelax.pt.riotxmppchat.EventHandling;

import org.jivesoftware.smack.packet.Presence;

public class OnFriendPresenceChangedEvent {

    private Presence presence;

    public OnFriendPresenceChangedEvent(Presence presence) {
        this.presence = presence;
    }

    public Presence getPresence() {
        return presence;
    }
}

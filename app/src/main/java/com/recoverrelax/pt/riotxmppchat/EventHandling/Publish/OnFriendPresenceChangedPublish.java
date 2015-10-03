package com.recoverrelax.pt.riotxmppchat.EventHandling.Publish;

import org.jivesoftware.smack.packet.Presence;

public class OnFriendPresenceChangedPublish {

    private Presence presence;

    public OnFriendPresenceChangedPublish(Presence presence) {
        this.presence = presence;
    }

    public Presence getPresence() {
        return presence;
    }
}

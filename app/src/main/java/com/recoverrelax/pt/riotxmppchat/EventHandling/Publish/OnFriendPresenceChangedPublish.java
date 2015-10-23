package com.recoverrelax.pt.riotxmppchat.EventHandling.Publish;

import org.jivesoftware.smack.packet.Presence;

public class OnFriendPresenceChangedPublish extends AbstractPublish {

    private Presence presence;

    public OnFriendPresenceChangedPublish(Presence presence) {
        this.presence = presence;
    }

    public Presence getPresence() {
        return presence;
    }
}

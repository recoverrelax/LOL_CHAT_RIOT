package com.recoverrelax.pt.riotxmppchat.Riot.Interface;

import org.jivesoftware.smack.AbstractXMPPConnection;

public interface RiotXmppConnectionHelper {
    void connect(AbstractXMPPConnection connection);
    void login(AbstractXMPPConnection connection);
}

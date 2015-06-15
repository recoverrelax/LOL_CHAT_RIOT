package com.recoverrelax.pt.riotxmppchat.Riot.Enum;

import android.support.annotation.ColorRes;

import com.recoverrelax.pt.riotxmppchat.R;

import org.jivesoftware.smack.packet.Presence;

public enum PresenceMode {
    AVAILABLE("Online", R.color.presence_mode_available),
    AWAY("Away", R.color.presence_mode_away),
    CHAT("Chatting", R.color.presence_mode_chat),
    DND("DN Disturb", R.color.presence_mode_dnd),
    XA("E-Away", R.color.presence_mode_xa),
    UNAVAILABLE("OFF", R.color.presence_mode_unavailable);

    private String descriptiveName;
    private @ColorRes int statusColor;

    PresenceMode(String descriptiveName, @ColorRes int statusColor){
        this.descriptiveName = descriptiveName;
        this.statusColor = statusColor;
    }

    public static PresenceMode getPresenceModeFromSmack(Presence.Mode presenceMode){
            for (PresenceMode pm : PresenceMode.values()) {
                if (pm.toString().toLowerCase().equals(presenceMode.toString()))
                    return pm;
            }
        return PresenceMode.UNAVAILABLE;
    }

    public String getDescriptiveName() {
        return descriptiveName;
    }

    public int getStatusColor() {
        return statusColor;
    }
}

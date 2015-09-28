package com.recoverrelax.pt.riotxmppchat.Riot.Enum;

import android.support.annotation.ColorRes;

import com.recoverrelax.pt.riotxmppchat.R;

import org.jivesoftware.smack.packet.Presence;

public enum PresenceMode {
    AVAILABLE("Online", R.color.presence_mode_available, R.color.presence_mode_chat),
    AWAY("Away", R.color.presence_mode_away, R.color.presence_mode_away),
    CHAT("Chatting", R.color.presence_mode_chat, R.color.presence_mode_chat),
    DND("DN Disturb", R.color.presence_mode_dnd, R.color.presence_mode_dnd),
    XA("E-Away", R.color.presence_mode_xa, R.color.presence_mode_xa),
    UNAVAILABLE("OFF", R.color.presence_mode_unavailable, R.color.presence_mode_xa);

    private String descriptiveName;
    private @ColorRes
    final int statusColor;
    final int statusColor2;

    PresenceMode(String descriptiveName, @ColorRes int statusColor, @ColorRes int statusColor2){
        this.descriptiveName = descriptiveName;
        this.statusColor = statusColor;
        this.statusColor2 = statusColor2;
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

    public int getStatusColor2() {
        return statusColor2;
    }
}

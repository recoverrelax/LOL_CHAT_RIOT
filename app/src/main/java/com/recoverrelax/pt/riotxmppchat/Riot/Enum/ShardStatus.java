package com.recoverrelax.pt.riotxmppchat.Riot.Enum;

import android.support.annotation.ColorRes;

import com.recoverrelax.pt.riotxmppchat.R;

public enum ShardStatus {
    ONLINE("Online", R.color.presence_mode_chat),
    ALERT("Alert", R.color.presence_mode_away),
    OFFLINE("Offline", R.color.presence_mode_xa),
    DEPLOYING("Deploying", R.color.presence_mode_dnd);

    private
    @ColorRes
    final int statusColor;
    private String descriptiveName;

    ShardStatus(String descriptiveName, @ColorRes int statusColor) {
        this.descriptiveName = descriptiveName;
        this.statusColor = statusColor;
    }

    public static ShardStatus getByName(String name) {
        for (ShardStatus ss : ShardStatus.values()) {
            if (ss.getDescriptiveName().toLowerCase().equals(name.toLowerCase()))
                return ss;
        }
        return ShardStatus.OFFLINE;
    }

    public String getDescriptiveName() {
        return descriptiveName;
    }

    public String getDescriptiveNameUpperCase() {
        return descriptiveName.toUpperCase();
    }

    public int getStatusColor() {
        return statusColor;
    }
}

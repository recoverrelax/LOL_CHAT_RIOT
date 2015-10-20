package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.GameConstants;

public enum PlayerRole {
    DUO(1),
    SUPPORT(2),
    CARRY(3),
    TOP(4);

    int roleId;

    PlayerRole(int roleId) {
        this.roleId = roleId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
}

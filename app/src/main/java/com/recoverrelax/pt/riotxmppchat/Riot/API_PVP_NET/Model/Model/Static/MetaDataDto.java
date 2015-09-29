package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static;

import org.parceler.Parcel;

@Parcel
public class MetaDataDto {

    private boolean isRune;
    private String tier;
    private String type;

    public MetaDataDto(){}

    public boolean isRune() {
        return isRune;
    }

    public String getTier() {
        return tier;
    }

    public String getType() {
        return type;
    }
}

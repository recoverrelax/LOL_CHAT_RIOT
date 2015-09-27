package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Status;

import org.parceler.Parcel;

@Parcel
public class Translation {

    private String content;
    private String locale;
    private String updated_at;

    public Translation(){}

    public String getContent() {
        return content;
    }

    public String getLocale() {
        return locale;
    }

    public String getUpdated_at() {
        return updated_at;
    }
}

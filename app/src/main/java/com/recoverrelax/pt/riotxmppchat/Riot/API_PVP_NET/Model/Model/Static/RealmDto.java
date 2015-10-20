package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static;

import org.parceler.Parcel;

import java.util.Map;

@Parcel
public class RealmDto {

    private String cdn;
    private String css;
    private String dd;
    private String l;
    private String lg;
    private Map<String, String> n;
    private int profileiconmax;
    private String store;
    private String v;

    public RealmDto() {
    }

    public String getCdn() {
        return cdn;
    }

    public String getCss() {
        return css;
    }

    public String getDd() {
        return dd;
    }

    public String getL() {
        return l;
    }

    public String getLg() {
        return lg;
    }

    public Map<String, String> getN() {
        return n;
    }

    public int getProfileiconmax() {
        return profileiconmax;
    }

    public String getStore() {
        return store;
    }

    public String getV() {
        return v;
    }
}

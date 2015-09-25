package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static;

import org.parceler.Parcel;

import java.util.Map;

@Parcel
public class SummonerSpellListDto {

    private Map<String, SummonerSpellDto> data;
    private String type;
    private String version;

    public SummonerSpellListDto() {}

    public Map<String, SummonerSpellDto> getData() {
        return data;
    }

    public String getType() {
        return type;
    }

    public String getVersion() {
        return version;
    }
}

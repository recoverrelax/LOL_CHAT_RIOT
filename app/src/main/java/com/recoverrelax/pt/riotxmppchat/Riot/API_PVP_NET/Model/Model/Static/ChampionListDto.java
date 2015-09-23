package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static;

import org.parceler.Parcel;

import java.util.Map;

@Parcel
public class ChampionListDto {

    public Map<String, ChampionDto> data;
    public Map<String, String> keys;
    public String type;
    public String version;
    public String format;

    public ChampionListDto(){}

    public Map<String, ChampionDto> getChampionList() {
        return data;
    }

    @Override
    public String toString() {
        return "ChampionListDto{" +
                "data=" + data +
                ", keys=" + keys +
                ", type='" + type + '\'' +
                ", version='" + version + '\'' +
                ", format='" + format + '\'' +
                '}';
    }
}

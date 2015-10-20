package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class ItemTreeDto {

    private String header;
    private List<String> tags;

    public ItemTreeDto() {
    }

    public String getHeader() {
        return header;
    }

    public List<String> getTags() {
        return tags;
    }
}

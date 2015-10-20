package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class RecommendedDto {

    public List<BlockDto> blocks;
    public String champion;
    public String map;
    public String mode;
    public boolean priority;
    public String title;
    public String type;

    public RecommendedDto() {
    }

}

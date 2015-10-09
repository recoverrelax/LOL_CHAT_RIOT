package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Champion;

import org.parceler.Parcel;

import java.util.List;

/**
 * This object contains a collection of champion information.
 *
 * /api/lol/{region}/v1.2/champion
 *
 * Retrieve all champions. (REST)
 */
@Parcel
public class Champion_ChampionListDto {

    List<Champion_ChampionDto> champions;

    public Champion_ChampionListDto() {}

    public List<Champion_ChampionDto> getChampions() {
        return champions;
    }

    public void setChampions(List<Champion_ChampionDto> champions) {
        this.champions = champions;
    }
}

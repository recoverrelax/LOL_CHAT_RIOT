package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Champion;

import org.parceler.Parcel;

/**
 * This object contains a collection of champion information.
 *
 * /api/lol/{region}/v1.2/champion
 *
 * Retrieve all champions. (REST)
 */
@Parcel
public class ChampionListDto {

    ChampionDto [] champions;

    public ChampionListDto() {}
}

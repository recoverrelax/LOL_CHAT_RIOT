package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiService;

import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.CurrentGame.CurrentGameInfo;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static.ChampionListDto;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static.RealmDto;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

public interface RiotApiService {
    String BASE_REGION_PVP_NET = "/{region}.api.pvp.net";
    String BASE_GLOBAL_PVP_NET = "/global.api.pvp.net";

    String STATIC_DATA_V = "v1.2";
    String GAME_DATA_V = "v1.3";

    /**
     * CURRENT_GAME *********************************************************************************************
     */

    @GET(BASE_REGION_PVP_NET + "/observer-mode/rest/consumer/getSpectatorGameInfo/{platformId}/{summonerId}")
    Observable<CurrentGameInfo> getCurrentGameInfoBySummonerId_CURRENT_GAME(
            @Path("region") String region,
            @Path("platformId") String platformId,
            @Path("summonerId") long summonerId);


    /**
     * STATIC DATA ***********************************************************************************************
     */

    @GET(BASE_GLOBAL_PVP_NET + "/api/lol/static-data/{region}/" + STATIC_DATA_V + "/champion?champData=all")
    Observable<ChampionListDto> getChampionList_STATIC_DATA(
            @Path("region") String region
    );

    @GET(BASE_GLOBAL_PVP_NET + "/api/lol/static-data/{region}/" + STATIC_DATA_V + "/champion?champData=image")
    Observable<ChampionListDto> getChampionListFiltered_STATIC_DATA(
            @Path("region") String region
    );

    @GET(BASE_GLOBAL_PVP_NET + "/api/lol/static-data/{region}/" + STATIC_DATA_V + "/realm")
    Observable<RealmDto> getRealmData_STATIC_DATA(@Path("region") String region);

    /**
     * GAME
     */

    @GET(BASE_REGION_PVP_NET + "/api/lol/static-data/{region}/" + GAME_DATA_V + "game/by-summoner/{summonerId}/recent")
    Observable<ChampionListDto> getRecentGamesBySummoner_GAME(
            @Path("region") String region,
            @Path("summonerId") String summonerId
    );
}

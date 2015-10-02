package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiService;

import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.CurrentGame.CurrentGameInfo;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Game.RecentGamesDto;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static.ChampionListDto;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static.ItemListDto;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static.RealmDto;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static.SummonerSpellListDto;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Status.ShardStatus;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Summoner.SummonerDto;

import java.util.Map;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

public interface RiotApiService {
    String BASE_REGION_PVP_NET = "/{region}.api.pvp.net";
    String BASE_GLOBAL_PVP_NET = "/global.api.pvp.net";
    String BASE_STATUS_LOL = "/status.leagueoflegends.com";

    String STATIC_DATA_V = "v1.2";
    String GAME_DATA_V = "v1.3";
    String SUMMONER_DATA_V = "v1.4";

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

    @GET(BASE_GLOBAL_PVP_NET + "/api/lol/static-data/{region}/" + STATIC_DATA_V + "/champion?champData=image,skins")
    Observable<ChampionListDto> getChampionListFiltered_STATIC_DATA(
            @Path("region") String region
    );

    @GET(BASE_GLOBAL_PVP_NET + "/api/lol/static-data/{region}/" + STATIC_DATA_V + "/realm")
    Observable<RealmDto> getRealmData_STATIC_DATA(@Path("region") String region);

    @GET(BASE_GLOBAL_PVP_NET + "/api/lol/static-data/{region}/" + STATIC_DATA_V + "/summoner-spell?spellData=all")
    Observable<SummonerSpellListDto> getSummonerSpellList_STATIC_DATA(
            @Path("region") String region
    );

    @GET(BASE_GLOBAL_PVP_NET + "/api/lol/static-data/{region}/" + STATIC_DATA_V + "/summoner-spell?spellData=image")
    Observable<SummonerSpellListDto> getSummonerSpellListFiltered_STATIC_DATA(
            @Path("region") String region
    );

    @GET(BASE_GLOBAL_PVP_NET + "/api/lol/static-data/{region}/" + STATIC_DATA_V + "/item?itemListData=image")
    Observable<ItemListDto> getItemListFiltered_STATIC_DATA(
            @Path("region") String region
    );


    /**
     * STATUS
     */

    @GET(BASE_STATUS_LOL + "/shards/{region}")
    Observable<ShardStatus> getShardStatus_STATUS(
            @Path("region") String region
    );

    /**
     * GAME
     */

    @GET(BASE_REGION_PVP_NET + "/api/lol/{region}/" + GAME_DATA_V + "/game/by-summoner/{summonerId}/recent")
    Observable<RecentGamesDto> getRecentMatchList_GAME(
            @Path("region") String region,
            @Path("summonerId") String summonerId
    );

    /**
     * SUMMONER
     */

    @GET(BASE_REGION_PVP_NET + "/api/lol/{region}/" + SUMMONER_DATA_V + "/summoner/{summonerIdList}")
    Observable<Map<String, SummonerDto>> getSummonerListByIds_SUMMONER(
            @Path("region") String region,
            @Path("summonerIdList") String summonerIdList
    );
}

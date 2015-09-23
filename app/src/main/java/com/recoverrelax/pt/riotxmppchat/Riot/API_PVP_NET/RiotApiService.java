package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET;

import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.CurrentGame.CurrentGameInfo;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Game.RecentGamesDto;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static.ChampionListDto;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface RiotApiService {
    String baseUrl = "/{region}.api.pvp.net";
    String globalUrl = "/global.api.pvp.net";

    /**
     *  champion-v1.2
     */

    @GET(baseUrl + "/api/lol/{region}/v1.3/champion")
    Observable<RecentGamesDto> getAllFreeChampionsBasicInfo(
            @Path("region") String region,
            @Query("freeToPlay") boolean filterByFreeToPlay);

//    @GET(baseUrl + "/api/lol/{region}/v1.3/game/by-summoner/{summonerId}/recent")
//    Observable<RecentGamesDto> getRecentGamesBySummonerId(
//            @Path("region") String region,
//            @Path("summonerId") String summonerId);

    /**
     * current-game-v1.0
     */

    @GET(baseUrl + "/observer-mode/rest/consumer/getSpectatorGameInfo/{platformId}/{summonerId}")
    Observable<CurrentGameInfo> getCurrentGameInfoBySummonerId(
            @Path("region") String region,
            @Path("platformId") String platformId,
            @Path("summonerId") long summonerId);

    @GET(globalUrl + "/api/lol/static-data/euw/v1.2/champion?champData=all")
    Observable<ChampionListDto> getAllChampionBasicInfo();

    /**
     * lol-static-data
     *
     * id, title, name, imageName, key
     */
    @GET(globalUrl + "/api/lol/static-data/euw/v1.2/champion?champData=image")
    Observable<ChampionListDto> getAllChampionBasicInfoFiltered();
}

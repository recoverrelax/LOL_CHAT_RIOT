package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET;

import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.CurrentGame.CurrentGameInfo;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Game.RecentGamesDto;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface RiotApiService {
    String baseUrl = "/{region}.api.pvp.net";

    /**
     *  champion-v1.2
     */

    @GET(baseUrl + "/api/lol/{region}/v1.3/champion")
    Observable<RecentGamesDto> getAllChampionsBasicInfo(
            @Path("region") String region);

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
            @Path("platformId") String platformId,
            @Query("summonerId") long summonerId);
}

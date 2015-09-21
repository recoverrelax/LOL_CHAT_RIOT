package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET;

import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Parent.RecentGamesBySID;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface RiotApiService {
    String baseUrl = "/{region}.api.pvp.net";

    @GET(baseUrl + "/api/lol/{region}/v1.3/game/by-summoner/{summonerId}/recent")
    Observable<RecentGamesBySID> getRecentGamesBySummonerId(
        @Path("region") String region,
        @Path("summonerId") String summonerId,
        @Query("api_key") String apiKey);

}

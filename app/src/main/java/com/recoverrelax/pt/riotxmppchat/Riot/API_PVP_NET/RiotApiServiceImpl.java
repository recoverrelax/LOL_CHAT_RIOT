package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET;

import android.content.Context;

import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Parent.RecentGamesBySID;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotGlobals;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RiotApiServiceImpl {

    private static final String TAG = RiotApiServiceImpl.class.getSimpleName();
    private static RiotApiServiceImpl instance = null;
    private RiotApiService riotApiService;

    public RiotApiServiceImpl() {
        riotApiService = ApiProvider.getInstance().getRiotApiService();
    }

    public static RiotApiServiceImpl getInstance() {
        if (instance == null) {
            instance = new RiotApiServiceImpl();
        }
        return instance;
    }

    public Observable<RecentGamesBySID> updateMaintenenceScheduleStatus(String region, String summonerId){
        return riotApiService.getRecentGamesBySummonerId(region, summonerId, getApiKey())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public String getApiKey(){
        return RiotGlobals.API_KEY;
    }
}

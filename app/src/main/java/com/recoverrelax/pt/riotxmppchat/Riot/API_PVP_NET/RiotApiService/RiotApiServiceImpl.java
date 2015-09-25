package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiService;

import android.support.annotation.Nullable;
import android.util.Log;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.CurrentGame.CurrentGameInfo;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static.ChampionListDto;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static.RealmDto;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotServer;
import com.recoverrelax.pt.riotxmppchat.Storage.DataStorage;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Singleton
public class RiotApiServiceImpl {

    private static final String TAG = RiotApiServiceImpl.class.getSimpleName();
    private static RiotApiServiceImpl instance = null;

    @Inject
    RiotApiService riotApiService;

    @Inject
    DataStorage dataStorage;

    @Singleton
    @Inject
    public RiotApiServiceImpl() {
        MainApplication.getInstance().getAppComponent().inject(this);
    }

    /**
     * platform id --> logged server
     * region -- > logged server
     */
    public Observable<CurrentGameInfo> getCurrentGameInfoBySummonerId(long summonerId){
        String platformId = getPlatformId();
        String region = getRegion();

        if(region == null || platformId == null)
            return Observable.error(new Throwable("For some reason, region or platform is invalid"));

           return riotApiService.getCurrentGameInfoBySummonerId_CURRENT_GAME(region, platformId, summonerId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<ChampionListDto> getAllChampionBasicInfoFiltered(){
        String region = getRegion();

        if(region == null)
            return Observable.error(new Throwable("For some reason, region is invalid"));

        return riotApiService.getChampionListFiltered_STATIC_DATA(region)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<ChampionListDto> getAllChampionBasicInfo(){
        String region = getRegion();

        if(region == null)
            return Observable.error(new Throwable("For some reason, region is invalid"));

        return riotApiService.getChampionList_STATIC_DATA(region)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<RealmDto> getRealmData(){
        String region = getRegion();

        if(region == null)
            return Observable.error(new Throwable("For some reason, region is invalid"));

        return riotApiService.getRealmData_STATIC_DATA(region)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    private @Nullable String getRegion(){
        String server = dataStorage.getServer();
        RiotServer riotServerByName = RiotServer.getRiotServerByName(server);
        return riotServerByName == null ? null : riotServerByName.getServerRegion();
    }

    private @Nullable String getPlatformId(){
        String server = dataStorage.getServer();
        RiotServer riotServerByName = RiotServer.getRiotServerByName(server);

        return riotServerByName == null ? null : riotServerByName.getServerPlatformId();
    }
}

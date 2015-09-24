package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.CurrentGame.CurrentGameInfo;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static.ChampionListDto;
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
        String server = dataStorage.getServer();
        RiotServer riotServerByName = RiotServer.getRiotServerByName(server);

        if(riotServerByName == null)
            return Observable.just(null);

        String region = riotServerByName.getServerRegion();
        String platformId = riotServerByName.getServerPlatformId();

        if(platformId == null && region != null){
            return Observable.just(null);
        }
        return riotApiService.getCurrentGameInfoBySummonerId(region, platformId, summonerId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<ChampionListDto> getAllChampionBasicInfoFiltered(){
        return riotApiService.getAllChampionBasicInfoFiltered()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<ChampionListDto> getAllChampionBasicInfo(){
        return riotApiService.getAllChampionBasicInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }
}

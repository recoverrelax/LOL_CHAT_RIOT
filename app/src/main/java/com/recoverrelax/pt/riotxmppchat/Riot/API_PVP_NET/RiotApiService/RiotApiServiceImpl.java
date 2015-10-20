package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiService;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.LogUtils;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Champion.Champion_ChampionDto;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Champion.Champion_ChampionListDto;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.CurrentGame.CurrentGameInfo;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Game.RecentGamesDto;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static.ChampionListDto;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static.ItemListDto;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static.RealmDto;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static.SummonerSpellListDto;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Status.ShardStatus;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Summoner.SummonerDto;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotServer;
import com.recoverrelax.pt.riotxmppchat.Storage.DataStorage;

import java.util.List;
import java.util.Map;

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
    ApiProvider apiProvider;

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
    public Observable<CurrentGameInfo> getCurrentGameInfoBySummonerId(long summonerId) {
        String platformId = getPlatformId();
        String region = getRegion();

        if (region == null || platformId == null)
            return Observable.error(new Throwable("For some reason, region or platform is invalid"));

        return apiProvider.getRiotApiServiceSecure().getCurrentGameInfoBySummonerId_CURRENT_GAME(region, platformId, summonerId)
                .doOnError(throwable -> LogUtils.LOGI(TAG, throwable.toString(), throwable))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<ChampionListDto> getAllChampionBasicInfoFiltered() {
        String region = getRegion();

        if (region == null)
            return Observable.error(new Throwable("For some reason, region is invalid"));

        return apiProvider.getRiotApiServiceSecure().getChampionListFiltered_STATIC_DATA(region)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<ChampionListDto> getAllChampionBasicInfo() {
        String region = getRegion();

        if (region == null)
            return Observable.error(new Throwable("For some reason, region is invalid"));

        return apiProvider.getRiotApiServiceSecure().getChampionList_STATIC_DATA(region)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<SummonerSpellListDto> getAllSummonerSpellBasicInfoFiltered() {
        String region = getRegion();

        if (region == null)
            return Observable.error(new Throwable("For some reason, region is invalid"));

        return apiProvider.getRiotApiServiceSecure().getSummonerSpellListFiltered_STATIC_DATA(region)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<List<Champion_ChampionDto>> getFreeChampRotation() {
        String region = getRegion();

        if (region == null)
            return Observable.error(new Throwable("For some reason, region is invalid"));

        return apiProvider.getRiotApiServiceSecure().getAllChampion_CHAMPION(region, true)
                .map(Champion_ChampionListDto::getChampions)
                .subscribeOn(Schedulers.computation());
    }

    public Observable<ItemListDto> getItemListBasicInfoFiltered() {
        String region = getRegion();

        if (region == null)
            return Observable.error(new Throwable("For some reason, region is invalid"));

        return apiProvider.getRiotApiServiceSecure().getItemListFiltered_STATIC_DATA(region)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<RecentGamesDto> getRecentMatchList(String summonerId) {
        String region = getRegion();

        if (region == null)
            return Observable.error(new Throwable("For some reason, region is invalid"));

        return apiProvider.getRiotApiServiceSecure().getRecentMatchList_GAME(region, summonerId)
                .doOnError(Throwable::printStackTrace)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<Map<String, SummonerDto>> getSummonerListByIds(List<String> summonerIdList) {
        String region = getRegion();

        if (region == null)
            return Observable.error(new Throwable("For some reason, region is invalid"));

        String join = TextUtils.join(",", summonerIdList);

        return apiProvider.getRiotApiServiceSecure().getSummonerListByIds_SUMMONER(region, join)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<RealmDto> getRealmData() {
        String region = getRegion();

        if (region == null)
            return Observable.error(new Throwable("For some reason, region is invalid"));

        return apiProvider.getRiotApiServiceSecure().getRealmData_STATIC_DATA(region)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<ShardStatus> getShardStatus(@Nullable String region) {
        if (region == null)
            return Observable.error(new Throwable("For some reason, region is invalid"));

        return apiProvider.getRiotApiService().getShardStatus_STATUS(region)
                .doOnError(throwable -> LogUtils.LOGI(TAG, throwable.toString(), throwable))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    private
    @Nullable String getRegion() {
        String server = dataStorage.getServer();
        RiotServer riotServerByName = RiotServer.getRiotServerByName(server);
        return riotServerByName == null ? null : riotServerByName.getServerRegion();
    }

    private
    @Nullable String getPlatformId() {
        String server = dataStorage.getServer();
        RiotServer riotServerByName = RiotServer.getRiotServerByName(server);

        return riotServerByName == null ? null : riotServerByName.getServerPlatformId();
    }
}

package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET;

import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Game.RecentGamesDto;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static.ChampionDto;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static.ItemDto;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static.ItemListDto;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static.SummonerSpellDto;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Status.Service;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Summoner.SummonerDto;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiService.RiotApiServiceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Singleton
public class RiotApiOperations {

    @Inject
    RiotApiServiceImpl riotApiServiceImpl;

    @Singleton
    @Inject
    public RiotApiOperations(){
        MainApplication.getInstance().getAppComponent().inject(this);
    }

    public Observable<Map<Integer, String>> getChampionsImage(){
        return riotApiServiceImpl.getAllChampionBasicInfoFiltered()
                .flatMap(championListDto -> Observable.just(championListDto.getChampionList()))
                .flatMap(mapStringChamp -> {
                    Map<Integer, String> newMap = new HashMap<>();

                    for (Map.Entry<String, ChampionDto> entry : mapStringChamp.entrySet())
                        newMap.put(entry.getValue().getId(), entry.getValue().getImage().getFull());

                    return Observable.just(newMap);
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Map<Integer, String>> getSummonerSpellImage(){
        return riotApiServiceImpl.getAllSummonerSpellBasicInfoFiltered()
                .flatMap(summonerSpellListDto -> Observable.just(summonerSpellListDto.getData()))
                .flatMap(mapStringSummonerSpell -> {
                    Map<Integer, String> newMap = new HashMap<>();

                    for (Map.Entry<String, SummonerSpellDto> entry : mapStringSummonerSpell.entrySet())
                        newMap.put(entry.getValue().getId(), entry.getValue().getImage().getFull());

                    return Observable.just(newMap);
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Map<Integer, String>> getItemImage(){
        return riotApiServiceImpl.getItemListBasicInfoFiltered()
                .map(ItemListDto::getData)
                .map(mapStringItemList -> {
                    Map<Integer, String> newMap = new HashMap<>();

                    for (Map.Entry<String, ItemDto> entry : mapStringItemList.entrySet()) {
                        newMap.put(entry.getValue().getId(), entry.getValue().getImage().getFull());
                    }

                    return newMap;
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Pair<String, List<Service>>> getShardIncidents(@Nullable String region){
        return riotApiServiceImpl.getShardStatus(region)
                .flatMap(shardStatus -> Observable.just(shardStatus.getName())
                                .map(shardRegionName -> new Pair<>(
                                                shardRegionName,
                                                shardStatus.getServices()
                                        )
                                )
                )
                .take(4)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<RecentGamesDto> getRecentGamesList(@Nullable String summonerId){
        return riotApiServiceImpl.getRecentMatchList(summonerId)
                    .subscribeOn(Schedulers.computation());
    }

    public Observable<Map<String, SummonerDto>> getSummonerListByIds(List<String> summonerIdList){
        int size = summonerIdList.size();
        int division = size / 39;
        int finalSize = size % 39 == 0 ? division : division+1;

        List<List<String>> split = split(summonerIdList, finalSize);
        List<Observable<?>> observableList = new ArrayList<>();

        for(List<String> listStr: split){
            observableList.add(riotApiServiceImpl.getSummonerListByIds(listStr));
        }

        return Observable.zip(observableList, RiotApiOperations::mergeMaps);
//
//
//
//        return riotApiServiceImpl.getSummonerListByIds(summonerIdList)
//                .map(mapStringSummonerList -> {
//                    Map<Integer, String> newMap = new HashMap<>();
//
//                    for (Map.Entry<String, SummonerDto> entry : mapStringSummonerList.entrySet()) {
//                        newMap.put((int)entry.getValue().getId(), entry.getValue().getName());
//                    }
//
//                    return newMap;
//                })
//                .subscribeOn(Schedulers.computation())
//                .observeOn(AndroidSchedulers.mainThread());
    }

    public static List<List<String>> split(List<String> list, int size)
            throws NullPointerException, IllegalArgumentException {
        if (list == null) {
            throw new NullPointerException("The list parameter is null.");
        }

        if (size <= 0) {
            throw new IllegalArgumentException(
                    "The size parameter must be more than 0.");
        }

        List<List<String>> result = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            result.add(new ArrayList<>());
        }

        int index = 0;

        for (String t : list) {
            result.get(index).add(t);
            index = (index + 1) % size;
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> mergeMaps(Object... maps) {
        final Map<K, V> result = new HashMap<K, V>();
        for (Object map: maps) {
            // unchecked <K,V> require @SuppressWarnings
            result.putAll((Map<K,V>)map);
        }
        return result;
    }


}

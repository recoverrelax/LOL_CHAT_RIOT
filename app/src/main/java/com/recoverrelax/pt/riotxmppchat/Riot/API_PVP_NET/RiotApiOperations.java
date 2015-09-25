package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET;

import android.support.v4.util.Pair;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.HelperModel.LiveGameBannedChamp;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.HelperModel.LiveGameParticipant;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static.ChampionDto;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiService.RiotApiServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
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

    /**
     *
     * @param liveGamePair : pair.first - list banned champs ; pair.second - list participants
     * @return the same list filled with championImage
     */
    public Observable<Pair<List<LiveGameBannedChamp>, List<LiveGameParticipant>>> getChampionsImage(Pair<List<LiveGameBannedChamp>, List<LiveGameParticipant>> liveGamePair){
        return riotApiServiceImpl.getAllChampionBasicInfoFiltered()
                .flatMap(championListDto -> Observable.just(championListDto.getChampionList()))
                .flatMap(mapStringChamp -> {
                    Map<Integer, String> newMap = new HashMap<>();

                    for (Map.Entry<String, ChampionDto> entry : mapStringChamp.entrySet())
                        newMap.put(entry.getValue().getId(), entry.getValue().getImage().getFull());

                    return Observable.just(newMap);
                })
                .flatMap(imagesMap -> {

                    List<LiveGameBannedChamp> liveBanned = liveGamePair.first;
                    List<LiveGameParticipant> liveParticipants = liveGamePair.second;

                    for(LiveGameBannedChamp lgb: liveBanned){
                        String s = imagesMap.get(lgb.getChampionID());

                        if(s != null){
                            lgb.setChampionImage(s);
                        }
                    }

                    for(LiveGameParticipant lgp: liveParticipants){
                        String s = imagesMap.get((Integer)(int)lgp.getChampionId());

                        if(s != null){
                            lgp.setChampionImage(s);
                        }
                    }

                    return Observable.just(liveGamePair);
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<LiveGameBannedChamp>> getChampionsImage(List<LiveGameBannedChamp> liveGameBans){
        return riotApiServiceImpl.getAllChampionBasicInfoFiltered()
                .flatMap(championListDto -> Observable.just(championListDto.getChampionList()))
                .flatMap(mapStringChamp -> {
                    Map<Integer, String> newMap = new HashMap<>();

                    for (Map.Entry<String, ChampionDto> entry : mapStringChamp.entrySet())
                        newMap.put(entry.getValue().getId(), entry.getValue().getImage().getFull());

                    for(LiveGameBannedChamp lgb: liveGameBans){
                        String s = newMap.get(lgb.getChampionID());

                        if(s != null){
                            lgb.setChampionImage(s);
                        }
                    }

                    return Observable.just(liveGameBans);
                })

                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<LiveGameParticipant>> getChampionsImage2(List<LiveGameParticipant> liveParticipants){
        return riotApiServiceImpl.getAllChampionBasicInfoFiltered()
                .flatMap(championListDto -> Observable.just(championListDto.getChampionList()))
                .flatMap(mapStringChamp -> {
                    Map<Integer, String> newMap = new HashMap<>();

                    for (Map.Entry<String, ChampionDto> entry : mapStringChamp.entrySet())
                        newMap.put(entry.getValue().getId(), entry.getValue().getImage().getFull());

                    for(LiveGameParticipant lgp: liveParticipants){
                        String s = newMap.get((Integer)(int)lgp.getChampionId());

                        if(s != null){
                            lgp.setChampionImage(s);
                        }
                    }
                    return Observable.just(liveParticipants);
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
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

}

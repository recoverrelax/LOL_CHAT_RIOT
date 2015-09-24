package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.HelperModel.LiveGameBannedChamp;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static.ChampionDto;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
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

    /**
     *
     * @param liveGameBannedChamps : list of BannedChampionImage
     * @return the same list filled with championImage
     */
    public Observable<List<LiveGameBannedChamp>> getChampionsImage(List<LiveGameBannedChamp> liveGameBannedChamps){
        return riotApiServiceImpl.getAllChampionBasicInfoFiltered()
                .flatMap(championListDto -> Observable.just(championListDto.getChampionList()))
                .flatMap(championDtoMap -> Observable.create(new Observable.OnSubscribe<List<LiveGameBannedChamp>>() {
                    @Override
                    public void call(Subscriber<? super List<LiveGameBannedChamp>> subscriber) {
                        for(Map.Entry<String, ChampionDto> entry: championDtoMap.entrySet()) {
                            int id = entry.getValue().getId();
                            for(LiveGameBannedChamp bci: liveGameBannedChamps){
                                if(bci.getChampionID() == id){
                                    bci.setChampionImage(entry.getValue().getImage().getFull());
                                }
                            }
                        }

                        subscriber.onNext(liveGameBannedChamps);
                        subscriber.onCompleted();
                     }
                })
                )
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread());
    }
}

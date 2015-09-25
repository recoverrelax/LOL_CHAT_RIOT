package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppGlobals;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static.RealmDto;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiService.RiotApiServiceImpl;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Singleton
public class RiotApiRealmDataVersion {

    @Inject
    RiotApiServiceImpl riotApiServiceImpl;

    private Observable<RealmDto> realmData;

    @Singleton
    @Inject
    public RiotApiRealmDataVersion(){
        MainApplication.getInstance().getAppComponent().inject(this);
        realmData = riotApiServiceImpl.getRealmData().cache();
    }

    public Observable<String> getChampionDDBaseUrl(){
        return realmData
                .map(realmDto -> realmDto.getN().get(AppGlobals.DD_VERSION.CHAMPION_JSON_ID))
                .map(ddVersion -> AppGlobals.DD_VERSION.CHAMPION_SQUARE.replace(AppGlobals.DD_VERSION.DD_VERSION, ddVersion))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<String> getProfileIconBaseUrl(){
        return realmData
                .map(realmDto -> realmDto.getN().get(AppGlobals.DD_VERSION.PROFILEICON_JSON_ID))
                .map(ddVersion -> AppGlobals.DD_VERSION.PROFILE_SQUARE.replace(AppGlobals.DD_VERSION.DD_VERSION, ddVersion))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}

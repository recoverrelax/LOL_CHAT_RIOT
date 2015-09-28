package com.recoverrelax.pt.riotxmppchat;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiService.RiotApiServiceImpl;
import com.recoverrelax.pt.riotxmppchat.Storage.DataStorage;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final MainApplication applicationContext;

    @Inject
    public AppModule(MainApplication context){
        this.applicationContext = context;
    }

    @Provides
    @Singleton
    MainApplication provideApplicationContext(){
        return this.applicationContext;
    }

    @Provides
    @Singleton
    DataStorage provideDataStorage(){
        return new DataStorage(applicationContext);
    }

    @Provides
    @Singleton
    RiotApiServiceImpl provideRiotApiServiceImpl(){
        return new RiotApiServiceImpl();
    }

    @Provides
    @Singleton
    Bus provideEventBus(){
        return new Bus(ThreadEnforcer.ANY);
    }
}

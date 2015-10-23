package com.recoverrelax.pt.riotxmppchat;

import com.recoverrelax.pt.riotxmppchat.Storage.DataStorage;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private final MainApplication applicationContext;

    @Inject
    public AppModule(MainApplication context) {
        this.applicationContext = context;
    }

    @Provides
    @Singleton MainApplication provideApplicationContext() {
        return this.applicationContext;
    }

    @Provides
    @Singleton DataStorage provideDataStorage() {
        return new DataStorage(applicationContext);
    }
}

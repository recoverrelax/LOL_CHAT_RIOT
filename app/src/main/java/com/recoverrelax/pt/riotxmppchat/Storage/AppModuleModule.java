package com.recoverrelax.pt.riotxmppchat.Storage;
import com.recoverrelax.pt.riotxmppchat.NotificationCenter.MessageSpeechNotification;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModuleModule {

    private DataStorage dataStorage;
    private RiotXmppDBRepository riotXmppDBRepository;
    private MessageSpeechNotification messageSpeechNotif;

    @Inject
    public AppModuleModule(DataStorage dataStorage, RiotXmppDBRepository riotXmppDBRepository, MessageSpeechNotification messageSpeechNotif){
        this.dataStorage = dataStorage;
        this.messageSpeechNotif = messageSpeechNotif;
        this.riotXmppDBRepository = riotXmppDBRepository;
    }

    @Provides
    @Singleton
    DataStorage provideDataStorage(){
        return dataStorage;
    }

    @Provides
    @Singleton
    MessageSpeechNotification provideSpeechNotification(){
        return messageSpeechNotif;
    }

    @Provides
    @Singleton
    RiotXmppDBRepository provideRiotRepository(){
        return riotXmppDBRepository;
    }
}

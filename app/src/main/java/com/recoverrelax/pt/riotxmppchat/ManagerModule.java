package com.recoverrelax.pt.riotxmppchat;
import com.recoverrelax.pt.riotxmppchat.Network.Manager.RiotChatManager;
import com.recoverrelax.pt.riotxmppchat.Network.Manager.RiotConnectionManager;
import com.recoverrelax.pt.riotxmppchat.Network.Manager.RiotRosterManager;
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.RiotXmppRosterImpl;
import com.recoverrelax.pt.riotxmppchat.NotificationCenter.MessageNotification;
import com.recoverrelax.pt.riotxmppchat.NotificationCenter.StatusNotification;
import com.recoverrelax.pt.riotxmppchat.Storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.Storage.RiotXmppDBRepository;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ManagerModule {

    @Inject
    public ManagerModule(){
    }

    @Provides
    @Singleton
    RiotChatManager provideRiotChatManager(RiotXmppDBRepository riotXmppDBRepository, MessageNotification notif){
        return new RiotChatManager(riotXmppDBRepository, notif);
    }

    @Provides
    @Singleton
    RiotConnectionManager provideRiotConnectionManager(Bus bus, RiotRosterManager riotRosterManager, RiotXmppRosterImpl rosterImpl){
        return new RiotConnectionManager(bus, riotRosterManager, rosterImpl);
    }



    @Provides
    @Singleton
    RiotRosterManager provideRiotRosterManager(Bus busInstance, Provider<StatusNotification> statusNotificationProvider){
        return new RiotRosterManager(busInstance, statusNotificationProvider);
    }
}

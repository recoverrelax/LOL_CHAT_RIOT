package com.recoverrelax.pt.riotxmppchat;
import com.recoverrelax.pt.riotxmppchat.Network.Manager.RiotRosterManager;
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.FriendMessageListImpl;
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.PersonalMessageImpl;
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.RiotXmppConnectionImpl;
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.RiotXmppDashboardImpl;
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.RiotXmppRosterImpl;
import com.recoverrelax.pt.riotxmppchat.NotificationCenter.MessageNotification;
import com.recoverrelax.pt.riotxmppchat.NotificationCenter.MessageSpeechNotification;
import com.recoverrelax.pt.riotxmppchat.NotificationCenter.StatusNotification;
import com.recoverrelax.pt.riotxmppchat.Storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.Storage.RiotXmppDBRepository;
import com.squareup.otto.Bus;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class NotificationModule {

    @Inject
    public NotificationModule(){

    }

    @Provides
    MessageNotification provideMessageNotification(Bus bus, DataStorage dataStorageInstance, RiotRosterManager riotRosterManager,
                                                   RiotXmppDBRepository xmppDBRepository, MessageSpeechNotification speechNotification){
        return new MessageNotification(bus, dataStorageInstance, riotRosterManager, xmppDBRepository, speechNotification);
    }

    @Provides
    StatusNotification provideStatusNotification(Bus bus, DataStorage dataStorage, RiotXmppDBRepository xmppDBRepository,
                                                 MessageSpeechNotification speechNotification){
        return new StatusNotification(bus, dataStorage, xmppDBRepository, speechNotification);
    }

    @Provides
    @Singleton
    MessageSpeechNotification provideSpeechNotification(MainApplication applicationContext){
        return new MessageSpeechNotification(applicationContext);
    }
}

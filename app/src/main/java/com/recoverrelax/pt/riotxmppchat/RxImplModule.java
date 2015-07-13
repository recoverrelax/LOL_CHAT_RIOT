package com.recoverrelax.pt.riotxmppchat;
import com.recoverrelax.pt.riotxmppchat.Network.Manager.RiotRosterManager;
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.FriendMessageListImpl;
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.PersonalMessageImpl;
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.RiotXmppConnectionImpl;
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.RiotXmppDashboardImpl;
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.RiotXmppRosterImpl;
import com.recoverrelax.pt.riotxmppchat.Storage.RiotXmppDBRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RxImplModule {

    @Inject
    public RxImplModule(){

    }

    @Provides
    @Singleton
    RiotXmppConnectionImpl provideConnectionImpl(){
        return new RiotXmppConnectionImpl();
    }

    @Provides
    @Singleton
    RiotXmppDashboardImpl provideDashBoardImpl(RiotXmppDBRepository repository, RiotRosterManager riotRosterManager){
        return new RiotXmppDashboardImpl(repository, riotRosterManager);
    }

    @Provides
    @Singleton
    RiotXmppRosterImpl provideRosterImpl(RiotRosterManager rosterManager){
        return new RiotXmppRosterImpl(rosterManager);
    }

    @Provides
    @Singleton
    PersonalMessageImpl providePersonalMessageImpl(RiotRosterManager rosterManager){
        return new PersonalMessageImpl(rosterManager);
    }

    @Provides
    @Singleton
    FriendMessageListImpl provideFriendMessageListImpl(RiotRosterManager rosterManager){
        return new FriendMessageListImpl(rosterManager);
    }
}

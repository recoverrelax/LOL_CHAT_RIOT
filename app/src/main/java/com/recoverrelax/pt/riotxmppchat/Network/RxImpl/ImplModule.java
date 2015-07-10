package com.recoverrelax.pt.riotxmppchat.Network.RxImpl;
import com.recoverrelax.pt.riotxmppchat.Storage.RiotXmppDBRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ImplModule {

    public ImplModule(){

    }

    @Provides
    @Singleton
    RiotXmppConnectionImpl provideConnectionImpl(){
        return new RiotXmppConnectionImpl();
    }

    @Provides
    @Singleton
    RiotXmppDashboardImpl provideDashBoardImpl(RiotXmppDBRepository repository){
        return new RiotXmppDashboardImpl(repository);
    }

    @Provides
    @Singleton
    RiotXmppRosterImpl provideRosterImpl(){
        return new RiotXmppRosterImpl();
    }

    @Provides
    @Singleton
    PersonalMessageImpl providePersonalMessageImpl(){
        return new PersonalMessageImpl();
    }

    @Provides
    @Singleton
    FriendMessageListImpl provideFriendMessageListImpl(){
        return new FriendMessageListImpl();
    }

}

package com.recoverrelax.pt.riotxmppchat.EventHandling;


import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.NewMessageReceivedEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.NewMessageReceivedNotifyEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.OnFriendPresenceChangedEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.OnNewFriendPlayingEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.OnReconnectEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.NewMessageReceivedNotifyPublish;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.NewMessageReceivedPublish;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.OnFriendPresenceChangedPublish;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.OnNewFriendPlayingPublish;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.OnReconnectPublish;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EventHandler {

    @Inject
    Bus bus;

    private List<NewMessageReceivedEvent> newMessageEventList = new ArrayList<>();
    private List<NewMessageReceivedNotifyEvent> newMessageNotifyEventList = new ArrayList<>();
    private List<OnReconnectEvent> reconnectEventList = new ArrayList<>();
    private List<OnNewFriendPlayingEvent> onFriendPlayingEventList = new ArrayList<>();
    private List<OnFriendPresenceChangedEvent> onFriendPresenceChangedEventList = new ArrayList<>();

    @Singleton
    @Inject
    public EventHandler(){
        MainApplication.getInstance().getAppComponent().inject(this);
        bus.register(this);
    }

    public void registerForNewMessageEvent(NewMessageReceivedEvent event){
        if(!newMessageEventList.contains(event))
            newMessageEventList.add(event);
    }

    public void unregisterForNewMessageNotifyEvent(NewMessageReceivedNotifyEvent event){
        if(newMessageNotifyEventList.contains(event))
            newMessageNotifyEventList.remove(event);
    }

    public void registerForNewMessageNotifyEvent(NewMessageReceivedNotifyEvent event){
        if(!newMessageNotifyEventList.contains(event))
            newMessageNotifyEventList.add(event);
    }

    public void unregisterForNewMessageEvent(NewMessageReceivedEvent event){
        if(newMessageEventList.contains(event))
            newMessageEventList.remove(event);
    }

    public void registerForRecconectEvent(OnReconnectEvent event){
        if(!reconnectEventList.contains(event))
            reconnectEventList.add(event);
    }

    public void unregisterForRecconectEvent(OnReconnectEvent event){
        if(reconnectEventList.contains(event))
            reconnectEventList.remove(event);
    }

    public void registerForFriendPlayingEvent(OnNewFriendPlayingEvent event){
        if(!onFriendPlayingEventList.contains(event))
            onFriendPlayingEventList.add(event);
    }

    public void unregisterForFriendPlayingEvent(OnNewFriendPlayingEvent event){
        if(onFriendPlayingEventList.contains(event))
            onFriendPlayingEventList.remove(event);
    }

    public void registerForFriendPresenceChangedEvent(OnFriendPresenceChangedEvent event){
        if(!onFriendPresenceChangedEventList.contains(event))
            onFriendPresenceChangedEventList.add(event);
    }

    public void unregisterForFriendPresenceChangedEvent(OnFriendPresenceChangedEvent event){
        if(onFriendPresenceChangedEventList.contains(event))
            onFriendPresenceChangedEventList.remove(event);
    }

    @Subscribe
    public void publishNewMessages(NewMessageReceivedPublish publishEvent){
        for(NewMessageReceivedEvent event: newMessageEventList){
            event.onNewMessageReceived(publishEvent.getUserXmppAddress(),
                    publishEvent.getUsername(),
                    publishEvent.getMessage(),
                    publishEvent.getButtonLabel());
        }
    }

    @Subscribe
    public void notifyNewMessages(NewMessageReceivedNotifyPublish publishEvent){
        for(NewMessageReceivedNotifyEvent event: newMessageNotifyEventList){
            event.onNewMessageNotifyReceived(publishEvent.getUserXmppAddress(),
                    publishEvent.getUsername(),
                    publishEvent.getMessage(),
                    publishEvent.getButtonLabel());
        }
    }

    @Subscribe
    public void onReconnect(OnReconnectPublish recconectEvent){
        for(OnReconnectEvent event: reconnectEventList){
            event.onReconnect();
        }
    }

    @Subscribe
    public void onNewFriendPlaying(final OnNewFriendPlayingPublish friendPlayingEvent) {
        for(OnNewFriendPlayingEvent event: onFriendPlayingEventList){
            event.onNewFriendPlaying();
        }
    }

    @Subscribe
    public void OnFriendPresenceChanged(final OnFriendPresenceChangedPublish friendPresence) {
        for(OnFriendPresenceChangedEvent event: onFriendPresenceChangedEventList){
            event.onFriendPresenceChanged(friendPresence.getPresence());
        }
    }
}

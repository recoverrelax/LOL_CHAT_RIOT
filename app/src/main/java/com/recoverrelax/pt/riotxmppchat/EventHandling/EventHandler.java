package com.recoverrelax.pt.riotxmppchat.EventHandling;


import android.util.Log;

import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.Event;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.NewMessageReceivedEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.NewMessageReceivedNotifyEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.OnDisconnectEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.OnFriendPresenceChangedEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.OnNewFriendPlayingEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.OnReconnectEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.NewMessageReceivedNotifyPublish;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.NewMessageReceivedPublish;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.OnDisconnectPublish;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.OnFriendPresenceChangedPublish;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.OnNewFriendPlayingPublish;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.OnReconnectPublish;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.Network.Manager.RiotRosterManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EventHandler {

    private final String TAG = EventHandler.this.getClass().getSimpleName();

    @Inject
    Bus bus;

    @Inject
    RiotRosterManager rosterManager;

//    private List<NewMessageReceivedEvent> newMessageEventList = new ArrayList<>();
//    private List<NewMessageReceivedNotifyEvent> newMessageNotifyEventList = new ArrayList<>();
//
//    private List<OnReconnectEvent> reconnectEventList = new ArrayList<>();
//    private List<OnDisconnectEvent> disconnectEventList = new ArrayList<>();
//
//
//    private List<OnNewFriendPlayingEvent> onFriendPlayingEventList = new ArrayList<>();
//    private List<OnFriendPresenceChangedEvent> onFriendPresenceChangedEventList = new ArrayList<>();
//    private HashMap<>

    private HashMap<Class<?>, List<? extends Event>> eventMap = new HashMap<>();

    @Singleton
    @Inject
    public EventHandler(){
        MainApplication.getInstance().getAppComponent().inject(this);
        bus.register(this);
    }

    public <T extends Event>void registerForEvent(T event) {

        if(event == null)
            throw new NullPointerException("Cannot register for a null event");

        Class<?>[] interfaces = event.getClass().getInterfaces();

        Log.i(TAG, interfaces.length + "length: ");

        for (Class<?>  i : interfaces) {
            addEvent(i, event);
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Event> void addEvent(Class<?> i, T t_event) {

        if(eventMap == null)
            return;

        List<? extends Event> eventList = eventMap.get(i);

        if(eventList == null){
            List<T> event = new ArrayList<>();
            event.add(t_event);
            eventMap.put(i, event);
        }else{
            List<T> eventList1 = (List<T>) (List<?>) eventList;
            eventList1.add(t_event);
        }
    }


    public void registerForNewMessageEvent(NewMessageReceivedEvent event){
//        if(!newMessageEventList.contains(event))
//            newMessageEventList.add(event);
        registerForEvent(event);
    }

    public void unregisterForNewMessageNotifyEvent(NewMessageReceivedNotifyEvent event){
//        if(newMessageNotifyEventList.contains(event))
//            newMessageNotifyEventList.remove(event);
    }

    public void registerForNewMessageNotifyEvent(NewMessageReceivedNotifyEvent event){
//        if(!newMessageNotifyEventList.contains(event))
//            newMessageNotifyEventList.add(event);
    }

    public void unregisterForNewMessageEvent(NewMessageReceivedEvent event){
//        if(newMessageEventList.contains(event))
//            newMessageEventList.remove(event);
    }

    public void registerForReconnectEvent(OnReconnectEvent event){
//        if(!reconnectEventList.contains(event))
//            reconnectEventList.add(event);
    }

    public void unregisterForReconnectEvent(OnReconnectEvent event){
//        if(reconnectEventList.contains(event))
//            reconnectEventList.remove(event);
    }

    public void registerForDisconnectEvent(OnDisconnectEvent event){
//        if(!disconnectEventList.contains(event))
//            disconnectEventList.add(event);
    }

    public void unregisterForDisconnectEvent(OnDisconnectEvent event){
//        if(disconnectEventList.contains(event))
//            disconnectEventList.remove(event);
    }

    public void registerForFriendPlayingEvent(OnNewFriendPlayingEvent event){
//        if(!onFriendPlayingEventList.contains(event))
//            onFriendPlayingEventList.add(event);
    }

    public void unregisterForFriendPlayingEvent(OnNewFriendPlayingEvent event){
//        if(onFriendPlayingEventList.contains(event))
//            onFriendPlayingEventList.remove(event);
    }

    public void registerForFriendPresenceChangedEvent(OnFriendPresenceChangedEvent event){
//        if(!onFriendPresenceChangedEventList.contains(event))
//            onFriendPresenceChangedEventList.add(event);
    }

    public void unregisterForFriendPresenceChangedEvent(OnFriendPresenceChangedEvent event){
//        if(onFriendPresenceChangedEventList.contains(event))
//            onFriendPresenceChangedEventList.remove(event);
    }

    @SuppressWarnings("unchecked")
    @Subscribe
    public void publishNewMessages(NewMessageReceivedPublish publishEvent){
        if(!rosterManager.isConnected())
            return;

        List<NewMessageReceivedEvent> eventList = (List<NewMessageReceivedEvent>) eventMap.get(NewMessageReceivedEvent.class);

        for(NewMessageReceivedEvent event: eventList){
            event.onNewMessageReceived(publishEvent.getUserXmppAddress(),
                    publishEvent.getUsername(),
                    publishEvent.getMessage(),
                    publishEvent.getButtonLabel());
        }
    }

    @Subscribe
    public void notifyNewMessages(NewMessageReceivedNotifyPublish publishEvent){
//        if(!rosterManager.isConnected())
//            return;
//
//        for(NewMessageReceivedNotifyEvent event: newMessageNotifyEventList){
//            event.onNewMessageNotifyReceived(publishEvent.getUserXmppAddress(),
//                    publishEvent.getUsername(),
//                    publishEvent.getMessage(),
//                    publishEvent.getButtonLabel());
//        }
    }

//    @Subscribe
//    public void onReconnect(OnReconnectPublish recconectEvent){
////        for(OnReconnectEvent event: reconnectEventList){
////            event.onReconnect();
////        }
//    }
//
//    @Subscribe
//    public void onDisconnect(OnDisconnectPublish disconnectEvent){
//        for(OnDisconnectEvent event: disconnectEventList){
//            event.onDisconnect();
//        }
//    }
//
//    @Subscribe
//    public void onNewFriendPlaying(final OnNewFriendPlayingPublish friendPlayingEvent) {
//        if(!rosterManager.isConnected())
//            return;
//
//        for(OnNewFriendPlayingEvent event: onFriendPlayingEventList){
//            event.onNewFriendPlaying();
//        }
//    }
//
//    @Subscribe
//    public void OnFriendPresenceChanged(final OnFriendPresenceChangedPublish friendPresence) {
//        if(!rosterManager.isConnected())
//            return;
//
//        for(OnFriendPresenceChangedEvent event: onFriendPresenceChangedEventList){
//            event.onFriendPresenceChanged(friendPresence.getPresence());
//        }
//    }
//
    public boolean isApplicationPaused(){
        return false;
//        return reconnectEventList == null || reconnectEventList.size() == 0;
    }


}

package com.recoverrelax.pt.riotxmppchat.EventHandling;


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
import com.recoverrelax.pt.riotxmppchat.Storage.BusHandler;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EventHandler {

    @Inject
    BusHandler bus;

    @Inject
    RiotRosterManager rosterManager;

    private List<NewMessageReceivedEvent> newMessageEventList = new ArrayList<>();
    private List<NewMessageReceivedNotifyEvent> newMessageNotifyEventList = new ArrayList<>();

    private List<OnReconnectEvent> reconnectEventList = new ArrayList<>();
    private List<OnDisconnectEvent> disconnectEventList = new ArrayList<>();

    private List<OnNewFriendPlayingEvent> onFriendPlayingEventList = new ArrayList<>();
    private List<OnFriendPresenceChangedEvent> onFriendPresenceChangedEventList = new ArrayList<>();

    private Map<Class<?>, List<? extends Event>> eventList = new HashMap<>();

    @Singleton
    @Inject
    public EventHandler() {
        MainApplication.getInstance().getAppComponent().inject(this);
        bus.register(this);
    }

//    @SuppressWarnings("unchecked")
//    public <T extends Event> void registerForEvent(T event) {
//
//        String eventListKey = event.getClass().getSimpleName();
//        if(eventListKey.equals("")) // empty string means anonymous class
//            return;
//
//
//        if(!eventList.containsKey(eventListKey)){ // first time adding to the list, must create first
//           eventList.put(eventListKey, new ArrayList<T>());
//        }
//
//        List<T> events = (List<T>) (List<?>)eventList.get(eventListKey);
//        if(events.contains())
//        eventList.get(eventListKey).add(eventValue);
//
//    }

    public void registerForNewMessageEvent(NewMessageReceivedEvent event) {
        if (!newMessageEventList.contains(event))
            newMessageEventList.add(event);
    }

    public void unregisterForNewMessageNotifyEvent(NewMessageReceivedNotifyEvent event) {
        if (newMessageNotifyEventList.contains(event))
            newMessageNotifyEventList.remove(event);
    }

    public void registerForNewMessageNotifyEvent(NewMessageReceivedNotifyEvent event) {
        if (!newMessageNotifyEventList.contains(event))
            newMessageNotifyEventList.add(event);
    }

    public void unregisterForNewMessageEvent(NewMessageReceivedEvent event) {
        if (newMessageEventList.contains(event))
            newMessageEventList.remove(event);
    }

    public void registerForReconnectEvent(OnReconnectEvent event) {
        if (!reconnectEventList.contains(event))
            reconnectEventList.add(event);
    }

    public void unregisterForReconnectEvent(OnReconnectEvent event) {
        if (reconnectEventList.contains(event))
            reconnectEventList.remove(event);
    }

    public void registerForDisconnectEvent(OnDisconnectEvent event) {
        if (!disconnectEventList.contains(event))
            disconnectEventList.add(event);
    }

    public void unregisterForDisconnectEvent(OnDisconnectEvent event) {
        if (disconnectEventList.contains(event))
            disconnectEventList.remove(event);
    }

    public void registerForFriendPlayingEvent(OnNewFriendPlayingEvent event) {
        if (!onFriendPlayingEventList.contains(event))
            onFriendPlayingEventList.add(event);
    }

    public void unregisterForFriendPlayingEvent(OnNewFriendPlayingEvent event) {
        if (onFriendPlayingEventList.contains(event))
            onFriendPlayingEventList.remove(event);
    }

    public void registerForFriendPresenceChangedEvent(OnFriendPresenceChangedEvent event) {
        if (!onFriendPresenceChangedEventList.contains(event))
            onFriendPresenceChangedEventList.add(event);
    }

    public void unregisterForFriendPresenceChangedEvent(OnFriendPresenceChangedEvent event) {
        if (onFriendPresenceChangedEventList.contains(event))
            onFriendPresenceChangedEventList.remove(event);
    }

    @Subscribe
    public void publishNewMessages(NewMessageReceivedPublish publishEvent) {
        if (!rosterManager.isConnected())
            return;

        for (NewMessageReceivedEvent event : newMessageEventList) {
            event.onNewMessageReceived(publishEvent.getUserXmppAddress(),
                    publishEvent.getUsername(),
                    publishEvent.getMessage(),
                    publishEvent.getButtonLabel());
        }
    }

    @Subscribe
    public void notifyNewMessages(NewMessageReceivedNotifyPublish publishEvent) {
        if (!rosterManager.isConnected())
            return;

        for (NewMessageReceivedNotifyEvent event : newMessageNotifyEventList) {
            event.onNewMessageNotifyReceived(publishEvent.getUserXmppAddress(),
                    publishEvent.getUsername(),
                    publishEvent.getMessage(),
                    publishEvent.getButtonLabel());
        }
    }

    @Subscribe
    public void onReconnect(OnReconnectPublish recconectEvent) {
        for (OnReconnectEvent event : reconnectEventList) {
            event.onReconnect();
        }
    }

    @Subscribe
    public void onDisconnect(OnDisconnectPublish disconnectEvent) {
        for (OnDisconnectEvent event : disconnectEventList) {
            event.onDisconnect();
        }
    }

    @Subscribe
    public void onNewFriendPlaying(final OnNewFriendPlayingPublish friendPlayingEvent) {
        if (!rosterManager.isConnected())
            return;

        for (OnNewFriendPlayingEvent event : onFriendPlayingEventList) {
            event.onNewFriendPlaying();
        }
    }

    @Subscribe
    public void OnFriendPresenceChanged(final OnFriendPresenceChangedPublish friendPresence) {
        if (!rosterManager.isConnected())
            return;

        for (OnFriendPresenceChangedEvent event : onFriendPresenceChangedEventList) {
            event.onFriendPresenceChanged(friendPresence.getPresence());
        }
    }

    public boolean isApplicationPaused() {
        return reconnectEventList == null || reconnectEventList.size() == 0;
    }
}

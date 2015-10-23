package com.recoverrelax.pt.riotxmppchat.Storage;

import com.recoverrelax.pt.riotxmppchat.EventHandling.Publish.AbstractPublish;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BusHandler {

    private Bus activeBus;

    @Singleton
    @Inject
    public BusHandler() {
        activeBus = new Bus(ThreadEnforcer.ANY);
    }

    public <T extends AbstractPublish> void post(T publish) {
        activeBus.post(publish);
    }

    public <T> void unregister(T u) {
        activeBus.unregister(u);
    }

    public <T> void register(T r) {
        activeBus.register(r);
    }
}

package com.recoverrelax.pt.riotxmppchat.Riot.Interface;

public interface RiotXmppDataLoaderCallback<T> {
    void onFailure(Throwable ex);
    void onSuccess(T result);
    void onComplete();
    void destroyLoader();
}

package com.recoverrelax.pt.riotxmppchat.Riot.Interface;

public interface RosterDataLoaderCallback<T> {
    void onFailure(Throwable ex);
    void onSuccess(T result);
    void onComplete();
    void destroyLoader();
}

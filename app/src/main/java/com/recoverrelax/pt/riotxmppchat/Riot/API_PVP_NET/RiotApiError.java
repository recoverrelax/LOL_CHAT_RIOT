package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET;

import android.support.annotation.StringRes;

public class RiotApiError {

    public RiotApiErrorEnum error;
    public @StringRes int errorRes;

    public RiotApiError(RiotApiErrorEnum error, int errorRes){
        this.error = error;
        this.errorRes = errorRes;
    }

    public RiotApiErrorEnum getError() {
        return error;
    }

    public void setError(RiotApiErrorEnum error) {
        this.error = error;
    }

    public int getErrorRes() {
        return errorRes;
    }

    public void setErrorRes(int errorRes) {
        this.errorRes = errorRes;
    }
}

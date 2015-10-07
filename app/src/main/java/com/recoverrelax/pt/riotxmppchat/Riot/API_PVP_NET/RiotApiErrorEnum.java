package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET;


public enum RiotApiErrorEnum {

    E_400, // bad request
    E_404, // Game data not found
    E_401, // unauthorized
    E_500, // internal server error
    E_503, // service unavailable
    DEFAULT;

    public boolean is404(){
        return this.equals(RiotApiErrorEnum.E_404);
    }
}

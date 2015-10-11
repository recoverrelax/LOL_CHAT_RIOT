package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET;


public enum RiotApiErrorEnum {

    E_400("400"), // bad request
    E_429("429"), // bad request
    E_404("404"), // Game data not found
    E_401("401"), // unauthorized
    E_500("500"), // internal server error
    E_503("503"), // service unavailable
    DEFAULT("");

    private String apiError;

    RiotApiErrorEnum(String apiError){
        this.apiError = apiError;
    }

    public String getApiError() {
        return apiError;
    }

    public boolean is404(){
        return this.equals(RiotApiErrorEnum.E_404);
    }
}

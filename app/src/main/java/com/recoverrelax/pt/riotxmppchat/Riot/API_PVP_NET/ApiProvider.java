package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.converter.GsonConverter;

public class ApiProvider {
    private static final String TAG = ApiProvider.class.getSimpleName();
    private RestAdapter mRestAdapter;
    private RiotApiService riotApiService;
    private static volatile ApiProvider ApiProviderInstance = null;
    private static final Endpoint baseEndPoint = Endpoints.newFixedEndpoint("https://");

    private ApiProvider() {
        Gson gson = (new GsonBuilder()).registerTypeAdapter(Date.class, new DateTimeTypeAdapter()).create();
        this.mRestAdapter = (new RestAdapter.Builder())
                .setErrorHandler(ErrorHandler.DEFAULT)
                .setLog(new AndroidLog(TAG))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(baseEndPoint)
                .setConverter(new GsonConverter(gson)).build();
        this.riotApiService = this.mRestAdapter.create(RiotApiService.class);
    }

    public static ApiProvider getInstance() {
        if(ApiProviderInstance == null) {

            synchronized(ApiProvider.class) {
                if(ApiProviderInstance == null) {
                    ApiProviderInstance = new ApiProvider();
                }
            }
        }

        return ApiProviderInstance;
    }

    public RiotApiService getRiotApiService() {
        return this.riotApiService;
    }
}

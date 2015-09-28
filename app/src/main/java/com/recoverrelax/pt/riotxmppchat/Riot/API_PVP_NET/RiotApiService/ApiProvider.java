package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.DateTimeTypeAdapter;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.RiotGlobals;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.converter.GsonConverter;

@Singleton
public class ApiProvider {
    private static final String TAG = ApiProvider.class.getSimpleName();

    private RestAdapter mRestAdapterSecure;
    private RiotApiService riotApiServiceSecure;

    private RestAdapter mRestAdapter;
    private RiotApiService riotApiService;

    private static final Endpoint baseEndPointSecure = Endpoints.newFixedEndpoint("https://");
    private static final Endpoint baseEndPoint = Endpoints.newFixedEndpoint("http://");

    @Singleton
    @Inject
    public ApiProvider() {
        Gson gson = (new GsonBuilder()).registerTypeAdapter(Date.class, new DateTimeTypeAdapter()).create();

        /**
         * Secure HTTPS And WITH KEY
         */
        this.mRestAdapterSecure = (new RestAdapter.Builder())
                .setErrorHandler(ErrorHandler.DEFAULT)
                .setLog(new AndroidLog(TAG))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(baseEndPointSecure)
                .setRequestInterceptor(request ->
                        request.addQueryParam("api_key", RiotGlobals.API_KEY)
                )
                .setConverter(new GsonConverter(gson)).build();

        this.riotApiServiceSecure = this.mRestAdapterSecure.create(RiotApiService.class);

        /**
         * NonSecure HTTPS (HTTP) without key
         */
        this.mRestAdapter = (new RestAdapter.Builder())
                .setErrorHandler(ErrorHandler.DEFAULT)
                .setLog(new AndroidLog(TAG))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(baseEndPoint)
                .setConverter(new GsonConverter(gson)).build();

        this.riotApiService = this.mRestAdapter.create(RiotApiService.class);
    }

    public RiotApiService getRiotApiServiceSecure() {
        return this.riotApiServiceSecure;
    }

    public RiotApiService getRiotApiService() {
        return riotApiService;
    }
}

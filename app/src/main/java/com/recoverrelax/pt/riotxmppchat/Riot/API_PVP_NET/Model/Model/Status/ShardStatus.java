package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Status;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class ShardStatus {

    private String hostname;
    private List<String> locales;
    private String name;
    private String regon_tag;
    private List<Service> services;
    private String slug;

    public ShardStatus() {
    }

    public String getHostname() {
        return hostname;
    }

    public List<String> getLocales() {
        return locales;
    }

    public String getName() {
        return name;
    }

    public String getRegon_tag() {
        return regon_tag;
    }

    public List<Service> getServices() {
        return services;
    }

    public String getSlug() {
        return slug;
    }
}

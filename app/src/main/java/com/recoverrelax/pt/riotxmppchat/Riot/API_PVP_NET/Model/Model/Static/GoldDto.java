package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static;

import org.parceler.Parcel;

@Parcel
public class GoldDto {

    private int base;
    private boolean purchasable;
    private int sell;
    private int total;

    public GoldDto() {

    }

    public int getBase() {
        return base;
    }

    public boolean isPurchasable() {
        return purchasable;
    }

    public int getSell() {
        return sell;
    }

    public int getTotal() {
        return total;
    }
}

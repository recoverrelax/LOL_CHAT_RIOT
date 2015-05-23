package com.recoverrelax.pt.riotxmppchat.MyUtil.drawer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.edgelabs.pt.mybaseapp.R;

public enum ENavDrawer {
    NAVDRAWER_ITEM_0(0, R.string.navdrawer_item0, R.drawable.ic_number1, R.drawable.ic_number1_selected),
    NAVDRAWER_ITEM_1(1, R.string.navdrawer_item1, R.drawable.ic_number1, R.drawable.ic_number1_selected),
//    NAVDRAWER_ITEM_2(2, R.string.navdrawer_item2, R.drawable.ic_number1, R.drawable.ic_number1_selected),
//    NAVDRAWER_ITEM_3(3, R.string.navdrawer_item3, R.drawable.ic_number1, R.drawable.ic_number1_selected),
//    NAVDRAWER_ITEM_4(4, R.string.navdrawer_item4, R.drawable.ic_number1, R.drawable.ic_number1_selected),
    NAVDRAWER_SAME_POSITION(-1);

    private int navDrawerId, navDrawerTitleId, navDrawerDrawableId, navDrawerDrawableTranspId;
    public static final int SIZE = ENavDrawer.values().length;

    ENavDrawer(int navDrawerId, int navDrawerTitleId, int navDrawerDrawableId, int navDrawerDrawableTranspId){
        this.navDrawerId = navDrawerId;
        this.navDrawerTitleId = navDrawerTitleId;
        this.navDrawerDrawableId = navDrawerDrawableId;
        this.navDrawerDrawableTranspId = navDrawerDrawableTranspId;
    }

    ENavDrawer(int navDrawerId){
        this.navDrawerId = navDrawerId;
    }

    @Nullable
    public static ENavDrawer getById(@NonNull int enumId){
        for(ENavDrawer thisEnum: ENavDrawer.values()){
            if(thisEnum.getNavDrawerId() == enumId)
                return thisEnum;
        }
        return null;
    }

    public int getNavDrawerId() {
        return navDrawerId;
    }

    public String getNavDrawerTitle() {
        return MainApplication.getInstance().getResources().getString(navDrawerTitleId);
    }

    public int getNavDrawerDrawableId() {
        return navDrawerDrawableId;
    }

    public int getNavDrawerDrawableTranspId() {
        return navDrawerDrawableTranspId;
    }
}

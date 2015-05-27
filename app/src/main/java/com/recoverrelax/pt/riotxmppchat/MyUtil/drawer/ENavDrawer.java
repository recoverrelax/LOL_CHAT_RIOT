package com.recoverrelax.pt.riotxmppchat.MyUtil.drawer;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.edgelabs.pt.mybaseapp.R;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.FriendMessageListFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.FriendListFragment;

public enum ENavDrawer {
    NAVDRAWER_ITEM_0(0, R.string.navdrawer_item0, R.drawable.ic_number1, R.drawable.ic_number1_selected),
    NAVDRAWER_ITEM_1(1, R.string.navdrawer_item1, R.drawable.ic_number1, R.drawable.ic_number1_selected);
//    NAVDRAWER_ITEM_2(2, R.string.navdrawer_item2, R.drawable.ic_number1, R.drawable.ic_number1_selected),
//    NAVDRAWER_ITEM_3(3, R.string.navdrawer_item3, R.drawable.ic_number1, R.drawable.ic_number1_selected),
//    NAVDRAWER_ITEM_4(4, R.string.navdrawer_item4, R.drawable.ic_number1, R.drawable.ic_number1_selected),

    private int navDrawerId, navDrawerTitleId, navDrawerDrawableId, navDrawerDrawableTranspId;
    public static final int SIZE = ENavDrawer.values().length;

    ENavDrawer(int navDrawerId, int navDrawerTitleId, int navDrawerDrawableId, int navDrawerDrawableTranspId){
        this.navDrawerId = navDrawerId;
        this.navDrawerTitleId = navDrawerTitleId;
        this.navDrawerDrawableId = navDrawerDrawableId;
        this.navDrawerDrawableTranspId = navDrawerDrawableTranspId;
    }

    public static ENavDrawer getById(@NonNull int enumId){
        for(ENavDrawer thisEnum: ENavDrawer.values()){
            if(thisEnum.getNavDrawerId() == enumId)
                return thisEnum;
        }
        return ENavDrawer.NAVDRAWER_ITEM_0;
    }

    public Fragment getFrag() {
        switch(this.navDrawerId){
            case 0:
                return FriendListFragment.newInstance();
            case 1:
                return FriendMessageListFragment.newInstance();
            default:
                return FriendListFragment.newInstance();
        }
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

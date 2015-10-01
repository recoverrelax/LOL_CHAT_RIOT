package com.recoverrelax.pt.riotxmppchat.MyUtil;

import android.view.View;

import java.util.List;

import butterknife.ButterKnife;

public class AppViewUtils {

    @SuppressWarnings("unchecked")
    public static <T extends View> void initAndBindView(List<T> list, View parentView, int ... views){
        for(int viewId: views)
            list.add((T) ButterKnife.findById(parentView, viewId));
    }
}

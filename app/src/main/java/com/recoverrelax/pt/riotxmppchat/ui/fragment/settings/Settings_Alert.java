package com.recoverrelax.pt.riotxmppchat.ui.fragment.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.recoverrelax.pt.riotxmppchat.R;

import butterknife.ButterKnife;

public class Settings_Alert extends Fragment {

    public Settings_Alert(){

    }

    public static Settings_Alert newInstance(){
        return new Settings_Alert();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_alert_fragment, container, false);
        ButterKnife.inject(this, view);
        return view;
    }
}

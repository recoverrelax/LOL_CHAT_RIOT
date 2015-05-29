package com.recoverrelax.pt.riotxmppchat.ui.fragment.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edgelabs.pt.mybaseapp.R;

import butterknife.ButterKnife;

public class Settings_General extends Fragment {

    public Settings_General(){

    }

    public static Settings_General newInstance(){
        return new Settings_General();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_general_fragment, container, false);
        ButterKnife.inject(this, view);
        return view;
    }
}

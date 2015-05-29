package com.recoverrelax.pt.riotxmppchat.ui.fragment.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.edgelabs.pt.mybaseapp.R;
import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.MainApplication;

import java.lang.annotation.RetentionPolicy;

import LolChatRiotDb.NotificationDb;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class Settings_Notification extends Fragment {

    @InjectView(R.id.visible_text)
    CheckBox visible_text;

    @InjectView(R.id.visible_sound)
    CheckBox visible_sound;

    @InjectView(R.id.background_text)
    CheckBox background_text;

    @InjectView(R.id.background_sound)
    CheckBox background_sound;

    private String connectedXmppUser;

    public Settings_Notification(){

    }

    public static Settings_Notification newInstance(){
        return new Settings_Notification();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectedXmppUser = MainApplication.getInstance().getRiotXmppService().getConnectedXmppUser();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_notification_fragment, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(connectedXmppUser!= null) {
            NotificationDb notification = RiotXmppDBRepository.getNotification(connectedXmppUser);

            background_text.setChecked(notification.getTextNotificationOffline());
            background_sound.setChecked(notification.getSoundNotificationOffline());
            visible_text.setChecked(notification.getTextNotificationOnline());
            visible_sound.setChecked(notification.getSoundNotificationOnline());
        }
    }

    @OnClick({R.id.background_text_layout, R.id.background_sound_layout, R.id.visible_text_layout, R.id.visible_sound_layout})
    public void onItemClick(View view){
        int id = view.getId();
        switch(id){
            case R.id.background_text_layout:
                background_text.setChecked(!background_text.isChecked());
                break;
            case R.id.background_sound_layout:
                background_sound.setChecked(!background_sound.isChecked());
                break;
            case R.id.visible_text_layout:
                visible_text.setChecked(!visible_text.isChecked());
                break;
            case R.id.visible_sound_layout:
                visible_sound.setChecked(!visible_sound.isChecked());
                break;
        }
    }

    @OnCheckedChanged({R.id.background_text, R.id.background_sound, R.id.visible_text, R.id.visible_sound})
    public void onChecked(CompoundButton cb, boolean isChecked){
        int id = cb.getId();

        if(connectedXmppUser!= null) {
            NotificationDb notification = RiotXmppDBRepository.getNotification(connectedXmppUser);

            switch (id) {
                case R.id.background_text:
                    notification.setTextNotificationOffline(background_text.isChecked());
                    break;
                case R.id.background_sound:
                    notification.setSoundNotificationOffline(background_sound.isChecked());
                    break;
                case R.id.visible_text:
                    notification.setTextNotificationOnline(visible_text.isChecked());
                    break;
                case R.id.visible_sound:
                    notification.setSoundNotificationOnline(visible_sound.isChecked());
                    break;
            }
            RiotXmppDBRepository.updateNotification(notification);
        }
    }
}

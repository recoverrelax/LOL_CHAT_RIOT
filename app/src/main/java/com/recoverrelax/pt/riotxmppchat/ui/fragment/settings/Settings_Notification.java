package com.recoverrelax.pt.riotxmppchat.ui.fragment.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.recoverrelax.pt.riotxmppchat.MyUtil.storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.MainApplication;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class Settings_Notification extends Fragment {

    @InjectView(R.id.visible_text)
    SwitchCompat visible_text;

    @InjectView(R.id.visible_sound)
    SwitchCompat visible_sound;

    @InjectView(R.id.background_text)
    SwitchCompat background_text;

    @InjectView(R.id.background_sound)
    SwitchCompat background_sound;

    @InjectView(R.id.notificationAlwaysOn)
    SwitchCompat notificationAlwaysOn;

    private String connectedXmppUser;
    private DataStorage dataStorage;

    public Settings_Notification(){

    }

    public static Settings_Notification newInstance(){
        return new Settings_Notification();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectedXmppUser = MainApplication.getInstance().getRiotXmppService().getConnectedXmppUser();
        dataStorage = DataStorage.getInstance();
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

//        if(connectedXmppUser!= null) {
//            notification = RiotXmppDBRepository.getNotification(connectedXmppUser);
//
//            notificationAlwaysOn.setChecked(dataStorage.getNotificationsAlwaysOn());
//            background_text.setChecked(notification.getTextNotificationOffline());
//            background_sound.setChecked(notification.getSoundNotificationOffline());
//            visible_text.setChecked(notification.getTextNotificationOnline());
//            visible_sound.setChecked(notification.getSoundNotificationOnline());
//        }
    }

    @OnClick({R.id.visible_text, R.id.visible_sound, R.id.background_sound, R.id.background_text, R.id.notificationAlwaysOn})
    public void onItemClick(SwitchCompat switchButton){
        int id = switchButton.getId();

//        if(notification != null) {
//
//            switch (id) {
//                case R.id.background_text:
////                    background_text.setChecked(switchButton.isChecked());
//                    notification.setTextNotificationOffline(switchButton.isChecked());
//                    break;
//                case R.id.background_sound:
////                    background_sound.setChecked(switchButton.isChecked());
//                    notification.setSoundNotificationOffline(switchButton.isChecked());
//                    break;
//                case R.id.visible_sound:
////                    visible_sound.setChecked(switchButton.isChecked());
//                    notification.setSoundNotificationOnline(switchButton.isChecked());
//                    break;
//                case R.id.visible_text:
////                    visible_text.setChecked(switchButton.isChecked());
//                    notification.setTextNotificationOnline(switchButton.isChecked());
//                    break;
//
//                case R.id.notificationAlwaysOn:
//                    dataStorage.setNotificationsAlwaysOn(notificationAlwaysOn.isChecked());
//                    break;
//            }
//            RiotXmppDBRepository.updateNotification(notification);
//        }
    }
}

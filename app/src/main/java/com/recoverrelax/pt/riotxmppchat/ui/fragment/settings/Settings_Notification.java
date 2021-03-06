package com.recoverrelax.pt.riotxmppchat.ui.fragment.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Storage.DataStorage;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;

public class Settings_Notification extends Fragment {

    @Bind(R.id.visible_text)
    SwitchCompat visible_text;

    @Bind(R.id.visible_sound)
    SwitchCompat visible_sound;

    @Bind(R.id.background_text)
    SwitchCompat background_text;

    @Bind(R.id.background_sound)
    SwitchCompat background_sound;

    @Bind(R.id.notificationAlwaysOn)
    SwitchCompat notificationAlwaysOn;
    @Inject DataStorage dataStorage;
    private String connectedXmppUser;

    public Settings_Notification() {

    }

    public static Settings_Notification newInstance() {
        return new Settings_Notification();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().getAppComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_notification_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainApplication.getInstance().getRiotXmppService().getRiotConnectionManager().getConnectedUser()
                .subscribe(new Subscriber<String>() {
                    @Override public void onCompleted() {
                    }

                    @Override public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(String connectedXmppUser) {
                        notificationAlwaysOn.setChecked(dataStorage.getNotificationsAlwaysOn());

                        background_text.setChecked(dataStorage.getGlobalNotifBackgroundText());
                        background_sound.setChecked(dataStorage.getGlobalNotifBackgroundSpeech());
                        visible_text.setChecked(dataStorage.getGlobalNotifForegroundText());
                        visible_sound.setChecked(dataStorage.getGlobalNotifForegroundSpeech());
                    }
                });
    }

    @OnClick({R.id.visible_text, R.id.visible_sound, R.id.background_sound, R.id.background_text, R.id.notificationAlwaysOn})
    public void onItemClick(SwitchCompat switchButton) {
        int id = switchButton.getId();

        switch (id) {
            case R.id.background_text:
                dataStorage.setGlobalNotifBackgroundText(background_text.isChecked());
                break;
            case R.id.background_sound:
                dataStorage.setGlobalNotifBackgroundSpeech(background_sound.isChecked());
                break;
            case R.id.visible_text:
                dataStorage.setGlobalNotifForegroundText(visible_text.isChecked());
                break;
            case R.id.visible_sound:
                dataStorage.setGlobalNotifForegroundSpeech(visible_sound.isChecked());
                break;

            case R.id.notificationAlwaysOn:
                dataStorage.setNotificationsAlwaysOn(notificationAlwaysOn.isChecked());
                break;
        }
    }
}

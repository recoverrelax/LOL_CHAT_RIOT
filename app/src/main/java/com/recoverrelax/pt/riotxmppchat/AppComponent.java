package com.recoverrelax.pt.riotxmppchat;

import com.recoverrelax.pt.riotxmppchat.Network.RiotXmppService;
import com.recoverrelax.pt.riotxmppchat.ui.activity.BaseActivity;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.DashBoardFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.FriendListFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.FriendMessageListFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.NotificationCustomDialogFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.PersonalMessageFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.RiotXmppCommunicationFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.settings.SettingsGeneralFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.settings.Settings_Notification;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
        AppModule.class,
        RxImplModule.class,
        NotificationModule.class,
        ManagerModule.class
        }
)
public interface AppComponent {

    /**
     * Core
     */
    void inject(RiotXmppService service);

    /**
     * Activities
     */
    void inject(BaseActivity baseActivity);

    /**
     * Fragments
     */

    void inject(DashBoardFragment frag);
    void inject(FriendListFragment frag);
    void inject(FriendMessageListFragment frag);
    void inject(PersonalMessageFragment fragment);
    void inject(SettingsGeneralFragment settings_generalFragment);
    void inject(Settings_Notification settings_notification);
    void inject(RiotXmppCommunicationFragment riotXmppCommunicationFragment);
    void inject(NotificationCustomDialogFragment notificationCustomDialogFragment);

    void inject(MainApplication mainApplication);


    /**
     * Others
     */
}

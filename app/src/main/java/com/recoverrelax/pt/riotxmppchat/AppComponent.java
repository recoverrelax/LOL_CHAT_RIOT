package com.recoverrelax.pt.riotxmppchat;

import com.recoverrelax.pt.riotxmppchat.Adapter.RecentGameAdapter;
import com.recoverrelax.pt.riotxmppchat.EventHandling.EventHandler;
import com.recoverrelax.pt.riotxmppchat.Network.RiotXmppService;
import com.recoverrelax.pt.riotxmppchat.NotificationCenter.MessageSpeechNotification;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiOperations;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiRealmDataVersion;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiService.RiotApiServiceImpl;
import com.recoverrelax.pt.riotxmppchat.ui.activity.BaseActivity;
import com.recoverrelax.pt.riotxmppchat.ui.activity.LoginActivity;
import com.recoverrelax.pt.riotxmppchat.ui.activity.MessageIconActivity;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.BaseFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.ChatFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.FriendMessageListFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.LiveGameFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.LogFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.NotificationCustomDialogFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.RecentGameFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.ShardFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.settings.SettingsGeneralFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.settings.Settings_Notification;
import com.recoverrelax.pt.riotxmppchat.ui.mvp.dashboard.DashBoardPresenterImpl;
import com.recoverrelax.pt.riotxmppchat.ui.mvp.friendlist.FriendListFragment;
import com.recoverrelax.pt.riotxmppchat.ui.mvp.friendlist.FriendListPresenterImpl;
import com.recoverrelax.pt.riotxmppchat.ui.mvp.friendlist.FriendsListAdapter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(
        modules = {
                AppModule.class
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
    void inject(LoginActivity loginActivity);

    /**
     * Fragments
     */


    void inject(LogFragment frag);

    void inject(FriendListFragment frag);

    void inject(FriendMessageListFragment frag);

    void inject(ChatFragment fragment);

    void inject(SettingsGeneralFragment settings_generalFragment);

    void inject(Settings_Notification settings_notification);

    void inject(NotificationCustomDialogFragment notificationCustomDialogFragment);

    void inject(MainApplication mainApplication);

    void inject(LiveGameFragment liveGameFragment);

    void inject(RiotApiServiceImpl riotApiService);

    void inject(RiotApiOperations riotApiOperations);

    void inject(RiotApiRealmDataVersion riotApiRealmDataVersion);

    void inject(FriendsListAdapter friendsListAdapter);

    void inject(ShardFragment shardFragment);

    void inject(RecentGameFragment recentGameFragment);

    void inject(RecentGameAdapter recentGameAdapter);

    void inject(MessageIconActivity messageIconActivity);

    void inject(BaseActivity baseActivity);

    void inject(BaseFragment baseFragment);

    void inject(EventHandler eventHandler);

    void inject(MessageSpeechNotification messageSpeechNotification);

    void inject(DashBoardPresenterImpl dashBoardPresenter);

    void inject(FriendListPresenterImpl friendListPresenter);


    /**
     * Others
     */
}

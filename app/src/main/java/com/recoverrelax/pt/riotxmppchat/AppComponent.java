package com.recoverrelax.pt.riotxmppchat;

import com.recoverrelax.pt.riotxmppchat.Adapter.RecentGameAdapter;
import com.recoverrelax.pt.riotxmppchat.EventHandling.EventHandler;
import com.recoverrelax.pt.riotxmppchat.Network.RiotXmppService;
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.RiotXmppDashboardImpl;
import com.recoverrelax.pt.riotxmppchat.NotificationCenter.MessageSpeechNotification;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiOperations;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiRealmDataVersion;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiService.RiotApiServiceImpl;
import com.recoverrelax.pt.riotxmppchat.ui.activity.BaseActivity;
import com.recoverrelax.pt.riotxmppchat.ui.activity.DashboardActivityKt;
import com.recoverrelax.pt.riotxmppchat.ui.activity.LoginActivityKt;
import com.recoverrelax.pt.riotxmppchat.ui.activity.MessageIconActivityKt;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.BaseFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.LiveGameFragmentKt;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.LogFragmentKt;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.NotificationCustomDialogFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.RecentGameFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.ShardFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.settings.SettingsGeneralFragment;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.settings.Settings_Notification;
import com.recoverrelax.pt.riotxmppchat.ui.mvp.chat.ChatPresenterImpl;
import com.recoverrelax.pt.riotxmppchat.ui.mvp.dashboard.DashBoardFragmentKt;
import com.recoverrelax.pt.riotxmppchat.ui.mvp.friendlist.FriendListPresenterImpl;
import com.recoverrelax.pt.riotxmppchat.ui.mvp.friendlist.FriendsListAdapter;
import com.recoverrelax.pt.riotxmppchat.ui.mvp.messagelist.MessageListPresenterImpl;

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
    void inject(LoginActivityKt loginActivity);
    void inject(BaseActivity baseActivity);
    void inject(MessageIconActivityKt messageIconActivityKt);

    /**
     * Fragments
     */


    void inject(SettingsGeneralFragment settings_generalFragment);

    void inject(Settings_Notification settings_notification);

    void inject(NotificationCustomDialogFragment notificationCustomDialogFragment);

    void inject(MainApplication mainApplication);

    void inject(RiotApiServiceImpl riotApiService);

    void inject(RiotApiOperations riotApiOperations);

    void inject(RiotApiRealmDataVersion riotApiRealmDataVersion);

    void inject(FriendsListAdapter friendsListAdapter);

    void inject(ShardFragment shardFragment);

    void inject(RecentGameFragment recentGameFragment);

    void inject(RecentGameAdapter recentGameAdapter);

    void inject(BaseFragment baseFragment);
    void inject(LiveGameFragmentKt liveGameFragmentKt);
    void inject(LogFragmentKt logFragmentKt);
    void inject(DashBoardFragmentKt dashBoardFragmentKt);

    void inject(EventHandler eventHandler);
    void inject(DashboardActivityKt dashboardActivityKt);

    void inject(MessageSpeechNotification messageSpeechNotification);

    void inject(FriendListPresenterImpl friendListPresenter);

    void inject(ChatPresenterImpl chatPresenter);

    void inject(MessageListPresenterImpl messageListPresenter);

    void inject(RiotXmppDashboardImpl riotXmppDashboard);


    /**
     * Others
     */
}

package com.recoverrelax.pt.riotxmppchat.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.recoverrelax.pt.riotxmppchat.Adapter.DashBoardLogAdapter;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.NewMessageReceivedNotifyEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.OnFriendPresenceChangedEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.OnNewFriendPlayingEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.EventHandler;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppSnackbarUtils;
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.RiotXmppDashboardImpl;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.HelperModel.ChampionInfo;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiOperations;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiRealmDataVersion;

import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import LolChatRiotDb.InAppLogDb;
import pt.reco.myutil.MyContext;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class DashBoardPresenterImpl implements
        DashBoardPresenter,
        NewMessageReceivedNotifyEvent,
        OnNewFriendPlayingEvent,
        OnFriendPresenceChangedEvent    {

    @Inject
    RiotXmppDashboardImpl dashboardImpl;
    @Inject
    RiotApiOperations riotApiOperations;
    @Inject
    RiotApiRealmDataVersion riotApiRealm;
    @Inject
    EventHandler eventHandler;

    private DashBoardPresenterCallbacks view;
    private DashBoardLogAdapter adapter;
    private CompositeSubscription subscriptions = new CompositeSubscription();
    private Context context;

    public DashBoardPresenterImpl(DashBoardPresenterCallbacks view, Context context){
        this.view = view;
        this.context = context;
        MainApplication.getInstance().getAppComponent().inject(this);
    }

    @Override
    public void getUnreadedMessageCount() {
        subscriptions.add(
                dashboardImpl.getUnreadedMessagesCount()
                        .subscribe(new Subscriber<String>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                view.onUnreadedMessagesFailed("?");
                            }

                            @Override
                            public void onNext(String s) {
                                view.onUnreadedMessagesReady(s);
                            }
                        })
        );
    }

    @Override
    public void getFriendStatusInfo() {
        subscriptions.add(
                dashboardImpl.getFriendStatusInfo()
                .subscribe(new Subscriber<RiotXmppDashboardImpl.FriendStatusInfo>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.onFriendStatusInfoFailed("?", "?", "?");
                    }

                    @Override
                    public void onNext(RiotXmppDashboardImpl.FriendStatusInfo friendStatusInfo) {
                        view.onFriendStatusInfoReady(
                                String.valueOf(friendStatusInfo.getFriendsOnline()),
                                String.valueOf(friendStatusInfo.getFriendsOffline()),
                                String.valueOf(friendStatusInfo.getFriendsPlaying())
                        );
                    }
                })
        );
    }

    @Override
    public void getFreeChampRotationList(int size) {
        subscriptions.add(
                Observable.zip(
                riotApiOperations.getFreeChampRotation(),
                riotApiOperations.getChampionsImage(),
                riotApiRealm.getChampionDDBaseUrl(),
                (freechampIds, champImages, champBaseUrl) -> {

                    List<ChampionInfo> championInfoList = new ArrayList<>();

                    for (Integer champId : freechampIds) {
                        ChampionInfo ci = new ChampionInfo();
                        ci.setChampionId(champId);
                        ci.setChampionName(champImages.get(champId).getChampionName());
                        ci.setChampionImage(champBaseUrl + champImages.get(champId).getChampionImage());

                        championInfoList.add(ci);
                    }
                    return championInfoList.subList(0, size);
                }
        )
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(() -> view.onFreeChampionRotationLoading(true))
                .doOnUnsubscribe(() -> view.onFreeChampionRotationLoading(false))
                .subscribe(new Subscriber<List<ChampionInfo>>() {
                    @Override public void onCompleted() { }

                    @Override public void onError(Throwable e) {
                        view.onFreeChampionRotationFailed();
                    }

                    @Override
                    public void onNext(List<ChampionInfo> championInfos) {
                        view.onFreeChampionRotationReady(championInfos, size);
                    }
                })
        );
    }

    @Override
    public void getLogLast20() {
        subscriptions.add(
                dashboardImpl.getLogLast20List()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<InAppLogDb>>() {
                    @Override public void onCompleted() { }
                    @Override public void onError(Throwable e) { }

                    @Override
                    public void onNext(List<InAppLogDb> inAppLogDbs) {
                        setAdapterItems(inAppLogDbs);
                    }
                })
        );
    }

    @Override
    public void getLogSingleItem() {
        subscriptions.add(
                dashboardImpl.getLogSingleItem()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<InAppLogDb>() {
                    @Override public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        onLogSingleItemFailed();
                    }

                    @Override
                    public void onNext(InAppLogDb inAppLogDb) {
                        setAdapterSingleItem(inAppLogDb);
                    }
                })
        );
    }

    private void onLogSingleItemFailed() {
        AppSnackbarUtils.showSnackBar(
                (Activity)context,
                R.string.service_currently_unavailable,
                AppSnackbarUtils.LENGTH_INDEFINITE,
                R.string.webservice_failed_retry,
                v -> {
                    getLogSingleItem();
                }
        );
    }

    private void setAdapterSingleItem(InAppLogDb inAppLogDb) {
        if (adapter != null && inAppLogDb != null) {
            adapter.setSingleItem(inAppLogDb);
        }
    }

    private void setAdapterItems(List<InAppLogDb> inAppLogDbs){
        if (adapter != null && inAppLogDbs != null) {
            adapter.setItems(inAppLogDbs);
        }
    }

    @Override
    public void getMessageIconDrawable() {
        Drawable drawable = MyContext.getDrawable(context, R.drawable.dashboard_new_message);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable.mutate(), MyContext.getColor(context, R.color.white));
        view.setMessageIconDrawable(drawable);
    }

    @Override
    public void configRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        view.setRecyclerViewLayoutParams(layoutManager);
    }

    @Override
    public void configAdapter(RecyclerView rv) {
        adapter = new DashBoardLogAdapter(context, new ArrayList<>(), rv, R.layout.dashboard_log_layout_white);
        view.setRecyclerViewAdapter(adapter);
    }

    @Override
    public void onResume(){
        eventHandler.registerForNewMessageNotifyEvent(this);
        eventHandler.registerForFriendPlayingEvent(this);
        eventHandler.registerForFriendPresenceChangedEvent(this);
    }

    @Override
    public void onPause(){
        adapter.removeSubscriptions();

        eventHandler.unregisterForNewMessageNotifyEvent(this);
        eventHandler.unregisterForFriendPlayingEvent(this);
        eventHandler.unregisterForFriendPresenceChangedEvent(this);

        subscriptions.clear();
    }

    @Override
    public void onNewMessageNotifyReceived(String userXmppAddress, String username, String message, String buttonLabel) {
        getLogSingleItem();
        getUnreadedMessageCount();
    }

    @Override
    public void onNewFriendPlaying() {
        getFriendStatusInfo();
    }

    @Override
    public void onFriendPresenceChanged(Presence presence) {
        getFriendStatusInfo();
    }
}

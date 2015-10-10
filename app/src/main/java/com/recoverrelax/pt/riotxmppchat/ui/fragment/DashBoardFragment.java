package com.recoverrelax.pt.riotxmppchat.ui.fragment;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.recoverrelax.pt.riotxmppchat.Adapter.DashBoardLogAdapter;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.NewMessageReceivedNotifyEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.OnFriendPresenceChangedEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.OnNewFriendPlayingEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.EventHandler;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.RiotXmppDashboardImpl;
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.RiotXmppRosterImpl;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.HelperModel.ChampionInfo;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiOperations;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiRealmDataVersion;
import com.recoverrelax.pt.riotxmppchat.Widget.FreeChampionRotation;
import com.recoverrelax.pt.riotxmppchat.ui.activity.LogActivity;

import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import LolChatRiotDb.InAppLogDb;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import pt.reco.myutil.MyContext;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashBoardFragment extends BaseFragment implements OnNewFriendPlayingEvent, OnFriendPresenceChangedEvent, NewMessageReceivedNotifyEvent {

    @Bind(R.id.message_number)
    TextView message_number;

    @Bind(R.id.playing_number)
    TextView playing_number;
    @Bind(R.id.online_number)
    TextView online_number;

    @Bind(R.id.offline_number)
    TextView offline_number;

    @Bind(R.id.freeChampRotation1)
    FreeChampionRotation freeChampRotation1;

    @Bind(R.id.messagesIcon)
    ImageView messagesIcon;

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
//
//    @Bind(R.id.progressBar)
//    ProgressBar progressBar;


    private boolean firstTimeOnCreate = true;

    private DashBoardLogAdapter adapter;

    @Inject
    RiotXmppRosterImpl rosterImpl;
    @Inject
    RiotXmppDashboardImpl dashboardImpl;
    @Inject
    EventHandler eventHandler;
    @Inject
    RiotApiOperations riotApiOperations;
    @Inject
    RiotApiRealmDataVersion riotApiRealm;

    private final CompositeSubscription subscriptions = new CompositeSubscription();


    private final String TAG = DashBoardFragment.this.getClass().getSimpleName();

    public DashBoardFragment() {
        // Required empty public constructor
    }

    public static DashBoardFragment newInstance() {
        return new DashBoardFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().getAppComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dashboard_fragment, container, false);

        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
//        showProgressBar(true);
        return view;
    }

    @OnTouch({R.id.dashboard_1, R.id.dashboard_2, R.id.dashboard_3, R.id.dashboard_4})
    public boolean onTileTouch(View view, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            view.setVisibility(View.INVISIBLE);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            view.setVisibility(View.VISIBLE);
        }
        return false;
    }

    @OnTouch(R.id.recyclerView)
    public boolean onLogClick(View view, MotionEvent event){
        startActivity(new Intent(this.getActivity(), LogActivity.class));
        return false;
    }

    @OnClick(R.id.dashboard_1)
    public void OnMessageClick(View view) {
        getBaseActivity().goToMessageListActivity();
    }

    @OnClick({R.id.dashboard_2, R.id.dashboard_3, R.id.dashboard_4})
    public void OnPlayingOnlineOrOfflineClick(View view) {
        getBaseActivity().goToFriendListActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Drawable drawable = MyContext.getDrawable(this.getActivity(), R.drawable.dashboard_new_message);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable.mutate(), MyContext.getColor(this.getActivity(), R.color.white));
        messagesIcon.setBackground(drawable);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setOnTouchListener((view, motionEvent) -> {
            startActivity(new Intent(DashBoardFragment.this.getActivity(), LogActivity.class));
            return true;
        });

        adapter = new DashBoardLogAdapter(getActivity(), new ArrayList<>(), recyclerView, R.layout.dashboard_log_layout_white);
        recyclerView.setAdapter(adapter);

        getWsInfo();
    }

    private void getWsInfo() {
        subscriptions.add(
                Observable.merge(
                        getUnreadedMessageCount(),
                        getFriendStatusInfo(),
                        getFreeChampRotationList(),
                        getLogLast20()
                ).subscribe()
        );
    }

    private Observable<RiotXmppDashboardImpl.FriendStatusInfo> getFriendStatusInfo() {
        return dashboardImpl.getFriendStatusInfo()
                .doOnError(throwable -> {
                    playing_number.setText("?");
                    offline_number.setText("?");
                    online_number.setText("?");
                })
                .doOnNext(friendStatusInfo -> {
                    playing_number.setText(String.valueOf(friendStatusInfo.getFriendsPlaying()));
                    offline_number.setText(String.valueOf(friendStatusInfo.getFriendsOffline()));
                    online_number.setText(String.valueOf(friendStatusInfo.getFriendsOnline()));
                });
    }

    private Observable<String> getUnreadedMessageCount() {
        return dashboardImpl.getUnreadedMessagesCount()
                .doOnError(throwable -> message_number.setText("?"))
                .doOnNext(message_number::setText);
    }

    private Observable<List<InAppLogDb>> getLogLast20() {
        return dashboardImpl.getLogLast20List()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(inAppLogDbs -> {
                    if (adapter != null && inAppLogDbs != null) {
                        adapter.setItems(inAppLogDbs);
                    }
//                    showProgressBar(false);
                });
    }

    private void getLogSingleItem() {
        Subscription subscribe = dashboardImpl.getLogSingleItem()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<InAppLogDb>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(InAppLogDb inAppLogDb) {
                        if (adapter != null && inAppLogDb != null) {
                            adapter.setSingleItem(inAppLogDb);
                        }
                    }
                });
        subscriptions.add(subscribe);
    }

    private Observable<List<ChampionInfo>> getFreeChampRotationList() {
        return Observable.zip(
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

                    return championInfoList;
                }
                )
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(champInfo -> {

                    // just to make sure, take at most the ImageView List Size
                    int size = freeChampRotation1.getGetFreeChamps().size();
                    List<ChampionInfo> championInfos1 = champInfo.subList(0, size);

                    for (int i = 0; i < size; i++) {
                        Glide.with(this)
                                .load(championInfos1.get(i).getChampionImage())
                                .into(freeChampRotation1.getGetFreeChamps().get(i));
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!firstTimeOnCreate) {
            getWsInfo();
        }
//        showProgressBar(false);
        firstTimeOnCreate = false;
        eventHandler.registerForNewMessageNotifyEvent(this);
        eventHandler.registerForFriendPlayingEvent(this);
        eventHandler.registerForFriendPresenceChangedEvent(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        subscriptions.clear();
        eventHandler.unregisterForNewMessageNotifyEvent(this);
        eventHandler.unregisterForFriendPlayingEvent(this);
        eventHandler.unregisterForFriendPresenceChangedEvent(this);
        adapter.removeSubscriptions();
    }

//    private void showProgressBar(boolean state){
//        progressBar.setVisibility(state ? View.VISIBLE : View.GONE);
//    }

    @Override
    public void onNewFriendPlaying() {
        getFriendStatusInfo().subscribe();
    }

    @Override
    public void onFriendPresenceChanged(Presence presence) {
        getFriendStatusInfo().subscribe();
    }

    @Override
    public void onNewMessageNotifyReceived(String userXmppAddress, String username, String message, String buttonLabel) {
        getLogSingleItem();
        getUnreadedMessageCount().subscribe();
    }
}

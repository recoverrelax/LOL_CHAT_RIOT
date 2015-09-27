package com.recoverrelax.pt.riotxmppchat.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.Adapter.DashBoardLogAdapter;
import com.recoverrelax.pt.riotxmppchat.EventHandling.OnFriendPresenceChangedEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.OnNewFriendPlayingEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.OnNewLogEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.OnNewMessageEventEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppMiscUtils;
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.RiotXmppDashboardImpl;
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.RiotXmppRosterImpl;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.activity.BaseActivity;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import LolChatRiotDb.InAppLogDb;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashBoardFragment extends RiotXmppCommunicationFragment {

    @Bind(R.id.dashboard_1)
    LinearLayout dashboard_1;

    @Bind(R.id.dashboard_2)
    LinearLayout dashboard_2;

    @Bind(R.id.dashboard_3)
    LinearLayout dashboard_3;

    @Bind(R.id.dashboard_4)
    LinearLayout dashboard_4;

    @Bind(R.id.message_number)
    TextView message_number;

    @Bind(R.id.playing_number)
    TextView playing_number;
    @Bind(R.id.online_number)
    TextView online_number;

    @Bind(R.id.offline_number)
    TextView offline_number;

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;


    private boolean firstTimeOnCreate = true;

    private DashBoardLogAdapter adapter;

    @Inject RiotXmppRosterImpl rosterImpl;
    @Inject RiotXmppDashboardImpl dashboardImpl;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dashboard_fragment, container, false);

        ButterKnife.bind(this, view);
        MainApplication.getInstance().getAppComponent().inject(this);

        setHasOptionsMenu(true);
        showProgressBar(true);
        return view;
    }

    @OnTouch({R.id.dashboard_1, R.id.dashboard_2, R.id.dashboard_3, R.id.dashboard_4})
    public boolean onTileTouch(View view, MotionEvent event){

        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            view.setVisibility(View.INVISIBLE);
        }
        else if (event.getAction() == MotionEvent.ACTION_UP)
        {
            view.setVisibility(View.VISIBLE);
        }
        return false;
    }

    @OnClick(R.id.dashboard_1)
    public void OnMessageClick(View view){
        if(getActivity() instanceof BaseActivity)
            ((BaseActivity)getActivity()).goToMessageListActivity();
    }

    @OnClick({R.id.dashboard_2, R.id.dashboard_3, R.id.dashboard_4})
    public void OnPlayingOnlineOrOfflineClick(View view){
        if(getActivity() instanceof BaseActivity)
            ((BaseActivity)getActivity()).goToFriendListActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Random random = new Random();
        List<Integer> colorList = AppMiscUtils.getXRamdomMaterialColorT(random, 4, getActivity(), 225);

        dashboard_1.setBackgroundColor(colorList.get(0));
        dashboard_2.setBackgroundColor(colorList.get(1));
        dashboard_3.setBackgroundColor(colorList.get(2));
        dashboard_4.setBackgroundColor(colorList.get(3));

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new DashBoardLogAdapter(getActivity(), new ArrayList<>(), recyclerView);
        recyclerView.setAdapter(adapter);

        getUnreadedMessageCount();
        getFriendStatusInfo();
        getLogLast20();
        getFullFriendsList();
    }

    private void getFullFriendsList() {
        rosterImpl.getFullFriendsList(true);
    }

    private void getFriendStatusInfo() {
        Subscription subscribe = dashboardImpl.getFriendStatusInfo()
                .subscribe(new Subscriber<RiotXmppDashboardImpl.FriendStatusInfo>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        playing_number.setText("?");
                        offline_number.setText("?");
                        online_number.setText("?");
                    }

                    @Override
                    public void onNext(RiotXmppDashboardImpl.FriendStatusInfo friendStatusInfo) {
                        playing_number.setText(String.valueOf(friendStatusInfo.getFriendsPlaying()));
                        offline_number.setText(String.valueOf(friendStatusInfo.getFriendsOffline()));
                        online_number.setText(String.valueOf(friendStatusInfo.getFriendsOnline()));
                    }
                });

        subscriptions.add(subscribe);
    }
    private void getUnreadedMessageCount() {
        Subscription subscribe = dashboardImpl.getUnreadedMessagesCount()
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        message_number.setText("?");
                    }

                    @Override
                    public void onNext(String messageCount) {
                        message_number.setText(messageCount);
                    }
                });

        subscriptions.add(subscribe);
    }
    private void getLogLast20() {
        Subscription subscribe = dashboardImpl.getLogLast20List()
                .subscribe(new Subscriber<List<InAppLogDb>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(List<InAppLogDb> inAppLogDbs) {
                        if (adapter != null && inAppLogDbs != null) {
                            getActivity().runOnUiThread(() -> adapter.setItems(inAppLogDbs));
                        }
                        showProgressBar(false);
                    }
                });
        subscriptions.add(subscribe);
    }
    private void getLogSingleItem() {
        Subscription subscribe = dashboardImpl.getLogSingleItem()
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
                            getActivity().runOnUiThread(() -> adapter.setSingleItem(inAppLogDb));
                        }
                    }
                });
        subscriptions.add(subscribe);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!firstTimeOnCreate) {
            getUnreadedMessageCount();
            getFriendStatusInfo();
            getLogLast20();
        }
        showProgressBar(false);
        firstTimeOnCreate = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        subscriptions.clear();
    }

    @Subscribe
    public void OnNewMessageReceived(final OnNewMessageEventEvent messageReceived) {
        getUnreadedMessageCount();
    }

    @Subscribe
    public void onNewFriendPlaying(final OnNewFriendPlayingEvent event) {
        getFriendStatusInfo();
    }

    private void showProgressBar(boolean state){
        progressBar.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    @Subscribe
    public void OnFriendPresenceChanged(final OnFriendPresenceChangedEvent friendPresence) {
        getFriendStatusInfo();
    }

    @Subscribe
    public void OnNewEventUpdate(OnNewLogEvent event){
        getLogSingleItem();
    }

}

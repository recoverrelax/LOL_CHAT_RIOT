package com.recoverrelax.pt.riotxmppchat.ui.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.Adapter.DashBoardLogAdapter;
import com.recoverrelax.pt.riotxmppchat.Adapter.PersonalMessageAdapter;
import com.recoverrelax.pt.riotxmppchat.EventHandling.FriendList.OnFriendPresenceChangedEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.OnNewFriendPlayingEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.OnNewLogEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.OnNewMessageReceivedEventEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppMiscUtils;
import com.recoverrelax.pt.riotxmppchat.Network.Helper.GlobalImpl;
import com.recoverrelax.pt.riotxmppchat.Network.Helper.RiotXmppDashboardImpl;
import com.recoverrelax.pt.riotxmppchat.Network.Helper.RiotXmppRosterHelper;
import com.recoverrelax.pt.riotxmppchat.Network.Helper.RiotXmppRosterImpl;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
import com.recoverrelax.pt.riotxmppchat.ui.activity.BaseActivity;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import LolChatRiotDb.InAppLogDb;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import rx.Subscriber;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashBoardFragment extends RiotXmppCommunicationFragment implements RiotXmppDashboardImpl.RiotXmppDashboardImplCallbacks {

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


    private boolean firstTimeFragmentStart = true;
    private static final long DELAY_BEFORE_LOAD_ITEMS = 500;
    private boolean firstTimeOnCreate = true;

    private DashBoardLogAdapter adapter;
    private RiotXmppRosterImpl rosterImpl;
    private RiotXmppDashboardImpl dashboardImpl;

    private final String TAG = DashBoardFragment.this.getClass().getSimpleName();

    public DashBoardFragment() {
        // Required empty public constructor
    }

    public static DashBoardFragment newInstance() {
        return new DashBoardFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        ButterKnife.bind(this, view);
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
        /*
        Those who doesnt have click listeners must return true for some reason
         */
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

        dashboardImpl = new RiotXmppDashboardImpl(this);
        rosterImpl = new RiotXmppRosterImpl(null, MainApplication.getInstance().getConnection());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new DashBoardLogAdapter(getActivity(), new ArrayList<>(), recyclerView);
        recyclerView.setAdapter(adapter);

        if (firstTimeFragmentStart) {
            new Handler().postDelayed(() -> {
                dashboardImpl.getUnreadedMessagesCount();
                dashboardImpl.getFriendStatusInfo();
                dashboardImpl.getLogLast20List();
                rosterImpl.getFullFriendsList(true);
            }, DELAY_BEFORE_LOAD_ITEMS);
        } else {
            dashboardImpl.getUnreadedMessagesCount();
            dashboardImpl.getFriendStatusInfo();
            dashboardImpl.getLogLast20List();
        }

        /**
         * Handler to Update the TimeStamp
         * of friends Playing or inQueue
         */
        firstTimeFragmentStart = false;

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!firstTimeOnCreate) {
            dashboardImpl.getUnreadedMessagesCount();
            dashboardImpl.getFriendStatusInfo();
            dashboardImpl.getLogLast20List();
        }
        showProgressBar(false);
        firstTimeOnCreate = false;
    }

    @Subscribe
    public void OnNewMessageReceived(final OnNewMessageReceivedEventEvent messageReceived) {
        dashboardImpl.getUnreadedMessagesCount();
    }

    @Subscribe
    public void onNewFriendPlaying(final OnNewFriendPlayingEvent event){
        dashboardImpl.getFriendStatusInfo();
    }

    public void showProgressBar(boolean state){
        progressBar.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onUnreadedMessageCountRetrieved(String messageCount) {
        getActivity().runOnUiThread(() -> message_number.setText(messageCount));
    }

    @Override
    public void onUnreadedMessageCountFailedReception() {
        onUnreadedMessageCountRetrieved("?");
    }

    @Override
    public void onFriendsStatusInfoRetrieved(RiotXmppDashboardImpl.FriendStatusInfo friendStatusInfo) {
        getActivity().runOnUiThread(() -> {
            playing_number.setText(String.valueOf(friendStatusInfo.getFriendsPlaying()));
            offline_number.setText(String.valueOf(friendStatusInfo.getFriendsOffline()));
            online_number.setText(String.valueOf(friendStatusInfo.getFriendsOnline()));
        });
    }

    @Subscribe
    public void OnFriendPresenceChanged(final OnFriendPresenceChangedEvent friendPresence) {
        getActivity().runOnUiThread(dashboardImpl::getFriendStatusInfo);
    }

    @Override
    public void onFriendsStatusInfoFailedReception() {
        getActivity().runOnUiThread(() -> {
            playing_number.setText("?");
            offline_number.setText("?");
            online_number.setText("?");
        });
    }

    @Override
    public void onLogListReceived(List<InAppLogDb> logList) {
        if(adapter != null && logList != null) {
         getActivity().runOnUiThread(() -> adapter.setItems(logList));
        }
        showProgressBar(false);
    }

    @Override
    public void onLogSingleItemReceived(InAppLogDb log) {
        LOGI("1111", "HERE");
        if(adapter != null && log != null) {
            getActivity().runOnUiThread(() -> adapter.setSingleItem(log));
        }
    }

    @Subscribe
    public void OnNewEventUpdate(OnNewLogEvent event){
        LOGI("111", "HERE2");
        dashboardImpl.getLogSingleItem();
    }

    @Override public void onLogSingleItemFailedReception() {}
    @Override public void onLogListFailedReception() {}

}

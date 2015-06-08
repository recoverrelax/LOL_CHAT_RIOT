package com.recoverrelax.pt.riotxmppchat.ui.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.recoverrelax.pt.riotxmppchat.Adapter.FriendsListAdapter;
import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.EventHandling.FriendList.OnFriendChangedEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.FriendList.OnFriendListFailedLoadingEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.FriendList.OnFriendListLoadedEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.FriendList.OnFriendPresenceChangedEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.OnNewMessageReceivedEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppAndroidUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.MessageNotification;
import com.recoverrelax.pt.riotxmppchat.Network.Helper.RiotXmppRosterImpl;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.Interface.RiotXmppRosterHelper;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
import com.squareup.otto.Subscribe;

import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendListFragment extends BaseFragment {

    private static final long DELAY_BEFORE_LOAD_ITEMS = 500;
    private final String TAG = FriendListFragment.this.getClass().getSimpleName();

    @InjectView(R.id.myFriendsListRecyclerView)
    RecyclerView myFriendsListRecyclerView;

    @InjectView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @InjectView(R.id.progressBarCircularIndeterminate)
    ProgressBar progressBarCircularIndeterminate;

    private boolean firstTimeFragmentStart = true;

    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout.OnRefreshListener swipeRefreshListener;

    /**
     * Adapter
     */
    private FriendsListAdapter adapter;
    private boolean firstTimeOnCreate = true;

    /**
     * Data Loading
     */

    private RiotXmppRosterHelper riotXmppRosterHelper;

    public FriendListFragment() {
        // Required empty public constructor
    }

    public static FriendListFragment newInstance() {
        return new FriendListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.friend_list_fragment, container, false);
        ButterKnife.inject(this, view);
        setHasOptionsMenu(true);
        showProgressBar(true);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        layoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
        myFriendsListRecyclerView.setLayoutManager(layoutManager);

        adapter = new FriendsListAdapter(this, new ArrayList<Friend>(), R.layout.friends_list_recyclerview_child_online, R.layout.friends_list_recyclerview_child_offline, myFriendsListRecyclerView);
        adapter.setOnChildClickListener(new FriendsListAdapter.OnFriendClick() {
            @Override
            public void onFriendClick(String friendUsername, String friendXmppName) {
                AppAndroidUtils.startPersonalMessageActivity(getActivity(), friendUsername, friendXmppName);
            }
        });

        myFriendsListRecyclerView.setAdapter(adapter);
        riotXmppRosterHelper = new RiotXmppRosterImpl(this, MainApplication.getInstance().getConnection());

        swipeRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                riotXmppRosterHelper.getFullFriendsList();
            }
        };
        /**
         * We need this. For some reason, the roster gets time to initialize so it wont return values after some time.
         */

        if (firstTimeFragmentStart) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    riotXmppRosterHelper.getFullFriendsList();
                }
            }, DELAY_BEFORE_LOAD_ITEMS);
        } else
            riotXmppRosterHelper.getFullFriendsList();

        /**
         * Handler to Update the TimeStamp
         * of friends Playing or inQueue
         */
        firstTimeFragmentStart = false;

        swipeRefreshLayout.setOnRefreshListener(swipeRefreshListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        MainApplication.getInstance().getBusInstance().register(this);
        getActivity().invalidateOptionsMenu();

        if (!firstTimeOnCreate)
            riotXmppRosterHelper.getFullFriendsList();
        firstTimeOnCreate = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        MainApplication.getInstance().getBusInstance().unregister(this);
    }

    @Subscribe
    public void onError(OnFriendListFailedLoadingEvent event) {
        LOGI(TAG, "Failed to load friendsList! =(");
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }

    @Subscribe
    public void OnFriendListLoaded(OnFriendListLoadedEvent friendList) {
        if (adapter != null) {
            adapter.setItems(friendList.getFriendList());

            if (swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);

            showProgressBar(false);
            setToolbarTitle(getResources().getString(R.string.friends_online) + " " + adapter.getOnlineFriendsCount());
        }
    }

    @Subscribe
    public void onFriendChanged(OnFriendChangedEvent friend) {
        Friend friend1 = friend.getFriend();

        if (adapter != null) {
            if (friend1 != null) {
                adapter.setFriendChanged(friend1);
            }

            if (swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);

            setToolbarTitle(getResources().getString(R.string.friends_online) + " " + adapter.getOnlineFriendsCount());
        }
    }

    @Subscribe
    public void OnFriendPresenceChanged(final OnFriendPresenceChangedEvent friendPresence) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                riotXmppRosterHelper.getPresenceChanged(friendPresence.getPresence());
            }
        });
    }

    public void showProgressBar(boolean state) {
        progressBarCircularIndeterminate.setVisibility(state ? View.VISIBLE : View.INVISIBLE);

        if (state)
            swipeRefreshLayout.setVisibility(View.GONE);
        else
            swipeRefreshLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);

        boolean hasUnreaded = RiotXmppDBRepository.hasUnreadedMessages(MainApplication.getInstance().getRiotXmppService().getConnectedXmppUser());

        MenuItem item = menu.findItem(R.id.newMessage);

        if (hasUnreaded) {
            item.setVisible(true);
        } else {
            item.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Subscribe
    public void OnNewMessageReceived(final OnNewMessageReceivedEvent messageReceived) {
        final Message message = messageReceived.getMessage();
        final String userXmppAddress = messageReceived.getMessageFrom();

        getActivity().invalidateOptionsMenu();
        final String username = MainApplication.getInstance().getRiotXmppService().getRoster().getEntry(userXmppAddress).getName();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new MessageNotification(FriendListFragment.this.getActivity(), message.getBody(), username, userXmppAddress, MessageNotification.NotificationType.SNACKBAR);
            }
        });
    }

    enum ToolbarState {
        TOOLBAR_STATE_NORMAL,
        TOOLBAR_STATE_TRANSPARENT;

        public boolean isNormal() {
            return this.equals(ToolbarState.TOOLBAR_STATE_NORMAL);
        }

        public boolean isTransparent() {
            return this.equals(ToolbarState.TOOLBAR_STATE_TRANSPARENT);
        }
    }

}

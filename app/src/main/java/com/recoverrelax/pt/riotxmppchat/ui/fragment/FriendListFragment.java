package com.recoverrelax.pt.riotxmppchat.ui.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.edgelabs.pt.mybaseapp.R;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.recoverrelax.pt.riotxmppchat.Adapter.FriendsListAdapter;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils;
import com.recoverrelax.pt.riotxmppchat.Riot.Interface.RiotXmppRosterHelper;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
import com.recoverrelax.pt.riotxmppchat.Network.Helper.RiotXmppRosterImpl;
import com.recoverrelax.pt.riotxmppchat.ui.activity.BaseActivity;
import com.recoverrelax.pt.riotxmppchat.ui.activity.FriendListActivity;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.RosterListener;

import java.util.ArrayList;
import java.util.Collection;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observer;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;
import static com.recoverrelax.pt.riotxmppchat.ui.fragment.FriendMessageListFragment.*;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendListFragment extends BaseFragment implements FriendsListAdapter.OnFriendClick, Observer<RiotXmppRosterImpl.FriendList>, RosterListener, ObservableScrollViewCallbacks {

    private final String TAG = FriendListFragment.this.getClass().getSimpleName();

    @InjectView(R.id.myFriendsListRecyclerView)
    ObservableRecyclerView myFriendsListRecyclerView;

    @InjectView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @InjectView(R.id.progressBarCircularIndeterminate)
    ProgressBarCircularIndeterminate progressBarCircularIndeterminate;

    /**
     * Activity Callback
     */
    private FriendMessageListFragActivityCallback friendMessageListFragActivityCallback;


    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout.OnRefreshListener swipeRefreshListener;

    /**
     * Adapter
     */
    private FriendsListAdapter adapter;

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
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        layoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
        myFriendsListRecyclerView.setLayoutManager(layoutManager);
        myFriendsListRecyclerView.setScrollViewCallbacks(this);

        adapter = new FriendsListAdapter(this, new ArrayList<Friend>(), R.layout.friends_list_recyclerview_child_online, R.layout.friends_list_recyclerview_child_offline, myFriendsListRecyclerView);
        myFriendsListRecyclerView.setAdapter(adapter);

        riotXmppRosterHelper = new RiotXmppRosterImpl(this, MainApplication.getInstance().getConnection());

        swipeRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                riotXmppRosterHelper.getFullFriendsList();
            }
        };
        riotXmppRosterHelper.getFullFriendsList();

        /**
         * Handler to Update the TimeStamp
         * of friends Playing or inQueue
         */
        showProgressBar(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        MainApplication.getInstance().getRiotXmppService().addRosterListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MainApplication.getInstance().getRiotXmppService().removeRosterListener(this);
    }

    @Override
    public void onFriendClick(String friendName, String userXmppAddress) {
        friendMessageListFragActivityCallback.replaceFragment(friendName, userXmppAddress);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            friendMessageListFragActivityCallback = (FriendMessageListFragActivityCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activities containing FriendMessaListFragment must implement FriendMessageListFragActivityCallback");
        }
    }


    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
        LOGI(TAG, "Failed to load friendsList! =(");
        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }

    public void showProgressBar(boolean state) {
        progressBarCircularIndeterminate.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onNext(RiotXmppRosterImpl.FriendList friendList) {
        if (adapter != null) {

            switch (friendList.getOperation()) {
                case FRIEND_ADD:
                    break;
                case FRIEND_CHANGED:
                    Friend friend = friendList.getFriendList().get(0);
                    if (friend != null) {
                        adapter.setFriendChanged(friend);
                    }
                    break;
                case FRIEND_DELETE:
                    break;
                case FRIEND_LIST:
                    adapter.setItems(friendList.getFriendList());

                    swipeRefreshLayout.setOnRefreshListener(swipeRefreshListener);
                    break;
                case FRIEND_UPDATE:
                    break;
            }

            if (swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);
            showProgressBar(false);
        }
        setToolbarTitle(getResources().getString(R.string.friends_online) + " " + adapter.getOnlineFriendsCount());
    }

    @Override
    public void entriesAdded(Collection<String> addresses) {

    }

    @Override
    public void entriesUpdated(Collection<String> addresses) {

    }

    @Override
    public void entriesDeleted(Collection<String> addresses) {

    }

    @Override
    public void presenceChanged(final Presence presence) {
        LogUtils.LOGI(TAG, "Callback called on the activity!");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                riotXmppRosterHelper.getPresenceChanged(presence);
            }
        });
    }

    @Override
    public void onScrollChanged(int scrollY, boolean b, boolean b1) {
//        toolbar.setTranslationY(-scrollY);

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }
}

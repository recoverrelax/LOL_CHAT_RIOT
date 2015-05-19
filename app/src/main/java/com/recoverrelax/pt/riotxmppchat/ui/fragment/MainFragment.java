package com.recoverrelax.pt.riotxmppchat.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edgelabs.pt.mybaseapp.R;
import com.recoverrelax.pt.riotxmppchat.Adapter.FriendsListAdapter;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.Riot.Interface.RosterDataLoaderCallback;
import com.recoverrelax.pt.riotxmppchat.Riot.Interface.RosterHelper;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
import com.recoverrelax.pt.riotxmppchat.Riot.RosterXMPPImpl;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements FriendsListAdapter.OnItemClickAdapter, RosterDataLoaderCallback<List<Friend>> {

    private final String TAG = MainFragment.this.getClass().getSimpleName();

    @InjectView(R.id.myFriendsListRecyclerView)
    RecyclerView myFriendsListRecyclerView;

    @InjectView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView.LayoutManager layoutManager;

    /**
     * Adapter
     */
    private FriendsListAdapter adapter;

    /**
     * Data Loading
     */

    private RosterHelper rosterHelper;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance(){
        return new MainFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        layoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
        myFriendsListRecyclerView.setLayoutManager(layoutManager);

        adapter = new FriendsListAdapter(getActivity(), new ArrayList<Friend>(), R.layout.friends_list_recyclerview_child, this);
        myFriendsListRecyclerView.setAdapter(adapter);

        rosterHelper = new RosterXMPPImpl(MainFragment.this, this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                rosterHelper.getFullFriendsList(MainApplication.getInstance().getXmppConnection().getConnection());
            }
        });
        rosterHelper.getFullFriendsList(MainApplication.getInstance().getXmppConnection().getConnection());
    }

    @Override
    public void onFriendClick(Friend friend) {

    }

    @Override
    public void onFailure(Throwable ex) {
        LOGI(TAG, "Failed to load friendsList! =(");
        if(swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onSuccess(List<Friend> result) {
        LOGI(TAG, "Loaded friendsList! =(");
        if (adapter != null) {
            adapter.setItems(result);
            if(swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void destroyLoader() {

    }
}

package com.recoverrelax.pt.riotxmppchat.ui.fragment;


import android.os.Bundle;
import android.os.Handler;
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
import com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils;
import com.recoverrelax.pt.riotxmppchat.Riot.Interface.RiotXmppRosterHelper;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
import com.recoverrelax.pt.riotxmppchat.Riot.Network.Helper.RiotXmppRiotXmppRosterImpl;
import com.recoverrelax.pt.riotxmppchat.Riot.Network.RiotXmppService;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.RosterListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observer;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements FriendsListAdapter.OnItemClickAdapter, Observer<RiotXmppRiotXmppRosterImpl.FriendList>, RosterListener {

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

    private RiotXmppRosterHelper riotXmppRosterHelper;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance(){
        return new MainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
//
        layoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
        myFriendsListRecyclerView.setLayoutManager(layoutManager);
//
        adapter = new FriendsListAdapter(getActivity(), new ArrayList<Friend>(), R.layout.friends_list_recyclerview_child, this, myFriendsListRecyclerView);
        myFriendsListRecyclerView.setAdapter(adapter);
//
        riotXmppRosterHelper = new RiotXmppRiotXmppRosterImpl(this, MainApplication.getInstance().getConnection());

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                riotXmppRosterHelper.getFullFriendsList();
            }
        });
        riotXmppRosterHelper.getFullFriendsList();

        /**
         * Handler to Update the TimeStamp
         * of friends Playing or inQueue
         */
  }

     @Override public void onFriendClick(Friend friend) {}
    @Override public void onCompleted() {}

    @Override
    public void onError(Throwable e) {
        LOGI(TAG, "Failed to load friendsList! =(");
        if(swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onNext(RiotXmppRiotXmppRosterImpl.FriendList friendList) {
        if (adapter != null) {

            switch(friendList.getOperation()){
                case FRIEND_ADD:
                    break;
                case FRIEND_CHANGED:
                    Friend friend = friendList.getFriendList().get(0);
                    if(friend != null){
                        adapter.setFriendChanged(friend);
                    }

                    break;
                case FRIEND_DELETE:
                    break;
                case FRIEND_LIST:
                    adapter.setItems(friendList.getFriendList());
                    break;
                case FRIEND_UPDATE:
                    break;
            }

            if(swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);
        }
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
}

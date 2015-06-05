package com.recoverrelax.pt.riotxmppchat.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.recoverrelax.pt.riotxmppchat.Adapter.FriendMessageListAdapter;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.OnNewMessageReceivedEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.MessageList.OnMessageListReceivedEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.MessageList.OnMessageSingleItemReceivedEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.Network.Helper.FriendMessageListHelper;
import com.recoverrelax.pt.riotxmppchat.Network.Helper.FriendMessageListImpl;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.FriendListChat;
import com.squareup.otto.Subscribe;

import org.jivesoftware.smack.roster.Roster;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendMessageListFragment extends BaseFragment {

    @InjectView(R.id.friendMessageListRecycler)
    RecyclerView messageRecyclerView;

    @InjectView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @InjectView(R.id.progressBarCircularIndeterminate)
    ProgressBarCircularIndeterminate progressBarCircularIndeterminate;

    private final String TAG = FriendMessageListFragment.this.getClass().getSimpleName();
    private RecyclerView.LayoutManager layoutManager;

    /**
     * Adapter
     */
    private FriendMessageListAdapter adapter;
    private boolean firstTime = true;

    private FriendMessageListHelper friendMessageListHelper;
    private SwipeRefreshLayout.OnRefreshListener swipeRefreshListener;

    public FriendMessageListFragment() {
        // Required empty public constructor
    }

    public static FriendMessageListFragment newInstance() {
        return new FriendMessageListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friend_message_list, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setToolbarTitle(getResources().getString(R.string.message_list_title));
        showProgressBar(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        messageRecyclerView.setLayoutManager(layoutManager);

        adapter = new FriendMessageListAdapter(getActivity(), new ArrayList<FriendListChat>(), R.layout.friend_message_list_child_layout);
        messageRecyclerView.setAdapter(adapter);

        Roster roster = MainApplication.getInstance().getRiotXmppService().getRoster();
        friendMessageListHelper = new FriendMessageListImpl(this, roster);

        swipeRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                friendMessageListHelper.getPersonalMessageList(MainApplication.getInstance().getRiotXmppService().getConnectedXmppUser());
            }
        };
        swipeRefreshLayout.setOnRefreshListener(swipeRefreshListener);
    }

    @Subscribe
    public void OnFriendsListListReceived(OnMessageListReceivedEvent event) {
        adapter.setItems(event.getFriendListChats());

        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);

        showProgressBar(false);
    }

    @Subscribe
    public void OnFriendsListReceived(OnMessageSingleItemReceivedEvent event) {
        adapter.setItem(event.getFriendList());

        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);

        showProgressBar(false);
    }

    public void showProgressBar(boolean state) {
        progressBarCircularIndeterminate.setVisibility(state? View.VISIBLE : View.INVISIBLE);

        if(state)
            swipeRefreshLayout.setVisibility(View.GONE);
        else
            swipeRefreshLayout.setVisibility(View.VISIBLE);
    }


    @Override
    public void onResume() {
        super.onResume();
        MainApplication.getInstance().getBusInstance().register(this);
        friendMessageListHelper.getPersonalMessageList(MainApplication.getInstance().getRiotXmppService().getConnectedXmppUser());
    }

    @Override
    public void onPause() {
        super.onPause();
        MainApplication.getInstance().getBusInstance().unregister(this);
    }

    @Subscribe
    public void OnNewMessageReceived(final OnNewMessageReceivedEvent messageReceived) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(adapter.constains(messageReceived.getMessageFrom())){
                    friendMessageListHelper.getPersonalMessageSingleItem(MainApplication.getInstance().getRiotXmppService().getConnectedXmppUser(),
                            messageReceived.getMessageFrom());
                }else{
                    friendMessageListHelper.getPersonalMessageList(MainApplication.getInstance().getRiotXmppService().getConnectedXmppUser());
                }
            }
        });
    }
}

package com.recoverrelax.pt.riotxmppchat.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.recoverrelax.pt.riotxmppchat.Adapter.FriendMessageListAdapter;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.OnNewMessageReceivedEventEvent;
import com.recoverrelax.pt.riotxmppchat.Network.Helper.FriendMessageListHelper;
import com.recoverrelax.pt.riotxmppchat.Network.Helper.FriendMessageListImpl;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.FriendListChat;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGE;
import static com.recoverrelax.pt.riotxmppchat.Network.Helper.FriendMessageListImpl.FriendMessageListImplCallbacks;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendMessageListFragment extends RiotXmppCommunicationFragment implements FriendMessageListImplCallbacks {

    @InjectView(R.id.friendMessageListRecycler)
    RecyclerView messageRecyclerView;

    @InjectView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @InjectView(R.id.progressBarCircularIndeterminate)
    ProgressBar progressBarCircularIndeterminate;

    private final String TAG = FriendMessageListFragment.this.getClass().getSimpleName();

    /**
     * Adapter
     */
    private FriendMessageListAdapter adapter;

    private FriendMessageListHelper friendMessageListHelper;

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

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        messageRecyclerView.setLayoutManager(layoutManager);

        adapter = new FriendMessageListAdapter(getActivity(), new ArrayList<>(), R.layout.friend_message_list_child_layout);
        messageRecyclerView.setAdapter(adapter);

        friendMessageListHelper = new FriendMessageListImpl(this);

        SwipeRefreshLayout.OnRefreshListener swipeRefreshListener = friendMessageListHelper::getPersonalMessageList;
        swipeRefreshLayout.setOnRefreshListener(swipeRefreshListener);
    }

    public void showProgressBar(boolean state) {
        progressBarCircularIndeterminate.setVisibility(state ? View.VISIBLE : View.INVISIBLE);

        if(state)
            swipeRefreshLayout.setVisibility(View.GONE);
        else
            swipeRefreshLayout.setVisibility(View.VISIBLE);
    }


    @Override
    public void onResume() {
        super.onResume();
//        MainApplication.getInstance().getBusInstance().register(this);
        friendMessageListHelper.getPersonalMessageList();
    }

    @Override
    public void onPause() {
        super.onPause();
//        MainApplication.getInstance().getBusInstance().unregister(this);
    }

    @Subscribe
    public void OnNewMessageReceived(final OnNewMessageReceivedEventEvent messageReceived) {
        getActivity().runOnUiThread(() -> {
            if (adapter.contains(messageReceived.getMessageFrom())) {
                friendMessageListHelper.getPersonalMessageSingleItem(messageReceived.getMessageFrom());
            } else {
                friendMessageListHelper.getPersonalMessageList();
            }
        });
    }

    public void onGeneralThrowableEvent(Throwable e){
        LOGE(TAG, "", e);
    }

    @Override
    public void onFriendsMessageSingleReceived(FriendListChat friendListChat) {
        adapter.setSingleItem(friendListChat);

        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);

        showProgressBar(false);
    }

    @Override
    public void onFriendsMessageSingleFailedReception(Throwable e) {
        onGeneralThrowableEvent(e);
    }

    @Override
    public void onFriendsMessageListReceived(List<FriendListChat> friendListChat) {
        adapter.setItems(friendListChat);

        if (swipeRefreshLayout.isRefreshing())
            swipeRefreshLayout.setRefreshing(false);

        showProgressBar(false);
    }

    @Override
    public void onFriendsMessageListFailedReception(Throwable e) {
        onGeneralThrowableEvent(e);
    }
}

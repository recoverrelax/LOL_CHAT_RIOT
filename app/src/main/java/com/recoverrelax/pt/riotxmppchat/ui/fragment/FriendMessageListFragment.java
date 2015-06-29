package com.recoverrelax.pt.riotxmppchat.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.recoverrelax.pt.riotxmppchat.Adapter.FriendMessageListAdapter;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.OnNewMessageReceivedEventEvent;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppContextUtils;
import com.recoverrelax.pt.riotxmppchat.Network.Helper.FriendMessageListHelper;
import com.recoverrelax.pt.riotxmppchat.Network.Helper.FriendMessageListImpl;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.FriendListChat;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGE;
import static com.recoverrelax.pt.riotxmppchat.Network.Helper.FriendMessageListImpl.FriendMessageListImplCallbacks;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendMessageListFragment extends RiotXmppCommunicationFragment implements FriendMessageListImplCallbacks {

    @Bind(R.id.friendMessageListRecycler)
    RecyclerView messageRecyclerView;

    @Bind(R.id.progressBarCircularIndeterminate)
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
        ButterKnife.bind(this, view);
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

        adapter = new FriendMessageListAdapter(getActivity(), new ArrayList<>());
        messageRecyclerView.setAdapter(adapter);
        adapter.setRowClickListener(
                (view, friendName, friendXmppAddress, cardColor) -> {

                    LinearLayout parentRow = ButterKnife.findById(view, R.id.parent_row);

                    Animation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                    parentRow.setAnimation(alphaAnimation);
                    alphaAnimation.setDuration(200);
                    alphaAnimation.start();

                    AppContextUtils.startPersonalMessageActivityBgColor(getActivity(), friendName, friendXmppAddress, cardColor, null);
                });

        friendMessageListHelper = new FriendMessageListImpl(this);
    }

    public void showProgressBar(boolean state) {
        progressBarCircularIndeterminate.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        friendMessageListHelper.getPersonalMessageList();
    }

    @Override
    public void onPause() {
        super.onPause();
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
        showProgressBar(false);
    }

    @Override
    public void onFriendsMessageSingleFailedReception(Throwable e) {
        onGeneralThrowableEvent(e);
    }

    @Override
    public void onFriendsMessageListReceived(List<FriendListChat> friendListChat) {
        adapter.setItems(friendListChat);
        showProgressBar(false);
    }

    @Override
    public void onFriendsMessageListFailedReception(Throwable e) {
        onGeneralThrowableEvent(e);
    }
}

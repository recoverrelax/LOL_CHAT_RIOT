package com.recoverrelax.pt.riotxmppchat.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.recoverrelax.pt.riotxmppchat.Network.Helper.FriendMessageListImpl;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.FriendListChat;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendMessageListFragment extends RiotXmppCommunicationFragment {

    @Bind(R.id.friendMessageListRecycler)
    RecyclerView messageRecyclerView;

    @Bind(R.id.progressBarCircularIndeterminate)
    ProgressBar progressBarCircularIndeterminate;

    private final String TAG = FriendMessageListFragment.this.getClass().getSimpleName();

    /**
     * Adapter
     */
    private FriendMessageListAdapter adapter;

    private FriendMessageListImpl friendMessageListHelper;
    private final CompositeSubscription subscriptions = new CompositeSubscription();

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

        friendMessageListHelper = new FriendMessageListImpl();
    }

    public void showProgressBar(boolean state) {
        progressBarCircularIndeterminate.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPersonalMessageList();
    }

    private void getPersonalMessageList() {
        Subscription subscribe = friendMessageListHelper.getPersonalMessageList()
                .subscribe(new Subscriber<List<FriendListChat>>() {
                    @Override public void onCompleted() { }
                    @Override public void onError(Throwable e) { }

                    @Override
                    public void onNext(List<FriendListChat> friendListChats) {
                        adapter.setItems(friendListChats);
                        showProgressBar(false);
                    }
                });
        subscriptions.add(subscribe);
    }

    @Override
    public void onPause() {
        super.onPause();
        subscriptions.clear();
    }

    @Subscribe
    public void OnNewMessageReceived(final OnNewMessageReceivedEventEvent messageReceived) {
        getActivity().runOnUiThread(() -> {
            if (adapter.contains(messageReceived.getMessageFrom())) {
                getPersonalMessageSingleItem(messageReceived.getMessageFrom());
            } else {
                getPersonalMessageList();
            }
        });
    }

    private void getPersonalMessageSingleItem(String messageReceived) {
        Subscription subscribe = friendMessageListHelper.getPersonalMessageSingleItem(messageReceived)
                .subscribe(new Subscriber<FriendListChat>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        onGeneralThrowableEvent(e);
                    }

                    @Override
                    public void onNext(FriendListChat friendListChat) {
                        adapter.setSingleItem(friendListChat);
                        showProgressBar(false);
                    }
                });
        subscriptions.add(subscribe);
    }

    public void onGeneralThrowableEvent(Throwable e){
        LOGE(TAG, "", e);
    }
}

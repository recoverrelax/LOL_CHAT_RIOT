package com.recoverrelax.pt.riotxmppchat.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.recoverrelax.pt.riotxmppchat.Adapter.FriendMessageListAdapter;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.NewMessageReceivedNotifyEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.EventHandler;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppContextUtils;
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.FriendMessageListImpl;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.FriendListChat;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.LogUtils.LOGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendMessageListFragment extends BaseFragment implements NewMessageReceivedNotifyEvent{

    @Bind(R.id.friendMessageListRecycler)
    RecyclerView messageRecyclerView;

    @Bind(R.id.progressBarCircularIndeterminate)
    ProgressBar progressBarCircularIndeterminate;

    @Inject
    EventHandler handler;

    private final String TAG = FriendMessageListFragment.this.getClass().getSimpleName();

    /**
     * Adapter
     */
    private FriendMessageListAdapter adapter;

    @Inject FriendMessageListImpl friendMessageListHelper;
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
        View view = inflater.inflate(R.layout.friend_message_list_fragment, container, false);
        ButterKnife.bind(this, view);
        MainApplication.getInstance().getAppComponent().inject(this);

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
                (view, friendName, friendXmppAddress) -> {
//
//                    CardView parentRow = ButterKnife.findById(view, R.id.friends_list_cardview);
//
////                    Animation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
////                    parentRow.setAnimation(alphaAnimation);
////                    alphaAnimation.setDuration(200);
////                    alphaAnimation.start();

//                    ScaleAnimation anim = new ScaleAnimation(1, 5, 1, 5);
//                    anim.setDuration(1000);
//                    anim.setFillAfter(false);
//                    parentRow.setAnimation(anim);
//                    anim.start();

                    AppContextUtils.startPersonalMessageActivity(getActivity(), friendName, friendXmppAddress);
                });
    }

    public void showProgressBar(boolean state) {
        progressBarCircularIndeterminate.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPersonalMessageList();
        handler.registerForNewMessageNotifyEvent(this);
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

        if(adapter != null)
            adapter.removeSubscriptions();

        handler.unregisterForNewMessageNotifyEvent(this);
    }

    private void getPersonalMessageSingleItem(String userXmppAddress) {
        Subscription subscribe = friendMessageListHelper.getPersonalMessage(userXmppAddress)
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

    @Override
    public void onNewMessageNotifyReceived(String userXmppAddress, String username, String message, String buttonLabel) {
        if (adapter.contains(userXmppAddress)) {
            getPersonalMessageSingleItem(userXmppAddress);
        } else {
            getPersonalMessageList();
        }
    }
}

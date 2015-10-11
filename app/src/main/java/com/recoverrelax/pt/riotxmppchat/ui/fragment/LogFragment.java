package com.recoverrelax.pt.riotxmppchat.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.recoverrelax.pt.riotxmppchat.Adapter.DashBoardLogAdapter;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.NewMessageReceivedNotifyEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.EventHandler;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.RiotXmppDashboardImpl;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Widget.AppProgressBar;

import java.util.ArrayList;

import javax.inject.Inject;

import LolChatRiotDb.InAppLogDb;
import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass.
 */
public class LogFragment extends BaseFragment implements NewMessageReceivedNotifyEvent {

    @Bind(R.id.logRecyclerView)
    RecyclerView logRecyclerView;

    @Bind(R.id.progressBar)
    AppProgressBar progressBar;

    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private final String TAG = LogFragment.this.getClass().getSimpleName();
    private DashBoardLogAdapter adapter;

    private CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    EventHandler eventHandler;

    @Inject
    RiotXmppDashboardImpl dashboardImpl;

    public LogFragment() {
        // Required empty public constructor
    }

    public static LogFragment newInstance() {
        return new LogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().getAppComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.log_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        logRecyclerView.setLayoutManager(layoutManager);

        adapter = new DashBoardLogAdapter(getActivity(), new ArrayList<>(), logRecyclerView, R.layout.dashboard_log_layout_black);
        logRecyclerView.setAdapter(adapter);

        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(this::getLogLast20);

        getLogLast20();
    }

    private void getLogLast20() {
        subscriptions.add(
                 dashboardImpl.getLogLast20List()
                .doOnSubscribe(() -> swipeRefreshLayout.setEnabled(true))
                .doOnUnsubscribe(() -> progressBar.setVisibility(View.GONE))
                         .observeOn(AndroidSchedulers.mainThread())
                         .subscribe(inAppLogDbs -> {
                             if (adapter != null && inAppLogDbs != null) {
                                 adapter.setItems(inAppLogDbs);
                             }
                             progressBar.setVisibility(View.VISIBLE);
                             swipeRefreshLayout.setRefreshing(false);
                         })
        );
    }

    private void getLogSingleItem() {

        subscriptions.add(
                dashboardImpl.getLogSingleItem()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<InAppLogDb>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                            }

                            @Override
                            public void onNext(InAppLogDb inAppLogDb) {
                                if (adapter != null && inAppLogDb != null) {
                                    adapter.setSingleItem(inAppLogDb);
                                }
                            }
                        })
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        eventHandler.registerForNewMessageNotifyEvent(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        subscriptions.clear();
        subscriptions.clear();
        eventHandler.unregisterForNewMessageNotifyEvent(this);
        adapter.removeSubscriptions();
    }

    @Override
    public void onNewMessageNotifyReceived(String userXmppAddress, String username, String message, String buttonLabel) {
        getLogSingleItem();
    }
}
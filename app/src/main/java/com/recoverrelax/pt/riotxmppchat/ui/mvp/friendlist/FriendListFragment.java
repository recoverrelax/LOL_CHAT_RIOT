package com.recoverrelax.pt.riotxmppchat.ui.mvp.friendlist;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.recoverrelax.pt.riotxmppchat.Adapter.FriendsListAdapter;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppContextUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FriendListFragment extends BaseFragment
        implements FriendListPresenterCallbacks {

    private final String TAG = FriendListFragment.this.getClass().getSimpleName();

    @Bind(R.id.myFriendsListRecyclerView)
    RecyclerView myFriendsListRecyclerView;

    @Bind(R.id.progressBarCircularIndeterminate)
    ProgressBar progressBarCircularIndeterminate;

    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;


    private FriendListPresenter presenter;

    public FriendListFragment() {}

    public static FriendListFragment newInstance() {
        return new FriendListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().getAppComponent().inject(this);
        presenter = new FriendListPresenterImpl(this, this.getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.friend_list_fragment, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        showProgressBar(true);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        presenter.configRecyclerView();
        presenter.configAdapter(myFriendsListRecyclerView);
        presenter.configSwipeRefresh(swipeRefreshLayout);
    }

    public void showProgressBar(boolean state) {
        progressBarCircularIndeterminate.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
        setOptionsMenu(menu);
    }

    private void setOptionsMenu(Menu menu) {
        presenter.setOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return presenter.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }


    @Override
    public void onFriendListReady(int onlineFriendCount) {
        showProgressBar(false);
        setToolbarTitle(getResources().getString(R.string.friends_online, String.valueOf(onlineFriendCount)));
    }

    @Override
    public void onFriendListFailed(Throwable e) {
        AppContextUtils.printStackTrace(e);
    }

    @Override
    public void onFriendListCompleted() {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onSearchFriendListReady(int onlineFriendCount) {
        showProgressBar(false);
        setToolbarTitle(getResources().getString(R.string.friends_online, String.valueOf(onlineFriendCount)));
    }

    @Override
    public void onSearchFriendListFailed(Throwable e) {
        AppContextUtils.printStackTrace(e);
    }

    @Override
    public void onSearchFriendListCompleted() {}

    @Override
    public void onSingleFriendReady(int onlineFriendCount) {
        setToolbarTitle(getResources().getString(R.string.friends_online, String.valueOf(onlineFriendCount)));
    }

    @Override
    public void onSingleFriendFailed(Throwable e) {
        AppContextUtils.printStackTrace(e);
    }

    @Override
    public void onSingleFriendCompleted() {}

    @Override
    public void setRecyclerViewLayoutParams(RecyclerView.LayoutManager layoutManager) {
        myFriendsListRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void setRecyclerViewAdapter(FriendsListAdapter adapter) {
        myFriendsListRecyclerView.setAdapter(adapter);
    }
}

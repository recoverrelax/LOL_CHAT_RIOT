package com.recoverrelax.pt.riotxmppchat.ui.fragment;


import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.recoverrelax.pt.riotxmppchat.Adapter.FriendsListAdapter;
import com.recoverrelax.pt.riotxmppchat.Storage.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.EventHandling.OnFriendPresenceChangedEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.OnReconnectSuccessListenerEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppContextUtils;
import com.recoverrelax.pt.riotxmppchat.Storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.RiotXmppRosterImpl;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
import com.squareup.otto.Subscribe;

import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendListFragment extends RiotXmppCommunicationFragment implements FriendsListAdapter.OnAdapterChildClick {

    private final String TAG = FriendListFragment.this.getClass().getSimpleName();

    @Bind(R.id.myFriendsListRecyclerView)
    RecyclerView myFriendsListRecyclerView;

    @Bind(R.id.progressBarCircularIndeterminate)
    ProgressBar progressBarCircularIndeterminate;

    private RecyclerView.LayoutManager layoutManager;

    private boolean SHOW_OFFLINE_USERS;
    private SearchView searchView;

    /**
     * Adapter
     */
    private FriendsListAdapter adapter;
    /**
     * Data Loading
     */

    private final CompositeSubscription subscriptions = new CompositeSubscription();


    @Inject RiotXmppRosterImpl riotXmppRosterImpl;

    public FriendListFragment() {
        // Required empty public constructor
    }

    public static FriendListFragment newInstance() {
        return new FriendListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().getAppComponent().inject(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        subscriptions.clear();

        if(adapter != null)
            adapter.removeSubscriptions();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.friend_list_fragment, container, false);

        ButterKnife.bind(this, view);
        SHOW_OFFLINE_USERS = mDataStorage.showOfflineUsers();
        setHasOptionsMenu(true);
        showProgressBar(true);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        layoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
        myFriendsListRecyclerView.setLayoutManager(layoutManager);

        adapter = new FriendsListAdapter(this, new ArrayList<>(), mDataStorage.showOfflineUsers() , myFriendsListRecyclerView);
        adapter.setAdapterClickListener(this);

        myFriendsListRecyclerView.setAdapter(adapter);

        getFullFriendList(SHOW_OFFLINE_USERS);
    }

    @Override
    public void onAdapterFriendClick(String friendUsername, String friendXmppAddress) {
        AppContextUtils.startPersonalMessageActivity(getActivity(), friendUsername, friendXmppAddress);
    }

    @Override
    public void onAdapterFriendOptionsClick(View view, String friendXmppAddress) {
        final PopupMenu popupMenu = new PopupMenu(getActivity(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_friend_options, popupMenu.getMenu());

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(menuItem -> {


            switch (menuItem.getItemId()) {
                case R.id.notifications:
                    FragmentManager manager = getActivity().getFragmentManager();
                    NotificationCustomDialogFragment myDialog = NotificationCustomDialogFragment.newInstance(friendXmppAddress);

                    new Handler().postDelayed(
                            () -> myDialog.show(manager, "baseDialog"),
                            50);
                    break;
                case R.id.other_1:
                        MainApplication.getInstance().getRiotXmppService().getRiotRosterManager().getFriendNameFromXmppAddress(friendXmppAddress)
                                .subscribe(new Subscriber<String>() {
                                    @Override public void onCompleted() { }
                                    @Override public void onError(Throwable e) { }

                                    @Override
                                    public void onNext(String friendName) {
                                        AppContextUtils.startPersonalMessageActivity(FriendListFragment.this.getActivity(), friendName,
                                                friendXmppAddress);
                                    }
                                });
                    break;

                default:
                    break;
            }
            return false;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getFullFriendList(SHOW_OFFLINE_USERS);
    }

    @Subscribe
    public void onReconnect(OnReconnectSuccessListenerEvent event){
        getFullFriendList(SHOW_OFFLINE_USERS);
    }

    @Subscribe
    public void OnFriendPresenceChanged(final OnFriendPresenceChangedEvent friendPresence) {
        getSingleFriend(friendPresence.getPresence());
    }

    public void showProgressBar(boolean state) {
        progressBarCircularIndeterminate.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);

//        final boolean hasUnreaded = MainApplication.getInstance().hasNewMessages();

        riotXmppDBRepository.hasUnreadedMessages()
                .subscribe(new Subscriber<Boolean>() {
                    @Override public void onCompleted() { }
                    @Override public void onError(Throwable e) { }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        setOptionsMenu(menu, aBoolean);
                    }
                });
    }

    private void setOptionsMenu(Menu menu, boolean hasUnreaded) {
        final MenuItem refresh = menu.findItem(R.id.refresh);
        refresh.setVisible(true);

        final MenuItem showHideOffline = menu.findItem(R.id.show_hide_offline);
        showHideOffline.setVisible(true);

        final MenuItem orderAlphabetical = menu.findItem(R.id.order_alphabetically);
        orderAlphabetical.setVisible(true);

        final MenuItem orderStatus = menu.findItem(R.id.order_status);
        orderStatus.setVisible(true);


        final MenuItem newMessage = menu.findItem(R.id.newMessage);

        MenuItem search = menu.findItem(R.id.search);
        search.setVisible(true);

        final MenuItem addFriend = menu.findItem(R.id.addFriend);
        addFriend.setVisible(true);

        /**
         * Setup the SearchView
         */
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = null;
        final boolean[] modifiedOriginal = {false};

        if (search != null) {
            searchView = (SearchView) search.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

            MenuItemCompat.setOnActionExpandListener(search, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    refresh.setVisible(false);
                    addFriend.setVisible(false);
                    newMessage.setVisible(false);
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    if (modifiedOriginal[0])
                        getFullFriendList(SHOW_OFFLINE_USERS);
                    refresh.setVisible(true);
                    addFriend.setVisible(true);
                    if (hasUnreaded) {
                        newMessage.setVisible(true);
                    }
                    return true;
                }
            });

            final EditText et = ButterKnife.findById(searchView, android.support.v7.appcompat.R.id.search_src_text);

            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String s = editable.toString();
                    if (!s.equals("") && !s.equals(" ")) {
                        getSearchFriendsList(s);
                        modifiedOriginal[0] = true;
                    }
                }
            });

            ButterKnife.findById(searchView, android.support.v7.appcompat.R.id.search_close_btn).setOnClickListener(
                    view -> {
                            if (modifiedOriginal[0])
                                getFullFriendList(SHOW_OFFLINE_USERS);
                            et.setText("");
                        }
                    );

        }
    }

    public void getSingleFriend(Presence presence){
        Subscription subscribe = riotXmppRosterImpl.getPresenceChanged(presence)
                .subscribe(new Subscriber<Friend>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Friend friend) {
                        if (adapter != null) {
                            if (friend != null) {
                                adapter.setFriendChanged(friend);
                            }
                            setToolbarTitle(getResources().getString(R.string.friends_online) + " " + adapter.getOnlineFriendsCount());
                        }
                    }
                });
        subscriptions.add(subscribe);
    }

    private void getSearchFriendsList(String s) {
        Subscription subscribe = riotXmppRosterImpl.searchFriendsList(s)
                .subscribe(new Subscriber<List<Friend>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Friend> friends) {
                        if (adapter != null) {
                            adapter.setItems(friends);
                            showProgressBar(false);
                            setToolbarTitle(getResources().getString(R.string.friends_online) + " " + adapter.getOnlineFriendsCount());
                        }
                    }
                });
        subscriptions.add(subscribe);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch(itemId){
            case R.id.refresh:
                getFullFriendList(SHOW_OFFLINE_USERS);
                return true;
            case R.id.addFriend:
                Snackbar
                        .make(this.getActivity().getWindow().getDecorView().getRootView(),
                                getResources().getString(R.string.add_friend_soon), Snackbar.LENGTH_LONG).show();
                return true;
            case R.id.show_hide_offline:
                SHOW_OFFLINE_USERS = !mDataStorage.showOfflineUsers();
                mDataStorage.showOfflineUsers(SHOW_OFFLINE_USERS);
                getFullFriendList(SHOW_OFFLINE_USERS);
                return true;

            case R.id.order_alphabetically:
            case R.id.order_status:
                Snackbar.make(getActivity().getWindow().getDecorView().getRootView(),
                        "Feature Coming soon!", Snackbar.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getFullFriendList(boolean showOffline) {
        Subscription subscribe = riotXmppRosterImpl.getFullFriendsList(showOffline)
                .subscribe(new Subscriber<List<Friend>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Friend> friends) {
                        if (adapter != null) {
                            adapter.setItems(friends);
                            showProgressBar(false);
                            setToolbarTitle(getResources().getString(R.string.friends_online) + " " + adapter.getOnlineFriendsCount());
                        }
                    }
                });

        subscriptions.add(subscribe);
    }
}

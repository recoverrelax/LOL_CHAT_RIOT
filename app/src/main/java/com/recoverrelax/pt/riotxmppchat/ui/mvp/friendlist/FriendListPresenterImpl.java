package com.recoverrelax.pt.riotxmppchat.ui.mvp.friendlist;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.os.Handler;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.OnFriendPresenceChangedEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.EventHandler;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppContextUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppSnackbarUtils;
import com.recoverrelax.pt.riotxmppchat.Network.Manager.RiotRosterManager;
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.RiotXmppRosterImpl;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
import com.recoverrelax.pt.riotxmppchat.Storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.NotificationCustomDialogFragment;

import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class FriendListPresenterImpl implements
        FriendListPresenter, OnFriendPresenceChangedEvent, FriendsListAdapter.OnAdapterChildClick {

    @Inject RiotXmppRosterImpl riotXmppRosterImpl;
    @Inject RiotRosterManager rosterManager;
    @Inject EventHandler handler;
    @Inject DataStorage mDataStorage;

    private FriendsListAdapter adapter;
    private FriendListPresenterCallbacks view;
    private CompositeSubscription subscriptions = new CompositeSubscription();
    private Context context;

    private boolean SHOW_OFFLINE_USERS;
    private int SORT_MODE = RiotXmppRosterImpl.SORT_MODE_STATUS;

    public FriendListPresenterImpl(FriendListPresenterCallbacks view, Context context) {
        this.view = view;
        this.context = context;
        MainApplication.getInstance().getAppComponent().inject(this);

        SHOW_OFFLINE_USERS = mDataStorage.showOfflineUsers();
        SORT_MODE = mDataStorage.getSortMode();
    }

    @Override
    public void getFullFriendList(boolean showOffline, int sortMode) {

        if (!rosterManager.isConnected())
            return;

        subscriptions.add(

                riotXmppRosterImpl.getFullFriendsList(showOffline, sortMode)
                        .flatMap(riotXmppRosterImpl::updateFriendListWithChampAndProfileUrl)
                        .subscribe(new Subscriber<List<Friend>>() {
                            @Override
                            public void onCompleted() {
                                view.onFriendListCompleted();
                            }

                            @Override
                            public void onError(Throwable e) {
                                view.onFriendListFailed(e);
                            }

                            @Override
                            public void onNext(List<Friend> friends) {
                                setAdapterItems(friends);
                                view.onFriendListReady(adapter.getOnlineFriendsCount());
                            }
                        })
        );
    }

    @Override
    public void getSearchFriendsList(String s) {

        if (!rosterManager.isConnected())
            return;

        subscriptions.add(
                riotXmppRosterImpl.searchFriendsList(s)
                        .flatMap(riotXmppRosterImpl::updateFriendListWithChampAndProfileUrl)
                        .subscribeOn(Schedulers.computation())
                        .subscribe(new Subscriber<List<Friend>>() {
                            @Override
                            public void onCompleted() {
                                view.onSearchFriendListCompleted();
                            }

                            @Override
                            public void onError(Throwable e) {
                                view.onSearchFriendListFailed(e);
                            }

                            @Override
                            public void onNext(List<Friend> friends) {
                                setAdapterItems(friends);
                                view.onSearchFriendListReady(adapter.getOnlineFriendsCount());
                            }
                        })
        );
    }

    public void getSingleFriend(Presence presence) {

        if (!rosterManager.isConnected())
            return;

        subscriptions.add(
                riotXmppRosterImpl.getPresenceChanged(presence)
                        .flatMap(riotXmppRosterImpl::updateFriendWithChampAndProfileUrl)
                        .subscribe(new Subscriber<Friend>() {
                            @Override
                            public void onCompleted() {
                                view.onSingleFriendCompleted();
                            }

                            @Override
                            public void onError(Throwable e) {
                                view.onSingleFriendFailed(e);
                            }

                            @Override
                            public void onNext(Friend friend) {
                                setAdapterSingleItem(friend);
                                view.onSingleFriendReady(adapter.getOnlineFriendsCount());
                            }
                        })
        );
    }

    @Override
    public void configRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false);
        view.setRecyclerViewLayoutParams(layoutManager);
    }

    @Override
    public void configAdapter(RecyclerView recyclerView) {
        adapter = new FriendsListAdapter(context, new ArrayList<>(), mDataStorage.showOfflineUsers(), recyclerView);
        adapter.setAdapterClickListener(this);

        view.setRecyclerViewAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.refresh:
                getFullFriendList(SHOW_OFFLINE_USERS, SORT_MODE);
                return true;
            case R.id.addFriend:
                AppSnackbarUtils.showSnackBar((Activity) context, R.string.add_friend_soon, AppSnackbarUtils.LENGTH_LONG);
                return true;
            case R.id.show_hide_offline:
                SHOW_OFFLINE_USERS = !mDataStorage.showOfflineUsers();
                mDataStorage.showOfflineUsers(SHOW_OFFLINE_USERS);
                getFullFriendList(SHOW_OFFLINE_USERS, SORT_MODE);
                return true;

            case R.id.order_alphabetically:
                SORT_MODE = RiotXmppRosterImpl.SORT_MODE_NAME;
                mDataStorage.setSortMode(SORT_MODE);
                getFullFriendList(SHOW_OFFLINE_USERS, SORT_MODE);
                return true;
            case R.id.order_status:
                SORT_MODE = RiotXmppRosterImpl.SORT_MODE_STATUS;
                mDataStorage.setSortMode(SORT_MODE);
                getFullFriendList(SHOW_OFFLINE_USERS, SORT_MODE);
                return true;
            default:
                return true;
        }
    }

    @Override
    public void setOptionsMenu(Menu menu) {
        final MenuItem refresh = menu.findItem(R.id.refresh);
        refresh.setVisible(false);

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

        SearchView searchView;
        /**
         * Setup the SearchView
         */
        SearchManager searchManager = (SearchManager) context.getSystemService(Context.SEARCH_SERVICE);

        final boolean[] modifiedOriginal = {false};

        searchView = (SearchView) search.getActionView();

        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(((Activity) context).getComponentName()));

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
                        getFullFriendList(SHOW_OFFLINE_USERS, SORT_MODE);
                    ((Activity) context).invalidateOptionsMenu();
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
                            getFullFriendList(SHOW_OFFLINE_USERS, SORT_MODE);
                        et.setText("");
                    }
            );

        }
    }

    @Override
    public void configSwipeRefresh(SwipeRefreshLayout swipeRefreshLayout) {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (rosterManager.isConnected())
                getFullFriendList(SHOW_OFFLINE_USERS, SORT_MODE);
            else
                swipeRefreshLayout.setRefreshing(false);
        });
    }

    public void setAdapterItems(List<Friend> friends) {
        if (adapter != null)
            adapter.setItems(friends);
    }

    public void setAdapterSingleItem(Friend friend) {
        if (adapter != null)
            if (friend != null)
                adapter.setFriendChanged(friend);
    }


    @Override
    public void onResume() {
        handler.registerForFriendPresenceChangedEvent(this);
        getFullFriendList(SHOW_OFFLINE_USERS, SORT_MODE);
    }

    @Override
    public void onPause() {
        handler.unregisterForFriendPresenceChangedEvent(this);

        if (adapter != null)
            adapter.removeSubscriptions();

        subscriptions.clear();
    }

    @Override
    public void onFriendPresenceChanged(Presence presence) {
        getSingleFriend(presence);
    }

    @Override
    public void onAdapterFriendClick(String friendUsername, String friendXmppAddress) {
        AppContextUtils.startChatActivity(context, friendUsername, friendXmppAddress);
    }

    @Override
    public void onAdapterFriendOptionsClick(View view, String friendXmppAddress, String friendUsername, boolean isPlaying) {
        final PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_friend_options, popupMenu.getMenu());

        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(menuItem -> {


            switch (menuItem.getItemId()) {
                case R.id.notifications:
                    FragmentManager manager = ((Activity) context).getFragmentManager();
                    NotificationCustomDialogFragment myDialog = NotificationCustomDialogFragment.newInstance(friendXmppAddress);

                    new Handler().postDelayed(
                            () -> myDialog.show(manager, "baseDialog"),
                            50);
                    break;
                case R.id.other_1:
                    rosterManager.getFriendNameFromXmppAddress(friendXmppAddress)
                            .subscribe(friendName -> {
                                AppContextUtils.startChatActivity(context, friendName,
                                        friendXmppAddress);
                            });
                    break;

//                case R.id.current_game:
//
//                    if (MainApplication.getInstance().isRecentGameEnabled) {
//                        Intent intent = new Intent(this.getActivity(), LiveGameActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        intent.putExtra(LiveGameActivity.FRIEND_XMPP_ADDRESS_INTENT, friendXmppAddress);
//                        intent.putExtra(LiveGameActivity.FRIEND_XMPP_USERNAME_INTENT, friendUsername);
//                        startActivity(intent);
//                        AppContextUtils.overridePendingTransitionBackAppDefault(this.getActivity());
//                    } else
//                        AppContextUtils.showSnackbar(this.getBaseActivity(), R.string.feature_coming, Snackbar.LENGTH_LONG, null);
//
//                    break;

//                case R.id.recent_game:
//
//                    if (MainApplication.getInstance().isLiveGameEnabled) {
//                        Intent intent2 = new Intent(this.getActivity(), RecentGameActivity.class);
//                        intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        intent2.putExtra(RecentGameActivity.FRIEND_XMPP_ADDRESS_INTENT, friendXmppAddress);
//                        intent2.putExtra(RecentGameActivity.FRIEND_XMPP_USERNAME_INTENT, friendUsername);
//                        startActivity(intent2);
//                        AppContextUtils.overridePendingTransitionBackAppDefault(this.getActivity());
//                    } else
//                        AppContextUtils.showSnackbar(this.getBaseActivity(), R.string.feature_coming, Snackbar.LENGTH_LONG, null);
//                    break;
                default:
                    break;
            }
            return false;
        });
    }
}

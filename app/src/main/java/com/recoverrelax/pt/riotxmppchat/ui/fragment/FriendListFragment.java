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
import com.recoverrelax.pt.riotxmppchat.EventHandling.FriendList.OnFriendPresenceChangedEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.FriendList.OnReconnectListenerEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppContextUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.storage.DataStorage;
import com.recoverrelax.pt.riotxmppchat.Network.Helper.RiotXmppRosterImpl;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.Interface.RiotXmppRosterHelper;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.recoverrelax.pt.riotxmppchat.Network.Helper.RiotXmppRosterImpl.RiotXmppRosterImplCallbacks;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendListFragment extends RiotXmppCommunicationFragment implements FriendsListAdapter.OnAdapterChildClick, RiotXmppRosterImplCallbacks {

    private static final long DELAY_BEFORE_LOAD_ITEMS = 500;
    private final String TAG = FriendListFragment.this.getClass().getSimpleName();

    @InjectView(R.id.myFriendsListRecyclerView)
    RecyclerView myFriendsListRecyclerView;

    @InjectView(R.id.progressBarCircularIndeterminate)
    ProgressBar progressBarCircularIndeterminate;

    private boolean firstTimeFragmentStart = true;

    private RecyclerView.LayoutManager layoutManager;
    private DataStorage mDataStorage;
    private boolean SHOW_OFFLINE_USERS;

    /**
     * Adapter
     */
    private FriendsListAdapter adapter;
    private boolean firstTimeOnCreate = true;
    /**
     * Data Loading
     */

    private RiotXmppRosterHelper riotXmppRosterHelper;

    public FriendListFragment() {
        // Required empty public constructor
    }

    public static FriendListFragment newInstance() {
        return new FriendListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataStorage = DataStorage.getInstance();
        SHOW_OFFLINE_USERS = mDataStorage.showOfflineUsers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.friend_list_fragment, container, false);

        ButterKnife.inject(this, view);
        setHasOptionsMenu(true);
        showProgressBar(true);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        layoutManager = new GridLayoutManager(getActivity(), 2, LinearLayoutManager.VERTICAL, false);
        myFriendsListRecyclerView.setLayoutManager(layoutManager);

        adapter = new FriendsListAdapter(this, new ArrayList<>(), R.layout.friends_list_recyclerview_child_online, R.layout.friends_list_recyclerview_child_offline, myFriendsListRecyclerView);
        adapter.setAdapterClickListener(this);

        myFriendsListRecyclerView.setAdapter(adapter);
        riotXmppRosterHelper = new RiotXmppRosterImpl(this, MainApplication.getInstance().getConnection());

        /**
         * We need this. For some reason, the roster gets time to initialize so it wont return values after some time.
         */

        if (firstTimeFragmentStart) {
            new Handler().postDelayed(
                    () -> getFullFriendList(SHOW_OFFLINE_USERS), DELAY_BEFORE_LOAD_ITEMS
            );
        } else
            getFullFriendList(SHOW_OFFLINE_USERS);

        /**
         * Handler to Update the TimeStamp
         * of friends Playing or inQueue
         */
        firstTimeFragmentStart = false;
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
            switch(menuItem.getItemId()){
                case R.id.notifications:
                    FragmentManager manager = getActivity().getFragmentManager();
                    String connectedXmppUser = MainApplication.getInstance().getRiotXmppService().getConnectedXmppUser();
                    NotificationCustomDialogFragment myDialog = NotificationCustomDialogFragment.newInstance(friendXmppAddress, connectedXmppUser);
                    myDialog.show(manager, "baseDialog");
                    break;
                case R.id.other_1:
                    break;
                case R.id.other_2:
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
        if (!firstTimeOnCreate)
            getFullFriendList(SHOW_OFFLINE_USERS);
        firstTimeOnCreate = false;
    }

    @Subscribe
    public void onReconnect(OnReconnectListenerEvent event){
        getFullFriendList(SHOW_OFFLINE_USERS);
    }

    @Subscribe
    public void OnFriendPresenceChanged(final OnFriendPresenceChangedEvent friendPresence) {
        getActivity().runOnUiThread(
                () -> riotXmppRosterHelper.getPresenceChanged(friendPresence.getPresence(), SHOW_OFFLINE_USERS)
        );
    }

    public void showProgressBar(boolean state) {
        progressBarCircularIndeterminate.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);

        final boolean hasUnreaded = MainApplication.getInstance().hasNewMessages();

        final MenuItem refresh = menu.findItem(R.id.refresh);
        refresh.setVisible(true);

        final MenuItem showHideOffline = menu.findItem(R.id.show_hide_offline);
        showHideOffline.setVisible(true);


        final MenuItem newMessage = menu.findItem(R.id.newMessage);

        MenuItem search = menu.findItem(R.id.search);
        search.setVisible(true);

        final MenuItem addFriend = menu.findItem(R.id.addFriend);
        addFriend.setVisible(true);

        /**
         * Setup the SearchView
         */
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = null;
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
                    if(hasUnreaded) {
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
                        riotXmppRosterHelper.searchFriendsList(s);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getFullFriendList(boolean showOffline) {
        riotXmppRosterHelper.getFullFriendsList(showOffline);
    }

    @Override
    public void onFullFriendsListReceived(List<Friend> friendList) {
        if (adapter != null) {
            adapter.setItems(friendList);
            showProgressBar(false);
            setToolbarTitle(getResources().getString(R.string.friends_online) + " " + adapter.getOnlineFriendsCount());
        }
    }

    @Override public void onFullFriendsListFailedReception() { }

    @Override
    public void onSearchedFriendListReceived(List<Friend> friendList) {
        onFullFriendsListReceived(friendList);
    }

    @Override public void onSearchedFriendListFailedReception() { }

    @Override
    public void onSingleFriendReceived(Friend friend) {
        if (adapter != null) {
            if (friend != null) {
                adapter.setFriendChanged(friend);
            }
            setToolbarTitle(getResources().getString(R.string.friends_online) + " " + adapter.getOnlineFriendsCount());
        }
        /**
         * TODO: there is a bug here, review the adaptar fag
         */
    }

    @Override public void onSingleFriendFailedReception() { }

}

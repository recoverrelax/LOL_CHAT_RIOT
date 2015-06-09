package com.recoverrelax.pt.riotxmppchat.ui.fragment;


import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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
import com.recoverrelax.pt.riotxmppchat.EventHandling.FriendList.OnFriendChangedEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.FriendList.OnFriendListFailedLoadingEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.FriendList.OnFriendListLoadedEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.FriendList.OnFriendPresenceChangedEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.OnNewMessageReceivedEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppAndroidUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.SnackBarNotification;
import com.recoverrelax.pt.riotxmppchat.Network.Helper.RiotXmppRosterImpl;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.Interface.RiotXmppRosterHelper;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
import com.squareup.otto.Subscribe;

import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendListFragment extends BaseFragment {

    private static final long DELAY_BEFORE_LOAD_ITEMS = 500;
    private final String TAG = FriendListFragment.this.getClass().getSimpleName();

    @InjectView(R.id.myFriendsListRecyclerView)
    RecyclerView myFriendsListRecyclerView;

    @InjectView(R.id.progressBarCircularIndeterminate)
    ProgressBar progressBarCircularIndeterminate;

    private boolean firstTimeFragmentStart = true;

    private RecyclerView.LayoutManager layoutManager;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 ?
                R.layout.friend_list_fragment_no_animation : R.layout.friend_list_fragment, container, false);

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

        adapter = new FriendsListAdapter(this, new ArrayList<Friend>(), R.layout.friends_list_recyclerview_child_online, R.layout.friends_list_recyclerview_child_offline, myFriendsListRecyclerView);
        adapter.setOnChildClickListener(new FriendsListAdapter.OnFriendClick() {
            @Override
            public void onFriendClick(String friendUsername, String friendXmppAddress) {
                AppAndroidUtils.startPersonalMessageActivity(getActivity(), friendUsername, friendXmppAddress);
            }
        });

        myFriendsListRecyclerView.setAdapter(adapter);
        riotXmppRosterHelper = new RiotXmppRosterImpl(this, MainApplication.getInstance().getConnection());

        /**
         * We need this. For some reason, the roster gets time to initialize so it wont return values after some time.
         */

        if (firstTimeFragmentStart) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    riotXmppRosterHelper.getFullFriendsList();
                }
            }, DELAY_BEFORE_LOAD_ITEMS);
        } else
            riotXmppRosterHelper.getFullFriendsList();

        /**
         * Handler to Update the TimeStamp
         * of friends Playing or inQueue
         */
        firstTimeFragmentStart = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainApplication.getInstance().getBusInstance().register(this);
        getActivity().invalidateOptionsMenu();

        if (!firstTimeOnCreate)
            riotXmppRosterHelper.getFullFriendsList();
        firstTimeOnCreate = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        MainApplication.getInstance().getBusInstance().unregister(this);
    }

    @Subscribe
    public void onError(OnFriendListFailedLoadingEvent event) {
        LOGI(TAG, "Failed to load friendsList! =(");
    }

    @Subscribe
    public void OnFriendListLoaded(OnFriendListLoadedEvent friendList) {
        if (adapter != null) {
            adapter.setItems(friendList.getFriendList());
            LOGI("TAGS", "Refreshed");
            showProgressBar(false);
            setToolbarTitle(getResources().getString(R.string.friends_online) + " " + adapter.getOnlineFriendsCount());
        }
    }

    @Subscribe
    public void onFriendChanged(OnFriendChangedEvent friend) {
        Friend friend1 = friend.getFriend();

        if (adapter != null) {
            if (friend1 != null) {
                adapter.setFriendChanged(friend1);
            }

            setToolbarTitle(getResources().getString(R.string.friends_online) + " " + adapter.getOnlineFriendsCount());
        }
    }

    @Subscribe
    public void OnFriendPresenceChanged(final OnFriendPresenceChangedEvent friendPresence) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                riotXmppRosterHelper.getPresenceChanged(friendPresence.getPresence());
            }
        });
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
                        riotXmppRosterHelper.getFullFriendsList();
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
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (modifiedOriginal[0])
                                riotXmppRosterHelper.getFullFriendsList();
                            et.setText("");
                        }
                    });

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int[] itemId = {item.getItemId()};

        switch(itemId[0]){
            case R.id.refresh:
                riotXmppRosterHelper.getFullFriendsList();
                return true;

            case R.id.addFriend:
                Snackbar
                        .make(this.getActivity().getWindow().getDecorView().getRootView(),
                                getResources().getString(R.string.add_friend_soon), Snackbar.LENGTH_LONG).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Subscribe
    public void OnNewMessageReceived(final OnNewMessageReceivedEvent messageReceived) {
        final Message message = messageReceived.getMessage();
        final String userXmppAddress = messageReceived.getMessageFrom();
        getActivity().invalidateOptionsMenu();
        final String username = MainApplication.getInstance().getRiotXmppService().getRoster().getEntry(userXmppAddress).getName();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new SnackBarNotification(FriendListFragment.this.getActivity(), username + " says: \n" + message.getBody(), "PM",
                        username, messageReceived.getMessageFrom());
            }
        });
    }

}

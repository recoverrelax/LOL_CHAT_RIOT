package com.recoverrelax.pt.riotxmppchat.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.recoverrelax.pt.riotxmppchat.Adapter.RecentGameAdapter;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.activity.BaseActivity;
import com.recoverrelax.pt.riotxmppchat.ui.activity.CurrentGameActivity;

import java.util.ArrayList;
import java.util.List;

import LolChatRiotDb.InAppLogDb;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecentGameFragment extends BaseFragment {

    private final String TAG = RecentGameFragment.this.getClass().getSimpleName();
    private String friendXmppAddress;
    private String friendUsername;

    @Bind(R.id.recentGameParentLayout)
    FrameLayout recentGameParentLayout;

    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.myRecyclerView)
    RecyclerView myRecyclerView;

    /**
     * Adapter
     */
    private RecentGameAdapter adapter;

    public RecentGameFragment() {
        // Required empty public constructor
    }

    public static RecentGameFragment newInstance(String friendXmppAddress, String friendUsername) {
        RecentGameFragment frag = new RecentGameFragment();

        Bundle args = new Bundle();
        args.putString(CurrentGameActivity.FRIEND_XMPP_ADDRESS_INTENT, friendXmppAddress);
        args.putString(CurrentGameActivity.FRIEND_XMPP_USERNAME_INTENT, friendUsername);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        MainApplication.getInstance().getAppComponent().inject(this);

        if (savedInstanceState == null) {
            Bundle args = getArguments();
            if (args != null) {
                friendXmppAddress = args.getString(CurrentGameActivity.FRIEND_XMPP_ADDRESS_INTENT);
                friendUsername = args.getString(CurrentGameActivity.FRIEND_XMPP_USERNAME_INTENT);
            }
        } else {
            friendXmppAddress = (String) savedInstanceState.getSerializable(CurrentGameActivity.FRIEND_XMPP_ADDRESS_INTENT);
            friendUsername = (String) savedInstanceState.getSerializable(CurrentGameActivity.FRIEND_XMPP_USERNAME_INTENT);
        }
        ((BaseActivity) getActivity()).setTitle(getActivity().getResources().getString(R.string.recent_game__fragment_title) + " " + friendUsername);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.recent_game_fragment, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getActivity(), LinearLayoutManager.VERTICAL, false);
        myRecyclerView.setLayoutManager(layoutManager);

        List<InAppLogDb> dumbList = new ArrayList<>();
        dumbList.add(new InAppLogDb());
        dumbList.add(new InAppLogDb());
        dumbList.add(new InAppLogDb());
        dumbList.add(new InAppLogDb());
        dumbList.add(new InAppLogDb());
        dumbList.add(new InAppLogDb());
        dumbList.add(new InAppLogDb());
        dumbList.add(new InAppLogDb());
        dumbList.add(new InAppLogDb());

        adapter = new RecentGameAdapter(this.getActivity(), dumbList);
        myRecyclerView.setAdapter(adapter);

//        long userId_riotApi = AppXmppUtils.getSummonerIdByXmppAddress(friendXmppAddress);
    }
}

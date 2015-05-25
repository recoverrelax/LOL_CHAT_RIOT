package com.recoverrelax.pt.riotxmppchat.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.edgelabs.pt.mybaseapp.R;
import com.recoverrelax.pt.riotxmppchat.Adapter.FriendMessageListAdapter;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.Network.Helper.FriendMessageListHelper;
import com.recoverrelax.pt.riotxmppchat.Network.Helper.FriendMessageListImpl;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.FriendListChat;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observer;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendMessageListFragment extends Fragment implements Observer<List<FriendListChat>> {

    @InjectView(R.id.friendMessageListRecycler)
    RecyclerView messageRecyclerView;

    private RecyclerView.LayoutManager layoutManager;

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
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        messageRecyclerView.setLayoutManager(layoutManager);

        adapter = new FriendMessageListAdapter(getActivity(), new ArrayList<FriendListChat>(), R.layout.friend_message_list_child_layout);
        messageRecyclerView.setAdapter(adapter);

        friendMessageListHelper = new FriendMessageListImpl(this, MainApplication.getInstance().getRiotXmppService().getRoster());
        friendMessageListHelper.getPersonalMessageList();
    }

    @Override
    public void onCompleted() {}

    @Override
    public void onError(Throwable e) {
    }

    @Override
    public void onNext(List<FriendListChat> friendListChats) {
        adapter.setItems(friendListChats);

    }

}

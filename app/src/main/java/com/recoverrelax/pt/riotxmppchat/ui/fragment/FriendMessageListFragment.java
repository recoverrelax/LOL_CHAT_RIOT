package com.recoverrelax.pt.riotxmppchat.ui.fragment;


import android.app.Activity;
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
import com.recoverrelax.pt.riotxmppchat.Network.RiotXmppService;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.FriendListChat;

import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendMessageListFragment extends Fragment implements FriendMessageListImpl.FriendMessageListImplCallback, RiotXmppService.NewMessageObserver {

    @InjectView(R.id.friendMessageListRecycler)
    RecyclerView messageRecyclerView;

    private final String TAG = FriendMessageListFragment.this.getClass().getSimpleName();
    private RecyclerView.LayoutManager layoutManager;

    /**
     * Activity Callback
     */
    private FriendMessageListFragActivityCallback friendMessageListFragActivityCallback;

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
        friendMessageListHelper.getPersonalMessageList(MainApplication.getInstance().getRiotXmppService().getConnectedXmppUser());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            friendMessageListFragActivityCallback = (FriendMessageListFragActivityCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activities containing FriendMessaListFragment must implement FriendMessageListFragActivityCallback");
        }
    }

    @Override
    public void OnFriendsListReceived(List<FriendListChat> friendListChats) {
        adapter.setItems(friendListChats);
    }

    @Override
    public void OnNewMessageNotification(Message message, String messageFrom) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /**
                 * TODO: change this to update a single item, and not whole list
                 */
                friendMessageListHelper.getPersonalMessageList(MainApplication.getInstance().getRiotXmppService().getConnectedXmppUser());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MainApplication.getInstance().getRiotXmppService().addNewMessageObserver(this);
        friendMessageListHelper.getPersonalMessageList(MainApplication.getInstance().getRiotXmppService().getConnectedXmppUser());
    }

    @Override
    public void onPause() {
        super.onPause();
        MainApplication.getInstance().getRiotXmppService().removeNewMessageObserver(this);
    }

    public interface FriendMessageListFragActivityCallback {
        void replaceFragment(String userXmppName);
    }
}

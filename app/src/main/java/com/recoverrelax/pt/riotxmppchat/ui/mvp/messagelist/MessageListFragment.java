package com.recoverrelax.pt.riotxmppchat.ui.mvp.messagelist;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageListFragment extends BaseFragment implements MessageListPresenterCallbacks {

    private final String TAG = MessageListFragment.this.getClass().getSimpleName();

    @Bind(R.id.friendMessageListRecycler) RecyclerView messageRecyclerView;
    @Bind(R.id.progressBarCircularIndeterminate) ProgressBar progressBarCircularIndeterminate;

    private MessageListPresenter presenter;

    public MessageListFragment() {

    }

    public static MessageListFragment newInstance() {
        return new MessageListFragment();
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

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new MessageListPresenterImpl(this, this.getActivity());
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

        presenter.configRecyclerView();
        presenter.configAdapter(messageRecyclerView);
    }

    public void showProgressBar(boolean state) {
        progressBarCircularIndeterminate.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
    }

    @Override public void setRecyclerViewLayoutParams(RecyclerView.LayoutManager layoutManager) {
        messageRecyclerView.setLayoutManager(layoutManager);
    }

    @Override public void setRecyclerViewAdapter(MessageListAdapter adapter) {
        messageRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override public void onPause() {
        super.onPause();
        presenter.onPause();
    }
}

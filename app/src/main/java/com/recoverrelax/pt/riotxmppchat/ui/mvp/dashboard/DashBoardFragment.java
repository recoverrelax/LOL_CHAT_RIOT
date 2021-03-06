//package com.recoverrelax.pt.riotxmppchat.ui.mvp.dashboard;
//
//
//import android.content.Intent;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//import com.recoverrelax.pt.riotxmppchat.MyUtil.AppSnackbarUtils;
//import com.recoverrelax.pt.riotxmppchat.R;
//import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.HelperModel.ChampionInfo;
//import com.recoverrelax.pt.riotxmppchat.Widget.FreeChampionRotation;
//import com.recoverrelax.pt.riotxmppchat.ui.activity.LogActivity;
//import com.recoverrelax.pt.riotxmppchat.ui.fragment.BaseFragment;
//
//import java.util.List;
//
//import butterknife.Bind;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import butterknife.OnTouch;
//
///**
// * A simple {@link Fragment} subclass.
// */
//public class DashBoardFragment extends BaseFragment implements DashBoardPresenterCallbacks {
//
//    private final String TAG = DashBoardFragment.this.getClass().getSimpleName();
//    @Bind(R.id.message_number) TextView message_number;
//    @Bind(R.id.playing_number) TextView playing_number;
//
//    @Bind(R.id.online_number) TextView online_number;
//    @Bind(R.id.offline_number) TextView offline_number;
//
//    @Bind(R.id.freeChampRotation1) FreeChampionRotation freeChampRotation1;
//    @Bind(R.id.messagesIcon) ImageView messagesIcon;
//    @Bind(R.id.recyclerView) RecyclerView recyclerView;
//    private DashBoardPresenter presenter;
//
//    public DashBoardFragment() {
//    }
//
//    public static DashBoardFragment newInstance() {
//        return new DashBoardFragment();
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        presenter = new DashBoardPresenterImpl(this, this.getActivity());
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.dashboard_fragment, container, false);
//
//        ButterKnife.bind(this, view);
//        setHasOptionsMenu(true);
//        return view;
//    }
//
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        presenter.getMessageIconDrawable();
//        presenter.configRecyclerView();
//        presenter.configAdapter(recyclerView);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        presenter.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        presenter.onPause();
//    }
//
//    @OnTouch({R.id.dashboard_1, R.id.dashboard_2, R.id.dashboard_3, R.id.dashboard_4})
//    public boolean onTileTouch(View view, MotionEvent event) {
//
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            view.setVisibility(View.INVISIBLE);
//        } else if (event.getAction() == MotionEvent.ACTION_UP) {
//            view.setVisibility(View.VISIBLE);
//        }
//        return false;
//    }
//
//    @OnTouch(R.id.recyclerView)
//    public boolean onLogClick(View view, MotionEvent event) {
//        startActivity(new Intent(this.getActivity(), LogActivity.class));
//        return true;
//    }
//
//    @OnClick(R.id.dashboard_1)
//    public void OnMessageClick(View view) {
//        getBaseActivity().goToMessageListActivity();
//    }
//
//    @OnClick({R.id.dashboard_2, R.id.dashboard_3, R.id.dashboard_4})
//    public void OnPlayingOnlineOrOfflineClick(View view) {
//        getBaseActivity().goToFriendListActivity();
//    }
//
//    @Override
//    public void onUnreadedMessagesReady(String um) {
//        message_number.setText(um);
//    }
//
//    @Override
//    public void onUnreadedMessagesFailed(String um) {
//        message_number.setText(um);
//    }
//
//    @Override
//    public void onFriendStatusInfoReady(String online, String offline, String playing) {
//        playing_number.setText(playing);
//        offline_number.setText(offline);
//        online_number.setText(online);
//    }
//
//    @Override
//    public void onFriendStatusInfoFailed(String online, String offline, String playing) {
//        playing_number.setText("?");
//        offline_number.setText("?");
//        online_number.setText("?");
//    }
//
//    @Override
//    public void onFreeChampionRotationReady(List<ChampionInfo> championInfo, int size) {
//        for (int i = 0; i < size; i++) {
//            Glide.with(this)
//                    .load(championInfo.get(i).getChampionImage())
//                    .into(freeChampRotation1.getGetFreeChamps().get(i));
//        }
//    }
//
//    @Override
//    public void onFreeChampionRotationFailed() {
//        AppSnackbarUtils.showSnackBar(
//                DashBoardFragment.this.getActivity(),
//                R.string.service_currently_unavailable,
//                AppSnackbarUtils.LENGTH_INDEFINITE,
//                R.string.webservice_failed_retry,
//                v -> {
//                    presenter.getFreeChampRotationList(freeChampRotation1.getGetFreeChamps().size());
//                }
//        );
//    }
//
//    @Override
//    public void onFreeChampionRotationLoading(boolean state) {
//        freeChampRotation1.showProgressBar(state);
//    }
//
//    @Override
//    public void setMessageIconDrawable(Drawable drawable) {
//        messagesIcon.setBackground(drawable);
//    }
//
//    @Override public int getNrChampionsNeeded() {
//        return freeChampRotation1.getGetFreeChamps().size();
//    }
//
//    @Override
//    public void setRecyclerViewLayoutParams(RecyclerView.LayoutManager layoutManager) {
//        recyclerView.setLayoutManager(layoutManager);
//    }
//
//    @Override
//    public void setRecyclerViewAdapter(DashBoardAdapter adapter) {
//        recyclerView.setAdapter(adapter);
//    }
//}

package com.recoverrelax.pt.riotxmppchat.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.CurrentGame.CurrentGameInfo;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Static.ChampionListDto;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiServiceImpl;
import com.recoverrelax.pt.riotxmppchat.ui.activity.CurrentGameActivity;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentGameFragment extends BaseFragment {

    private final String TAG = CurrentGameFragment.this.getClass().getSimpleName();
    private String friendXmppAddress;

    @Bind(R.id.mapName)
    TextView mapName;

    @Bind(R.id.gameQueueType)
    TextView gameQueueType;

    @Bind(R.id.gameMode)
    TextView gameMode;

    @Bind(R.id.gameDuration)
    TextView gameDuration;

    @Bind({R.id.ban1, R.id.ban2, R.id.ban3, R.id.ban4, R.id.ban5, R.id.ban6})
    List<ImageView> banTextViews;

    @Inject
    RiotApiServiceImpl riotApiServiceImpl;

    public CurrentGameFragment() {
        // Required empty public constructor
    }

    public static CurrentGameFragment newInstance(String friendXmppAddress) {
        CurrentGameFragment frag = new CurrentGameFragment();

        Bundle args = new Bundle();
        args.putString(CurrentGameActivity.FRIEND_XMPP_ADDRESS_INTENT, friendXmppAddress);
        frag.setArguments(args);

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().getAppComponent().inject(this);

        if (savedInstanceState == null) {
            Bundle args = getArguments();
            if (args != null) {
                friendXmppAddress = args.getString(CurrentGameActivity.FRIEND_XMPP_ADDRESS_INTENT);
            }
        } else {
            friendXmppAddress = (String) savedInstanceState.getSerializable(CurrentGameActivity.FRIEND_XMPP_ADDRESS_INTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.current_game_fragment, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
//        showProgressBar(true);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        long userId_riotApi = AppXmppUtils.getSummonerIdByXmppAddress(friendXmppAddress);

//        riotApiServiceImpl.getCurrentGameInfoBySummonerId(userId_riotApi)
//                .doOnNext(this::fetchGameData)
//                .doOnError(throwable -> Log.e(TAG, "", throwable))
//                .flatMap(new Func1<CurrentGameInfo, Observable<CurrentGameInfo>>() {
//                    @Override
//                    public Observable<CurrentGameInfo> call(CurrentGameInfo currentGameInfo) {
////                        return currentGameInfo.getBannedChampions().
//                    }
//                })


    }

    private void fetchGameData(CurrentGameInfo currentGameInfo) {
        mapName.setText(currentGameInfo.getMapName());
        gameQueueType.setText(currentGameInfo.getGameQueueFormatted());
        gameMode.setText(currentGameInfo.getGameMode());
        gameDuration.setText(currentGameInfo.getGameStartTimeFormatted());
    }
}

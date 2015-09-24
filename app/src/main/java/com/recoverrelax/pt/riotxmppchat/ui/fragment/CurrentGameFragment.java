package com.recoverrelax.pt.riotxmppchat.ui.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.HttpUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.KamehameUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.CurrentGame.CurrentGameInfo;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.CurrentGame.CurrentGameParticipant;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.HelperModel.LiveGameBannedChamp;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.HelperModel.LiveGameParticipant;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiOperations;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiServiceImpl;
import com.recoverrelax.pt.riotxmppchat.Widget.AppProgressBar;
import com.recoverrelax.pt.riotxmppchat.Widget.CurrentGameBanList;
import com.recoverrelax.pt.riotxmppchat.Widget.CurrentGameGlobalInfo;
import com.recoverrelax.pt.riotxmppchat.Widget.CurrentGameSingleParticipant;
import com.recoverrelax.pt.riotxmppchat.ui.activity.CurrentGameActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentGameFragment extends BaseFragment {

    private final String TAG = CurrentGameFragment.this.getClass().getSimpleName();
    private String friendXmppAddress;

//    @Bind(R.id.progressBar)
//    AppProgressBar progressBar;

    @Bind(R.id.currentGameGlobalInfo)
    CurrentGameGlobalInfo currentGameGlobalInfo;

    @Bind(R.id.banList)
    CurrentGameBanList banList;

    @Inject
    RiotApiServiceImpl riotApiServiceImpl;

    @Inject
    RiotApiOperations riotApiOperations;

    @Bind({R.id.team100_player1, R.id.team100_player2, R.id.team100_player3, R.id.team100_player4, R.id.team100_player5})
    List<CurrentGameSingleParticipant> team100;

    @Bind({R.id.team200_player1, R.id.team200_player2, R.id.team200_player3, R.id.team200_player4, R.id.team200_player5})
    List<CurrentGameSingleParticipant> team200;

    private static String TEMPORARY_ICON_URL = "http://ddragon.leagueoflegends.com/cdn/5.18.1/img/champion/";

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

        riotApiServiceImpl.getCurrentGameInfoBySummonerId(userId_riotApi) // get Observable<CurrentGameInfo>
                .doOnNext(this::fetchGameData) // update basic info in view
                .flatMap(currentGameInfo ->
                        Observable.from(currentGameInfo.getBannedChampions()) // for each bannedChampion Object
                                .flatMap(bannedChampion -> // create new BannedChampionImage objects
                                                Observable.just(
                                                        new LiveGameBannedChamp(
                                                                (int) bannedChampion.getChampionId(),
                                                                (int) bannedChampion.getTeamId()
                                                        )
                                                )
                                )
                                .toList() // list of BannedChampionImage objects
                                          // with Ids and teamIds set
                                .take(banList.getSize())
                                .flatMap(liveGameBannedChamps ->
                                                Observable.from(currentGameInfo.getParticipants()) // for each bannedChampion Object
                                                        .flatMap(cgp ->
                                                                        Observable.just(
                                                                                new LiveGameParticipant(
                                                                                        cgp.getSpell1Id(),
                                                                                        cgp.getSpell2Id(),
                                                                                        cgp.getChampionId(),
                                                                                        cgp.getSummonerName(),
                                                                                        cgp.getTeamId()
                                                                                )
                                                                        )
                                                        )
                                                        .toList() // list of BannedChampionImage objects
                                                        .take((team100.size() + team200.size()))
                                                        .flatMap(liveGameParticipants ->
                                                                        Observable.just(
                                                                                new Pair<>(liveGameBannedChamps, liveGameParticipants)
                                                                        )
                                                        )
                                )
                                .flatMap(riotApiOperations::getChampionsImage)
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnNext(pair -> {
                                    fetchBannedChampions(pair.first);
                                    fetchParticipants(pair.second);
                                })
                                .map(o -> currentGameInfo)
                )
                .subscribe(new Subscriber<CurrentGameInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (HttpUtils.is404NotFound(e)) {
                            KamehameUtils.showSnackbar(CurrentGameFragment.this.getActivity(),
                                    R.string.current_game_summoner_not_in_game,
                                    false,
                                    Snackbar.LENGTH_LONG,
                                    null,
                                    null);

                            new Handler().postDelayed(() -> CurrentGameFragment.this.getActivity().finish(), 2000);
                        }
                    }

                    @Override
                    public void onNext(CurrentGameInfo currentGameInfo) {
//                        showProgressBar(false);
                    }
                });
    }

    private void fetchParticipants(List<LiveGameParticipant> participants) {

    }

    private void fetchGameData(CurrentGameInfo currentGameInfo) {
        currentGameGlobalInfo.setMapName(currentGameInfo.getMapName());
        currentGameGlobalInfo.setGameQueueType(currentGameInfo.getGameQueueFormatted());
        currentGameGlobalInfo.setGameMode(currentGameInfo.getGameMode());
        currentGameGlobalInfo.setGameDuration(currentGameInfo.getGameStartTimeFormatted());
    }

    private void fetchBannedChampions(List<LiveGameBannedChamp> liveGameBannedChamps){
       List<LiveGameBannedChamp> team100 = new ArrayList<>();
       List<LiveGameBannedChamp> team200 = new ArrayList<>();

        for(LiveGameBannedChamp lg: liveGameBannedChamps){
            if(lg.getTeamId() == 100){
                team100.add(lg);
            }
            else{
                team200.add(lg);
            }
        }

        for(int i = 0; i < team100.size(); i++){
            ImageView imageView = banList.getTeam100Bans().get(i);

            Picasso.with(this.getActivity())
                    .load(TEMPORARY_ICON_URL + team100.get(i).getChampionImage())
                    .into(imageView);
        }

        for(int i = 0; i < team200.size(); i++){
            ImageView imageView = banList.getTeam200Bans().get(i);

            Picasso.with(this.getActivity())
                    .load(TEMPORARY_ICON_URL + team200.get(i).getChampionImage())
                    .into(imageView);
        }
    }

    private void sortBannedChampionImageByTeam(List<LiveGameBannedChamp> liveGameBannedChamps){
        Collections.sort(liveGameBannedChamps, (c1, c2) ->
                (c1.getTeamId() > c2.getTeamId())
                        ? 1
                        : (c1.getTeamId() == c2.getTeamId())
                        ? 0
                        : -1);
    }

//    public void showProgressBar(boolean state){
//        progressBar.setVisibility(state? View.VISIBLE : View.GONE);
//    }
}

package com.recoverrelax.pt.riotxmppchat.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.CurrentGame.BannedChampion;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.CurrentGame.CurrentGameInfo;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.CurrentGame.CurrentGameParticipant;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.HelperModel.LiveGameBannedChamp;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiOperations;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiRealmDataVersion;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiService.RiotApiServiceImpl;
import com.recoverrelax.pt.riotxmppchat.Widget.AppProgressBar;
import com.recoverrelax.pt.riotxmppchat.Widget.CurrentGameBanList;
import com.recoverrelax.pt.riotxmppchat.Widget.CurrentGameGlobalInfo;
import com.recoverrelax.pt.riotxmppchat.Widget.CurrentGameSingleParticipantBase;
import com.recoverrelax.pt.riotxmppchat.ui.activity.BaseActivity;
import com.recoverrelax.pt.riotxmppchat.ui.activity.CurrentGameActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentGameFragment extends BaseFragment {

    private final String TAG = CurrentGameFragment.this.getClass().getSimpleName();
    private String friendXmppAddress;
    private String friendUsername;

//    @Bind(R.id.progressBar)
//    AppProgressBar progressBar;

    @Bind(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;

    @Bind(R.id.currentGameGlobalInfo)
    CurrentGameGlobalInfo currentGameGlobalInfo;

    @Bind(R.id.banList)
    CurrentGameBanList banList;

    @Bind(R.id.progressBar)
    AppProgressBar progressBar;

    @Bind(R.id.currentGameParticipantMainContent)
    LinearLayout currentGameParticipantMainContent;

    @Inject
    RiotApiServiceImpl riotApiServiceImpl;

    @Inject
    RiotApiOperations riotApiOperations;

    @Inject
    RiotApiRealmDataVersion realmData;

    @Bind({R.id.team100_player1, R.id.team100_player2, R.id.team100_player3, R.id.team100_player4, R.id.team100_player5})
    List<CurrentGameSingleParticipantBase> team100;

    @Bind({R.id.team200_player1, R.id.team200_player2, R.id.team200_player3, R.id.team200_player4, R.id.team200_player5})
    List<CurrentGameSingleParticipantBase> team200;

    public CurrentGameFragment() {
        // Required empty public constructor
    }

    public static CurrentGameFragment newInstance(String friendXmppAddress, String friendUsername) {
        CurrentGameFragment frag = new CurrentGameFragment();

        Bundle args = new Bundle();
        args.putString(CurrentGameActivity.FRIEND_XMPP_ADDRESS_INTENT, friendXmppAddress);
        args.putString(CurrentGameActivity.FRIEND_XMPP_USERNAME_INTENT, friendUsername);
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
                friendUsername = args.getString(CurrentGameActivity.FRIEND_XMPP_USERNAME_INTENT);
            }
        } else {
            friendXmppAddress = (String) savedInstanceState.getSerializable(CurrentGameActivity.FRIEND_XMPP_ADDRESS_INTENT);
            friendUsername = (String) savedInstanceState.getSerializable(CurrentGameActivity.FRIEND_XMPP_USERNAME_INTENT);
        }
        ((BaseActivity)getActivity()).setTitle(getActivity().getResources().getString(R.string.current_game__fragment_title) + " " + friendUsername);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.current_game_fragment, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        long userId_riotApi = AppXmppUtils.getSummonerIdByXmppAddress(friendXmppAddress);

        Observable<CurrentGameInfo> obsCurrentGameInfoBySummonerId = riotApiServiceImpl.getCurrentGameInfoBySummonerId(userId_riotApi)
                                                                        .cache()
                                                                        .subscribeOn(Schedulers.io())
                                                                        .observeOn(AndroidSchedulers.mainThread());

        Observable<Map<Integer, String>> obsChampionsImage = riotApiOperations.getChampionsImage()
                .cache()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        /**
         * Get the General Live Game Data and Update the View
         */

        obsCurrentGameInfoBySummonerId // get Observable<CurrentGameInfo>
                .doOnSubscribe(() -> currentGameGlobalInfo.enableProgressBar(true))
                .doOnUnsubscribe(() -> currentGameGlobalInfo.enableProgressBar(false))
                .doOnNext(this::fetchGameData)
                .subscribe();

        /**
         * Get the Banned Champions Information and Update the View
         */

        obsCurrentGameInfoBySummonerId // get Observable<CurrentGameInfo>
                .doOnSubscribe(() -> banList.enableProgressBar(true))
                .map(CurrentGameInfo::getBannedChampions) // for each bannedChampion Object
                .take(banList.getSize())
                .flatMap(bannedChampionList ->
                                obsChampionsImage
                                        .flatMap(championImagesMap ->
                                                        Observable.from(bannedChampionList)
                                                                .doOnNext(bannedChampion -> {
                                                                    String s = championImagesMap.get((int) bannedChampion.getChampionId());
                                                                    if (s != null)
                                                                        bannedChampion.setChampionImage(s);
                                                                })
                                                                .toList()
                                        )
                )
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(this::fetchBannedChampions)
                .subscribe();

        /**
         * Get the Picked Champions Information and Update the View
         */

        obsCurrentGameInfoBySummonerId // get Observable<CurrentGameInfo>
                .doOnSubscribe(() -> enableProgressBarParticipantContent(true))
                .map(CurrentGameInfo::getParticipants) // for each bannedChampion Object
                .take((team100.size() + team200.size()))
                .flatMap(participantList ->
                                obsChampionsImage
                                        .flatMap(championImagesMap ->
                                                        Observable.from(participantList)
                                                                .doOnNext(participant -> {
                                                                    String s = championImagesMap.get((int) participant.getChampionId());
                                                                    if (s != null)
                                                                        participant.setChampionImage(s);
                                                                })
                                                                .toList()
                                        )
                )
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(this::fetchParticipants)
                .subscribe();
    }

    private void fetchParticipants(List<CurrentGameParticipant> participants) {

        realmData.getChampionDDBaseUrl()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ddChampionSquareUrl -> {
                    List<CurrentGameParticipant> team1001 = new ArrayList<>();
                    List<CurrentGameParticipant> team2001 = new ArrayList<>();

                    for (CurrentGameParticipant lg : participants) {
                        if (lg.getTeamId() == 100) {
                            team1001.add(lg);
                        } else {
                            team2001.add(lg);
                        }
                    }

                    for (int i = 0; i < team1001.size(); i++) {
                        CurrentGameSingleParticipantBase cgp = CurrentGameFragment.this.team100.get(i);
                        cgp.setVisibility(View.VISIBLE);
                        ImageView imageView = cgp.getChampionPlaying();
                        TextView playerName = cgp.getPlayerName();

                        CurrentGameParticipant liveGameParticipant = team1001.get(i);

                        String path = ddChampionSquareUrl + liveGameParticipant.getChampionImage();
                        Picasso.with(CurrentGameFragment.this.getActivity())
                                .load(path)
                                .into(imageView);

                        playerName.setText(liveGameParticipant.getSummonerName());
                    }

                    for (int i = 0; i < team2001.size(); i++) {
                        CurrentGameSingleParticipantBase cgp = CurrentGameFragment.this.team200.get(i);
                        cgp.setVisibility(View.VISIBLE);
                        ImageView imageView = cgp.getChampionPlaying();
                        TextView playerName = cgp.getPlayerName();

                        CurrentGameParticipant liveGameParticipant = team2001.get(i);

                        String path = ddChampionSquareUrl + liveGameParticipant.getChampionImage();
                        Picasso.with(CurrentGameFragment.this.getActivity())
                                .load(path)
                                .into(imageView);

                        playerName.setText(liveGameParticipant.getSummonerName());
                    }

                    enableProgressBarParticipantContent(false);
                });
    }

    private void enableProgressBarParticipantContent(boolean state){
        progressBar.setVisibility(state? View.VISIBLE : View.GONE);
        currentGameParticipantMainContent.setVisibility(state? View.INVISIBLE : View.VISIBLE);
    }

    private void fetchGameData(CurrentGameInfo currentGameInfo) {
        currentGameGlobalInfo.setMapName(currentGameInfo.getMapName());
        currentGameGlobalInfo.setGameQueueType(currentGameInfo.getGameQueueFormatted());
        currentGameGlobalInfo.setGameMode(currentGameInfo.getGameMode());
        currentGameGlobalInfo.setGameDuration(currentGameInfo.getGameStartTimeFormatted());
    }

    private void fetchBannedChampions(List<BannedChampion> bannedChampions){
        realmData.getChampionDDBaseUrl()
                .subscribe(ddChampionSquareUrl -> {
                    List<BannedChampion> team1001 = new ArrayList<>();
                    List<BannedChampion> team2001 = new ArrayList<>();

                    for(BannedChampion bc: bannedChampions){
                        if(bc.getTeamId() == 100){
                            team1001.add(bc);
                        }
                        else{
                            team2001.add(bc);
                        }
                    }

                    for(int i = 0; i < team1001.size(); i++){
                        ImageView imageView = banList.getTeam100Bans().get(i);
                        imageView.setVisibility(View.VISIBLE);

                        Picasso.with(CurrentGameFragment.this.getActivity())
                                .load(ddChampionSquareUrl + team1001.get(i).getChampionImage())
                                .into(imageView);
                    }

                    for(int i = 0; i < team2001.size(); i++){
                        ImageView imageView = banList.getTeam200Bans().get(i);
                        imageView.setVisibility(View.VISIBLE);

                        Picasso.with(CurrentGameFragment.this.getActivity())
                                .load(ddChampionSquareUrl + team2001.get(i).getChampionImage())
                                .into(imageView);
                    }

                    banList.enableProgressBar(false);
                });
    }
}

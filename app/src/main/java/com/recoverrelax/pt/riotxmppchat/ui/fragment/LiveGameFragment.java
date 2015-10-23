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

import com.bumptech.glide.Glide;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppSnackbarUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.LogUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.CurrentGame.BannedChampion;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.CurrentGame.CurrentGameInfo;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.CurrentGame.CurrentGameParticipant;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.HelperModel.ChampionInfo;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiOperations;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiRealmDataVersion;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiService.RiotApiServiceImpl;
import com.recoverrelax.pt.riotxmppchat.Widget.AppProgressBar;
import com.recoverrelax.pt.riotxmppchat.Widget.CurrentGameBanList;
import com.recoverrelax.pt.riotxmppchat.Widget.CurrentGameGlobalInfo;
import com.recoverrelax.pt.riotxmppchat.Widget.CurrentGameSingleParticipantBase;
import com.recoverrelax.pt.riotxmppchat.ui.activity.LiveGameActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass.
 */
public class LiveGameFragment extends BaseFragment {

    private final String TAG = LiveGameFragment.this.getClass().getSimpleName();
    @Bind(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;
    @Bind(R.id.currentGameGlobalInfo)
    CurrentGameGlobalInfo currentGameGlobalInfo;

    //    @Bind(R.id.progressBar)
//    AppProgressBar progressBar;
    @Bind(R.id.banList)
    CurrentGameBanList banList;
    @Bind(R.id.progressBar)
    AppProgressBar progressBar;
    @Bind(R.id.currentGameParticipantMainContent)
    LinearLayout currentGameParticipantMainContent;
    @Bind(R.id.progressBarPicks)
    AppProgressBar progressBarPicksAndBans;
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
    private String friendXmppAddress;
    private String friendUsername;
    private CompositeSubscription subscriptions = new CompositeSubscription();

    public LiveGameFragment() {
        // Required empty public constructor
    }

    public static LiveGameFragment newInstance(String friendXmppAddress, String friendUsername) {
        LiveGameFragment frag = new LiveGameFragment();

        Bundle args = new Bundle();
        args.putString(LiveGameActivity.FRIEND_XMPP_ADDRESS_INTENT, friendXmppAddress);
        args.putString(LiveGameActivity.FRIEND_XMPP_USERNAME_INTENT, friendUsername);
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
                friendXmppAddress = args.getString(LiveGameActivity.FRIEND_XMPP_ADDRESS_INTENT);
                friendUsername = args.getString(LiveGameActivity.FRIEND_XMPP_USERNAME_INTENT);
            }
        } else {
            friendXmppAddress = (String) savedInstanceState.getSerializable(LiveGameActivity.FRIEND_XMPP_ADDRESS_INTENT);
            friendUsername = (String) savedInstanceState.getSerializable(LiveGameActivity.FRIEND_XMPP_USERNAME_INTENT);
        }
        setToolbarTitle(getActivity().getResources().getString(R.string.current_game__fragment_title) + " " + friendUsername);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.live_game_fragment, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getDataFromWs();
    }

    @Override
    public void onPause() {
        super.onPause();
        subscriptions.clear();
    }

    private void getDataFromWs() {
        subscriptions.clear();
        long userId_riotApi = AppXmppUtils.getSummonerIdByXmppAddress(friendXmppAddress);

        Observable<CurrentGameInfo> obsCurrentGameInfoBySummonerId = riotApiServiceImpl.getCurrentGameInfoBySummonerId(userId_riotApi)
                .cache()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Observable<Map<Integer, ChampionInfo>> obsChampionsImage = riotApiOperations.getChampionsImage()
                .cache()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Observable<Map<Integer, String>> obsSummonerSpellImage = riotApiOperations.getSummonerSpellImage()
                .cache()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Observable<String> championDDBaseUrl = realmData.getChampionDDBaseUrl();
        Observable<String> summonerSpellDDBaseUrl = realmData.getSummonerSpellDDBaseUrl();

        getGeneralLiveData(obsCurrentGameInfoBySummonerId);
        getBannedChampionInfo(obsCurrentGameInfoBySummonerId, obsChampionsImage, championDDBaseUrl);
        getPickedChampsAndSpells(obsCurrentGameInfoBySummonerId, obsChampionsImage, obsSummonerSpellImage, championDDBaseUrl, summonerSpellDDBaseUrl);

    }

    private void getGeneralLiveData(Observable<CurrentGameInfo> obsCurrentGameInfoBySummonerId) {
        /**
         * Get General Live Data
         */
        subscriptions.add(
                obsCurrentGameInfoBySummonerId
                        .doOnSubscribe(() -> currentGameGlobalInfo.enableProgressBar(true))
                        .doOnUnsubscribe(() -> currentGameGlobalInfo.enableProgressBar(false))
                        .subscribe(currentGameInfo -> {
                                    currentGameGlobalInfo.setMapName(currentGameInfo.getMapName());
                                    currentGameGlobalInfo.setGameQueueType(currentGameInfo.getGameQueueFormatted());
                                    currentGameGlobalInfo.setGameMode(currentGameInfo.getGameMode());
                                    currentGameGlobalInfo.setGameDuration(currentGameInfo.getGameStartTimeFormatted());
                                }, throwable -> {
                                    AppSnackbarUtils.showSnackBar(
                                            LiveGameFragment.this.getBaseActivity(),
                                            R.string.service_currently_unavailable,
                                            AppSnackbarUtils.LENGTH_INDEFINITE,
                                            R.string.webservice_failed_retry,
                                            v -> getGeneralLiveData(
                                                    obsCurrentGameInfoBySummonerId
                                            )
                                    );
                                }
                        )
        );
    }

    private void getBannedChampionInfo(Observable<CurrentGameInfo> obsCurrentGameInfoBySummonerId, Observable<Map<Integer, ChampionInfo>> obsChampionsImage, Observable<String> championDDBaseUrl) {
        /**
         * Get Banned Champion Info
         */
        subscriptions.add(
                obsCurrentGameInfoBySummonerId // get Observable<CurrentGameInfo>
                        .doOnSubscribe(() -> banList.enableProgressBar(true))
                        .doOnUnsubscribe(() -> banList.enableProgressBar(false))
                        .map(CurrentGameInfo::getBannedChampions) // for each bannedChampion Object
                        .take(banList.getSize())
                        .flatMap(bannedChampions ->
                                        Observable.zip(obsChampionsImage, championDDBaseUrl, (championImagesMap, champUrl) -> {
                                            for (BannedChampion bc : bannedChampions) {
                                                String s = championImagesMap.get((int) bc.getChampionId()).getChampionImage();

                                                if (s != null)
                                                    bc.setChampionImage(champUrl + s);
                                            }
                                            return bannedChampions;
                                        })
                        )
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<List<BannedChampion>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                AppSnackbarUtils.showSnackBar(
                                        LiveGameFragment.this.getBaseActivity(),
                                        R.string.service_currently_unavailable,
                                        AppSnackbarUtils.LENGTH_INDEFINITE,
                                        R.string.webservice_failed_retry,
                                        v -> getBannedChampionInfo(obsCurrentGameInfoBySummonerId, obsChampionsImage, championDDBaseUrl)
                                );
                            }

                            @Override
                            public void onNext(List<BannedChampion> bannedChampions) {
                                fetchBannedChampions(bannedChampions);
                            }
                        })
        );
    }

    private void getPickedChampsAndSpells(Observable<CurrentGameInfo> obsCurrentGameInfoBySummonerId, Observable<Map<Integer, ChampionInfo>> obsChampionsImage, Observable<Map<Integer, String>> obsSummonerSpellImage, Observable<String> championDDBaseUrl, Observable<String> summonerSpellDDBaseUrl) {
        /**
         * Get Picked Champions and Spells
         */
        subscriptions.add(
                obsCurrentGameInfoBySummonerId
                        .doOnSubscribe(() -> enableProgressBarParticipantContent(true))
                        .doOnUnsubscribe(() -> enableProgressBarParticipantContent(false))
                        .map(CurrentGameInfo::getParticipants) // for each bannedChampion Object
                        .take((team100.size() + team200.size()))
                        .flatMap(participantList ->
                                        Observable.zip(obsChampionsImage, championDDBaseUrl, (championImagesMap, championDDBaseUrl1) -> {
                                            for (CurrentGameParticipant cgp : participantList) {
                                                String s = championImagesMap.get((int) cgp.getChampionId()).getChampionImage();
                                                if (s != null)
                                                    cgp.setChampionImage(championDDBaseUrl1 + s);
                                            }
                                            return participantList;
                                        })
                        )
                        .flatMap(participantList ->
                                        Observable.zip(obsSummonerSpellImage, summonerSpellDDBaseUrl, (summonerSpellImagesMap, summonerSpellDDBaseUrl1) -> {
                                            for (CurrentGameParticipant cgp : participantList) {
                                                String s1 = summonerSpellImagesMap.get((int) cgp.getSpell1Id());
                                                if (s1 != null)
                                                    cgp.setSpell1Image(summonerSpellDDBaseUrl1 + s1);
                                                String s2 = summonerSpellImagesMap.get((int) cgp.getSpell2Id());
                                                if (s2 != null)
                                                    cgp.setSpell2Image(summonerSpellDDBaseUrl1 + s2);
                                            }
                                            return participantList;
                                        })
                        )
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<List<CurrentGameParticipant>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                AppSnackbarUtils.showSnackBar(
                                        LiveGameFragment.this.getBaseActivity(),
                                        R.string.service_currently_unavailable,
                                        AppSnackbarUtils.LENGTH_INDEFINITE,
                                        R.string.webservice_failed_retry,
                                        v -> getPickedChampsAndSpells(obsCurrentGameInfoBySummonerId, obsChampionsImage, obsSummonerSpellImage, championDDBaseUrl, summonerSpellDDBaseUrl)
                                );
                            }

                            @Override
                            public void onNext(List<CurrentGameParticipant> currentGameParticipants) {
                                fetchParticipants(currentGameParticipants);
                            }
                        })
        );
    }

    private void fetchParticipants(List<CurrentGameParticipant> participants) {

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
            CurrentGameSingleParticipantBase cgp = LiveGameFragment.this.team100.get(i);

            ImageView champion = cgp.getChampionPlaying();
            ImageView summonerS1 = cgp.getSummonerSpell1();
            ImageView summonerS2 = cgp.getSummonerSpell2();
            TextView playerName = cgp.getPlayerName();

            CurrentGameParticipant liveGameParticipant = team1001.get(i);

            String pathChampion = liveGameParticipant.getChampionImage();
            String pathSS1 = liveGameParticipant.getSpell1Image();
            String pathSS2 = liveGameParticipant.getSpell2Image();
            LogUtils.LOGI(TAG, pathSS1);

            Glide.with(LiveGameFragment.this.getActivity())
                    .load(pathChampion)
                    .into(champion);

            Glide.with(LiveGameFragment.this.getActivity())
                    .load(pathSS1)
                    .into(summonerS1);

            Glide.with(LiveGameFragment.this.getActivity())
                    .load(pathSS2)
                    .into(summonerS2);

            playerName.setText(liveGameParticipant.getSummonerName());
            cgp.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < team2001.size(); i++) {
            CurrentGameSingleParticipantBase cgp = LiveGameFragment.this.team200.get(i);

            ImageView champion = cgp.getChampionPlaying();
            ImageView summonerS1 = cgp.getSummonerSpell1();
            ImageView summonerS2 = cgp.getSummonerSpell2();
            TextView playerName = cgp.getPlayerName();

            CurrentGameParticipant liveGameParticipant = team2001.get(i);

            String pathChampion = liveGameParticipant.getChampionImage();
            String pathSS1 = liveGameParticipant.getSpell1Image();
            String pathSS2 = liveGameParticipant.getSpell2Image();

            Glide.with(LiveGameFragment.this.getActivity())
                    .load(pathChampion)
                    .into(champion);

            Glide.with(LiveGameFragment.this.getActivity())
                    .load(pathSS1)
                    .into(summonerS1);

            Glide.with(LiveGameFragment.this.getActivity())
                    .load(pathSS2)
                    .into(summonerS2);

            playerName.setText(liveGameParticipant.getSummonerName());
            cgp.setVisibility(View.VISIBLE);
        }
    }

    private void enableProgressBarParticipantContent(boolean state) {
        progressBarPicksAndBans.setVisibility(state ? View.VISIBLE : View.GONE);
        currentGameParticipantMainContent.setVisibility(state ? View.INVISIBLE : View.VISIBLE);
    }

    private void fetchBannedChampions(List<BannedChampion> bannedChampions) {

        List<BannedChampion> team1001 = new ArrayList<>();
        List<BannedChampion> team2001 = new ArrayList<>();

        for (BannedChampion bc : bannedChampions) {
            if (bc.getTeamId() == 100) {
                team1001.add(bc);
            } else {
                team2001.add(bc);
            }
        }

        for (int i = 0; i < team1001.size(); i++) {
            ImageView imageView = banList.getTeam100Bans().get(i);
            imageView.setVisibility(View.VISIBLE);

            Glide.with(LiveGameFragment.this.getActivity())
                    .load(team1001.get(i).getChampionImage())
                    .into(imageView);
        }

        for (int i = 0; i < team2001.size(); i++) {
            ImageView imageView = banList.getTeam200Bans().get(i);
            imageView.setVisibility(View.VISIBLE);

            Glide.with(LiveGameFragment.this.getActivity())
                    .load(team2001.get(i).getChampionImage())
                    .into(imageView);
        }
    }
}

package com.recoverrelax.pt.riotxmppchat.ui.fragment;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.recoverrelax.pt.riotxmppchat.Adapter.RecentGameAdapter;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppContextUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppGlobals;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Game.PlayerDto;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Game.RecentGamesDto;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.HelperModel.ChampionInfo;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.HelperModel.RecentGameWrapper;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.HelperModel.TeamInfo;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Summoner.SummonerDto;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiOperations;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiRealmDataVersion;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiService.RiotApiServiceImpl;
import com.recoverrelax.pt.riotxmppchat.Widget.AppProgressBar;
import com.recoverrelax.pt.riotxmppchat.ui.activity.LiveGameActivity;

import java.util.ArrayList;
import java.util.Arrays;
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
public class RecentGameFragment extends BaseFragment {

    private final String TAG = RecentGameFragment.this.getClass().getSimpleName();
    private String friendXmppAddress;
    private String friendUsername;

    @Bind(R.id.recentGameParentLayout)
    FrameLayout recentGameParentLayout;

    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.progressBar)
    AppProgressBar progressBar;

    @Bind(R.id.myRecyclerView)
    RecyclerView myRecyclerView;

    private long userId_riotApi;

    @Inject
    RiotApiOperations riotApiOperation;

    @Inject
    RiotApiRealmDataVersion realmData;

    @Inject
    RiotApiServiceImpl riotApiImpl;

    /**
     * Adapter
     */
    private RecentGameAdapter adapter;
    private CompositeSubscription subscriptions = new CompositeSubscription();

    public RecentGameFragment() {
        // Required empty public constructor
    }

    public static RecentGameFragment newInstance(String friendXmppAddress, String friendUsername) {
        RecentGameFragment frag = new RecentGameFragment();

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
        if( friendUsername != null && friendUsername.equals(LiveGameActivity.FRIEND_XMPP_USERNAME_ME))
            setToolbarTitle(getActivity().getResources().getString(R.string.recent_game_me_fragment_title));
        else
            setToolbarTitle(getActivity().getResources().getString(R.string.recent_game__fragment_title, friendUsername));

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

        adapter = new RecentGameAdapter(this.getActivity(), new ArrayList<>());
        myRecyclerView.setAdapter(adapter);

        userId_riotApi = AppXmppUtils.getSummonerIdByXmppAddress(friendXmppAddress);

        swipeRefreshLayout.setEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getRecentGamesInformation();
    }

    @Override
    public void onPause() {
        super.onPause();

        subscriptions.clear();
    }

    public void getRecentGamesInformation() {

        /**
         * DataStructure Wrapper for the Adapter
         */
        List<RecentGameWrapper> finalGameList = new ArrayList<>();

        /**
         * Api Calls zipped
         */
        List<Observable<?>> observableList = Arrays.asList(
                riotApiOperation.getSummonerSpellImage(), // 0
                riotApiOperation.getChampionsImage(), // 1
                riotApiOperation.getItemImage(), // 2
                realmData.getSummonerSpellDDBaseUrl(), // 3
                realmData.getItemDDBaseUrl(), // 4
                realmData.getChampionDDBaseUrl(), // 5
                realmData.getSkinDDBaseUrl() //6
        );

        /**
         * Make every call, build the Adapter dataStructure and update the Adapter
         */

        doWsCall(finalGameList, observableList);
    }

    private void doWsCall(List<RecentGameWrapper> finalGameList, List<Observable<?>> observableList) {
        subscriptions.add(
                Observable.zip(observableList, args -> {
                    Map<Integer, String> ssImages = (Map<Integer, String>) args[0];
                    Map<Integer, ChampionInfo> championImage = (Map<Integer, ChampionInfo>) args[1];
                    Map<Integer, String> itemImage = (Map<Integer, String>) args[2];
                    String ssUrl = (String) args[3];
                    String itemUrl = (String) args[4];
                    String championUrl = (String) args[5];
                    String skinUrl = (String) args[6];

                    final long[] mySummonerId = new long[1];
                    final long[] gameType = new long[1];

                    return riotApiOperation.getRecentGamesList(String.valueOf(userId_riotApi))
                            .map((RecentGamesDto recentGamesDto) -> {
                                mySummonerId[0] = recentGamesDto.getSummonerId();
                                return recentGamesDto.getGames();
                            })
                            .flatMap(Observable::from)
                            .observeOn(Schedulers.computation())
                            .doOnNext(game -> {
                                RecentGameWrapper recentGameWrapper = new RecentGameWrapper();

                                recentGameWrapper.setGameType(game.getSubType());
                                recentGameWrapper.setGameWhen(game.getCreateDate());
                                recentGameWrapper.setPlayerPosition(game.getStats().getPlayerPosition());

                                recentGameWrapper.setSummonerSpellUrl1(ssUrl + ssImages.get(game.getSpell1()));
                                recentGameWrapper.setSummonerSpellUrl2(ssUrl + ssImages.get(game.getSpell2()));

                                List<String> newItemUrl = new ArrayList<>();
                                newItemUrl.add(itemUrl + itemImage.get(game.getStats().getItem0()));
                                newItemUrl.add(itemUrl + itemImage.get(game.getStats().getItem1()));
                                newItemUrl.add(itemUrl + itemImage.get(game.getStats().getItem2()));
                                newItemUrl.add(itemUrl + itemImage.get(game.getStats().getItem3()));
                                newItemUrl.add(itemUrl + itemImage.get(game.getStats().getItem4()));
                                newItemUrl.add(itemUrl + itemImage.get(game.getStats().getItem5()));

                                recentGameWrapper.setItemList(newItemUrl);

                                List<String> fullNameSkinUrl = new ArrayList<>();
                                ChampionInfo championInfo = championImage.get(game.getChampionId());

                                for (String skin : championInfo.getChampionSkinImage())
                                    fullNameSkinUrl.add(skinUrl + skin + AppGlobals.DD_VERSION.SKIN_FILE_EXTENSION);

                                recentGameWrapper.setSkinList(fullNameSkinUrl);
                                recentGameWrapper.setIsWin(game.getStats().isWin());

                                recentGameWrapper.setMyChampionUrl(championUrl + championImage.get(game.getChampionId()).getChampionImage());
                                recentGameWrapper.setMyTeamId(game.getTeamId());
                                recentGameWrapper.setKill(String.valueOf(game.getStats().getChampionsKilled()));
                                recentGameWrapper.setDead(String.valueOf(game.getStats().getNumDeaths()));
                                recentGameWrapper.setAssists(String.valueOf(game.getStats().getAssists()));
                                recentGameWrapper.setGold(game.getStats().getGoldEarned());
                                recentGameWrapper.setCs(String.valueOf(game.getStats().getMinionsKilled()));

                                List<PlayerDto> fellowPlayers = game.getFellowPlayers();
                                if (fellowPlayers != null) {
                                    for (PlayerDto playerDto : fellowPlayers) {
                                        recentGameWrapper.addPlayer(playerDto.getSummonerId(),
                                                championUrl + championImage.get(playerDto.getChampionId()).getChampionImage(),
                                                playerDto.getTeamId());
                                    }
                                }
                                recentGameWrapper.addPlayer(
                                        mySummonerId[0],
                                        championUrl + championImage.get(game.getChampionId()).getChampionImage(),
                                        game.getTeamId()
                                );

                                finalGameList.add(recentGameWrapper);
                            })
                            .toList()
                            .map(whatever -> finalGameList)
                            .flatMap(this::updateWithSummonerNames)
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnSubscribe(() -> enableProgressBar(true))
                            .doOnUnsubscribe(() -> enableProgressBar(false))
                            .subscribe(new Subscriber<List<RecentGameWrapper>>() {
                                @Override
                                public void onCompleted() {
                                }

                                @Override
                                public void onError(Throwable e) {
                                    AppContextUtils.showSnackbar(RecentGameFragment.this.getActivity(), R.string.service_currently_unavailable, Snackbar.LENGTH_INDEFINITE, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            doWsCall(finalGameList, observableList);
                                        }
                                    });
                                }

                                @Override
                                public void onNext(List<RecentGameWrapper> recentGameWrappers) {
                                    adapter.setItems(recentGameWrappers);
                                }
                            });
                })
                        .ignoreElements().subscribe()
        );
    }




    public Observable<List<RecentGameWrapper>> updateWithSummonerNames(List<RecentGameWrapper> gameList) {

        List<String> summonerIdList = new ArrayList<>();

        return Observable.from(gameList)
                .flatMap(recentGameWrapper -> {
                    List<TeamInfo> sumList = new ArrayList<>();
                    List<TeamInfo> team100 = recentGameWrapper.getTeam100();
                    if(team100 != null)
                    sumList.addAll(team100);

                    List<TeamInfo> team200 = recentGameWrapper.getTeam200();
                    if(team200 != null)
                    sumList.addAll(team200);
                    return Observable.from(sumList)
                            .map(TeamInfo::getPlayerId)
                            .map(String::valueOf)
                            .toList()
                            .doOnNext(summonerIdList::addAll)
                            .map(whatever -> recentGameWrapper);
                })
                .toList()
                .flatMap(recentGameWrappers -> riotApiOperation.getSummonerListByIds(summonerIdList)
                        .map(map -> {
                            for (RecentGameWrapper game : recentGameWrappers)
                                for (List<TeamInfo> lti : game.getTeamUrlMap().values())
                                    for (TeamInfo ti : lti) {
                                        String playerIdString = String.valueOf(ti.getPlayerId());
                                        SummonerDto summonerDto = map.get(playerIdString);
                                        ti.setPlayerName(summonerDto.getName());
                                    }
                            return recentGameWrappers;
                        }));


    }

    public void enableProgressBar(boolean state) {
        progressBar.setVisibility(state ? View.VISIBLE : View.INVISIBLE);
    }
}

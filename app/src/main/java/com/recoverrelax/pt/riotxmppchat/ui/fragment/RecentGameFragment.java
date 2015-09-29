package com.recoverrelax.pt.riotxmppchat.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.recoverrelax.pt.riotxmppchat.Adapter.RecentGameAdapter;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Game.GameDto;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Game.PlayerDto;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiOperations;
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiRealmDataVersion;
import com.recoverrelax.pt.riotxmppchat.Riot.Enum.TeamCode;
import com.recoverrelax.pt.riotxmppchat.ui.activity.BaseActivity;
import com.recoverrelax.pt.riotxmppchat.ui.activity.CurrentGameActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

    private Subscription subscription;
    private long userId_riotApi;

    @Inject
    RiotApiOperations riotApiOperation;

    @Inject
    RiotApiRealmDataVersion realmData;

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

        adapter = new RecentGameAdapter(this.getActivity(), new ArrayList<>());
        myRecyclerView.setAdapter(adapter);

        userId_riotApi = AppXmppUtils.getSummonerIdByXmppAddress(friendXmppAddress);
    }

    @Override
    public void onResume() {
        super.onResume();

        getRecentGamesInformation();
    }

    public void getRecentGamesInformation() {

        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();

        Observable<Map<Integer, String>> obsChampionsImage = riotApiOperation.getChampionsImage()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Observable<Map<Integer, String>> obsSummonerSpellImage = riotApiOperation.getSummonerSpellImage()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Observable<Map<Integer, String>> obsSummonerItemImage = riotApiOperation.getItemImage()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Observable<String> obsRealmDataChampion = realmData.getChampionDDBaseUrl()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Observable<String> obsRealmDataSummonerSpell = realmData.getSummonerSpellDDBaseUrl()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Observable<String> obsRealmDataItem = realmData.getItemDDBaseUrl()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        /**
         * The DataSet to return at the end
         */

        /**
         *  GET THE SUMMONER SPELL DATA (2 SUMMONER SPELLS)
         */

        List<GameDto> gameList = new ArrayList<>();

        Map<Integer, String> ssImages = new HashMap<>();
        Map<Integer, String> itemImage = new HashMap<>();
        Map<Integer, String> championImage = new HashMap<>();

        final String[] ssUrl = new String[1];
        final String[] itemUrl = new String[1];
        final String[] championUrl = new String[1];


        riotApiOperation.getRecentGamesList(String.valueOf(userId_riotApi))
                .doOnNext(gameDtos -> {
                    gameList.addAll(gameDtos);
                    Log.i("1234", gameDtos.size() + "");
                })

                .flatMap(ignoreThis -> obsSummonerSpellImage
                                .doOnNext(ssImages::putAll)
                )

                .flatMap(ignoreThis -> obsSummonerSpellImage
                                .doOnNext(ssImages::putAll)
                )

                .flatMap(ignoreThis -> obsRealmDataSummonerSpell
                                .doOnNext(s -> ssUrl[0] = s)
                )

                .flatMap(ignoreThis -> obsSummonerItemImage
                                .doOnNext(itemImage::putAll)
                )

                .flatMap(ignoreThis -> obsRealmDataItem
                                .doOnNext(s -> itemUrl[0] = s)
                )

                .flatMap(ignoreThis -> obsChampionsImage
                                .doOnNext(championImage::putAll)
                )

                .flatMap(ignoreThis -> obsRealmDataChampion
                                .doOnNext(s -> championUrl[0] = s)
                )

                .flatMap(whatever -> Observable.from(gameList)
                                .doOnNext(game -> {
                                    game.setSpell1Image(ssUrl[0] + ssImages.get(game.getSpell1()));
                                    game.setSpell2Image(ssUrl[0] + ssImages.get(game.getSpell2()));

                                    String s0 = itemImage.get(game.getStats().getItem0());
                                    String s1 = itemImage.get(game.getStats().getItem1());
                                    String s2 = itemImage.get(game.getStats().getItem2());
                                    String s3 = itemImage.get(game.getStats().getItem3());
                                    String s4 = itemImage.get(game.getStats().getItem4());
                                    String s5 = itemImage.get(game.getStats().getItem5());

                                    game.getStats().setItemsImage(itemUrl[0] + s0, itemUrl[0] + s1, itemUrl[0] + s2, itemUrl[0] + s3, itemUrl[0] + s4, itemUrl[0] + s5);

                                    game.setChampionImage(championUrl[0] + championImage.get(game.getChampionId()));
                                }).toList()
                                .flatMap(gameDto -> Observable.from(gameList)
                                                .flatMap(gameDto1 ->
                                                                Observable.from(gameDto1.getFellowPlayers())
                                                                        .doOnNext(playerDto -> playerDto.setChampionImage(championUrl[0] + championImage.get(playerDto.getChampionId())))
                                                                        .toList()
                                                )
                                                .flatMap(playerDtos -> Observable.just(gameDto))

                                )
                )
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<GameDto>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<GameDto> gameDtos) {
                        adapter.setItems(gameDtos);
                    }
                });


//        subscription =riotApiOperation.getRecentGamesList(String.valueOf(userId_riotApi)) //List<GameDto>
//                .flatMap(gameDtoList -> obsChampionsImage // Map<Integer, String>
//                                .flatMap(championImageMap ->
//                                                Observable.from(gameDtoList)

//                                                        .flatMap(gameDto ->
//                                                                Observable.from(gameDto.getFellowPlayers())
//                                                                    .doOnNext(playerDto -> {
//                                                                        String s = championImageMap.get(playerDto.getChampionId());
//                                                                        if (s != null)
//                                                                            playerDto.setChampionImage(s);
//                                                                    })
//                                                                        .toList()
//                                                                        .map(ignoreFellows -> gameDto)
//                                                        )
//                                                        .toList()
//
//                                )
//                )

    }
}

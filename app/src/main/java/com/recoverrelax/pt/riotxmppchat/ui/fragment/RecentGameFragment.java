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
import com.recoverrelax.pt.riotxmppchat.ui.activity.BaseActivity;
import com.recoverrelax.pt.riotxmppchat.ui.activity.CurrentGameActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
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

        if(subscription!= null && !subscription.isUnsubscribed())
            subscription.unsubscribe();

        Observable<Map<Integer, String>> obsChampionsImage = riotApiOperation.getChampionsImage()
                .cache()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Observable<Map<Integer, String>> obsSummonerSpellImage = riotApiOperation.getSummonerSpellImage()
                .cache()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Observable<Map<Integer, String>> obsSummonerItemImage = riotApiOperation.getItemImage()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Observable<String> obsRea = realmData.getChampionDDBaseUrl()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Observable<List<GameDto>> recentGamesListObs = riotApiOperation.getRecentGamesList(String.valueOf(userId_riotApi))
                .cache()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        /**
         *  GET THE SUMMONER SPELL DATA (2 SUMMONER SPELLS)
         */

//        recentGamesListObs
//            .flatMap(gameList -> obsSummonerSpellImage  // get the Summoner Spell Image Url from RIOT API
//                                    .flatMap(ssMap ->
//                                                    Observable.from(gameList)
//                                                            .doOnNext(gameDto -> {
//                                                                String s1 = ssMap.get(gameDto.getSpell1());
//                                                                if (s1 != null)
//                                                                    gameDto.setSpell1Image(s1);
//                                                                String s2 = ssMap.get(gameDto.getSpell2());
//                                                                if (s2 != null)
//                                                                    gameDto.setSpell2Image(s2);
//                                                            })
//                                                            .toList()
//                                    )
//            )

        recentGamesListObs
                .flatMap(gameList -> obsSummonerSpellImage
                                    .flatMap(ssMap -> realmData.getSummonerSpellDDBaseUrl()
                                                      .flatMap(ddSummonerSpellUrl -> Observable.from(gameList)

                                    )


                        .flatMap(Observable::from)
                        .flatMap(gameDto -> obsSummonerSpellImage
                                        .map(ssMap -> {
                                            gameDto.setSpell1Image(ssMap.get(gameDto.getSpell1()));
                                            gameDto.setSpell2Image(ssMap.get(gameDto.getSpell2()));
                                            return gameDto;
                                        })
                        )
                        .toList()
                        .subscribeOn(Schedulers.computation())
                        .subscribe(adapter::setItems);




//        subscription =riotApiOperation.getRecentGamesList(String.valueOf(userId_riotApi)) //List<GameDto>
//                .flatMap(gameDtoList -> obsChampionsImage // Map<Integer, String>
//                                .flatMap(championImageMap ->
//                                                Observable.from(gameDtoList)
//                                                        .doOnNext(gameDto -> {
//                                                            String s = championImageMap.get(gameDto.getChampionId());
//                                                            if (s != null)
//                                                                gameDto.setChampionImage(s);
//                                                        })
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
//                .flatMap(gameDtoList -> obsSummonerSpellImage // Map<Integer, String>
//                                .flatMap(summonerSpellImageMap ->
//                                                Observable.from(gameDtoList)
//                                                        .doOnNext(gameDto -> {
//                                                            String s1 = summonerSpellImageMap.get(gameDto.getSpell1());
//                                                            if (s1 != null)
//                                                                gameDto.setSpell1Image(s1);
//                                                            String s2 = summonerSpellImageMap.get(gameDto.getSpell2());
//                                                            if (s2 != null)
//                                                                gameDto.setSpell2Image(s2);
//                                                        })
//                                                .toList()
//                                )
//                )
//                .flatMap(gameDtoList ->
//                                obsSummonerItemImage // Map<Integer, String>
//                                .flatMap(itemImageMap ->
//                                                Observable.from(gameDtoList)
//                                                        .doOnNext(gameDto -> {
//                                                                    String s0 = itemImageMap.get(gameDto.getStats().getItem0());
//                                                                    String s1 = itemImageMap.get(gameDto.getStats().getItem1());
//                                                                    String s2 = itemImageMap.get(gameDto.getStats().getItem2());
//                                                                    String s3 = itemImageMap.get(gameDto.getStats().getItem3());
//                                                                    String s4 = itemImageMap.get(gameDto.getStats().getItem4());
//                                                                    String s5 = itemImageMap.get(gameDto.getStats().getItem5());
//
//                                                                    gameDto.getStats().setItemsImage(s0, s1, s2, s3, s4, s5);
//                                                            })
//                                                        .toList()
//                                )
//                )
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(adapter::setItems);
    }
}

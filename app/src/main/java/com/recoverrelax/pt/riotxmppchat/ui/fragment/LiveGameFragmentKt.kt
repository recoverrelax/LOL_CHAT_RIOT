package com.recoverrelax.pt.riotxmppchat.ui.fragment

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.widget.NestedScrollView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.recoverrelax.pt.riotxmppchat.MainApplication
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.*
import com.recoverrelax.pt.riotxmppchat.R
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.CurrentGame.BannedChampion
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.CurrentGame.CurrentGameInfo
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.CurrentGame.CurrentGameParticipant
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.HelperModel.ChampionInfo
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiOperations
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiRealmDataVersion
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiService.RiotApiServiceImpl
import com.recoverrelax.pt.riotxmppchat.Widget.AppProgressBar
import com.recoverrelax.pt.riotxmppchat.Widget.CurrentGameBanList
import com.recoverrelax.pt.riotxmppchat.Widget.CurrentGameGlobalInfo
import com.recoverrelax.pt.riotxmppchat.Widget.CurrentGameSingleParticipantBase
import com.recoverrelax.pt.riotxmppchat.ui.activity.LiveGameActivityKt
import org.jetbrains.anko.support.v4.act
import org.jetbrains.anko.support.v4.find
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.util.*
import javax.inject.Inject

class LiveGameFragmentKt: BaseFragment(){

    private val TAG = "LiveGameFragmentKt"

    private val currentGameGlobalInfo by lazy{find<CurrentGameGlobalInfo>(R.id.currentGameGlobalInfo)}
    private val banList by lazy{find<CurrentGameBanList>(R.id.banList)}
    private val currentGameParticipantMainContent by lazy{find<LinearLayout>(R.id.currentGameParticipantMainContent)}
    private val progressBarPicksAndBans by lazy{find<AppProgressBar>(R.id.progressBarPicks)}

    private val team100 by lazy{
        findList<CurrentGameSingleParticipantBase>(
                R.id.team100_player1, R.id.team100_player2, R.id.team100_player3, R.id.team100_player4, R.id.team100_player5
        )
    }
    private val team200 by lazy{
        findList<CurrentGameSingleParticipantBase>(
                R.id.team200_player1, R.id.team200_player2, R.id.team200_player3, R.id.team200_player4, R.id.team200_player5
        )
    }

    @Inject lateinit var riotApiServiceImpl: RiotApiServiceImpl
    @Inject lateinit var riotApiOperations: RiotApiOperations
    @Inject lateinit var realmData: RiotApiRealmDataVersion

    init{
        MainApplication.getInstance().appComponent.inject(this)
    }

    companion object{
        fun newInstance(friendXmppAddress: String, friendUsername: String): LiveGameFragmentKt {
            val frag = LiveGameFragmentKt()

            val args = Bundle()
            args.putString(LiveGameActivityKt.FRIEND_XMPP_ADDRESS_INTENT, friendXmppAddress)
            args.putString(LiveGameActivityKt.FRIEND_XMPP_USERNAME_INTENT, friendUsername)
            frag.arguments = args

            return frag
        }
    }

    private var friendXmppAddress: String? = null
    private var friendUsername: String? = null
    private val subscriptions = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(savedInstanceState == null){
            arguments?.run{
                friendXmppAddress = getString(LiveGameActivityKt.FRIEND_XMPP_ADDRESS_INTENT)
                friendUsername = getString(LiveGameActivityKt.FRIEND_XMPP_USERNAME_INTENT)
            }
        }
        setToolbarTitle("${act.stringFromRes(R.string.current_game__fragment_title)} $friendUsername")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.live_game_fragment, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onResume() {
        super.onResume()
        getDataFromWs()
    }

    override fun onPause() {
        super.onPause()
        subscriptions.clear()
    }

    private fun getDataFromWs() {
        subscriptions.clear()

        friendXmppAddress?.let{
            val userId_riotApi = getSummonerIdFromXmppAddress(it)

            val obsCurrentGameInfoBySummonerId = riotApiServiceImpl.getCurrentGameInfoBySummonerId(userId_riotApi)
                    .cache()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

            val obsChampionsImage = riotApiOperations.championsImage
                    .cache()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

            val obsSummonerSpellImage = riotApiOperations.summonerSpellImage
                    .cache()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())

            val championDDBaseUrl = realmData.championDDBaseUrl
            val summonerSpellDDBaseUrl = realmData.summonerSpellDDBaseUrl

            getGeneralLiveData(obsCurrentGameInfoBySummonerId)
            getBannedChampionInfo(obsCurrentGameInfoBySummonerId, obsChampionsImage, championDDBaseUrl)
            getPickedChampsAndSpells(obsCurrentGameInfoBySummonerId, obsChampionsImage, obsSummonerSpellImage, championDDBaseUrl, summonerSpellDDBaseUrl)
        }
    }

    private fun getGeneralLiveData(obsCurrentGameInfoBySummonerId: Observable<CurrentGameInfo>) {
        /**
         * Get General Live Data
         */
        subscriptions.add(
                obsCurrentGameInfoBySummonerId
                        .doOnSubscribe { currentGameGlobalInfo.enableProgressBar(true) }
                        .doOnUnsubscribe { currentGameGlobalInfo.enableProgressBar(false) }
                        .subscribe(
                                {
                                    currentGameGlobalInfo.setMapName(it.mapName)
                                    currentGameGlobalInfo.setGameQueueType(it.gameQueueFormatted)
                                    currentGameGlobalInfo.setGameMode(it.gameMode)
                                    currentGameGlobalInfo.setGameDuration(it.gameStartTimeFormatted)
                                },
                                {
                                    act.showSnackBar(
                                            messageStringRes = R.string.service_currently_unavailable,
                                            duration = Snackbar.LENGTH_INDEFINITE,
                                            actionTextRes = R.string.webservice_failed_retry,
                                            listener = View.OnClickListener {
                                                getGeneralLiveData(obsCurrentGameInfoBySummonerId)
                                            }
                                    )
                                }
                        )
        )
    }

    private fun getBannedChampionInfo(obsCurrentGameInfoBySummonerId: Observable<CurrentGameInfo>, obsChampionsImage: Observable<Map<Int, ChampionInfo>>, championDDBaseUrl: Observable<String>) {
        /**
         * Get Banned Champion Info
         */
        subscriptions.add(
                obsCurrentGameInfoBySummonerId // get Observable<CurrentGameInfo>
                        .doOnSubscribe { banList.enableProgressBar(true) }
                        .doOnUnsubscribe { banList.enableProgressBar(false) }
                        .map{ it.bannedChampions } // for each bannedChampion Object
                        .take(banList.size)
                        .flatMap { bannedChampions ->
                                Observable.zip(obsChampionsImage, championDDBaseUrl) { championImagesMap, champUrl ->
                                    for (bc in bannedChampions) {
                                        val s = championImagesMap[bc.championId.toInt()]?.championImage
                                        s?.let{ bc.championImage = champUrl + it }
                                    }
                                    bannedChampions
                                }
                        }
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {fetchBannedChampions(it)},
                                {
                                    act.showSnackBar(
                                            messageStringRes =  R.string.service_currently_unavailable,
                                            duration = Snackbar.LENGTH_INDEFINITE,
                                            actionTextRes =  R.string.webservice_failed_retry,
                                            listener = View.OnClickListener {
                                                getBannedChampionInfo(obsCurrentGameInfoBySummonerId, obsChampionsImage, championDDBaseUrl)
                                            }
                                    )
                                }
                        )
        )
    }

    private fun getPickedChampsAndSpells(obsCurrentGameInfoBySummonerId: Observable<CurrentGameInfo>, obsChampionsImage: Observable<Map<Int, ChampionInfo>>, obsSummonerSpellImage: Observable<Map<Int, String>>, championDDBaseUrl: Observable<String>, summonerSpellDDBaseUrl: Observable<String>) {
        /**
         * Get Picked Champions and Spells
         */
        subscriptions.add(
                obsCurrentGameInfoBySummonerId
                        .doOnSubscribe { enableProgressBarParticipantContent(true) }
                        .doOnUnsubscribe { enableProgressBarParticipantContent(false) }
                        .map{it.participants } // for each bannedChampion Object
                        .take(team100.size + team200.size)
                        .flatMap { participantList ->
                                Observable.zip(obsChampionsImage, championDDBaseUrl) { championImagesMap, championDDBaseUrl1 ->
                                    for (cgp in participantList) {
                                        val s = championImagesMap[cgp.championId.toInt()]?.championImage
                                        s?.let{cgp.championImage = championDDBaseUrl1 + it}
                                    }
                                    participantList
                                }
                        }.flatMap { participantList ->
                                Observable.zip(obsSummonerSpellImage, summonerSpellDDBaseUrl) { summonerSpellImagesMap, summonerSpellDDBaseUrl1 ->
                                    for (cgp in participantList) {
                                        val s1 = summonerSpellImagesMap[cgp.spell1Id.toInt()]
                                        if (s1 != null)
                                            cgp.spell1Image = summonerSpellDDBaseUrl1 + s1
                                        val s2 = summonerSpellImagesMap[cgp.spell2Id.toInt()]
                                        if (s2 != null)
                                            cgp.spell2Image = summonerSpellDDBaseUrl1 + s2
                                    }
                                    participantList
                                }
                      }
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {fetchParticipants(it)},
                                {
                                    act.showSnackBar(
                                            messageStringRes = R.string.service_currently_unavailable,
                                            duration = Snackbar.LENGTH_INDEFINITE,
                                            actionTextRes = R.string.webservice_failed_retry,
                                            listener = View.OnClickListener {
                                                getPickedChampsAndSpells(
                                                        obsCurrentGameInfoBySummonerId, obsChampionsImage,
                                                        obsSummonerSpellImage, championDDBaseUrl, summonerSpellDDBaseUrl
                                                )
                                            }
                                    )
                                }
                        )
        )
    }

    private fun fetchParticipants(participants: List<CurrentGameParticipant>) {

        val team1001 = ArrayList<CurrentGameParticipant>()
        val team2001 = ArrayList<CurrentGameParticipant>()

        for (lg in participants) {
            if (lg.teamId == 100L) {
                team1001.add(lg)
            } else {
                team2001.add(lg)
            }
        }

        for (i in team1001.indices) {
            val cgp = LiveGameFragmentKt@this.team100[i]

            val champion = cgp.championPlaying
            val summonerS1 = cgp.summonerSpell1
            val summonerS2 = cgp.summonerSpell2
            val playerName = cgp.playerName

            val liveGameParticipant = team1001[i]

            val pathChampion = liveGameParticipant.championImage
            val pathSS1 = liveGameParticipant.spell1Image
            val pathSS2 = liveGameParticipant.spell2Image

            Glide.with(act).load(pathChampion).into(champion)
            Glide.with(act).load(pathSS1).into(summonerS1)
            Glide.with(act).load(pathSS2).into(summonerS2)

            playerName.text = liveGameParticipant.summonerName
            cgp.setVisible()
        }

        for (i in team2001.indices) {
            val cgp = LiveGameFragmentKt@this.team200.get(i)

            val champion = cgp.championPlaying
            val summonerS1 = cgp.summonerSpell1
            val summonerS2 = cgp.summonerSpell2
            val playerName = cgp.playerName

            val liveGameParticipant = team2001[i]

            val pathChampion = liveGameParticipant.championImage
            val pathSS1 = liveGameParticipant.spell1Image
            val pathSS2 = liveGameParticipant.spell2Image

            Glide.with(act).load(pathChampion).into(champion)
            Glide.with(act).load(pathSS1).into(summonerS1)
            Glide.with(act).load(pathSS2).into(summonerS2)

            playerName.text = liveGameParticipant.summonerName
            cgp.setVisible()
        }
    }

    private fun enableProgressBarParticipantContent(state: Boolean) {
        progressBarPicksAndBans.visibility = if (state) View.VISIBLE else View.GONE
        currentGameParticipantMainContent.visibility = if (state) View.INVISIBLE else View.VISIBLE
    }

    private fun fetchBannedChampions(bannedChampions: List<BannedChampion>) {

        val team1001 = ArrayList<BannedChampion>()
        val team2001 = ArrayList<BannedChampion>()

        for (bc in bannedChampions) {
            if (bc.teamId == 100L) {
                team1001.add(bc)
            } else {
                team2001.add(bc)
            }
        }

        for (i in team1001.indices) {
            val imageView = banList.team100Bans[i]
            imageView.setVisible()

            Glide.with(act).load(team1001[i].championImage).into(imageView)
        }

        for (i in team2001.indices) {
            val imageView = banList.team200Bans[i]
            imageView.setVisible()

            Glide.with(act).load(team2001[i].championImage).into(imageView)
        }
    }
}
package com.recoverrelax.pt.riotxmppchat.ui.mvp.dashboard

import LolChatRiotDb.InAppLogDb
import android.os.Bundle
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.NewMessageReceivedNotifyEvent
import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.OnFriendPresenceChangedEvent
import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.OnNewFriendPlayingEvent
import com.recoverrelax.pt.riotxmppchat.EventHandling.EventHandler
import com.recoverrelax.pt.riotxmppchat.MainApplication
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppSnackbarUtils
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.colorFromRes
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.drawableFromRes
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.showSnackBar
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.RiotXmppDashboardImpl
import com.recoverrelax.pt.riotxmppchat.R
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.HelperModel.ChampionInfo
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiOperations
import com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.RiotApiRealmDataVersion
import com.recoverrelax.pt.riotxmppchat.Widget.FreeChampionRotation
import com.recoverrelax.pt.riotxmppchat.ui.activity.LogActivityKt
import com.recoverrelax.pt.riotxmppchat.ui.fragment.BaseFragment
import org.jetbrains.anko.onClick
import org.jetbrains.anko.onTouch
import org.jetbrains.anko.support.v4.act
import org.jetbrains.anko.support.v4.find
import org.jetbrains.anko.support.v4.intentFor
import org.jivesoftware.smack.packet.Presence
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription
import java.util.*
import javax.inject.Inject

class DashBoardFragmentKt(): BaseFragment(), NewMessageReceivedNotifyEvent,
        OnNewFriendPlayingEvent, OnFriendPresenceChangedEvent {

    private val TAG = "DashBoardFragmentKt"

    private val message_number      by lazy{find<TextView>(R.id.message_number)}
    private val playing_number      by lazy{find<TextView>(R.id.playing_number)}
    private val online_number       by lazy{find<TextView>(R.id.online_number)}
    private val offline_number      by lazy{find<TextView>(R.id.offline_number)}
    private val freeChampRotation1  by lazy{find<FreeChampionRotation>(R.id.freeChampRotation1)}
    private val messagesIcon        by lazy{find<ImageView>(R.id.messagesIcon)}
    private val recyclerView        by lazy{find<RecyclerView>(R.id.recyclerView)}
    private val dashboard_1         by lazy{find<LinearLayout>(R.id.dashboard_1)}
    private val dashboard_2         by lazy{find<LinearLayout>(R.id.dashboard_2)}
    private val dashboard_3         by lazy{find<LinearLayout>(R.id.dashboard_3)}
    private val dashboard_4         by lazy{find<LinearLayout>(R.id.dashboard_4)}

    @Inject lateinit var eventHandler: EventHandler
    @Inject lateinit var dashboardImpl: RiotXmppDashboardImpl
    @Inject lateinit var riotApiOperations: RiotApiOperations
    @Inject lateinit var riotApiRealm: RiotApiRealmDataVersion

    private val myLayoutManager   by lazy{ LinearLayoutManager(act, LinearLayoutManager.VERTICAL, false) }
    private val myAdapter         by lazy{ DashboardAdapterKt(act, recyclerView, R.layout.dashboard_log_layout_white) }
    private val subscriptions     by lazy{CompositeSubscription()}

    companion object{
        fun newInstance(): DashBoardFragmentKt { return DashBoardFragmentKt() }
    }

    init{
        MainApplication.getInstance().appComponent.inject(this)
    }

    override fun onResume() {
        super.onResume()
        eventHandler.registerForNewMessageNotifyEvent(this)
        eventHandler.registerForFriendPlayingEvent(this)
        eventHandler.registerForFriendPresenceChangedEvent(this)

        getLogLast20()
        getUnreadedMessageCount()
        getFriendStatusInfo()
        getFreeChampRotationList(freeChampRotation1.getFreeChamps.size)
    }

    override fun onPause() {
        super.onPause()

        myAdapter.removeSubscriptions()
        eventHandler.unregisterForNewMessageNotifyEvent(this)
        eventHandler.unregisterForFriendPlayingEvent(this)
        eventHandler.unregisterForFriendPresenceChangedEvent(this)

        subscriptions.clear()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dashboard_fragment, container, false)
        setHasOptionsMenu(true)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        configFunctionality()
        configListeners()
    }

    private fun configListeners() {

        fun doTouchEvent(motionEvent: MotionEvent, view: View): Boolean {
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                view.visibility = View.INVISIBLE
            } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                view.visibility = View.VISIBLE
            }
            return false
        }

        dashboard_1.onTouch { view, motionEvent -> doTouchEvent(motionEvent, view) }
        dashboard_2.onTouch { view, motionEvent -> doTouchEvent(motionEvent, view) }
        dashboard_3.onTouch { view, motionEvent -> doTouchEvent(motionEvent, view) }
        dashboard_4.onTouch { view, motionEvent -> doTouchEvent(motionEvent, view) }

        recyclerView.onTouch { view, motionEvent ->
            startActivity(intentFor<LogActivityKt>())
            true
        }

        dashboard_1.onClick { baseActivity.goToMessageListActivity() }
        dashboard_2.onClick { baseActivity.goToFriendListActivity() }
        dashboard_3.onClick { baseActivity.goToFriendListActivity() }
        dashboard_4.onClick { baseActivity.goToFriendListActivity() }


    }

    private fun configFunctionality() {
        /**
         * Tint Message Icon
         */

        val messageDrawable = DrawableCompat.wrap(act.drawableFromRes(R.drawable.dashboard_new_message)).mutate().apply {
            DrawableCompat.setTint(this.mutate(), act.colorFromRes(R.color.white))
        }
        messagesIcon.background = messageDrawable

        /**
         * Config RecyclerView
         */
        recyclerView.layoutManager = myLayoutManager
        recyclerView.adapter = myAdapter
    }

    private fun getLogLast20() {
        subscriptions.add(
                dashboardImpl.logLast20List
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe{
                            setAdapterItems(it)
                        }
        )
    }

    private fun getUnreadedMessageCount() {
        subscriptions.add(
                dashboardImpl.unreadedMessagesCount
                        .subscribe(
                            { message_number.text = it },
                            { message_number.text = "?" }
                )
        )
    }

    private fun getFriendStatusInfo() {
        subscriptions.add(
                dashboardImpl.friendStatusInfo
                        .subscribe(
                                {
                                    playing_number.text = it.friendsOnline.toString()
                                    offline_number.text = it.friendsOffline.toString()
                                    online_number.text = it.friendsPlaying.toString()
                                },
                                {
                                    playing_number.text = "?"
                                    offline_number.text = "?"
                                    online_number.text = "?"
                                }
                        )
        )
    }

    private fun getFreeChampRotationList(size: Int) {
        subscriptions.add(
                Observable.zip<List<Int>, Map<Int, ChampionInfo>, String, List<ChampionInfo>>(
                        riotApiOperations.freeChampRotation,
                        riotApiOperations.championsImage,
                        riotApiRealm.championDDBaseUrl)
                {

                    freechampIds, champImages, champBaseUrl ->

                    val championInfoList = ArrayList<ChampionInfo>()

                    for (champId in freechampIds) {
                        val ci = ChampionInfo()
                        ci.championId = champId.toLong()
                        ci.championName = champImages[champId]?.championName
                        ci.championImage = champBaseUrl + champImages[champId]?.championImage

                        championInfoList.add(ci)
                    }

                    championInfoList.subList(0, size)
                }

                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { freeChampRotation1.showProgressBar(true) }
                .doOnUnsubscribe { freeChampRotation1.showProgressBar(false) }
                .subscribe(
                        {
                            for (i in 0..size - 1) {
                                Glide.with(this)
                                        .load(it[i].championImage)
                                        .into(freeChampRotation1.getFreeChamps[i])
                            }
                        },
                        {
                            act.showSnackBar(
                                    messageStringRes = R.string.service_currently_unavailable,
                                    duration = AppSnackbarUtils.LENGTH_INDEFINITE,
                                    actionTextRes = R.string.webservice_failed_retry,
                                    listener = View.OnClickListener {
                                        getFreeChampRotationList(freeChampRotation1.getFreeChamps.size)
                                    }

                            )
                        },
                        {}
                )
        )
    }

    private fun getLogSingleItem() {
        subscriptions.add(
                dashboardImpl.logSingleItem
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                {setAdapterSingleItem(it)},
                                {
                                    act.showSnackBar(
                                            messageStringRes = R.string.service_currently_unavailable,
                                            duration = AppSnackbarUtils.LENGTH_INDEFINITE,
                                            actionTextRes = R.string.webservice_failed_retry,
                                            listener = View.OnClickListener { getLogSingleItem() }
                                    )
                                }
                        )
        )
    }

    private fun setAdapterSingleItem(inAppLogDb: InAppLogDb?) {
        inAppLogDb?.let{
            myAdapter.setSingleItem(it)
        }
    }

    private fun setAdapterItems(inAppLogDbs: MutableList<InAppLogDb>?) {
        inAppLogDbs?.let{
            myAdapter.items = it
        }
    }

    override fun onNewMessageNotifyReceived(userXmppAddress: String?, username: String?, message: String?, buttonLabel: String?) {
        getLogSingleItem()
        getUnreadedMessageCount()
    }

    override fun onNewFriendPlaying() {
        getFriendStatusInfo()
    }

    override fun onFriendPresenceChanged(presence: Presence?) {
        getFriendStatusInfo()
    }
}
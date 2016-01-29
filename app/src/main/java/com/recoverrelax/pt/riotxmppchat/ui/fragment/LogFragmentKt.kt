package com.recoverrelax.pt.riotxmppchat.ui.fragment

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.NewMessageReceivedNotifyEvent
import com.recoverrelax.pt.riotxmppchat.EventHandling.EventHandler
import com.recoverrelax.pt.riotxmppchat.MainApplication
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.setGone
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.setVisible
import com.recoverrelax.pt.riotxmppchat.Network.RxImpl.RiotXmppDashboardImpl
import com.recoverrelax.pt.riotxmppchat.R
import com.recoverrelax.pt.riotxmppchat.Widget.AppProgressBar
import com.recoverrelax.pt.riotxmppchat.ui.mvp.dashboard.DashboardAdapterKt
import org.jetbrains.anko.support.v4.act
import org.jetbrains.anko.support.v4.find
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription
import javax.inject.Inject

class LogFragmentKt: BaseFragment(), NewMessageReceivedNotifyEvent {

    private val TAG = "LogFragmentKt"

    private val logRecyclerView by lazy{find<RecyclerView>(R.id.logRecyclerView)}
    private val progressBar by lazy{find<AppProgressBar>(R.id.progressBar)}

    private val myLayoutManager by lazy{ LinearLayoutManager(act, LinearLayoutManager.VERTICAL, false) }
    private val myAdapter by lazy{ DashboardAdapterKt(act, logRecyclerView, R.layout.dashboard_log_layout_black) }

    @Inject lateinit var eventHandler: EventHandler
    @Inject lateinit var dashboardImpl: RiotXmppDashboardImpl
    private val subscriptions = CompositeSubscription()

    companion object{
        fun newInstance(): LogFragmentKt {
            return LogFragmentKt()
        }
    }

    init{
        MainApplication.getInstance().appComponent.inject(this)
    }

    override fun onResume() {
        super.onResume()
        eventHandler.registerForNewMessageNotifyEvent(this)
    }

    override fun onPause() {
        super.onPause()
        eventHandler.unregisterForNewMessageNotifyEvent(this)
        subscriptions.clear()
        myAdapter.removeSubscriptions()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.log_fragment, container, false);
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        with(logRecyclerView){
            layoutManager = myLayoutManager
            adapter = myAdapter
        }
        getLogLast20()
    }

    private fun getLogLast20() {
        subscriptions.add(
                dashboardImpl.logLast20List
                        .doOnUnsubscribe { progressBar.setGone() }
                        .doOnSubscribe { progressBar.setVisible() }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe { inAppLog ->
                            inAppLog?.let{
                                myAdapter.items = it
                            }
                    }
        )
    }

    private fun getLogSingleItem() {
        subscriptions.add(
                dashboardImpl.logSingleItem
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe{
                            it?.let{
                                myAdapter.setSingleItem(it)
                            }
                        }
        )
    }

    override fun onNewMessageNotifyReceived(userXmppAddress: String?, username: String?, message: String?, buttonLabel: String?) {
        getLogSingleItem()
    }
}
package com.recoverrelax.pt.riotxmppchat.ui.mvp.dashboard

import LolChatRiotDb.InAppLogDb
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.EMPTY_STRING
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.getElapsedTimeDateFormat
import com.recoverrelax.pt.riotxmppchat.R
import org.jetbrains.anko.find
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class DashboardAdapterKt(
        val context: Context,
        val recyclerView: RecyclerView,
        val layout: Int): RecyclerView.Adapter<DashboardAdapterKt.ViewHolder>() {

    private val subscriptions = CompositeSubscription()
    private val inflater by lazy{ LayoutInflater.from(context) }
    public var items: MutableList<InAppLogDb> by Delegates.observable(arrayListOf(), { prop, old, new ->
        removeSubscriptions()
        notifyDataSetChanged()
    })

    public fun removeSubscriptions() {
        subscriptions.clear()
    }

    private val updateInterval = 30000L

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder? {
        val view = inflater.inflate(layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.logMessage.text = item.logMessage

        if(item.logDate != null ){
            subscriptions.add(
                    Observable.interval(updateInterval, TimeUnit.MILLISECONDS)
                            .map{item.logDate.getElapsedTimeDateFormat()}
                            .doOnSubscribe { holder.logDate.text = item.logDate.getElapsedTimeDateFormat() }
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe{ holder.logDate.text = it }
            )
        } else {
            holder.logDate.text = EMPTY_STRING
        }

    }

    public fun setSingleItem(item: InAppLogDb){
        items.add(0, item)
        notifyItemInserted(0)
        recyclerView.scrollToPosition(0)
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val logDate by lazy{view.find<TextView>(R.id.logDate)}
        val logMessage by lazy{view.find<TextView>(R.id.logMessage)}
    }
}
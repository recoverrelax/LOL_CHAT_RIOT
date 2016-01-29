package com.recoverrelax.pt.riotxmppchat.ui.activity

import android.support.design.widget.AppBarLayout
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.TextView
import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.OnDisconnectEvent
import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.OnReconnectEvent
import com.recoverrelax.pt.riotxmppchat.R
import org.jetbrains.anko.findOptional

class BaseActivityKt: AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OnReconnectEvent, OnDisconnectEvent {

    private val NAVDRAWER_LAUNCH_DELAY = 250

    private val toolbar by lazy{findOptional<Toolbar>(R.id.app_bar)}
    private val appBarLayout by lazy{findOptional<AppBarLayout>(R.id.appBarLayout)}
    private val navigationView by lazy{findOptional<NavigationView>(R.id.navigationView)}
    private val drawer_layout by lazy{findOptional<DrawerLayout>(R.id.drawer_layout)}
    private val toolbar_title by lazy{findOptional<TextView>(R.id.toolbar_title)}


    override fun onNavigationItemSelected(item: MenuItem?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun onReconnect() {
        throw UnsupportedOperationException()
    }

    override fun onDisconnect() {
        throw UnsupportedOperationException()
    }

}
package com.recoverrelax.pt.riotxmppchat.ui.activity

import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.colorFromRes
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.overridePendingTransitionBackAppDefault
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.stringFromRes
import com.recoverrelax.pt.riotxmppchat.R
import com.recoverrelax.pt.riotxmppchat.ui.fragment.settings.SettingsGeneralFragment
import com.recoverrelax.pt.riotxmppchat.ui.fragment.settings.Settings_Alert
import com.recoverrelax.pt.riotxmppchat.ui.fragment.settings.Settings_Notification
import org.jetbrains.anko.find

class SettingsActivityKt: MessageIconActivityKt(){

    private val settings_pager by lazy{find<ViewPager>(R.id.settings_pager)}
    private val tabs by lazy{find<TabLayout>(R.id.tabs)}

    private val pagerAdapter by lazy{
        SettingsPagerAdapter(supportFragmentManager)
    }

    override fun getLayoutResources() = R.layout.settings_activity
    override fun hasNewMessageIcon() = false
    override fun getToolbarTitle() = stringFromRes(R.string.settings_activity)
    override fun getToolbarColor() = colorFromRes(R.color.primaryColor)
    override fun getToolbarTitleColor() = null
    override fun getNavigationViewPosition() = R.id.navigation_item_4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        settings_pager.adapter = pagerAdapter
        tabs.setupWithViewPager(settings_pager)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransitionBackAppDefault()
    }

    inner class SettingsPagerAdapter(val fm: FragmentManager): FragmentStatePagerAdapter(fm) {

        private val TITLES = arrayOf("Notification", "General", "Alert")

        override fun getItem(position: Int): Fragment? {
            when (position) {
                0 -> return Settings_Notification.newInstance()
                1 -> return SettingsGeneralFragment.newInstance()
                else -> return Settings_Alert.newInstance()
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return TITLES[position]
        }

        override fun getCount(): Int {
            return TITLES.size
        }

    }
}
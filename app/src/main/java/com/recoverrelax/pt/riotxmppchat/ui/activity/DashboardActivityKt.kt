package com.recoverrelax.pt.riotxmppchat.ui.activity

import android.content.Intent
import android.os.Bundle
import com.recoverrelax.pt.riotxmppchat.MainApplication
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.colorFromRes
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.injectFragment
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.stringFromRes
import com.recoverrelax.pt.riotxmppchat.R
import com.recoverrelax.pt.riotxmppchat.ui.mvp.dashboard.DashBoardFragmentKt

class DashboardActivityKt: MessageIconActivityKt(){
    override fun getLayoutResources() = R.layout.dashboard_activity
    override fun getToolbarTitle() = stringFromRes(R.string.dashboard)
    override fun getToolbarColor() = colorFromRes(R.color.black)
    override fun getToolbarTitleColor() = null
    override fun getNavigationViewPosition() = R.id.navigation_item_0
    override fun hasNewMessageIcon() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainApplication.getInstance().appComponent.inject(this)
        if(savedInstanceState == null){
            injectFragment(R.id.container, DashBoardFragmentKt.newInstance())
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME))
    }
}
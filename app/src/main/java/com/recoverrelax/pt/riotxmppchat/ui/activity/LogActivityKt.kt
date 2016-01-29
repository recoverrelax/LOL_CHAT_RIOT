package com.recoverrelax.pt.riotxmppchat.ui.activity

import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.colorFromRes
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.injectFragment
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.overridePendingTransitionBackAppDefault
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.stringFromRes
import com.recoverrelax.pt.riotxmppchat.R
import com.recoverrelax.pt.riotxmppchat.ui.fragment.LogFragmentKt
import org.jetbrains.anko.find

class LogActivityKt: MessageIconActivityKt(){

    private val collapsing_toolbar by lazy{find<CollapsingToolbarLayout>(R.id.collapsing_toolbar)}

    override fun getLayoutResources() = R.layout.log_activity
    override fun getToolbarTitle() = stringFromRes(R.string.log_activity)
    override fun getToolbarColor() = colorFromRes(R.color.white)
    override fun getToolbarTitleColor() = colorFromRes(R.color.black)
    override fun getNavigationViewPosition() = R.id.navigation_item_1
    override fun hasNewMessageIcon() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(savedInstanceState == null){
            injectFragment(R.id.container, LogFragmentKt.newInstance())
        }

        collapsing_toolbar.title = "Latest Friend's activity"
        collapsing_toolbar.setCollapsedTitleTextColor(colorFromRes(R.color.white))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransitionBackAppDefault()
    }
}
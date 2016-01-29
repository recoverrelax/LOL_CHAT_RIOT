package com.recoverrelax.pt.riotxmppchat.ui.activity

import android.os.Bundle
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.colorFromRes
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.injectFragment
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.overridePendingTransitionBackAppDefault
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.stringFromRes
import com.recoverrelax.pt.riotxmppchat.R
import com.recoverrelax.pt.riotxmppchat.ui.mvp.friendlist.FriendListFragment

class FriendListActivityKt: MessageIconActivityKt(){


    override fun getToolbarTitle() = stringFromRes(R.string.friends_online)
    override fun getToolbarColor() = colorFromRes(R.color.primaryColor120)
    override fun getToolbarTitleColor() = null
    override fun getLayoutResources() = R.layout.friends_list_activity
    override fun getNavigationViewPosition() = R.id.navigation_item_1
    override fun hasNewMessageIcon() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(savedInstanceState == null){
            injectFragment(R.id.container, FriendListFragment.newInstance())
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransitionBackAppDefault()
    }
}
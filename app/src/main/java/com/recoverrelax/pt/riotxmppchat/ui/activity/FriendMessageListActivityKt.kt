package com.recoverrelax.pt.riotxmppchat.ui.activity

import android.os.Bundle
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.colorFromRes
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.injectFragment
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.overridePendingTransitionBackAppDefault
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.stringFromRes
import com.recoverrelax.pt.riotxmppchat.R
import com.recoverrelax.pt.riotxmppchat.ui.mvp.messagelist.MessageListFragment

class FriendMessageListActivityKt: MessageIconActivityKt() {

    override fun getLayoutResources() = R.layout.friend_message_list_activity
    override fun hasNewMessageIcon() = false
    override fun getToolbarTitle() = stringFromRes(R.string.message_list_title)
    override fun getToolbarColor() = colorFromRes(R.color.transparent)
    override fun getToolbarTitleColor() = null
    override fun getNavigationViewPosition() = R.id.navigation_item_2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(savedInstanceState == null){
            injectFragment(R.id.container, MessageListFragment.newInstance())
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransitionBackAppDefault()
    }
}
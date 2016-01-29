package com.recoverrelax.pt.riotxmppchat.ui.activity

import android.os.Bundle
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.colorFromRes
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.injectFragment
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.overridePendingTransitionBackAppDefault
import com.recoverrelax.pt.riotxmppchat.R
import com.recoverrelax.pt.riotxmppchat.ui.mvp.chat.ChatFragment

class ChatActivityKt: MessageIconActivityKt(){

    companion object{
        @JvmField val INTENT_FRIEND_NAME = "intent_friend_name"
        @JvmField val INTENT_FRIEND_XMPPNAME = "intent_friend_xmppname"
        @JvmField var friendUsername: String? = null
        @JvmField var friendXmppName: String? = null
    }

    override fun getLayoutResources() = R.layout.personal_message_list_activity
    override fun getToolbarTitle() = null
    override fun getToolbarColor() = colorFromRes(R.color.transparent)
    override fun getToolbarTitleColor() = null
    override fun getNavigationViewPosition() = R.id.navigation_item_2
    override fun hasNewMessageIcon() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(savedInstanceState == null){
            intent.extras?.run{
                friendUsername = getString(INTENT_FRIEND_NAME)
                friendXmppName = getString(INTENT_FRIEND_XMPPNAME)
            }
            injectFragment(R.id.container, ChatFragment.newInstance(friendUsername, friendXmppName))
        }
    }



    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransitionBackAppDefault()
    }
}
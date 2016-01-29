package com.recoverrelax.pt.riotxmppchat.ui.activity

import android.os.Bundle
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.EMPTY_STRING
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.colorFromRes
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.injectFragment
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.overridePendingTransitionBackAppDefault
import com.recoverrelax.pt.riotxmppchat.R
import com.recoverrelax.pt.riotxmppchat.ui.fragment.RecentGameFragment

class RecentGameActivityKt: MessageIconActivityKt(){

    companion object{
        val FRIEND_XMPP_ADDRESS_INTENT = "friend_xmpp_address_intent"
        val FRIEND_XMPP_USERNAME_INTENT = "friend_xmpp_username"
    }

    override fun getLayoutResources() = R.layout.recent_game_activity
    override fun hasNewMessageIcon() = true
    override fun getToolbarTitle() = null
    override fun getToolbarColor() = colorFromRes(R.color.primaryColor)
    override fun getToolbarTitleColor() = null
    override fun getNavigationViewPosition() = -1

    private var friendXmppAddress: String? = null
    private var friendXmppUsername: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(savedInstanceState == null){
            intent?.extras?.run{
                friendXmppAddress = getString(FRIEND_XMPP_ADDRESS_INTENT, EMPTY_STRING)
                friendXmppUsername = getString(FRIEND_XMPP_USERNAME_INTENT, EMPTY_STRING)
            }

            injectFragment(R.id.container, RecentGameFragment.newInstance(friendXmppAddress, friendXmppUsername))
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransitionBackAppDefault()
    }
}
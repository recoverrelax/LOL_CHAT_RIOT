package com.recoverrelax.pt.riotxmppchat.ui.activity

import android.os.Bundle
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.EMPTY_STRING
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.colorFromRes
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.injectFragment
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.overridePendingTransitionBackAppDefault
import com.recoverrelax.pt.riotxmppchat.R
import com.recoverrelax.pt.riotxmppchat.ui.fragment.LiveGameFragmentKt

class LiveGameActivityKt: MessageIconActivityKt(){

    companion object{
        @JvmField val FRIEND_XMPP_ADDRESS_INTENT = "friend_xmpp_address_intent"
        @JvmField val FRIEND_XMPP_USERNAME_INTENT = "friend_xmpp_username"
        @JvmField val FRIEND_XMPP_USERNAME_ME = "me"
    }

    private var friendXmppAddress: String? = null
    private var friendXmppUsername: String? = null

    override fun getLayoutResources() = R.layout.live_game_activity
    override fun getToolbarTitle() = null
    override fun getToolbarColor() = colorFromRes(R.color.transparent)
    override fun getToolbarTitleColor() = null
    override fun getNavigationViewPosition() = R.id.navigation_item_1
    override fun hasNewMessageIcon() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(savedInstanceState == null){
            intent.extras?.run{
                friendXmppAddress = getString(FRIEND_XMPP_ADDRESS_INTENT, EMPTY_STRING)
                friendXmppUsername = getString(FRIEND_XMPP_USERNAME_INTENT, EMPTY_STRING)
            }

            injectFragment(R.id.container, LiveGameFragmentKt.newInstance(friendXmppAddress!!, friendXmppUsername!!))
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransitionBackAppDefault()
    }
}
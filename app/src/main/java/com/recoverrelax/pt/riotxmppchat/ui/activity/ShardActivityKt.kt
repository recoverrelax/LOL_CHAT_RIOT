package com.recoverrelax.pt.riotxmppchat.ui.activity

import android.os.Bundle
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.colorFromRes
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.injectFragment
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.overridePendingTransitionBackAppDefault
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.stringFromRes
import com.recoverrelax.pt.riotxmppchat.R
import com.recoverrelax.pt.riotxmppchat.ui.fragment.ShardFragment

class ShardActivityKt: MessageIconActivityKt(){

    override fun getLayoutResources() = R.layout.shard_activity
    override fun getToolbarTitle() = stringFromRes(R.string.title_activity_shard)
    override fun getToolbarColor() = colorFromRes(R.color.primaryColorDark)
    override fun getToolbarTitleColor() = null
    override fun getNavigationViewPosition() = R.id.navigation_item_4
    override fun hasNewMessageIcon() = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(savedInstanceState == null){
            injectFragment(R.id.container, ShardFragment.newInstance())
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransitionBackAppDefault()
    }
}
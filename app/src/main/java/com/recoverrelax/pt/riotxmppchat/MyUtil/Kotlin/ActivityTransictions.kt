package com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin

import android.app.Activity
import com.recoverrelax.pt.riotxmppchat.R

fun Activity.overridePendingTransitionBackAppDefault(){
    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
}
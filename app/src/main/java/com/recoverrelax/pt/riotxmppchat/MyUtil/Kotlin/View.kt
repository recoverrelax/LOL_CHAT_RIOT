package com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin

import android.support.v4.app.Fragment
import android.view.View
import org.jetbrains.anko.support.v4.find
import java.util.*

fun View.setVisible() = apply{ visibility = View.VISIBLE }
fun View.setGone() = apply{ visibility = View.GONE }
fun View.setInvisible() = apply{ visibility = View.INVISIBLE }

inline fun <reified T : View> Fragment.findList(vararg viewIdList : Int): List<T>{
    val list = ArrayList<T>()
    for(viewId in viewIdList){
        list.add(find(viewId))
    }
    return list
}
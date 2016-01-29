package com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin

val EMPTY_STRING = ""

fun java.util.Date.getElapsedTimeDateFormat() = android.text.format.DateUtils.getRelativeTimeSpanString(time, java.util.Date().time, 360).toString()
fun getSummonerIdFromXmppAddress(userXmppAddress: String) = userXmppAddress.remove("sum").remove("@pvp.net").toLong()
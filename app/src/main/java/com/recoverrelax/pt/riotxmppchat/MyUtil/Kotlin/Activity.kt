package com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin

import android.app.Activity
import android.graphics.drawable.Drawable
import android.support.annotation.IntDef
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.recoverrelax.pt.riotxmppchat.R
import org.jetbrains.anko.find
import kotlin.annotation.Retention

@IntDef(Snackbar.LENGTH_SHORT.toLong(), Snackbar.LENGTH_LONG.toLong(), Snackbar.LENGTH_INDEFINITE.toLong())
@Retention(AnnotationRetention.SOURCE)
private annotation class SnackbarDuration

fun Activity.showSnackBar(
        @StringRes messageStringRes: Int,
        @SnackbarDuration duration: Int = Snackbar.LENGTH_SHORT,
        backgroundColor: Int = R.color.colorDark,
        textColor: Int = R.color.white,
        actionTextRes: Int? = null,
        actionTextColor: Int = R.color.accentColor,
        listener: View.OnClickListener? = null): Snackbar {

    return showSnackBar(stringFromRes(messageStringRes), duration, backgroundColor, textColor, if(actionTextRes==null) null else stringFromRes(actionTextRes), actionTextColor, listener)
}

fun Activity.showSnackBar(
        messageString: String,
        @SnackbarDuration duration: Int = Snackbar.LENGTH_SHORT,
        backgroundColor: Int = R.color.colorDark,
        textColor: Int = R.color.white,
        actionText: String? = null,
        actionTextColor: Int = R.color.accentColor,
        listener: View.OnClickListener? = null): Snackbar {

    val snackbar = Snackbar.make(
            window.decorView.rootView,
            messageString,
            duration)

    val group = snackbar.view as ViewGroup
    val snackbarTextView = group.find<TextView>(android.support.design.R.id.snackbar_text)

    group.setBackgroundColor(colorFromRes(backgroundColor))
    snackbarTextView.setTextColor(colorFromRes(textColor))

    // set click listener
    if (listener != null && actionText != null) {
        snackbar.setAction(actionText, listener)
        snackbar.setActionTextColor(colorFromRes(actionTextColor))
    }

    snackbar.show()
    return snackbar
}

fun Activity.stringFromRes(id_: Int) = resources.getString(id_)
fun Activity.colorFromRes(id_: Int) = ContextCompat.getColor(this, id_)
fun Activity.drawableFromRes(id_: Int) = ContextCompat.getDrawable(this, id_)

fun Activity.injectFragment(containerId: Int, fragment: Fragment){
    (this as FragmentActivity).supportFragmentManager.beginTransaction().replace(containerId, fragment).commit()
}

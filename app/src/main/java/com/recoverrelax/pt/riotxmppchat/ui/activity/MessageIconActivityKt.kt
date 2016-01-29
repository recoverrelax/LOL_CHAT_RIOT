package com.recoverrelax.pt.riotxmppchat.ui.activity

import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuItem
import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.NewMessageReceivedEvent
import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.NewMessageReceivedNotifyEvent
import com.recoverrelax.pt.riotxmppchat.EventHandling.EventHandler
import com.recoverrelax.pt.riotxmppchat.MainApplication
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppSnackbarUtils
import com.recoverrelax.pt.riotxmppchat.MyUtil.Kotlin.showSnackBar
import com.recoverrelax.pt.riotxmppchat.R
import javax.inject.Inject

abstract class MessageIconActivityKt: BaseActivity(), NewMessageReceivedNotifyEvent, NewMessageReceivedEvent {

    @Inject lateinit var eventHandler: EventHandler

    init{
        MainApplication.getInstance().appComponent.inject(this)
    }

    protected abstract fun hasNewMessageIcon(): Boolean

    override fun onResume() {
        super.onResume()
        eventHandler.registerForNewMessageEvent(this)
        eventHandler.registerForNewMessageNotifyEvent(this)
        if (hasNewMessageIcon())
            invalidateOptionsMenu()
    }

    override fun onPause() {
        super.onPause()
        eventHandler.unregisterForNewMessageEvent(this)
        eventHandler.unregisterForNewMessageNotifyEvent(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        if (hasNewMessageIcon()) {
            menu.findItem(R.id.newMessage).let{
                riotRepository.hasUnreadedMessages()
                        .subscribe{ hasMessages ->
                            it.setVisible(hasMessages)
                        }
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.newMessage -> {
                goToMessageListActivity()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNewMessageNotifyReceived(userXmppAddress: String?, username: String?, message: String?, buttonLabel: String?) {
        if (hasNewMessageIcon())
            invalidateOptionsMenu()
    }

    override fun onNewMessageReceived(userXmppAddress: String, username: String, message: String, buttonLabel: String) {
        if (this.javaClass != ChatActivityKt::class.java || ChatActivityKt.friendXmppName.equals(userXmppAddress)) {
            if (message.toLowerCase().contains("offline")) {
                showSnackBar(message, Snackbar.LENGTH_LONG)
                AppSnackbarUtils.showSnackBar(this, message, AppSnackbarUtils.LENGTH_LONG)
            } else {
                AppSnackbarUtils.showSnackBar(this, message, AppSnackbarUtils.LENGTH_LONG, buttonLabel) { v -> goToMessageActivity(username, userXmppAddress) }
            }
        }
    }

}
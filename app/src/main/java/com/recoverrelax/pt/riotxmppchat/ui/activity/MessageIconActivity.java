package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.NewMessageReceivedEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.NewMessageReceivedNotifyEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.EventHandler;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppSnackbarUtils;
import com.recoverrelax.pt.riotxmppchat.R;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public abstract class MessageIconActivity extends BaseActivity implements NewMessageReceivedNotifyEvent, NewMessageReceivedEvent {

    @Inject
    EventHandler eventHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().getAppComponent().inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        eventHandler.registerForNewMessageEvent(this);
        eventHandler.registerForNewMessageNotifyEvent(this);
        if(hasNewMessageIcon())
            invalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        eventHandler.unregisterForNewMessageEvent(this);
        eventHandler.unregisterForNewMessageNotifyEvent(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if(hasNewMessageIcon()) {
            MenuItem messageItem = menu.findItem(R.id.newMessage);
            riotRepository.hasUnreadedMessages()
                    .subscribe(messageItem::setVisible);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.newMessage) {
            goToMessageListActivity();
            return true;
        }else
        return super.onOptionsItemSelected(item);
    }

    protected abstract boolean hasNewMessageIcon();

    @Override
    public void onNewMessageNotifyReceived(String userXmppAddress, String username, String message, String buttonLabel) {
        if(hasNewMessageIcon())
            invalidateOptionsMenu();
    }

    @Override
    public void onNewMessageReceived(String userXmppAddress, String username, String message, String buttonLabel) {
        if (!(this instanceof ChatActivity) || !((ChatActivity) this).friendXmppName.equals(userXmppAddress)) {
            if(message.toLowerCase().contains("offline")){
                AppSnackbarUtils.showSnackBar(this, message, AppSnackbarUtils.LENGTH_LONG);
            }else{
                AppSnackbarUtils.showSnackBar(this, message, AppSnackbarUtils.LENGTH_LONG, buttonLabel, v -> {
                    goToMessageActivity(username, userXmppAddress);
                });
            }
        }
    }
}

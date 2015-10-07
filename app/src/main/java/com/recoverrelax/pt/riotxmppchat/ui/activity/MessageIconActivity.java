package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuItem;

import com.recoverrelax.pt.riotxmppchat.EventHandling.Event.NewMessageReceivedNotifyEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.EventHandler;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.R;

import javax.inject.Inject;

import rx.Subscriber;

public abstract class MessageIconActivity extends BaseActivity implements NewMessageReceivedNotifyEvent {

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
        eventHandler.registerForNewMessageNotifyEvent(this);
        if(hasNewMessageIcon())
            invalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        eventHandler.unregisterForNewMessageNotifyEvent(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if(hasNewMessageIcon()) {
            MenuItem messageItem = menu.findItem(R.id.newMessage);

            riotRepository.hasUnreadedMessages()
                    .subscribe(new Subscriber<Boolean>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            messageItem.setVisible(aBoolean);
                        }
                    });
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
        if (!(this instanceof ChatActivity) || !((ChatActivity) this).friendXmppName.equals(userXmppAddress)) {
            Snackbar snackbar = Snackbar
                    .make(this.getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_LONG);

            if (userXmppAddress != null && username != null) {
                snackbar.setAction(buttonLabel, view -> {
                    goToMessageActivity(username, userXmppAddress);
                });
            }
            snackbar.show();
        }
        if(hasNewMessageIcon())
            invalidateOptionsMenu();
    }
}

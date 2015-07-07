package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.OnNewMessageEventEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.NotificationCenter.MessageNotification;
import com.recoverrelax.pt.riotxmppchat.MyUtil.NotificationCenter.StatusNotification;
import com.recoverrelax.pt.riotxmppchat.MyUtil.NotificationCenter2;
import com.recoverrelax.pt.riotxmppchat.R;
import com.squareup.otto.Subscribe;

import rx.Subscriber;
import rx.Subscription;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;

public abstract class RiotXmppNewMessageActivity extends BaseActivity{

    private Subscription subscribe;

    @Override
    protected void onResume() {
        super.onResume();
        MainApplication.getInstance().getBusInstance().register(this);
        if(hasNewMessageIcon())
            resetMessageIcon();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainApplication.getInstance().getBusInstance().unregister(this);

        if(subscribe != null && !subscribe.isUnsubscribed())
            subscribe.unsubscribe();
    }

    public void OnNewMessageReceived(final OnNewMessageEventEvent messageReceived) {
        if(hasNewMessageIcon())
            resetMessageIcon();
    }

    public void OnMessageSnackBarReady(MessageNotification event){
        if(doesReceiveMessages())
            event.sendSnackbarNotification(this);
    }

    public void OnStatusSnackBarReady(StatusNotification event){
        if(doesReceiveMessages())
            event.sendSnackbarNotification(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem messageItem = menu.findItem(R.id.newMessage);

        if(hasNewMessageIcon())
        new RiotXmppDBRepository().hasUnreadedMessages()
                .subscribe(new Subscriber<Boolean>() {
                    @Override public void onCompleted() { }
                    @Override public void onError(Throwable e) { }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        messageItem.setVisible(aBoolean);
                    }
                });
        return true;
    }

    public void resetMessageIcon(){
        Toolbar toolbar = getToolbar();
        MenuItem item = toolbar.getMenu().findItem(R.id.newMessage);

        new RiotXmppDBRepository().hasUnreadedMessages()
                .subscribe(new Subscriber<Boolean>() {
                    @Override public void onCompleted() { }
                    @Override public void onError(Throwable e) { }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if(toolbar != null){
                            if(item != null) {
                                item.setVisible(aBoolean);
                                LOGI("111", aBoolean ? "true" : "false");
                            }
                        }
                    }
                });
    }

    public abstract boolean hasNewMessageIcon();
    public abstract boolean doesReceiveMessages();

}

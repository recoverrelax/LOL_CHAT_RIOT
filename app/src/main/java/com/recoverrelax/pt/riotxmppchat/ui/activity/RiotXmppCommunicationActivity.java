package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.FriendStatusChangedEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.OnNewMessageReceivedEventEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.Network.Helper.RiotXmppRosterImpl;
import com.recoverrelax.pt.riotxmppchat.R;
import com.squareup.otto.Subscribe;

import rx.Subscriber;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;

public abstract class RiotXmppCommunicationActivity extends BaseActivity{

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
    }

    @Subscribe
    public void OnNewMessageReceived(final OnNewMessageReceivedEventEvent messageReceived) {
        resetMessageIcon();
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

}

package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.recoverrelax.pt.riotxmppchat.Storage.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.EventHandling.OnNewMessageEventEvent;
import com.recoverrelax.pt.riotxmppchat.R;

import rx.Subscriber;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.LogUtils.LOGI;

public abstract class RiotXmppNewMessageActivity extends BaseActivity{

    @Override
    protected void onResume() {
        super.onResume();
    }

    void OnNewMessageReceived(final OnNewMessageEventEvent messageReceived) {
        if(hasNewMessageIcon())
            resetMessageIcon();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem messageItem = menu.findItem(R.id.newMessage);

        if(hasNewMessageIcon())
            riotRepository.hasUnreadedMessages()
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

    void resetMessageIcon(){
        Toolbar toolbar = getToolbar();
        MenuItem item = toolbar.getMenu().findItem(R.id.newMessage);

        riotRepository.hasUnreadedMessages()
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

    protected abstract boolean hasNewMessageIcon();
}

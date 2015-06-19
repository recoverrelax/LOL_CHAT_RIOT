package com.recoverrelax.pt.riotxmppchat.ui.activity;

import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.FriendStatusGameNotificationEvent;
import com.recoverrelax.pt.riotxmppchat.EventHandling.Global.OnNewMessageReceivedEventEvent;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.NotificationCenter;
import com.recoverrelax.pt.riotxmppchat.R;
import com.squareup.otto.Subscribe;

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

    public void OnFriendStatusGameNotification(FriendStatusGameNotificationEvent notification){
        NotificationCenter.sendLeftGameSnackbarNotificationNoAction(this, notification.getFriendXmppAddress());
    }

    @Subscribe
    public void OnNewMessageReceived(final OnNewMessageReceivedEventEvent messageReceived) {
        String message = messageReceived.getMessage().getBody();
        String username = messageReceived.getUsername();
        String userXmppAddress = messageReceived.getMessageFrom();

        NotificationCenter.sendNewMessageSnackbarNotification(this, message, username, userXmppAddress, "CHAT");
        resetMessageIcon();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean b = super.onCreateOptionsMenu(menu);

        boolean hasUnreaded = MainApplication.getInstance().hasNewMessages();
        MenuItem messageItem = menu.findItem(R.id.newMessage);
        if(hasNewMessageIcon()) {
            if (hasUnreaded) {
                messageItem.setVisible(true);
//
//            messageItem.setActionView(R.layout.new_message_view);
//            View actionView = messageItem.getActionView();
//
//            imageviewMessage = ButterKnife.findById(actionView, R.id.newMessage);
//            actionView.setOnClickListener(view -> goToMessageListActivity());
//
//            AppContextUtils.setBlinkAnimation(imageviewMessage, true);
            } else {
                messageItem.setVisible(false);
//            messageItem.setActionView(null);
//            if(imageviewMessage != null)
//                AppContextUtils.setBlinkAnimation(imageviewMessage, false);
            }
        }

        return b;
    }

    public void resetMessageIcon(){
        boolean hasUnreaded = MainApplication.getInstance().hasNewMessages();

        Toolbar toolbar = getToolbar();
        if(toolbar != null){
            MenuItem item = toolbar.getMenu().findItem(R.id.newMessage);
            if(item != null)
                runOnUiThread(() -> item.setVisible(hasUnreaded));
        }

    }

    public abstract boolean hasNewMessageIcon();

}

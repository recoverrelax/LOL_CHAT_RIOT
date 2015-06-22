package com.recoverrelax.pt.riotxmppchat.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.DialogFragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ScrollView;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.R;

import LolChatRiotDb.NotificationDb;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class NotificationCustomDialogFragment extends DialogFragment {

    @InjectView(R.id.title)
    TextView title;

    @InjectView(R.id.isOnline)
    SwitchCompat isOnline;

    @InjectView(R.id.isOffline)
    SwitchCompat isOffline;

    @InjectView(R.id.hasLeftGame)
    SwitchCompat hasLeftGame;

    @InjectView(R.id.hasStartedGame)
    SwitchCompat hasStartedGame;

    @InjectView(R.id.hasSentPm)
    SwitchCompat hasSentPm;

    public static String USER_XMPP_ADDRESS;
    public static String FRIEND_XMPP_ADDRESS;

    private String connectedUserName;
    private String friendXmppUser;
    private NotificationDb notification;

    public static NotificationCustomDialogFragment newInstance(String friendXmppAddress, String connectedXmppAddress) {

        NotificationCustomDialogFragment notificationCustomDialogFragment = new NotificationCustomDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putString(USER_XMPP_ADDRESS, connectedXmppAddress);
        bundle.putString(FRIEND_XMPP_ADDRESS, friendXmppAddress);

        notificationCustomDialogFragment.setArguments(bundle);
        return notificationCustomDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ScrollView scroller = new ScrollView(getActivity());
        View view = inflater.inflate(R.layout.notification_custom_dialog_fragment, container, false);
        ButterKnife.inject(this, view);
        scroller.addView(view);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.materialBlueGrey200)));
        return scroller;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @OnClick({R.id.isOnline, R.id.isOffline, R.id.hasSentPm, R.id.hasStartedGame, R.id.hasLeftGame})
    public void onItemClick(SwitchCompat switchButton){
        int id = switchButton.getId();

        switch (id) {
            case R.id.isOnline:
                notification.setIsOnline(isOnline.isChecked());
                break;
            case R.id.isOffline:
                notification.setIsOffline(isOffline.isChecked());
                break;
            case R.id.hasSentPm:
                notification.setHasSentMePm(hasSentPm.isChecked());
                break;
            case R.id.hasStartedGame:
                notification.setHasStartedGame(hasStartedGame.isChecked());
                break;
            case R.id.hasLeftGame:
                notification.setHasLefGame(hasLeftGame.isChecked());
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        RiotXmppDBRepository.updateNotification(notification);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(savedInstanceState == null){
            Bundle extra = getArguments();

            connectedUserName = extra.getString(USER_XMPP_ADDRESS);
            friendXmppUser = extra.getString(FRIEND_XMPP_ADDRESS);

            notification = RiotXmppDBRepository.getNotificationByUser(connectedUserName, friendXmppUser);

            String connUserName2 = MainApplication.getInstance().getRiotXmppService().getRiotRosterManager().getRosterEntry(connectedUserName).getName();
            title.setText("Notify me when " + connUserName2 + " is...");

            isOffline.setChecked(notification.getIsOffline());
            isOnline.setChecked(notification.getIsOnline());

            hasSentPm.setChecked(notification.getHasSentMePm());
            hasLeftGame.setChecked(notification.getHasLefGame());
            hasStartedGame.setChecked(notification.getHasStartedGame());
        }
    }
}
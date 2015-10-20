package com.recoverrelax.pt.riotxmppchat.ui.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ScrollView;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppMiscUtils;
import com.recoverrelax.pt.riotxmppchat.Network.Manager.RiotRosterManager;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.Storage.RiotXmppDBRepository;

import org.jivesoftware.smack.roster.RosterEntry;

import javax.inject.Inject;

import LolChatRiotDb.NotificationDb;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pt.reco.myutil.MyContext;
import rx.Subscriber;

@SuppressWarnings("FieldCanBeLocal")
public class NotificationCustomDialogFragment extends DialogFragment {

    public static String FRIEND_XMPP_ADDRESS = "friend_xmpp_address";
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.isOnline)
    SwitchCompat isOnline;
    @Bind(R.id.isOffline)
    SwitchCompat isOffline;
    @Bind(R.id.hasLeftGame)
    SwitchCompat hasLeftGame;
    @Bind(R.id.hasStartedGame)
    SwitchCompat hasStartedGame;
    @Bind(R.id.hasSentPm)
    SwitchCompat hasSentPm;
    @Inject
    RiotXmppDBRepository riotXmppDBRepository;
    @Inject
    RiotRosterManager riotRosterManager;
    private String friendXmppUser;
    private NotificationDb notification;

    public static NotificationCustomDialogFragment newInstance(String friendXmppAddress) {

        NotificationCustomDialogFragment notificationCustomDialogFragment = new NotificationCustomDialogFragment();

        Bundle bundle = new Bundle();
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
        ButterKnife.bind(this, view);
        scroller.addView(view);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(MyContext.getColor(this.getActivity(), R.color.materialBlueGrey200)));
        return scroller;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.getInstance().getAppComponent().inject(this);
    }

    @OnClick({R.id.isOnline, R.id.isOffline, R.id.hasSentPm, R.id.hasStartedGame, R.id.hasLeftGame})
    public void onItemClick(SwitchCompat switchButton) {
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

        riotXmppDBRepository.updateNotification(notification)
                .subscribe(new Subscriber<Long>() {
                    @Override public void onCompleted() {
                    }

                    @Override public void onError(Throwable e) {
                    }

                    @Override public void onNext(Long aLong) {
                    }
                });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String backgroundColor = AppMiscUtils.getRandomMaterialColor(this.getActivity());
        title.setBackgroundColor(Color.parseColor(backgroundColor));

        if (savedInstanceState == null) {
            Bundle extra = getArguments();

            friendXmppUser = extra.getString(FRIEND_XMPP_ADDRESS);
            riotXmppDBRepository.getNotificationByUser(friendXmppUser)
                    .subscribe(new Subscriber<NotificationDb>() {
                        @Override public void onCompleted() {
                        }

                        @Override public void onError(Throwable e) {
                        }

                        @Override
                        public void onNext(NotificationDb notificationDb) {
                            notification = notificationDb;
                            isOffline.setChecked(notification.getIsOffline());
                            isOnline.setChecked(notification.getIsOnline());

                            hasSentPm.setChecked(notification.getHasSentMePm());
                            hasLeftGame.setChecked(notification.getHasLefGame());
                            hasStartedGame.setChecked(notification.getHasStartedGame());
                        }
                    });

            riotRosterManager.getRosterEntry(friendXmppUser)
                    .filter(rosterEntry -> rosterEntry != null)
                    .subscribe(new Subscriber<RosterEntry>() {
                        @Override public void onCompleted() {
                        }

                        @Override public void onError(Throwable e) {
                        }

                        @Override
                        public void onNext(RosterEntry rosterEntry) {
                            title.setText(getActivity().getResources().getString(R.string.notify_me_when, rosterEntry.getName()));
                        }
                    });
        }
    }
}
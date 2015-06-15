package com.recoverrelax.pt.riotxmppchat.ui.fragment;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ScrollView;
import android.widget.TextView;

import com.recoverrelax.pt.riotxmppchat.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NotificationCustomDialogFragment extends DialogFragment {

    @InjectView(R.id.title)
    TextView title;

    public static NotificationCustomDialogFragment newInstance() {
        return new NotificationCustomDialogFragment();
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
//        title.setTextColor(getResources().getColor(R.color.white));
        return scroller;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}
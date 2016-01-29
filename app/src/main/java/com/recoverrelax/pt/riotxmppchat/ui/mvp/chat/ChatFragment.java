package com.recoverrelax.pt.riotxmppchat.ui.mvp.chat;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppGlobals;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppMiscUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppSnackbarUtils;
import com.recoverrelax.pt.riotxmppchat.MyUtil.LogUtils;
import com.recoverrelax.pt.riotxmppchat.R;
import com.recoverrelax.pt.riotxmppchat.ui.activity.ChatActivityKt;
import com.recoverrelax.pt.riotxmppchat.ui.fragment.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("FieldCanBeLocal")
public class ChatFragment extends BaseFragment implements ChatPresenterCallbacks {

    private static final String TAG = ChatFragment.class.getSimpleName();


    @Bind(R.id.messageRecyclerView)
    RecyclerView messageRecyclerView;
    @Bind(R.id.chatEditText)
    EditText chatEditText;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.expandButton)
    ImageView expandButton;
    @Bind(R.id.message_layout)
    RelativeLayout message_layout;
    @Bind(R.id.uselessShape)
    FrameLayout uselessShape;

    private String friendXmppName;
    private String friendUsername;
    private SwipeRefreshLayout.OnRefreshListener swipeRefreshListener;

    private int DEFAULT_MESSAGE_RETURNED = AppGlobals.Message.DEFAULT_MESSAGES_RETURNED;
    private int messagesToReturn = DEFAULT_MESSAGE_RETURNED;

    private ChatPresenter presenter;

    public ChatFragment() {

    }

    public static ChatFragment newInstance(String friendUsername, String friendXmppName) {
        ChatFragment chatFragment = new ChatFragment();

        Bundle args = new Bundle();
        args.putString(ChatActivityKt.INTENT_FRIEND_NAME, friendUsername);
        args.putString(ChatActivityKt.INTENT_FRIEND_XMPPNAME, friendXmppName);
        chatFragment.setArguments(args);

        return chatFragment;
    }

    public static int convertDIPToPixels(Context context, int dip) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, displayMetrics);
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new ChatPresenterImpl(this, this.getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_message, container, false);
        ButterKnife.bind(this, view);
        MainApplication.getInstance().getAppComponent().inject(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle extras = getArguments();
        if (extras == null || !extras.containsKey(ChatActivityKt.INTENT_FRIEND_NAME) || !extras.containsKey(ChatActivityKt.INTENT_FRIEND_XMPPNAME)) {
            LogUtils.LOGE(TAG, "Something went wrong, we haven't got a xmppName");
        } else {
            friendUsername = extras.getString(ChatActivityKt.INTENT_FRIEND_NAME);
            friendXmppName = extras.getString(ChatActivityKt.INTENT_FRIEND_XMPPNAME);

            presenter.configRecyclerView();
            presenter.configAdapter(messageRecyclerView);

            presenter.getLastXPersonalMessageList(friendXmppName);

            swipeRefreshListener = () -> {
                doubleLoadedItems();
                presenter.getLastXPersonalMessageList(friendXmppName);
            };
            swipeRefreshLayout.setOnRefreshListener(swipeRefreshListener);
            uselessShape.setTranslationY(convertDIPToPixels(getActivity(), (70 / 2)));

            setToolbarTitle(getResources().getString(R.string.chatting_with, friendUsername));

            String cardColor = AppMiscUtils.getRandomMaterialColor(this.getActivity());
            int colorInt = ColorUtils.setAlphaComponent(Color.parseColor(cardColor), 190);

            getBaseActivity().setToolbarBackgroundColor(colorInt);
            swipeRefreshLayout.setBackgroundColor(colorInt);
        }
    }

    public void doubleLoadedItems() {
        this.messagesToReturn = messagesToReturn * 2;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @OnClick(R.id.expandButton)
    public void sendMessageButton(View view) {
        presenter.sendMessage(getChatText(), friendXmppName);
    }

    @Override public boolean isListRefreshing() {
        return swipeRefreshLayout.isRefreshing();
    }

    @Override public void setListRefreshing(boolean state) {
        if (!state && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        } else if (state && !swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override public void writeToChat(String s) {
        chatEditText.setText(s);
    }

    @Override public void clearChatText() {
        writeToChat("");
    }

    @Override public String getChatText() {
        return chatEditText.getText().toString();
    }

    @Override public String getFriendXmppName() {
        return friendXmppName;
    }

    @Override public String getFriendUsername() {
        return friendUsername;
    }

    @Override public int getMessageSize() {
        return messagesToReturn;
    }

    @Override public void sendFriendOfflineSnack() {
        AppSnackbarUtils.showSnackBar(
                ChatFragment.this.getActivity(),
                R.string.your_friend_is_offline,
                AppSnackbarUtils.LENGTH_LONG
        );
    }

    @Override public void setRecyclerViewLayoutParams(RecyclerView.LayoutManager layoutManager) {
        messageRecyclerView.setLayoutManager(layoutManager);
    }

    @Override public void setRecyclerViewAdapter(ChatAdapter adapter) {
        messageRecyclerView.setAdapter(adapter);
    }
}

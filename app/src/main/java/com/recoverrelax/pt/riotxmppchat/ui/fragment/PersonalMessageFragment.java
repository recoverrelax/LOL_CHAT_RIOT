package com.recoverrelax.pt.riotxmppchat.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.edgelabs.pt.mybaseapp.R;
import com.recoverrelax.pt.riotxmppchat.Adapter.PersonalMessageAdapter;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.Network.Helper.PersonalMessageHelper;
import com.recoverrelax.pt.riotxmppchat.Network.Helper.PersonalMessageImpl;

import java.util.ArrayList;
import java.util.List;

import LolChatRiotDb.MessageDb;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Observer;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalMessageFragment extends Fragment implements Observer<List<MessageDb>> {

    @InjectView(R.id.messageRecyclerView)
    RecyclerView messageRecyclerView;

    @InjectView(R.id.chatEditText)
    EditText chatEditText;

    private RecyclerView.LayoutManager layoutManager;

    /**
     * Adapter
     */
    private PersonalMessageAdapter adapter;

    private PersonalMessageHelper personalMessageHelper;


    public PersonalMessageFragment() {
        // Required empty public constructor
    }

    public static PersonalMessageFragment newInstance() {
        return new PersonalMessageFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        messageRecyclerView.setLayoutManager(layoutManager);

        adapter = new PersonalMessageAdapter(getActivity(), new ArrayList<MessageDb>(), R.layout.personal_message_from, R.layout.personal_message_to);
        messageRecyclerView.setAdapter(adapter);

        personalMessageHelper = new PersonalMessageImpl(this);
        personalMessageHelper.getPersonalMessageList();
    }

    @OnClick(R.id.sendImageView)
    public void sendMessageButton(View view){
        MainApplication.getInstance().getRiotXmppService().sendMessage(chatEditText.getText().toString(), null);
        chatEditText.setText("");
    }

    @Override
    public void onCompleted() {}

    @Override
    public void onError(Throwable e) {
    }

    @Override
    public void onNext(List<MessageDb> messageDbs) {
        adapter.setItems(messageDbs);
    }
}

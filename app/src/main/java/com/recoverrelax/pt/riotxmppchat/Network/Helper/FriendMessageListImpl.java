package com.recoverrelax.pt.riotxmppchat.Network.Helper;

import android.support.v4.app.Fragment;

import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.FriendListChat;

import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import LolChatRiotDb.MessageDb;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FriendMessageListImpl implements FriendMessageListHelper {

    private Observer<List<FriendListChat>> mCallback;
    private Subscription mSubscription;
    private Fragment mFragment;
    private Roster roster;

    private String TAG = this.getClass().getSimpleName();

    public FriendMessageListImpl(Observer<List<FriendListChat>> mCallback, Roster roster) {
        mFragment = (Fragment)mCallback;
        this.mCallback = mCallback;
        this.roster = roster;
    }

    @Override
    public void getPersonalMessageList() {
        mSubscription = AppObservable.bindFragment(mFragment,
                Observable.create(new Observable.OnSubscribe<List<FriendListChat>>() {
                    @Override
                    public void call(Subscriber<? super List<FriendListChat>> subscriber) {

                        List<FriendListChat> friendListChat = new ArrayList<>();
                        /**
                         * First get the friendsList
                         */

                        Collection<RosterEntry> entries = roster.getEntries();
                        List<Friend> friendList = new ArrayList<>();

                        for (RosterEntry entry : entries) {
                            friendList.add(new Friend(entry.getName(), entry.getUser(), roster.getPresence(entry.getUser())));
                        }

                        /**
                         * For each friend, get the last message, if there is a last message
                         */

                        for(Friend friend: friendList){
                            MessageDb message = RiotXmppDBRepository.getLastMessage(friend.getUserXmppAddress());
                            if(message != null)
                                friendListChat.add(new FriendListChat(friend, message));
                        }

                        subscriber.onNext(friendListChat);
                        subscriber.onCompleted();
                    }
                }))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mCallback);
    }
}

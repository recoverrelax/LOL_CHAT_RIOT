package com.recoverrelax.pt.riotxmppchat.Network.Helper;

import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.Network.RiotXmppService;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.FriendListChat;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.RosterEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import LolChatRiotDb.MessageDb;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FriendMessageListImpl implements FriendMessageListHelper {

    private RiotXmppService riotXmppService;
    private FriendMessageListImplCallbacks callback;

    public FriendMessageListImpl(FriendMessageListImplCallbacks callback) {
        this.riotXmppService = MainApplication.getInstance().getRiotXmppService();
        this.callback = callback;
    }

    @Override
    public void getPersonalMessageSingleItem(final String userToReturn) {
        Observable.create(new Observable.OnSubscribe<FriendListChat>() {
            @Override
            public void call(Subscriber<? super FriendListChat> subscriber) {

                FriendListChat friendListChat = null;
                /**
                 * First get the friendsList
                 */

                RosterEntry entry = riotXmppService.getRiotRosterManager().getRosterEntry(userToReturn);
                Presence presence = riotXmppService.getRiotRosterManager().getRosterPresence(entry.getUser());

                Friend friend = new Friend(entry.getName(), entry.getUser(), presence);

                /**
                 * For each friend, get the last message, if there is a last message
                 */

                MessageDb message = RiotXmppDBRepository.getLastMessage(friend.getUserXmppAddress());
                if (message != null)
                    friendListChat = new FriendListChat(friend, message);

                subscriber.onNext(friendListChat);
                subscriber.onCompleted();

            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FriendListChat>() {

                    @Override public void onCompleted() { }
                    @Override public void onError(Throwable e) {
                        if(callback != null)
                            callback.onFriendsMessageSingleFailedReception(e);
                    }

                    @Override
                    public void onNext(FriendListChat friendListChat) {
                        if(callback != null)
                            callback.onFriendsMessageSingleReceived(friendListChat);
                    }
                });
    }

    @Override
    public void getPersonalMessageList() {
        Observable.create(new Observable.OnSubscribe<List<FriendListChat>>() {
            @Override
            public void call(Subscriber<? super List<FriendListChat>> subscriber) {

                List<FriendListChat> friendListChat = new ArrayList<>();
                /**
                 * First get the friendsList
                 */

                Collection<RosterEntry> entries = riotXmppService.getRiotRosterManager().getRosterEntries();
                List<Friend> friendList = new ArrayList<>();

                for (RosterEntry entry : entries) {
                    friendList.add(new Friend(entry.getName(), entry.getUser(), riotXmppService.getRiotRosterManager().getRosterPresence(entry.getUser())));
                }

                /**
                 * For each friend, get the last message, if there is a last message
                 */

                for (Friend friend : friendList) {
                    MessageDb message = RiotXmppDBRepository.getLastMessage(friend.getUserXmppAddress());
                    if (message != null)
                        friendListChat.add(new FriendListChat(friend, message));
                }
                Collections.sort(friendListChat, new FriendListChat.LastMessageComparable());
                Collections.reverse(friendListChat);

                subscriber.onNext(friendListChat);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<FriendListChat>>() {

                               @Override public void onCompleted() { }
                               @Override public void onError(Throwable e) {
                                   if(callback != null)
                                       callback.onFriendsMessageListFailedReception(e);
                               }

                               @Override
                               public void onNext(List<FriendListChat> friendListChat) {
                                   if(callback != null)
                                       callback.onFriendsMessageListReceived(friendListChat);
                               }
                           }
                );
    }

    public interface FriendMessageListImplCallbacks{
        void onFriendsMessageSingleReceived(FriendListChat friendListChat);
        void onFriendsMessageSingleFailedReception(Throwable e);

        void onFriendsMessageListReceived(List<FriendListChat> friendListChat);
        void onFriendsMessageListFailedReception(Throwable e);
    }
}

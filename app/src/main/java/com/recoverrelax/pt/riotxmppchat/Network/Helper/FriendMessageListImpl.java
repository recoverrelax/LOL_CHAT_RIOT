package com.recoverrelax.pt.riotxmppchat.Network.Helper;

import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.Network.RiotXmppService;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.FriendListChat;
import java.util.Collections;
import java.util.List;

import LolChatRiotDb.MessageDb;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FriendMessageListImpl implements FriendMessageListHelper {

    private RiotXmppService riotXmppService;
    private FriendMessageListImplCallbacks callback;
    private FriendImpl friendImpl;

    public FriendMessageListImpl(FriendMessageListImplCallbacks callback) {
        this.riotXmppService = MainApplication.getInstance().getRiotXmppService();
        this.callback = callback;
        this.friendImpl = new FriendImpl();
    }

    @Override
    public void getPersonalMessageSingleItem(final String userToReturn) {

        Observable<Friend> friendFromRosterEntry = friendImpl.getFriendFromXmppAddress(userToReturn, riotXmppService);
        Observable<MessageDb> friendLastMessage = friendImpl.getFriendLastMessage(friendFromRosterEntry);

        Observable.zip(friendFromRosterEntry, friendLastMessage, FriendListChat::new)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<FriendListChat>() {

                    @Override public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) {
                        if (callback != null)
                            callback.onFriendsMessageSingleFailedReception(e);
                    }

                    @Override
                    public void onNext(FriendListChat friendListChat) {
                        if (callback != null)
                            callback.onFriendsMessageSingleReceived(friendListChat);
                    }
                });
    }

    @Override
    public void getPersonalMessageList() {
        Observable.from(riotXmppService.getRiotRosterManager().getRosterEntries())
                .flatMap(rosterEntry -> {

                    Observable<Friend> friendFromRosterEntry = friendImpl.getFriendFromRosterEntry(rosterEntry, riotXmppService);
                    Observable<MessageDb> friendLastMessage = friendImpl.getFriendLastMessage(friendFromRosterEntry);

                    return Observable.zip(friendFromRosterEntry, friendLastMessage, FriendListChat::new);
                })
                .filter(friendListChat -> friendListChat.getLastMessage() != null)
                .toList()
                .flatMap(friendListChatList -> {
                    Collections.sort(friendListChatList, new FriendListChat.LastMessageComparable());
                    Collections.reverse(friendListChatList);
                    return Observable.just(friendListChatList);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<FriendListChat>>() {

                               @Override
                               public void onCompleted() {
                               }

                               @Override
                               public void onError(Throwable e) {
                                   if (callback != null)
                                       callback.onFriendsMessageListFailedReception(e);
                               }

                               @Override
                               public void onNext(List<FriendListChat> friendListChat) {
                                   if (callback != null)
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

package com.recoverrelax.pt.riotxmppchat.Network.Helper;

import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.Network.RiotXmppService;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.FriendListChat;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.RosterEntry;

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

    public FriendMessageListImpl(FriendMessageListImplCallbacks callback) {
        this.riotXmppService = MainApplication.getInstance().getRiotXmppService();
        this.callback = callback;
    }

    @Override
    public void getPersonalMessageSingleItem(final String userToReturn) {
        Observable.just(riotXmppService.getRiotRosterManager())
                .flatMap(rosterManager -> {
                    RosterEntry entry = rosterManager.getRosterEntry(userToReturn);
                    Presence presence = rosterManager.getRosterPresence(entry.getUser());
                    String userXmppAddress = AppXmppUtils.parseXmppAddress(entry.getUser());

                    Friend friend = new Friend(entry.getName(), userXmppAddress, presence);
                    return Observable.just(friend);
                })
                .flatMap(friend -> Observable.just(
                        new FriendListChat(friend, RiotXmppDBRepository.getLastMessage(friend.getUserXmppAddress()))))
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
                    Presence rosterPresence = riotXmppService.getRiotRosterManager().getRosterPresence(rosterEntry.getUser());
                    String userXmppAddress = AppXmppUtils.parseXmppAddress(rosterEntry.getUser());

                    return Observable.just(new Friend(rosterEntry.getName(), userXmppAddress, rosterPresence));
                })
                .flatMap(friend -> {
                    MessageDb message = RiotXmppDBRepository.getLastMessage(friend.getUserXmppAddress());

                    return Observable.just(new FriendListChat(friend, message));
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

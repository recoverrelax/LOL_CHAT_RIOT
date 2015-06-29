package com.recoverrelax.pt.riotxmppchat.Network.Helper;

import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.Network.RiotXmppService;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.RosterEntry;

import LolChatRiotDb.MessageDb;
import rx.Observable;

public class FriendImpl {

    private RiotXmppDBRepository xmppRepository;

    FriendImpl(){
        xmppRepository = new RiotXmppDBRepository();
    }

    public Observable<Friend> getFriendFromXmppAddress(String userXmppAddress, RiotXmppService riotXmppService){
        return Observable.just(riotXmppService.getRiotRosterManager())
                .flatMap(rosterManager -> {
                    RosterEntry entry = rosterManager.getRosterEntry(userXmppAddress);
                    Presence presence = rosterManager.getRosterPresence(entry.getUser());
                    String finalUserXmppAddress = AppXmppUtils.parseXmppAddress(entry.getUser());

                    return Observable.just(new Friend(entry.getName(), finalUserXmppAddress, presence));
                });
    }

    public Observable<RosterEntry> getRosterEntries(RiotXmppService riotXmppService){
        return Observable.from(riotXmppService.getRiotRosterManager().getRosterEntries());
    }

    public Observable<Friend> getFriendFromRosterEntry(RosterEntry entry, RiotXmppService riotXmppService){
        return Observable.just(riotXmppService.getRiotRosterManager())
                .flatMap(rosterManager -> {
                    Presence presence = rosterManager.getRosterPresence(entry.getUser());
                    String finalUserXmppAddress = AppXmppUtils.parseXmppAddress(entry.getUser());

                    return Observable.just(new Friend(entry.getName(), finalUserXmppAddress, presence));
                });
    }

    public Observable<MessageDb> getFriendLastMessage(Observable<Friend> friend){
        return friend.flatMap(friend1 -> xmppRepository.getLastMessage(friend1.getUserXmppAddress()));
    }
}

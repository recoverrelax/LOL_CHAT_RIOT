package com.recoverrelax.pt.riotxmppchat.Network.Helper;

import com.recoverrelax.pt.riotxmppchat.Database.MessageDirection;
import com.recoverrelax.pt.riotxmppchat.Database.RiotXmppDBRepository;
import com.recoverrelax.pt.riotxmppchat.MainApplication;
import com.recoverrelax.pt.riotxmppchat.MyUtil.AppUtils.AppXmppUtils;
import com.recoverrelax.pt.riotxmppchat.Network.RiotXmppService;
import com.recoverrelax.pt.riotxmppchat.Riot.Model.Friend;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.RosterEntry;

import java.util.Collections;
import java.util.List;

import LolChatRiotDb.InAppLogDb;
import LolChatRiotDb.MessageDb;
import LolChatRiotDb.MessageDbDao;
import de.greenrobot.dao.query.QueryBuilder;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;

public class GlobalImpl {

    private String connectedXmppUser;
    private AbstractXMPPConnection connection;
    private MessageDbDao messageDao;

    public GlobalImpl(AbstractXMPPConnection connection){
        this.connection = connection;
        this.messageDao = MainApplication.getInstance().getDaoSession().getMessageDbDao();
    }

    public Observable<String> getConnectedXmppUser(){
        return Observable.just(AppXmppUtils.parseXmppAddress(connection.getUser()))
                .cache()
                .observeOn(Schedulers.newThread())
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Long> insertMessage(MessageDb message) {
        return Observable.just(messageDao.insert(message))
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    public void insertMessageNoRx(MessageDb message){
        messageDao.insert(message);
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
        return friend.flatMap(friend1 -> getLastMessage(friend1.getUserXmppAddress()));
    }

    public Observable<MessageDb> getLastMessage(String friendUser){
        return MainApplication.getInstance().getConnectedUser()
                .flatMap(connectedUser -> {
                    List<MessageDb> list = messageDao.queryBuilder()
                            .where(MessageDbDao.Properties.UserXmppId.eq(connectedUser),
                                    MessageDbDao.Properties.FromTo.eq(friendUser))
                            .orderDesc(MessageDbDao.Properties.Id)
                            .limit(1).build().list();

                    return list.size() == 0
                            ? Observable.just(null)
                            : Observable.just(list.get(0));
                });
    }

    public Observable<List<MessageDb>> getLastXMessages(int x, String userToGetMessagesFrom){
        return MainApplication.getInstance().getConnectedUser()
                .flatMap(connectedUser -> {
                    QueryBuilder qb = messageDao.queryBuilder();
                    qb.where(MessageDbDao.Properties.UserXmppId.eq(connectedUser),
                            MessageDbDao.Properties.FromTo.eq(userToGetMessagesFrom))
                            .orderDesc(MessageDbDao.Properties.Id)
                            .limit(x).build();
                    QueryBuilder.LOG_SQL = true;
                    List<MessageDb> messageList = qb.list();
                    return Observable.just(messageList);
                });
    }

    public Observable<Integer> getUnreadedMessages(){
        return MainApplication.getInstance().getConnectedUser()
                .flatMap(connectedUser -> {
                    QueryBuilder qb = messageDao.queryBuilder();
                    qb.where(MessageDbDao.Properties.UserXmppId.eq(connectedUser),
                            MessageDbDao.Properties.Direction.eq(MessageDirection.FROM.getId()),
                            MessageDbDao.Properties.WasRead.eq(false))
                            .build();
                    return Observable.just(qb.list().size());
                });
    }
    public Observable<Long> updateInappLog(InAppLogDb inappLog){
        return Observable.just(RiotXmppDBRepository.getInAppLogDbDao().insertOrReplace(inappLog))
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread());
    }
}

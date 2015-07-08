package com.recoverrelax.pt.riotxmppchat.Storage;

import com.recoverrelax.pt.riotxmppchat.MainApplication;

import java.util.Date;
import java.util.List;

import LolChatRiotDb.InAppLogDb;
import LolChatRiotDb.InAppLogDbDao;
import LolChatRiotDb.MessageDb;
import LolChatRiotDb.MessageDbDao;
import LolChatRiotDb.NotificationDb;
import LolChatRiotDb.NotificationDbDao;
import de.greenrobot.dao.query.QueryBuilder;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RiotXmppDBRepository {

    /**
     * MessageDao
     */

    private static final String TAG = RiotXmppDBRepository.class.getCanonicalName();

    private static MessageDbDao messageDao = MainApplication.getInstance().getDaoSession().getMessageDbDao();
    private static InAppLogDbDao inAppLogDbDao = MainApplication.getInstance().getDaoSession().getInAppLogDbDao();
    private static NotificationDbDao notificationDao = MainApplication.getInstance().getDaoSession().getNotificationDbDao();

    public Observable<Long> insertOrReplaceInappLog(InAppLogDb inappLog){
        return Observable.defer(() -> Observable.just(RiotXmppDBRepository.getInAppLogDbDao().insertOrReplace(inappLog)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<Long> insertOrReplaceInappLog(Integer logId, String logMessage, String targetXmppUser){
        return MainApplication.getInstance().getRiotXmppService().getRiotConnectionManager().getConnectedUser()
                .map(connectedUser -> new InAppLogDb(null, logId, new Date(), logMessage, connectedUser, targetXmppUser))
                .flatMap(this::insertOrReplaceInappLog)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Observable<Boolean> updateMessages(List<MessageDb> messages){
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try {
                    messageDao.updateInTx(messages);
                    subscriber.onNext(true);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable<Boolean> hasUnreadedMessages(){
        return getUnreadedMessages()
                .map(unreadedMessages -> unreadedMessages > 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<InAppLogDb>> getLast20List(String connectedUser){
        return Observable.defer( () ->
                Observable.just(RiotXmppDBRepository.getInAppLogDbDao().queryBuilder())
                .map(qb -> {
                    qb.where(InAppLogDbDao.Properties.UserXmppId.eq(connectedUser))
                            .orderDesc(InAppLogDbDao.Properties.Id)
                            .limit(20).build();
                    List<InAppLogDb> logList = qb.list();

                    if (logList.size() == 0)
                        return null;
                    else
                        return logList;
                })
        );
    }

    public Observable<Long> insertMessage(MessageDb message){
        return Observable.defer(() -> Observable.just(messageDao.insert(message)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Integer> getUnreadedMessages(){
        return MainApplication.getInstance().getRiotXmppService().getRiotConnectionManager().getConnectedUser()
                .map(connectedUser -> {
                    QueryBuilder qb = messageDao.queryBuilder();
                    qb.where(MessageDbDao.Properties.UserXmppId.eq(connectedUser),
                            MessageDbDao.Properties.Direction.eq(MessageDirection.FROM.getId()),
                            MessageDbDao.Properties.WasRead.eq(false))
                            .build();
                    return qb.list().size();
                });
    }

    public Observable<NotificationDb> getNotificationByUser(String friendUser) {
        return MainApplication.getInstance().getRiotXmppService().getRiotConnectionManager().getConnectedUser()
                .map(connectedUser -> {
                    QueryBuilder qb = getNotificationDao().queryBuilder();
                    qb.where(NotificationDbDao.Properties.UserXmppId.eq(connectedUser),
                            NotificationDbDao.Properties.FriendXmppId.eq(friendUser))
                            .build();

                    NotificationDb notificationDb;

                    if (qb.list().size() == 0) {
                        notificationDb = new NotificationDb(null, connectedUser, friendUser, false, false, false, false, false);
                    } else {
                        notificationDb = (NotificationDb) qb.list().get(0);
                    }
                    return notificationDb;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public Observable<Long> updateNotification(NotificationDb notif) {
        return Observable.defer(() -> Observable.just(getNotificationDao().insertOrReplace(notif)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static MessageDbDao getMessageDao() {
        return messageDao;
    }

    public static InAppLogDbDao getInAppLogDbDao() {
        return inAppLogDbDao;
    }

    public static NotificationDbDao getNotificationDao() {
        return notificationDao;
    }
}

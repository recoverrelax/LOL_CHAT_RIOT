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

    /**
     * Insert the specified log into the db
     * @param inappLog
     * @return
     */
    public Observable<Long> insertOrReplaceInappLog(InAppLogDb inappLog){
        return Observable.defer(() -> Observable.just(getInAppLogDbDao().insertOrReplace(inappLog)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    /**
     * Create a log from the input parameters and the save it in the database
     * @param logId logId of the log
     * @param logMessage the message of the log
     * @param targetXmppUser the user whose log is about
     * @return the id of the newly added log
     */
    public Observable<Long> insertOrReplaceInappLog(Integer logId, String logMessage, String targetXmppUser){
        return MainApplication.getInstance().getRiotXmppService().getRiotConnectionManager().getConnectedUser()
                .map(connectedUser -> new InAppLogDb(null, logId, new Date(), logMessage, connectedUser, targetXmppUser))
                .flatMap(this::insertOrReplaceInappLog)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    /**
     * Update all the logs with a transaction
     * @param messages the messages to update
     * @return true if the update was successfull
     */
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

    /**
     * Check if the connected user has unreaded messages
     * @return
     */
    public Observable<Boolean> hasUnreadedMessages(){
        return getUnreadedMessages()
                .map(unreadedMessages -> unreadedMessages > 0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Get the last 20 loggs for the connected input user
     * @param connectedUser to get logs from
     * @return list of logs
     */
    public Observable<List<InAppLogDb>> getLast20List(String connectedUser){
        return Observable.defer( () ->
                Observable.just(getInAppLogDbDao().queryBuilder())
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

    /**
     * Insert this message into the db
     * @param message to insert to the db
     * @return the message id
     */
    public Observable<Long> insertMessage(MessageDb message){
        return Observable.defer(() -> Observable.just(messageDao.insert(message)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * @return number of unreaded messages for the connected user
     */
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

    /**
     *
     * @param friendUser user to get notification from
     * @return the notification belonging to user
     */
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

    /**
     * Updates the notification
     * @param notif notification to update
     * @return the id of the updated notification
     */
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

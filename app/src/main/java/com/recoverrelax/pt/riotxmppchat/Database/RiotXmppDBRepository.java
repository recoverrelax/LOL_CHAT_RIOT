package com.recoverrelax.pt.riotxmppchat.Database;

import com.recoverrelax.pt.riotxmppchat.MainApplication;

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
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.recoverrelax.pt.riotxmppchat.MyUtil.google.LogUtils.LOGI;

public class RiotXmppDBRepository {

    /**
     * MessageDao
     */

    private static final String TAG = RiotXmppDBRepository.class.getCanonicalName();

    private static MessageDbDao messageDao = MainApplication.getInstance().getDaoSession().getMessageDbDao();
    private static InAppLogDbDao inAppLogDbDao = MainApplication.getInstance().getDaoSession().getInAppLogDbDao();
    private static NotificationDbDao notificationDao = MainApplication.getInstance().getDaoSession().getNotificationDbDao();

    public void insertOrReplaceInappLog(InAppLogDb inappLog){
        Observable.just(RiotXmppDBRepository.getInAppLogDbDao().insertOrReplace(inappLog))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Long aLong) {
                        LOGI(TAG, "Inserted InAppLogDb with id: " + aLong + " in the DB");
                    }
                });
    }

    public void updateMessages(List<MessageDb> messages){
        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                try{
                    messageDao.updateInTx(messages);
                    subscriber.onNext(true);
                    subscriber.onCompleted();
                }catch(Exception e){
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean b) {
                        LOGI(TAG, "Setted messages to Read");
                    }
                });
    }

    public static boolean hasUnreadedMessages(){
        String connectedUser = MainApplication.getInstance().getConnectedUserString();
        QueryBuilder qb = messageDao.queryBuilder();
        qb.where(MessageDbDao.Properties.UserXmppId.eq(connectedUser),
                MessageDbDao.Properties.Direction.eq(MessageDirection.FROM.getId()),
                MessageDbDao.Properties.WasRead.eq(false))
                .build();
        return qb.list().size()>0;
    }

    public Observable<List<InAppLogDb>> getLast20List(String connectedUser){
        return Observable.just(RiotXmppDBRepository.getInAppLogDbDao().queryBuilder())
                .flatMap(qb -> {
                    qb.where(InAppLogDbDao.Properties.UserXmppId.eq(connectedUser))
                            .orderDesc(InAppLogDbDao.Properties.Id)
                            .limit(20).build();
                    QueryBuilder.LOG_SQL = true;

                    List<InAppLogDb> logList = qb.list();

                    if(logList.size() == 0)
                        return Observable.just(null);
                    else
                        return Observable.just(logList);
                });
    }

    public void insertMessage(MessageDb message){
        Observable.just(messageDao.insert(message))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Long>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        LOGI(TAG, "Inserted MessageDb with id: " + aLong + " in the DB");
                    }
                });
    }

    public Observable<Integer> getUnreadedMessages(){
        return MainApplication.getInstance().getRiotXmppService().getConnectedUser()
                .flatMap(connectedUser -> {
                    QueryBuilder qb = messageDao.queryBuilder();
                    qb.where(MessageDbDao.Properties.UserXmppId.eq(connectedUser),
                            MessageDbDao.Properties.Direction.eq(MessageDirection.FROM.getId()),
                            MessageDbDao.Properties.WasRead.eq(false))
                            .build();
                    return Observable.just(qb.list().size());
                });
    }

    public static NotificationDb getNotificationByUser(String currentUser, String friendUser) {
        QueryBuilder qb = getNotificationDao().queryBuilder();
        qb.where(NotificationDbDao.Properties.UserXmppId.eq(currentUser),
                NotificationDbDao.Properties.FriendXmppId.eq(friendUser))
                .build();

        if (qb.list().size() == 0) {
            NotificationDb notificationDb = new NotificationDb(null, currentUser, friendUser, false, false, false, false, false);
            LOGI("123", notificationDb.toString());
            return notificationDb;
        } else {
            NotificationDb notificationDb = (NotificationDb) qb.list().get(0);
            LOGI("123", notificationDb.toString());
            return notificationDb;
        }
    }

    public static void updateNotification(NotificationDb notif) {
        getNotificationDao().insertOrReplace(notif);
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

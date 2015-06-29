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

    public RiotXmppDBRepository(){}
    
    private static MessageDbDao messageDao = MainApplication.getInstance().getDaoSession().getMessageDbDao();

    private static MessageDbDao getMessageDbDao(){
        return messageDao;
    }

    public Observable<Long> insertMessage(MessageDb message) {
        return Observable.just(messageDao.insert(message))
                .observeOn(Schedulers.newThread())
                .subscribeOn(AndroidSchedulers.mainThread());
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

    public static void updateMessages(List<MessageDb> messages){
        messageDao.updateInTx(messages);
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

    /**
     * NotificationDao
     */

    public static NotificationDbDao getNotificationDao(){
        return MainApplication.getInstance().getDaoSession().getNotificationDbDao();
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

    /**
     * InapplogDb
     */

    public static InAppLogDbDao getInappDbDao(){
        return MainApplication.getInstance().getDaoSession().getInAppLogDbDao();
    }

    public static void updateInappLog(InAppLogDb inappLog){
        getInappDbDao().insertOrReplace(inappLog);
    }

    @SuppressWarnings("unchecked")
    public static List<InAppLogDb> getLastXInappLogs(int x){

        String connectedUser = MainApplication.getInstance().getConnectedUserString();

        QueryBuilder qb = getInappDbDao().queryBuilder();
        qb.where(InAppLogDbDao.Properties.UserXmppId.eq(connectedUser))
                .orderDesc(InAppLogDbDao.Properties.Id)
                .limit(x).build();
        QueryBuilder.LOG_SQL = true;
        return (List<InAppLogDb>)qb.list();
    }
}

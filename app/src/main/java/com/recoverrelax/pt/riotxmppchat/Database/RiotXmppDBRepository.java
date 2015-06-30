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

    private static MessageDbDao messageDao = MainApplication.getInstance().getDaoSession().getMessageDbDao();
    private static InAppLogDbDao inAppLogDbDao = MainApplication.getInstance().getDaoSession().getInAppLogDbDao();
    private static NotificationDbDao notificationDao = MainApplication.getInstance().getDaoSession().getNotificationDbDao();

    public static long insertMessageNoRx(MessageDb message) {
        return messageDao.insert(message);
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

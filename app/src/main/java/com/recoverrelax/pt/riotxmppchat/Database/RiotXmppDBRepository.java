package com.recoverrelax.pt.riotxmppchat.Database;

import android.util.Log;

import com.recoverrelax.pt.riotxmppchat.MainApplication;

import java.util.List;

import LolChatRiotDb.MessageDb;
import LolChatRiotDb.MessageDbDao;
import LolChatRiotDb.NotificationDb;
import LolChatRiotDb.NotificationDbDao;
import de.greenrobot.dao.query.QueryBuilder;

import static junit.framework.Assert.assertTrue;


public class RiotXmppDBRepository {

    /**
     * MessageDao
     */

    public static MessageDbDao getMessageDao(){
        return MainApplication.getInstance().getDaoSession().getMessageDbDao();
    }

    public static long getMessageCount(){
        return getMessageDao().count();
    }

    public static void insertMessage(MessageDb message) {
        getMessageDao().insert(message);
    }

    public static List<MessageDb> getAllMessages(){
        MessageDbDao messageDao = getMessageDao();

        QueryBuilder qb = messageDao.queryBuilder();
        qb.orderDesc(MessageDbDao.Properties.Id)
                .limit(50).build();

        return qb.list();
    }

    public static MessageDb getLastMessage(String connectedUser, String friendUser){
        MessageDbDao messageDao = getMessageDao();

        QueryBuilder qb = messageDao.queryBuilder();

        List list = qb
                .where(MessageDbDao.Properties.UserXmppId.eq(connectedUser),
                       MessageDbDao.Properties.FromTo.eq(friendUser))
                .orderDesc(MessageDbDao.Properties.Id)
                .limit(1).build().list();
        Log.i("AS123", list.size() + "");
        if(list.size() > 0)
            return (MessageDb) list.get(0);
        else
            return null;
    }

    public static List<MessageDb> getLastMessageAsList(String connectedUser, String friendUser){
        MessageDbDao messageDao = getMessageDao();

        QueryBuilder qb = messageDao.queryBuilder();

        List list = qb
                .where(MessageDbDao.Properties.UserXmppId.eq(connectedUser),
                        MessageDbDao.Properties.FromTo.eq(friendUser))
                .orderDesc(MessageDbDao.Properties.Id)
                .limit(1).build().list();

        if(list.size() > 0)
            return (List<MessageDb>) list;
        else
            return null;
    }

    public static List<MessageDb> getLastXMessages(int x, String connectedUser, String userToGetMessagesFrom){

        MessageDbDao messageDao = getMessageDao();

        QueryBuilder qb = messageDao.queryBuilder();
        qb.where(MessageDbDao.Properties.UserXmppId.eq(connectedUser),
                MessageDbDao.Properties.FromTo.eq(userToGetMessagesFrom))
                .orderDesc(MessageDbDao.Properties.Id)
                .limit(x).build();
        QueryBuilder.LOG_SQL = true;
        return qb.list();
    }

    public static void updateMessages(List<MessageDb> messages){
        getMessageDao().updateInTx(messages);
    }

    public static boolean hasUnreadedMessages(String connectedUser){
        QueryBuilder qb = getMessageDao().queryBuilder();
        qb.where(MessageDbDao.Properties.UserXmppId.eq(connectedUser),
                MessageDbDao.Properties.Direction.eq(MessageDirection.FROM.getId()),
                MessageDbDao.Properties.WasRead.eq(false))
                .build();
        return qb.list().size()>0;
    }

    /**
     * Notification Dao
     */

    public static NotificationDbDao getNotificationDao(){
        return MainApplication.getInstance().getDaoSession().getNotificationDbDao();
    }

    public static boolean defaultSettingsNotificationsSetted(String connectedXmppUser) {
        List<NotificationDb> list = getNotificationDao().queryBuilder().where(
                NotificationDbDao.Properties.UserXmppId.eq(connectedXmppUser))
                .list();
        assertTrue("Something went wrong. Same user can't have more that 1 defined SettingsNotifications",
                list.size() < 2);

        return list.size() == 1;
    }

    /**
     * Just insert, no update ...
     * @param notif
     */
    public static void insertNotification(NotificationDb notif){
        getNotificationDao().insert(notif);
    }

    public static NotificationDb getNotification(String userXmppAddress){
        List<NotificationDb> list = getNotificationDao().queryBuilder()
                .where(NotificationDbDao.Properties.UserXmppId.eq(userXmppAddress)).list();
        return list.get(0);
    }

    public static void updateNotification(NotificationDb notif){
        getNotificationDao().update(notif);
    }
}

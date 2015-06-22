package com.recoverrelax.pt.riotxmppchat.Database;

import com.recoverrelax.pt.riotxmppchat.MainApplication;

import java.util.List;

import LolChatRiotDb.MessageDb;
import LolChatRiotDb.MessageDbDao;
import LolChatRiotDb.NotificationDb;
import LolChatRiotDb.NotificationDbDao;
import de.greenrobot.dao.query.QueryBuilder;
import rx.Observable;
import rx.functions.Func1;

public class RiotXmppDBRepository {

    /**
     * MessageDao
     */

    private static MessageDbDao getMessageDao(){
        return MainApplication.getInstance().getDaoSession().getMessageDbDao();
    }


    public static void insertMessage(MessageDb message) {
        getMessageDao().insert(message);
    }

    public static MessageDb getLastMessage(String friendUser){
        String connectedUser = MainApplication.getInstance().getRiotXmppService().getConnectedXmppUser();
        MessageDbDao messageDao = getMessageDao();

        QueryBuilder qb = messageDao.queryBuilder();

        List list = qb
                .where(MessageDbDao.Properties.UserXmppId.eq(connectedUser),
                       MessageDbDao.Properties.FromTo.eq(friendUser))
                .orderDesc(MessageDbDao.Properties.Id)
                .limit(1).build().list();

        return (MessageDb)list.get(0);
//        return Observable.just((MessageDb) list.get(0));
//        return Observable.just(MainApplication.getInstance().getRiotXmppService().getConnectedXmppUser())
//                .map(connectedUser -> {
//                    MessageDbDao messageDao = getMessageDao();
//                    QueryBuilder qb = messageDao.queryBuilder();
//
//                    List list = qb
//                            .where(MessageDbDao.Properties.UserXmppId.eq(connectedUser),
//                                    MessageDbDao.Properties.FromTo.eq(friendUser))
//                            .orderDesc(MessageDbDao.Properties.Id)
//                            .limit(1).build().list();
//
//                    return (MessageDb) list.get(0);
//                });
    }

    @SuppressWarnings("unchecked")
    public static List<MessageDb> getLastMessageAsList(String friendUser){
        String connectedUser = MainApplication.getInstance().getRiotXmppService().getConnectedXmppUser();
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

    @SuppressWarnings("unchecked")
    public static List<MessageDb> getLastXMessages(int x, String userToGetMessagesFrom){

        String connectedUser = MainApplication.getInstance().getRiotXmppService().getConnectedXmppUser();
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
     * NotificationDao
     */

    public static NotificationDbDao getNotificationDao(){
        return MainApplication.getInstance().getDaoSession().getNotificationDbDao();
    }

    public static NotificationDb getNotificationByUser(String currentUser, String friendUser){
        QueryBuilder qb = getNotificationDao().queryBuilder();
        qb.where(NotificationDbDao.Properties.UserXmppId.eq(currentUser),
                NotificationDbDao.Properties.FriendXmppId.eq(friendUser))
                .build();

        if(qb.list().size() == 0){
            return new NotificationDb(null, currentUser, friendUser, false, false, false, false, false);
        }else
            return (NotificationDb)qb.list().get(0);
    }

    public static void updateNotification(NotificationDb notif) {
        getNotificationDao().insertOrReplace(notif);
    }
}

package com.recoverrelax.pt.riotxmppchat.Database;

import com.recoverrelax.pt.riotxmppchat.MainApplication;

import java.util.List;

import LolChatRiotDb.MessageDb;
import LolChatRiotDb.MessageDbDao;
import de.greenrobot.dao.query.QueryBuilder;


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

    public static MessageDb getLastMessage(String userXmppName){
        MessageDbDao messageDao = getMessageDao();

        QueryBuilder qb = messageDao.queryBuilder();

        List list = qb
                .where(MessageDbDao.Properties.Message_riotXmppUser.eq(userXmppName))
                .orderDesc(MessageDbDao.Properties.Id)
                .limit(1).build().list();
        if(list.size() > 0)
            return ((List<MessageDb>)list).get(0);
        else
            return null;
    }

    public static List<MessageDb> getLastXMessages(int x){

        MessageDbDao messageDao = getMessageDao();

        QueryBuilder qb = messageDao.queryBuilder();
        qb.orderDesc(MessageDbDao.Properties.Id)
                .limit(x).build();

        return qb.list();
    }
}

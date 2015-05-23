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
        return getMessageDao().loadAll();
    }

    public static List<MessageDb> getLastXMessages(int x){

        MessageDbDao messageDao = getMessageDao();

        QueryBuilder qb = messageDao.queryBuilder();
        qb.orderDesc(MessageDbDao.Properties.Id)
                .limit(x).build();

        return qb.list();
    }
}

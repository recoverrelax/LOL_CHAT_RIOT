package LolChatRiotDb;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 *
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig messageDbDaoConfig;
    private final DaoConfig notificationDbDaoConfig;
    private final DaoConfig inAppLogDbDaoConfig;

    private final MessageDbDao messageDbDao;
    private final NotificationDbDao notificationDbDao;
    private final InAppLogDbDao inAppLogDbDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        messageDbDaoConfig = daoConfigMap.get(MessageDbDao.class).clone();
        messageDbDaoConfig.initIdentityScope(type);

        notificationDbDaoConfig = daoConfigMap.get(NotificationDbDao.class).clone();
        notificationDbDaoConfig.initIdentityScope(type);

        inAppLogDbDaoConfig = daoConfigMap.get(InAppLogDbDao.class).clone();
        inAppLogDbDaoConfig.initIdentityScope(type);

        messageDbDao = new MessageDbDao(messageDbDaoConfig, this);
        notificationDbDao = new NotificationDbDao(notificationDbDaoConfig, this);
        inAppLogDbDao = new InAppLogDbDao(inAppLogDbDaoConfig, this);

        registerDao(MessageDb.class, messageDbDao);
        registerDao(NotificationDb.class, notificationDbDao);
        registerDao(InAppLogDb.class, inAppLogDbDao);
    }

    public void clear() {
        messageDbDaoConfig.getIdentityScope().clear();
        notificationDbDaoConfig.getIdentityScope().clear();
        inAppLogDbDaoConfig.getIdentityScope().clear();
    }

    public MessageDbDao getMessageDbDao() {
        return messageDbDao;
    }

    public NotificationDbDao getNotificationDbDao() {
        return notificationDbDao;
    }

    public InAppLogDbDao getInAppLogDbDao() {
        return inAppLogDbDao;
    }

}

package LolChatRiotDb;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import LolChatRiotDb.NotificationDb;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table NOTIFICATION_DB.
*/
public class NotificationDbDao extends AbstractDao<NotificationDb, Long> {

    public static final String TABLENAME = "NOTIFICATION_DB";

    /**
     * Properties of entity NotificationDb.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserXmppId = new Property(1, String.class, "userXmppId", false, "USER_XMPP_ID");
        public final static Property SoundNotificationOnline = new Property(2, Boolean.class, "soundNotificationOnline", false, "SOUND_NOTIFICATION_ONLINE");
        public final static Property SoundNotificationOffline = new Property(3, Boolean.class, "soundNotificationOffline", false, "SOUND_NOTIFICATION_OFFLINE");
        public final static Property TextNotificationOnline = new Property(4, Boolean.class, "textNotificationOnline", false, "TEXT_NOTIFICATION_ONLINE");
        public final static Property TextNotificationOffline = new Property(5, Boolean.class, "textNotificationOffline", false, "TEXT_NOTIFICATION_OFFLINE");
    };


    public NotificationDbDao(DaoConfig config) {
        super(config);
    }
    
    public NotificationDbDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'NOTIFICATION_DB' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'USER_XMPP_ID' TEXT," + // 1: userXmppId
                "'SOUND_NOTIFICATION_ONLINE' INTEGER," + // 2: soundNotificationOnline
                "'SOUND_NOTIFICATION_OFFLINE' INTEGER," + // 3: soundNotificationOffline
                "'TEXT_NOTIFICATION_ONLINE' INTEGER," + // 4: textNotificationOnline
                "'TEXT_NOTIFICATION_OFFLINE' INTEGER);"); // 5: textNotificationOffline
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'NOTIFICATION_DB'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, NotificationDb entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String userXmppId = entity.getUserXmppId();
        if (userXmppId != null) {
            stmt.bindString(2, userXmppId);
        }
 
        Boolean soundNotificationOnline = entity.getSoundNotificationOnline();
        if (soundNotificationOnline != null) {
            stmt.bindLong(3, soundNotificationOnline ? 1l: 0l);
        }
 
        Boolean soundNotificationOffline = entity.getSoundNotificationOffline();
        if (soundNotificationOffline != null) {
            stmt.bindLong(4, soundNotificationOffline ? 1l: 0l);
        }
 
        Boolean textNotificationOnline = entity.getTextNotificationOnline();
        if (textNotificationOnline != null) {
            stmt.bindLong(5, textNotificationOnline ? 1l: 0l);
        }
 
        Boolean textNotificationOffline = entity.getTextNotificationOffline();
        if (textNotificationOffline != null) {
            stmt.bindLong(6, textNotificationOffline ? 1l: 0l);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public NotificationDb readEntity(Cursor cursor, int offset) {
        NotificationDb entity = new NotificationDb( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // userXmppId
            cursor.isNull(offset + 2) ? null : cursor.getShort(offset + 2) != 0, // soundNotificationOnline
            cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3) != 0, // soundNotificationOffline
            cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0, // textNotificationOnline
            cursor.isNull(offset + 5) ? null : cursor.getShort(offset + 5) != 0 // textNotificationOffline
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, NotificationDb entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserXmppId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setSoundNotificationOnline(cursor.isNull(offset + 2) ? null : cursor.getShort(offset + 2) != 0);
        entity.setSoundNotificationOffline(cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3) != 0);
        entity.setTextNotificationOnline(cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0);
        entity.setTextNotificationOffline(cursor.isNull(offset + 5) ? null : cursor.getShort(offset + 5) != 0);
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(NotificationDb entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(NotificationDb entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}

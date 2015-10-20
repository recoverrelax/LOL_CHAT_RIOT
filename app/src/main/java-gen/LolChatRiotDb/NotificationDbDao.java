package LolChatRiotDb;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table NOTIFICATION_DB.
 */
public class NotificationDbDao extends AbstractDao<NotificationDb, Long> {

    public static final String TABLENAME = "NOTIFICATION_DB";

    public NotificationDbDao(DaoConfig config) {
        super(config);
    }

    ;


    public NotificationDbDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "'NOTIFICATION_DB' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'USER_XMPP_ID' TEXT," + // 1: userXmppId
                "'FRIEND_XMPP_ID' TEXT," + // 2: friendXmppId
                "'IS_ONLINE' INTEGER," + // 3: isOnline
                "'IS_OFFLINE' INTEGER," + // 4: isOffline
                "'HAS_STARTED_GAME' INTEGER," + // 5: hasStartedGame
                "'HAS_LEF_GAME' INTEGER," + // 6: hasLefGame
                "'HAS_SENT_ME_PM' INTEGER);"); // 7: hasSentMePm
    }

    /**
     * Drops the underlying database table.
     */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'NOTIFICATION_DB'";
        db.execSQL(sql);
    }

    /**
     * @inheritdoc
     */
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

        String friendXmppId = entity.getFriendXmppId();
        if (friendXmppId != null) {
            stmt.bindString(3, friendXmppId);
        }

        Boolean isOnline = entity.getIsOnline();
        if (isOnline != null) {
            stmt.bindLong(4, isOnline ? 1l : 0l);
        }

        Boolean isOffline = entity.getIsOffline();
        if (isOffline != null) {
            stmt.bindLong(5, isOffline ? 1l : 0l);
        }

        Boolean hasStartedGame = entity.getHasStartedGame();
        if (hasStartedGame != null) {
            stmt.bindLong(6, hasStartedGame ? 1l : 0l);
        }

        Boolean hasLefGame = entity.getHasLefGame();
        if (hasLefGame != null) {
            stmt.bindLong(7, hasLefGame ? 1l : 0l);
        }

        Boolean hasSentMePm = entity.getHasSentMePm();
        if (hasSentMePm != null) {
            stmt.bindLong(8, hasSentMePm ? 1l : 0l);
        }
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    /**
     * @inheritdoc
     */
    @Override
    public NotificationDb readEntity(Cursor cursor, int offset) {
        NotificationDb entity = new NotificationDb( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // userXmppId
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // friendXmppId
                cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3) != 0, // isOnline
                cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0, // isOffline
                cursor.isNull(offset + 5) ? null : cursor.getShort(offset + 5) != 0, // hasStartedGame
                cursor.isNull(offset + 6) ? null : cursor.getShort(offset + 6) != 0, // hasLefGame
                cursor.isNull(offset + 7) ? null : cursor.getShort(offset + 7) != 0 // hasSentMePm
        );
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, NotificationDb entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserXmppId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setFriendXmppId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setIsOnline(cursor.isNull(offset + 3) ? null : cursor.getShort(offset + 3) != 0);
        entity.setIsOffline(cursor.isNull(offset + 4) ? null : cursor.getShort(offset + 4) != 0);
        entity.setHasStartedGame(cursor.isNull(offset + 5) ? null : cursor.getShort(offset + 5) != 0);
        entity.setHasLefGame(cursor.isNull(offset + 6) ? null : cursor.getShort(offset + 6) != 0);
        entity.setHasSentMePm(cursor.isNull(offset + 7) ? null : cursor.getShort(offset + 7) != 0);
    }

    /**
     * @inheritdoc
     */
    @Override
    protected Long updateKeyAfterInsert(NotificationDb entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long getKey(NotificationDb entity) {
        if (entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /**
     * @inheritdoc
     */
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

    /**
     * Properties of entity NotificationDb.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserXmppId = new Property(1, String.class, "userXmppId", false, "USER_XMPP_ID");
        public final static Property FriendXmppId = new Property(2, String.class, "friendXmppId", false, "FRIEND_XMPP_ID");
        public final static Property IsOnline = new Property(3, Boolean.class, "isOnline", false, "IS_ONLINE");
        public final static Property IsOffline = new Property(4, Boolean.class, "isOffline", false, "IS_OFFLINE");
        public final static Property HasStartedGame = new Property(5, Boolean.class, "hasStartedGame", false, "HAS_STARTED_GAME");
        public final static Property HasLefGame = new Property(6, Boolean.class, "hasLefGame", false, "HAS_LEF_GAME");
        public final static Property HasSentMePm = new Property(7, Boolean.class, "hasSentMePm", false, "HAS_SENT_ME_PM");
    }

}

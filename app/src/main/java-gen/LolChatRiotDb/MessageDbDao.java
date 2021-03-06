package LolChatRiotDb;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table MESSAGE_DB.
 */
public class MessageDbDao extends AbstractDao<MessageDb, Long> {

    public static final String TABLENAME = "MESSAGE_DB";

    public MessageDbDao(DaoConfig config) {
        super(config);
    }


    public MessageDbDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "'MESSAGE_DB' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'USER_XMPP_ID' TEXT," + // 1: userXmppId
                "'FROM_TO' TEXT," + // 2: fromTo
                "'DIRECTION' INTEGER," + // 3: direction
                "'DATE' INTEGER," + // 4: date
                "'MESSAGE' TEXT," + // 5: message
                "'WAS_READ' INTEGER);"); // 6: wasRead
    }

    /**
     * Drops the underlying database table.
     */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'MESSAGE_DB'";
        db.execSQL(sql);
    }

    /**
     * @inheritdoc
     */
    @Override
    protected void bindValues(SQLiteStatement stmt, MessageDb entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        String userXmppId = entity.getUserXmppId();
        if (userXmppId != null) {
            stmt.bindString(2, userXmppId);
        }

        String fromTo = entity.getFromTo();
        if (fromTo != null) {
            stmt.bindString(3, fromTo);
        }

        Integer direction = entity.getDirection();
        if (direction != null) {
            stmt.bindLong(4, direction);
        }

        java.util.Date date = entity.getDate();
        if (date != null) {
            stmt.bindLong(5, date.getTime());
        }

        String message = entity.getMessage();
        if (message != null) {
            stmt.bindString(6, message);
        }

        Boolean wasRead = entity.getWasRead();
        if (wasRead != null) {
            stmt.bindLong(7, wasRead ? 1l : 0l);
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
    public MessageDb readEntity(Cursor cursor, int offset) {
        MessageDb entity = new MessageDb( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // userXmppId
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // fromTo
                cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // direction
                cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)), // date
                cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // message
                cursor.isNull(offset + 6) ? null : cursor.getShort(offset + 6) != 0 // wasRead
        );
        return entity;
    }

    /**
     * @inheritdoc
     */
    @Override
    public void readEntity(Cursor cursor, MessageDb entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserXmppId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setFromTo(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setDirection(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setDate(cursor.isNull(offset + 4) ? null : new java.util.Date(cursor.getLong(offset + 4)));
        entity.setMessage(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setWasRead(cursor.isNull(offset + 6) ? null : cursor.getShort(offset + 6) != 0);
    }

    /**
     * @inheritdoc
     */
    @Override
    protected Long updateKeyAfterInsert(MessageDb entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    /**
     * @inheritdoc
     */
    @Override
    public Long getKey(MessageDb entity) {
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
     * Properties of entity MessageDb.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property UserXmppId = new Property(1, String.class, "userXmppId", false, "USER_XMPP_ID");
        public final static Property FromTo = new Property(2, String.class, "fromTo", false, "FROM_TO");
        public final static Property Direction = new Property(3, Integer.class, "direction", false, "DIRECTION");
        public final static Property Date = new Property(4, java.util.Date.class, "date", false, "DATE");
        public final static Property Message = new Property(5, String.class, "message", false, "MESSAGE");
        public final static Property WasRead = new Property(6, Boolean.class, "wasRead", false, "WAS_READ");
    }

}

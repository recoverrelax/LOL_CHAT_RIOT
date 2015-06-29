package pl.surecase.eu;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

    public static void main(String args[]) throws Exception {

        final String DB_NAME = "LolChatRiotDb";

        Schema schema = new Schema(4, DB_NAME);

        Entity message = schema.addEntity("MessageDb");
        message.addIdProperty();
        message.addStringProperty("userXmppId");
        message.addStringProperty("fromTo");
        message.addIntProperty("direction");
        message.addDateProperty("date");
        message.addStringProperty("message");
        message.addBooleanProperty("wasRead");

        Entity notification = schema.addEntity("NotificationDb");
        notification.addIdProperty();
        notification.addStringProperty("userXmppId");
        notification.addStringProperty("friendXmppId");
        notification.addBooleanProperty("isOnline");
        notification.addBooleanProperty("isOffline");
        notification.addBooleanProperty("hasStartedGame");
        notification.addBooleanProperty("hasLefGame");
        notification.addBooleanProperty("hasSentMePm");

        Entity inappLogg = schema.addEntity("InAppLogDb");
        inappLogg.addIdProperty();
        inappLogg.addIntProperty("logId");
        inappLogg.addDateProperty("logDate");
        inappLogg.addStringProperty("logMessage");
        inappLogg.addStringProperty("userXmppId");
        inappLogg.addStringProperty("friendXmppId");

        new DaoGenerator().generateAll(schema, args[0]);
    }
}

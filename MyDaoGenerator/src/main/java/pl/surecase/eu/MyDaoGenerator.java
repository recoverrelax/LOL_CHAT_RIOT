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

        Entity notif = schema.addEntity("NotificationDb");
        notif.addIdProperty();
        notif.addStringProperty("userXmppId");
        notif.addBooleanProperty("soundNotificationOnline");
        notif.addBooleanProperty("soundNotificationOffline");
        notif.addBooleanProperty("textNotificationOnline");
        notif.addBooleanProperty("textNotificationOffline");


        new DaoGenerator().generateAll(schema, args[0]);
    }
}

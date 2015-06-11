package LolChatRiotDb;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table NOTIFICATION_CUSTOM_DB.
 */
public class NotificationCustomDb {

    private Long id;
    private String userXmppId;
    private String targetUserXmppId;
    private Integer notificationId;
    private Boolean state;

    public NotificationCustomDb() {
    }

    public NotificationCustomDb(Long id) {
        this.id = id;
    }

    public NotificationCustomDb(Long id, String userXmppId, String targetUserXmppId, Integer notificationId, Boolean state) {
        this.id = id;
        this.userXmppId = userXmppId;
        this.targetUserXmppId = targetUserXmppId;
        this.notificationId = notificationId;
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserXmppId() {
        return userXmppId;
    }

    public void setUserXmppId(String userXmppId) {
        this.userXmppId = userXmppId;
    }

    public String getTargetUserXmppId() {
        return targetUserXmppId;
    }

    public void setTargetUserXmppId(String targetUserXmppId) {
        this.targetUserXmppId = targetUserXmppId;
    }

    public Integer getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Integer notificationId) {
        this.notificationId = notificationId;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

}
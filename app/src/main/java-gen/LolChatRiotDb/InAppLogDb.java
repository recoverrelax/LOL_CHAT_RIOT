package LolChatRiotDb;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 

/**
 * Entity mapped to table IN_APP_LOG_DB.
 */
public class InAppLogDb {

    private Long id;
    private Integer logId;
    private java.util.Date logDate;
    private String logMessage;
    private String userXmppId;
    private String friendXmppId;

    public InAppLogDb() {
    }

    public InAppLogDb(Long id) {
        this.id = id;
    }

    public InAppLogDb(Long id, Integer logId, java.util.Date logDate, String logMessage, String userXmppId, String friendXmppId) {
        this.id = id;
        this.logId = logId;
        this.logDate = logDate;
        this.logMessage = logMessage;
        this.userXmppId = userXmppId;
        this.friendXmppId = friendXmppId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public java.util.Date getLogDate() {
        return logDate;
    }

    public void setLogDate(java.util.Date logDate) {
        this.logDate = logDate;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String logMessage) {
        this.logMessage = logMessage;
    }

    public String getUserXmppId() {
        return userXmppId;
    }

    public void setUserXmppId(String userXmppId) {
        this.userXmppId = userXmppId;
    }

    public String getFriendXmppId() {
        return friendXmppId;
    }

    public void setFriendXmppId(String friendXmppId) {
        this.friendXmppId = friendXmppId;
    }

}

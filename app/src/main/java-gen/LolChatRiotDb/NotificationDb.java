package LolChatRiotDb;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table NOTIFICATION_DB.
 */
public class NotificationDb {

    private Long id;
    private String userXmppId;
    private String friendXmppId;
    private Boolean isOnline;
    private Boolean isOffline;
    private Boolean hasStartedGame;
    private Boolean hasLefGame;
    private Boolean hasSentMePm;

    public NotificationDb() {
    }

    public NotificationDb(Long id) {
        this.id = id;
    }

    public NotificationDb(Long id, String userXmppId, String friendXmppId, Boolean isOnline, Boolean isOffline, Boolean hasStartedGame, Boolean hasLefGame, Boolean hasSentMePm) {
        this.id = id;
        this.userXmppId = userXmppId;
        this.friendXmppId = friendXmppId;
        this.isOnline = isOnline;
        this.isOffline = isOffline;
        this.hasStartedGame = hasStartedGame;
        this.hasLefGame = hasLefGame;
        this.hasSentMePm = hasSentMePm;
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

    public String getFriendXmppId() {
        return friendXmppId;
    }

    public void setFriendXmppId(String friendXmppId) {
        this.friendXmppId = friendXmppId;
    }

    public Boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
    }

    public Boolean getIsOffline() {
        return isOffline;
    }

    public void setIsOffline(Boolean isOffline) {
        this.isOffline = isOffline;
    }

    public Boolean getHasStartedGame() {
        return hasStartedGame;
    }

    public void setHasStartedGame(Boolean hasStartedGame) {
        this.hasStartedGame = hasStartedGame;
    }

    public Boolean getHasLefGame() {
        return hasLefGame;
    }

    public void setHasLefGame(Boolean hasLefGame) {
        this.hasLefGame = hasLefGame;
    }

    public Boolean getHasSentMePm() {
        return hasSentMePm;
    }

    public void setHasSentMePm(Boolean hasSentMePm) {
        this.hasSentMePm = hasSentMePm;
    }

}
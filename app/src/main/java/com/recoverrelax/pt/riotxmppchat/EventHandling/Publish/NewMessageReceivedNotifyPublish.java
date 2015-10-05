package com.recoverrelax.pt.riotxmppchat.EventHandling.Publish;

public class NewMessageReceivedNotifyPublish {

    String userXmppAddress;
    String username;
    String message;
    String buttonLabel;

    public NewMessageReceivedNotifyPublish(String userXmppAddress, String username, String message, String buttonLabel) {
        this.userXmppAddress = userXmppAddress;
        this.username = username;
        this.message = message;
        this.buttonLabel = buttonLabel;
    }

    public String getUserXmppAddress() {
        return userXmppAddress;
    }

    public void setUserXmppAddress(String userXmppAddress) {
        this.userXmppAddress = userXmppAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getButtonLabel() {
        return buttonLabel;
    }

    public void setButtonLabel(String buttonLabel) {
        this.buttonLabel = buttonLabel;
    }
}

package com.recoverrelax.pt.riotxmppchat.EventHandling.PersonalMessageList;

import LolChatRiotDb.MessageDb;

public class OnLastPersonalMessageReceivedEvent {

    private MessageDb message;

    public OnLastPersonalMessageReceivedEvent(MessageDb message) {
        this.message = message;
    }

    public MessageDb getMessage() {
        return message;
    }
}

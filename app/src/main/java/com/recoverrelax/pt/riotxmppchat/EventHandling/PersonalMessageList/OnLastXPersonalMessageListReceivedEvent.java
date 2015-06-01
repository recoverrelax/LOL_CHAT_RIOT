package com.recoverrelax.pt.riotxmppchat.EventHandling.PersonalMessageList;

import java.util.List;

import LolChatRiotDb.MessageDb;

public class OnLastXPersonalMessageListReceivedEvent {

    private List<MessageDb> messageDbs;

    public OnLastXPersonalMessageListReceivedEvent(List<MessageDb> messageDbs) {
        this.messageDbs = messageDbs;
    }

    public List<MessageDb> getMessageDbs() {
        return messageDbs;
    }
}

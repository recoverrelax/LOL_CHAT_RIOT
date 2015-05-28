package com.recoverrelax.pt.riotxmppchat.Riot.Model;

import com.recoverrelax.pt.riotxmppchat.Database.MessageDirection;
import java.util.Comparator;
import java.util.Date;
import LolChatRiotDb.MessageDb;

public class FriendListChat {

    private Friend friend;
    private MessageDb lastMessage;
    private static final String EMPTY_MESSAGE = "";

    public FriendListChat(Friend friend, MessageDb lastMessage) {
        this.friend = friend;
        this.lastMessage = lastMessage;

    }

    public String getFriendName(){
        return this.friend.getName();
    }

    public String getUserXmppAddress(){
        return this.friend.getUserXmppAddress();
    }

    public String getFriendLastMessage(){
        return this.lastMessage == null
                ? EMPTY_MESSAGE
                : this.lastMessage.getDirection() == MessageDirection.FROM.getId() ?
                this.lastMessage.getMessage() : "You: " + this.lastMessage.getMessage();
    }

    public Date getFriendLastMessageDate(){
        return this.lastMessage == null
                ? null
                : this.lastMessage.getDate();
    }

    public String getFriendLastMessageDateAsString(){
        Date friendLastMessageDate = getFriendLastMessageDate();

        return friendLastMessageDate == null
                ? EMPTY_MESSAGE
                : friendLastMessageDate.toString();
    }

    public Friend getFriend() {
        return friend;
    }

    public MessageDb getLastMessage() {
        return lastMessage;
    }

    public void setMessage(MessageDb message){
        this.lastMessage = message;
    }

    public static class LastMessageComparable implements Comparator<FriendListChat> {
        @Override
        public int compare(FriendListChat f1, FriendListChat f2) {
            if(f1.getFriend().isOnline()){
                if(f2.getFriend().isOnline()){
                    // F1-ONLINE, F2-ONLINE
                    return compareByMessage(f1, f2);
                }else{
                    // F1-ONLINE, F2-OFFLINE
                    return 1;
                }
            }else{
                if(f2.getFriend().isOnline()){
                    // F1-OFFLINE, F2-ONLINE
                    return -1;
                }else{
                    // F1-OFFLINE, F2-OFFLINE
                    return 1;
                }
            }
        }

        public int compareByMessage(FriendListChat f1, FriendListChat f2){
            if(f1.getLastMessage() == null){
                if(f2.getLastMessage() == null){
                    // F1-NULL, F2-NULL
                    return 1;
                }else{
                    // F1-NULL, F2 != NULL
                    return -1;
                }
            }else{
                if(f2.getLastMessage() == null){
                    // F1 !NULL, F2-NULL
                    return 1;
                }else{
                    // F1 !NULL, F2 != NULL
                    return compareByMessageDate(f1, f2);
                }
            }
        }

        public int compareByMessageDate(FriendListChat f1, FriendListChat f2){
            if(f1.getLastMessage().getDate().after(f2.getLastMessage().getDate())){
                return -1;
            }else{
                return 1;
            }
        }
    }
}

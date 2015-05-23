package com.recoverrelax.pt.riotxmppchat.Database;

public enum MessageToFrom {
    TO(0),
    FROM(1);

    private int id;

    MessageToFrom(int id){
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public MessageToFrom getById(int id){
        for(MessageToFrom mtf: MessageToFrom.values()){
            if(mtf.getId() == id)
                return mtf;
        }
        return null;
    }
}

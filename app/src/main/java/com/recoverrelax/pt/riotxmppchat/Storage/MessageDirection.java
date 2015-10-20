package com.recoverrelax.pt.riotxmppchat.Storage;

public enum MessageDirection {
    TO(0),
    FROM(1);

    private int id;

    MessageDirection(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public MessageDirection getById(int id) {
        for (MessageDirection mtf : MessageDirection.values()) {
            if (mtf.getId() == id)
                return mtf;
        }
        return null;
    }
}

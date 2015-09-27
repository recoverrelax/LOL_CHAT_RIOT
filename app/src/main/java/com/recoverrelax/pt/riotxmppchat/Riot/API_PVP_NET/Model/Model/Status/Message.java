package com.recoverrelax.pt.riotxmppchat.Riot.API_PVP_NET.Model.Model.Status;

import org.parceler.Parcel;

import java.util.List;

@Parcel
public class Message {

    private String author;
    private String content;
    private String created_at;
    private long id;
    private String severity;
    private List<Translation> translations;
    private String updated_at;

    public Message() {
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getCreated_at() {
        return created_at;
    }

    public long getId() {
        return id;
    }

    public String getSeverity() {
        return severity;
    }

    public List<Translation> getTranslations() {
        return translations;
    }

    public String getUpdated_at() {
        return updated_at;
    }
}

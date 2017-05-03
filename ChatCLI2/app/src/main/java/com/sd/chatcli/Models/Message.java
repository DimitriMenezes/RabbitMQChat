package com.sd.chatcli.Models;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.Required;

public class Message extends RealmObject {

    @Required
    private String text;

    @Required
    private Date date;

    @Required
    private String sender;

    @Required
    private String receiver;


    private boolean sent;

    public Message(){

    }

    public Message(String text, Date date, String sender, String receiver) {
        this.text = text;
        this.date = date;
        this.sender = sender;
        this.receiver = receiver;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiver() {
        return receiver;
    }

    public Boolean isSent() {
        return sent;
    }

    public void setSent(Boolean sent) {
        this.sent = sent;
    }


    public String getPackage() {
        return text+":"+sender+":"+receiver;
    }

}

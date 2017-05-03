package com.sd.chatcli.Singleton;

import android.content.Context;
import android.util.Log;

import com.sd.chatcli.Models.Message;
import com.sd.chatcli.Models.User;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;

public class Databaser {

    private static Databaser INSTANCE;

    RealmConfiguration realmConfig;

    Realm realm;

    Context context;

    private Databaser() {}

    public static Databaser getINSTANCE() {
        if (INSTANCE == null)
            INSTANCE = new Databaser();
        return INSTANCE;
    }

    public void setContext(Context context) {
        destroy();
        reset();
        INSTANCE.context = context;

        INSTANCE.realmConfig = new RealmConfiguration.Builder(context).build();

    }

    public void createOrAcessUser(String user){
        Sender.getINSTANCE().setUsername(user);
        if(!isUserCreated(user))
            saveToDatabase(new User(user));
    }

    public List<User> getUsers(String sender) {
        if(context==null)
            throw new RuntimeException("chame setContext antes.");

        realm = Realm.getInstance(realmConfig);
        RealmResults<User> users = realm.where(User.class)
                .notEqualTo("name", sender)
                .findAll();

        return users.subList(0, users.size());
    }

    public void saveToDatabase(RealmObject realmObject) {
        if(context==null)
            throw new RuntimeException("chame setContext antes.");
        realm = Realm.getInstance(realmConfig);
        realm.beginTransaction();
        realm.copyToRealm(realmObject);
        realm.commitTransaction();
    }

    public boolean isUserCreated(String user){
        realm = Realm.getInstance(realmConfig);
        RealmResults<User> r = realm.where(User.class)
                .equalTo("name", user)
                .findAll();
        return r.size()>0;
    }

    public RealmResults<Message> getAllChatMessagesOf(String user, String buddy){
        if(context==null)
            throw new RuntimeException("chame setContext antes.");
        realm = Realm.getInstance(realmConfig);
        RealmResults<Message> messages = realm.where(Message.class)
                .beginGroup()
                    .equalTo("sender", user)
                    .equalTo("receiver", buddy)
                .endGroup()
                .or()
                .beginGroup()
                    .equalTo("sender", buddy)
                    .equalTo("receiver", user)
                .endGroup()
                .findAll();

        return messages;
    }

    public Message getLastMessage(String user, String buddy){
        RealmResults<Message> messages = getAllChatMessagesOf(user, buddy);
        for(Message m: messages){
            Log.v("getlastmessage", m.getText());
        }
        Message m;
        try{
            m = messages.last();
        }catch (IndexOutOfBoundsException e){
            m = new Message();
        }

        return m;
    }

    //limpar banco
    public void deleteAll(){
        RealmResults<Message> results = realm.where(Message.class).findAll();

        // All changes to data must happen in a transaction
        realm.beginTransaction();

        // Delete all matches
        realm.deleteAll();

        realm.commitTransaction();
    }

    public void reset(){
        INSTANCE = new Databaser();
    }

    public void destroy(){

    }
}

package com.sd.chatcli.Singleton;

import android.content.Context;
import android.util.Log;

public class Sender {

    Context context;

    private String username;

    private static Sender INSTANCE;

    public static Sender getINSTANCE() {
        if (INSTANCE == null)
            INSTANCE = new Sender();
        return INSTANCE;
    }

    private Sender() {}

    public void setContext(Context context) {
        reset();
        INSTANCE.context = context;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        Log.v("sender", username);
        this.username = username;
    }

    public void reset(){
        INSTANCE = new Sender();
    }

}

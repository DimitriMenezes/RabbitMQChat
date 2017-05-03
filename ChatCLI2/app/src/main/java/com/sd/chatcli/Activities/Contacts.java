package com.sd.chatcli.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sd.chatcli.Models.Message;
import com.sd.chatcli.Models.User;
import com.sd.chatcli.R;
import com.sd.chatcli.Singleton.ChatCLI;
import com.sd.chatcli.Singleton.Databaser;
import com.sd.chatcli.Singleton.Sender;

import java.util.ArrayList;
import java.util.List;

public class Contacts extends AppCompatActivity {

    /** The Chat list. */
    private ArrayList<User> uList;

    /** The user. */
    public static User user;

    private Toolbar informUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        showTheNameOfMy(Sender.getINSTANCE().getUsername());
        listenToMessages();

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        loadUserList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Databaser.getINSTANCE().setContext(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void showTheNameOfMy(String sender){
        informUser = (Toolbar) findViewById(R.id.contact_toolbar);
        setSupportActionBar(informUser);
        getSupportActionBar().setTitle("Bem vindo, " + sender);
    }


    private void loadUserList()
    {
        List<User> users_list = Databaser.getINSTANCE().getUsers(Sender.getINSTANCE().getUsername());

        if (users_list != null && users_list.size() != 0) {

                uList = new ArrayList<User>(users_list);

                ListView list = (ListView) findViewById(R.id.list);
                list.setAdapter(new UserAdapter());
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view,
                                            int position, long id) {


                        String item = (String) uList.get(position).getName();
                        Intent intent = new Intent(Contacts.this, Chat.class);
                        intent.putExtra("username", item);
                        Contacts.this.startActivity(intent);
                    }
                });
        }

    }

    private void listenToMessages(){

        final Handler incomingMessageHandler = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg) {
                String message = msg.getData().getString("msg");
                Gson gson = new Gson();
                Message message_db = gson.fromJson(message, Message.class);
                message_db.setSent(false);
                if(message_db != null){
                    Toast.makeText(Contacts.this, "Nova mensagem enviada por " + message_db.getSender(), Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(Contacts.this, "houston we have a problem at receiving", Toast.LENGTH_LONG).show();
                }

                if(!Databaser.getINSTANCE().isUserCreated(message_db.getSender())){
                    Databaser.getINSTANCE().saveToDatabase(new User(message_db.getSender()));
                    loadUserList();
                }

                Databaser.getINSTANCE().saveToDatabase(message_db);
            }
        };

        ChatCLI.getINSTANCE().subscribe(incomingMessageHandler);

    }

    private class UserAdapter extends BaseAdapter
    {

        /* (non-Javadoc)
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount()
        {
            return uList.size();
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public User getItem(int arg0)
        {
            return uList.get(arg0);
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getItemId(int)
         */
        @Override
        public long getItemId(int arg0)
        {
            return arg0;
        }

        /* (non-Javadoc)
         * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(int pos, View v, ViewGroup arg2)
        {
            if (v == null)
                v = getLayoutInflater().inflate(R.layout.chat_item, null);

            User c = getItem(pos);
            TextView lbl = (TextView) v;
            lbl.setText(c.getName());

            return v;
        }

    }

}

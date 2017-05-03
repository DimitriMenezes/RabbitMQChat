package com.sd.chatcli.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.sd.chatcli.Models.User;
import com.sd.chatcli.R;
import com.sd.chatcli.Singleton.ChatCLI;
import com.sd.chatcli.Singleton.Databaser;

import java.util.ArrayList;
import java.util.List;

public class Login extends AppCompatActivity {

    private Button btn_login;
    private AutoCompleteTextView text_user;


    protected ProgressDialog progressBar;
    protected int progressBarStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

    }


    @Override
    protected void onStart() {
        super.onStart();

        ChatCLI.getINSTANCE().setContext(getApplicationContext());
        // Set up the login form.
        text_user = (AutoCompleteTextView) findViewById(R.id.input_user);
        Databaser.getINSTANCE().setContext(getApplicationContext());
        addUsersToAutoComplete();

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(btn_proximoListener);
    }


    protected boolean validateEditText(EditText editText){
        String str = editText.getText().toString();
        if(isEmpty(str)) {
            editText.setError(alertingFail("Não pode ser vazio", editText));
            return  false;
        }
        editText.setError(null);
        return true;
    }

    protected void loadProgressBar(String msg){
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(true);
        progressBar.setMessage(msg);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        progressBar.show();
        progressBarStatus = 0;
    }

    protected boolean isEmpty(String str){
        if(str.length() <= 0){
            return true;
        }
        return false;
    }

    protected SpannableStringBuilder alertingFail(String estring, EditText editText){
        int ecolor = ContextCompat.getColor(this, R.color.main_color_green);;
        ForegroundColorSpan fgcspan = new ForegroundColorSpan(ecolor);
        SpannableStringBuilder builder = new SpannableStringBuilder(estring);
        builder.setSpan(fgcspan, 0, estring.length(), 0);
        editText.setError(builder);
        return builder;
    }

    private View.OnClickListener btn_proximoListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            if (validateEditText(text_user)) {

                loadProgressBar("Verificando Usuário");
                new CountDownTimer(1000,1000){
                    @Override
                    public void onTick(long millisUntilFinished){}

                    @Override
                    public void onFinish(){
                        progressBar.dismiss();

                        ChatCLI.getINSTANCE().destroy();
                        Databaser.getINSTANCE().createOrAcessUser(text_user.getText().toString());

                        Intent intent = new Intent(Login.this, Contacts.class);
                        intent.putExtra("username", text_user.getText().toString());
                        Login.this.startActivity(intent);

                    }
                }.start();
            }
        }
    };

    private void addUsersToAutoComplete() {
        List<String> usersListCollection = new ArrayList<>();
        usersListCollection.add("dimitri");
        usersListCollection.add("giliard");
        usersListCollection.add("keomas");
        usersListCollection.add("lucas");

        for (String s: usersListCollection)
            if(!Databaser.getINSTANCE().isUserCreated(s))
                Databaser.getINSTANCE().saveToDatabase(new User(s));
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(Login.this,
                        android.R.layout.simple_dropdown_item_1line, usersListCollection);

        text_user.setAdapter(adapter);
    }

}

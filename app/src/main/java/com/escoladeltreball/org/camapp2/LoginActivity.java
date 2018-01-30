package com.escoladeltreball.org.camapp2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends CameraLauncher {

    private Button signIn;
    private Button signUp;
    private EditText username;
    private EditText password;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signIn = (Button) findViewById(R.id.signIn);
        signUp = (Button) findViewById(R.id.signUp);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        signIn.setOnClickListener(v -> attempLogin());
        signUp.setOnClickListener(v -> attempLogin());

    }

    private void attempLogin() {
        userEmail = username.getText()+"";
        String pass = password.getText()+"";


        if (!userEmail.contains("@")){
            Toast.makeText(this,"Username should be an email", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Signed In", Toast.LENGTH_LONG);
            setUserLogin(userEmail);
            config.setProperty("user", userEmail);
            guardarConfig();
        }

//            if (username.getText().toString().equals(user)&&password.getText().toString().equals(pass)){
//                Intent intent = new Intent(this,CameraLauncher.class);
//                startActivity(intent);
//                //userLogin = user;
//            }else {
//            Toast.makeText(this,"User not registered",Toast.LENGTH_LONG).show();
//        }
    }
}

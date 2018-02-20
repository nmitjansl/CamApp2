package com.escoladeltreball.org.camapp2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.escoladeltreball.org.camapp2.api.firebase.FirebaseConnection;

public class LoginActivity extends AppCompatActivity {

    private Button signIn;
    private Button signUp;
    private EditText username;
    private EditText password;

    public static boolean loginOK = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signIn = (Button) findViewById(R.id.signIn);
        signUp = (Button) findViewById(R.id.signUp);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);

        signIn.setOnClickListener(v -> attempLogin());
        signUp.setOnClickListener(v -> buttonSignUp());

    }

    private void attempLogin() {
        String userEmail = username.getText()+"";
        String pass = password.getText()+"";

        if (!userEmail.contains("@") || pass.length() < 6){
            Toast toast = Toast.makeText(this,"Email or password are invalid", Toast.LENGTH_LONG);
            TextView v = toast.getView().findViewById(android.R.id.message);
            v.setTextColor(Color.RED);
            toast.show();
        }

        CameraLauncher.firebaseConnection.signIn(userEmail,pass,getApplicationContext());

        while (!loginOK) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (loginOK) {
                Toast toast = Toast.makeText(this, "Signed In", Toast.LENGTH_LONG);
                TextView v = toast.getView().findViewById(android.R.id.message);
                v.setTextColor(Color.GREEN);
                toast.show();
                CameraLauncher.config.setProperty("username", userEmail);
                CameraLauncher.guardarConfig();
                Intent intent = new Intent(this, CameraLauncher.class);
                startActivity(intent);
                finish();
            } else continue;
        }
        /*Toast toast = Toast.makeText(this, "Login failed", Toast.LENGTH_LONG);
        TextView v = toast.getView().findViewById(android.R.id.message);
        v.setTextColor(Color.RED);
        toast.show();*/
    }

    private void buttonSignUp() {
        Intent intent = new Intent(this, NewUser.class);
        startActivity(intent);
    }

}

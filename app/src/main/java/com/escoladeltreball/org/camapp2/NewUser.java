package com.escoladeltreball.org.camapp2;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NewUser extends CameraLauncher {

    private EditText newName;
    private EditText newEmail;
    private EditText newPassword;
    private Button createNewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        newName = (EditText) findViewById(R.id.userName);
        newEmail = (EditText) findViewById(R.id.email);
        newPassword = (EditText) findViewById(R.id.password);
        createNewUser = (Button) findViewById(R.id.buttonCreateUser);

        createNewUser.setOnClickListener(v -> attempSignUp());
    }


    private void attempSignUp() {
        String userName = newName.getText()+"";
        String userEmail = newEmail.getText()+"";
        String pass = newPassword.getText()+"";


        if (!userEmail.contains("@") || pass.length() < 6){
            Toast toast = Toast.makeText(this,"Email or password are invalid", Toast.LENGTH_LONG);
            TextView v = toast.getView().findViewById(android.R.id.message);
            v.setTextColor(Color.RED);
            toast.show();
        } else if (firebaseConnection.createUser(userEmail, pass, getApplicationContext(), userName)){
            Toast toast = Toast.makeText(this, "Signed In", Toast.LENGTH_LONG);
            TextView v = toast.getView().findViewById(android.R.id.message);
            v.setTextColor(Color.GREEN);
            toast.show();
            config.setProperty("email", userEmail);
            readUserLogin();
            guardarConfig();
            Intent intent = new Intent(this, CameraLauncher.class);
            startActivity(intent);
        }
    }
}

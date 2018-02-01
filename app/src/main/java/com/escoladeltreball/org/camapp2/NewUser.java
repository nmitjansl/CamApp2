package com.escoladeltreball.org.camapp2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class NewUser extends AppCompatActivity {

    private EditText newName;
    private EditText newUser;
    private EditText newPassword;
    private Button createNewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        newName = (EditText) findViewById(R.id.newName);
        newUser = (EditText) findViewById(R.id.newUserName);
        newPassword = (EditText) findViewById(R.id.newPassword);
        createNewUser = (Button) findViewById(R.id.buttonCreateUser);


    }
}

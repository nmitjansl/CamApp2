package com.escoladeltreball.org.camapp2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class NewUser extends AppCompatActivity {

    private EditText newUser;
    private EditText newPassword;
    private Button createNewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

    }
}

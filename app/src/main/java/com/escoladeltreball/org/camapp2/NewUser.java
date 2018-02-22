package com.escoladeltreball.org.camapp2;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.escoladeltreball.org.camapp2.api.firebase.FirebaseConnection;
import com.escoladeltreball.org.camapp2.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class NewUser extends AppCompatActivity {

    private EditText newName;
    private EditText newEmail;
    private EditText newPassword;
    private Button createUser;

    private boolean action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        newName = findViewById(R.id.userName);
        newEmail = findViewById(R.id.email);
        newPassword = findViewById(R.id.password);
        createUser = findViewById(R.id.buttonCreateUser);

        createUser.setOnClickListener(v -> attempSignUp());
    }


    private void attempSignUp() {
        String userName = newName.getText()+"";
        String userEmail = newEmail.getText()+"";
        String pass = newPassword.getText()+"";

        if (action) return;
        else {
            action = true;
            createUser.setEnabled(false);
        }

        if (userEmail.isEmpty() || !userEmail.contains("@") || pass.length() < 6){
            Toast toast = Toast.makeText(getApplicationContext(),"Email or password are invalid", Toast.LENGTH_LONG);
            TextView v = toast.getView().findViewById(android.R.id.message);
            v.setTextColor(Color.RED);
            toast.show();
            action = false;
            createUser.setEnabled(true);
        } else {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(userEmail, pass).addOnCompleteListener(
                    task -> {
                        if (task.isSuccessful()) {
                            Toast toast = Toast.makeText(this, "Signed In", Toast.LENGTH_LONG);
                            TextView v = toast.getView().findViewById(android.R.id.message);
                            v.setTextColor(Color.GREEN);
                            toast.show();
                            FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
                            assert u != null;
                            User user = FirebaseConnection.insertUserDB(u.getEmail(), u.getUid(), pass, userName);
                            CameraLauncher.updateUser(user);
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {}
                            Intent intent = new Intent(this, CameraLauncher.class);
                            startActivity(intent);
                            action = false;
                            createUser.setEnabled(true);
                            finish();
                        } else {
                            Toast toast = Toast.makeText(this, "Registration failed", Toast.LENGTH_LONG);
                            TextView v = toast.getView().findViewById(android.R.id.message);
                            v.setTextColor(Color.RED);
                            toast.show();
                            action = false;
                            createUser.setEnabled(true);
                        }
                    }
            );
        }
    }
}

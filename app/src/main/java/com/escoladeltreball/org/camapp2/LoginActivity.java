package com.escoladeltreball.org.camapp2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.escoladeltreball.org.camapp2.api.firebase.FirebaseConnection;
import com.escoladeltreball.org.camapp2.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button signIn;

    private boolean action;
    private Context context;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        signIn = findViewById(R.id.signIn);

        signIn.setOnClickListener(v -> attempLogin());
        findViewById(R.id.signUp).setOnClickListener(v -> buttonSignUp());

    }

    private void attempLogin() {
        final String userEmail = username.getText()+"";
        String pass = password.getText()+"";

        if (action) return;
        else {
            signIn.setEnabled(false);
            action = true;
        }

        if (userEmail.isEmpty() || !userEmail.contains("@") || pass.length() < 6){
            Toast toast = Toast.makeText(getApplicationContext(),"Email or password are invalid", Toast.LENGTH_LONG);
            TextView v = toast.getView().findViewById(android.R.id.message);
            v.setTextColor(Color.RED);
            toast.show();
            action = false;
            signIn.setEnabled(true);
        } else {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(userEmail, pass).addOnCompleteListener(
                    task -> {
                        if (task.isSuccessful()) {
                            Toast toast = Toast.makeText(context, "Signed In", Toast.LENGTH_LONG);
                            TextView v = toast.getView().findViewById(android.R.id.message);
                            v.setTextColor(Color.GREEN);
                            toast.show();
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {}

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference().child("users" + "/" + "users_data");

                            myRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        User user = snapshot.getValue(User.class);
                                        assert user != null;
                                        if (user.getEmail().equalsIgnoreCase(userEmail)) {
                                            CameraLauncher.updateUser(user);
                                            Intent intent = new Intent(context, CameraLauncher.class);
                                            startActivity(intent);
                                            action = false;
                                            signIn.setEnabled(true);
                                            finish();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast toast = Toast.makeText(context, "Authentication failed", Toast.LENGTH_LONG);
                                    TextView v = toast.getView().findViewById(android.R.id.message);
                                    v.setTextColor(Color.RED);
                                    toast.show();
                                    action = false;
                                    signIn.setEnabled(true);
                                    System.out.println("The read failed: " + databaseError.getCode());
                                }
                            });
                        } else {
                            Toast toast = Toast.makeText(context, "Authentication failed", Toast.LENGTH_LONG);
                            TextView v = toast.getView().findViewById(android.R.id.message);
                            v.setTextColor(Color.RED);
                            toast.show();
                            action = false;
                            signIn.setEnabled(true);
                        }
                    }
            );
        }
    }

    private void buttonSignUp() {
        Intent intent = new Intent(context, NewUser.class);
        startActivityForResult(intent,12);
        finish();
    }
}

package com.escoladeltreball.org.camapp2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.escoladeltreball.org.camapp2.api.firebase.FirebaseConnection;
import com.escoladeltreball.org.camapp2.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        setTitle("SIGN UP - CamAPP 2");

        newName = findViewById(R.id.userName);
        newEmail = findViewById(R.id.email);
        newPassword = findViewById(R.id.password);
        createUser = findViewById(R.id.buttonCreateUser);

        createUser.setOnClickListener(v -> attempSignUp());
    }


    private void attempSignUp() {
        String userName = newName.getText()+"";
        String userEmail = newEmail.getText()+"".replaceAll(" ", "");
        String pass = newPassword.getText()+"";

        if (action) return;
        else {
            setButtonRegister(true);
        }

        // username format
        Pattern pattern = Pattern.compile("^[a-z_\\d]{3,20}$", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        Matcher matcher = pattern.matcher(userName);
        if (!matcher.find()) {
            DynamicToast.makeError(getApplicationContext(),"Username are invalid!", 2).show();
            setButtonRegister(false);
        } else if (userEmail.isEmpty() || !userEmail.contains("@") || pass.length() < 6){
            DynamicToast.makeError(getApplicationContext(),"Email or password are invalid!", 2).show();
            setButtonRegister(false);
        } else {
            // Check if userName exists
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference().child("users" + "/" + "users_data");

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!action) return;
                    boolean canCreate = true;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        assert user != null;
                        if (user.getName().equalsIgnoreCase(userName)) {
                            DynamicToast.makeError(getApplicationContext(),"This username is already in use!", 3).show();
                            setButtonRegister(false);
                            canCreate = false;
                        }
                    }
                    if (canCreate) {
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(userEmail, pass).addOnCompleteListener(
                                task -> {
                                    if (task.isSuccessful()) {
                                        DynamicToast.makeSuccess(getApplicationContext(), "Registered Successfully! Welcome aboard.", 4).show();
                                        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
                                        assert u != null;
                                        User user = FirebaseConnection.insertUserDB(u.getEmail(), u.getUid(), pass, userName);
                                        CameraLauncher.updateUser(user);
                                        Intent intent = new Intent(getApplicationContext(), CameraLauncher.class);
                                        startActivity(intent);
                                        finish();
                                        setButtonRegister(false);
                                    } else {
                                        DynamicToast.makeError(getApplicationContext(),"Email is invalid or may be used by another user...", 4).show();
                                        setButtonRegister(false);
                                    }
                                }
                        );
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    DynamicToast.makeWarning(getApplicationContext(), "No connection...", 2).show();
                    setButtonRegister(false);
                    System.out.println("[NewUser] The read failed: " + databaseError.getCode());
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        Activity parent = getParent();
        if (parent != null) parent.finish();
        super.onDestroy();
    }

    private void setButtonRegister(boolean b) {
        action = b;
        createUser.setEnabled(!b);
        createUser.setPressed(b);
        if (b) createUser.setTextColor(Color.parseColor("#4a4a4a"));
        else createUser.setTextColor(Color.BLACK);
    }
}

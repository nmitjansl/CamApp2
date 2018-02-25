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
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button signIn;

    private boolean action;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setTitle("SIGN IN - CamAPP 2");
        setContentView(R.layout.activity_login);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        signIn = findViewById(R.id.signIn);

        signIn.setOnClickListener(v -> attempLogin());
        findViewById(R.id.signUp).setOnClickListener(v -> buttonSignUp());

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CameraLauncher.getUser() != null) finish();
    }

    private void attempLogin() {
        final String userEmail = username.getText()+"".replaceAll(" ", "");
        String pass = password.getText()+"";

        if (action) return;
        else {
            setButtonSignIn(true);
        }

        if (userEmail.isEmpty() || !userEmail.contains("@") || pass.length() < 6){
            DynamicToast.makeError(context, "Email or password invalid!", 2).show();
            setButtonSignIn(false);
        } else {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(userEmail, pass).addOnCompleteListener(
                    task -> {
                        if (task.isSuccessful()) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference().child("users" + "/" + "users_data");

                            myRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (!action) return;
                                    boolean isExists = false;
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        User user = snapshot.getValue(User.class);
                                        assert user != null;
                                        if (user.getEmail().equalsIgnoreCase(userEmail)) {
                                            CameraLauncher.updateUser(user);
                                            DynamicToast.makeSuccess(context, "Signed In", 2).show();
                                            Intent intent = new Intent(context, CameraLauncher.class);
                                            startActivity(intent);
                                            finish();
                                            setButtonSignIn(false);
                                            isExists = true;
                                        }
                                    }
                                    if (!isExists) {
                                        DynamicToast.makeError(context, "We are detected an error with your account, please, register again n.n", 10).show();
                                        FirebaseAuth.getInstance().getCurrentUser().delete();
                                        setButtonSignIn(false);
                                        buttonSignUp();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    DynamicToast.makeWarning(context, "No connection...", 2).show();
                                    setButtonSignIn(false);
                                    System.out.println("[LoginActivity] The read failed: " + databaseError.getCode());
                                }
                            });
                        } else {
                            DynamicToast.makeError(context, "Email or password wrong!", 2).show();
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            setButtonSignIn(false);
                        }
                    }
            );
        }
    }

    private void buttonSignUp() {
        Intent intent = new Intent(this, NewUser.class);
        startActivity(intent);
    }

    private void setButtonSignIn(boolean b) {
        action = b;
        signIn.setEnabled(!b);
        signIn.setPressed(b);
        if (b) signIn.setTextColor(Color.parseColor("#2f2f2f"));
        else signIn.setTextColor(Color.WHITE);
    }
}

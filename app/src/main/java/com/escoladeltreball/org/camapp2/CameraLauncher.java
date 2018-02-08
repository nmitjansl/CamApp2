package com.escoladeltreball.org.camapp2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.escoladeltreball.org.camapp2.api.firebase.FirebaseConnection;
import com.escoladeltreball.org.camapp2.models.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

public class CameraLauncher extends AppCompatActivity {
    private static File fconfig = new File(Environment.getExternalStorageDirectory(),"config.properties");
    protected static Properties config = new Properties();
    private static User user = new User();

    protected static FirebaseConnection firebaseConnection = new FirebaseConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        leerConfig();
        readUserLogin();
        if (user.getEmail().isEmpty()) {
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
        } else {
            setContentView(R.layout.activity_camera_launcher);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }
    }

    protected static void readUserLogin() {
        System.out.println("Email: " + config.getProperty("email"));
        user.setEmail(config.getProperty("email"));
        user.setName(config.getProperty("username"));
    }

    protected static void setUserLogin(User userobject) {
        user = userobject;
    }

    public static User getUserLogin() {return user;}

    protected static void guardarConfig() {
        generarConfig();
        try {
            config.store(new FileOutputStream(fconfig), "Config saved succesfully");
        } catch (IOException e) {}
    }

    private static void leerConfig() {
       generarConfig();
        try {
            config.load(new FileInputStream(fconfig));
        } catch (IOException e) {}
    }

    private static void generarConfig() {
        if (!fconfig.exists()) {
            try {
                fconfig.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                config.setProperty("username", "");
                config.setProperty("email", "");
                config.store(new FileOutputStream(fconfig), "First config save");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

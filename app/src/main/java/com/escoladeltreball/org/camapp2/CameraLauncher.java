package com.escoladeltreball.org.camapp2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

public class CameraLauncher extends AppCompatActivity {
    private static File fconfig = new File("config.properties");
    protected static Properties config;
    private static String userLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        leerConfig();
        userLogin = config.getProperty("user");
        if (userLogin == null) {
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

    protected static void setUserLogin(String emailUser) {
        userLogin = emailUser;
    }

    public static String getUserLogin() {return userLogin;}

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
                config.setProperty("user", null);
                config.setProperty("token", null);
                config.store(new FileOutputStream(fconfig), "First config save");
            } catch (IOException e) {}
        }
    }
}

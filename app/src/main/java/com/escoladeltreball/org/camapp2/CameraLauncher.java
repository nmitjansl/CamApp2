package com.escoladeltreball.org.camapp2;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.escoladeltreball.org.camapp2.api.firebase.FirebaseConnection;
import com.escoladeltreball.org.camapp2.models.User;

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

    private static final String GALLERY = "/CamApp2";
    private Uri imgUri;
    private static final int MY_REQUEST_CODE = 12;

    private static final String LOG_TAG = "CamAPP2Log";

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
                    openCamera();
                    if(imgUri != null){
                        firebaseConnection.upload(imgUri.toString()); // TODO pendiente a Luca lo diga
                    }
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

    private void openCamera() {
        try {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getImageUri());

            startActivityForResult(cameraIntent, MY_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Uri getImageUri() {


        File galleryFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + GALLERY);
        if (!galleryFile.exists()) {
            galleryFile.mkdirs();
            Log.e(LOG_TAG, "Directory created");
        } else {
            Log.e(LOG_TAG, "Directory not created");
        }
        File image = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + GALLERY, System.currentTimeMillis() + ".jpeg");
        Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", image);
        imgUri = uri;

        return uri;
    }
}

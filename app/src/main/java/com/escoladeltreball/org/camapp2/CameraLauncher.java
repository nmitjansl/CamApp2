package com.escoladeltreball.org.camapp2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.escoladeltreball.org.camapp2.api.firebase.FirebaseConnection;
import com.escoladeltreball.org.camapp2.models.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class CameraLauncher extends AppCompatActivity {
    private static File fuser;
    private static User user;

    protected FirebaseConnection firebaseConnection = new FirebaseConnection();

    private static final String GALLERY = "/CamApp2";
    private Uri imgUri;
    private static final int MY_REQUEST_CODE = 12;

    private static final String LOG_TAG = "CamAPP2Log";

    private static boolean started;

    private static ArrayList<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            if (!started) {
                readUser();
                System.out.println("Usuario leído");
                started = true;
            }
        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(),"CamAPP2 needs write SD permissions", Toast.LENGTH_LONG);
            TextView v = toast.getView().findViewById(android.R.id.message);
            v.setTextColor(Color.RED);
            toast.show();
            e.printStackTrace();
            requestPermissions();
        }
        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            setResult(Activity.RESULT_OK,intent);
            finish();
        } else {
            setContentView(R.layout.activity_camera_launcher);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            FloatingActionButton fab = findViewById(R.id.fab);
            fab.setOnClickListener(view -> {
                openCamera();
                if(imgUri != null){
                    firebaseConnection.upload(imgUri.toString()); // TODO pendiente a Luca lo diga
                }
            });
        }
    }

    @Override
    protected void onStop() {
        try {
            saveUser();
            System.out.println("Usuario guardado");
        } catch (IOException e) {
            requestPermissions();
            Toast toast = Toast.makeText(getApplicationContext(),"CamAPP2 needs write SD permissions", Toast.LENGTH_LONG);
            TextView v = toast.getView().findViewById(android.R.id.message);
            v.setTextColor(Color.RED);
            toast.show();
            e.printStackTrace();
        } finally {
            //finish();
            super.onStop();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void requestPermissions() {
        // TODO Pedir permisos en caso de que no los tenga
    }

    /* PUBLIC USER FUNCTIONS */

    protected static User getUser() {
        return user;
    }

    protected static void updateUser(User u) {
        user = u;
    }

    /* PRIVATE FUNCTIONS USER */

    private synchronized void saveUser() throws IOException {
        //Saving of object in a file
        FileOutputStream file = new FileOutputStream(fuser);
        ObjectOutputStream out = new ObjectOutputStream(file);

        // Method for serialization of object
        out.writeObject(user);

        out.close();
        file.close();
    }

    private synchronized void readUser() throws IOException, ClassNotFoundException {
        fuser = new File(getExternalFilesDir(null), "user.dat");
        if (!fuser.exists()) {
            saveUser();
            readUser();
            return;
        }
        FileInputStream file = new FileInputStream(fuser);
        ObjectInputStream in = new ObjectInputStream(file);

        // Method for deserialization of object
        user = (User)in.readObject();

        in.close();
        file.close();
    }

    /* PRIVATE CAMERA FUNCTIONS */

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

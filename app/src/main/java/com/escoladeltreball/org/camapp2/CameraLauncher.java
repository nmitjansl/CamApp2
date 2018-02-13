package com.escoladeltreball.org.camapp2;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;

public class CameraLauncher extends AppCompatActivity {


    private static final String GALLERY = "/CamApp2";
    private Uri imgUri;
    private static final int MY_REQUEST_CODE = 12;

    private static final String LOG_TAG = "CamAPP2Log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        openCamera();
        if(imgUri != null){
            //FirebaseConnection.upload(imgUri); // TODO descomentar esto cuando Luca lo diga
        }
        finish();

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
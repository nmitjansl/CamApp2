package com.escoladeltreball.org.camapp2;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends AppCompatActivity {

    /*static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;*/

    private static final String GALLERY = "/CamApp2";
    private static final int MY_REQUEST_CODE = 12;
    private static final int TAKE_PICTURE = 1;

    private static final String LOG_TAG = "CamAPP2Log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        //dispatchTakePictureIntent();

        openCamera();

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


        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + GALLERY);
        if (!file.exists()) {
            file.mkdirs();
            Log.e(LOG_TAG, "Directory created");
        } else {
            Log.e(LOG_TAG, "Directory not created");
        }
        File file2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + GALLERY, System.currentTimeMillis() + ".jpeg");

        Uri imgUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file2);

        return imgUri;
    }



    /*private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                Log.v("BLABLA", photoFile.toString());
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
            //startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }


    // Crea un File per guardar la foto en ell
    private File createImageFile() throws IOException {
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String timeStamp = "PEPITO";
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES), "CamApp2ALBUM");

        File image = File.createTempFile(
                imageFileName,  *//* prefix *//*
                ".jpg",         *//* suffix *//*
                storageDir      *//* directory *//*
        );

        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            galleryAddPic();
            Log.v("BLABLA", "CREAT!");
        }
        Log.v("BLABLA", "FORA!");
    }


    @Override
    protected void onResume() {
        super.onResume();
        galleryAddPic();
    }



    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }*/

}
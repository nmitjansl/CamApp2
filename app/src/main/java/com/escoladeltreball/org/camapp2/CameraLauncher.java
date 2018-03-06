package com.escoladeltreball.org.camapp2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.escoladeltreball.org.camapp2.api.firebase.FirebaseConnection;
import com.escoladeltreball.org.camapp2.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CameraLauncher extends AppCompatActivity {
    private static File fuser;
    private static User user;

    protected FirebaseConnection firebaseConnection = new FirebaseConnection();

    private static final String GALLERY = "/CamApp2";
    private static final String LOG_TAG = "CamAPP2Log";
    private Uri imgUri;
    private static final int MY_REQUEST_CODE = 12;
    private static final int LIST_CONTACTS_PERMISSION_CODE = 10;
    private static final int READ_CONTACTS_PERMISSION_CODE = 20;
    private static final int WRITE_SD_PERMISSION_CODE = 30;

    private static boolean started;
    private static ArrayList<User> userArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            if (!started) {
                readUser();
                started = true;
            }
        } catch (Exception e) {
            DynamicToast.makeWarning(this,"CamAPP2 needs write SD permissions", 3).show();
            e.printStackTrace();
            requestPermissions(WRITE_SD_PERMISSION_CODE);
        }
        if (user == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            setContentView(R.layout.activity_camera_launcher);

            // ToolTitle
            TextView tl = findViewById(R.id.textLogo);
            tl.append(" " + user.getName());

            // User's Gallery List
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference().child("users" + "/" + "users_data");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<String> userList = new ArrayList<>();
                    userList.add("");

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User post = snapshot.getValue(User.class);
                        userArrayList.add(post);
                        if (post.getEmail().equalsIgnoreCase(user.getEmail())) continue;
                        userList.add(post.getName());
                    }
                    Spinner s = findViewById(R.id.userList);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                            android.R.layout.simple_list_item_1, userList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    s.setAdapter(adapter);
                    s.getViewTreeObserver().addOnGlobalLayoutListener(() -> ((TextView) s.getSelectedView()).setTextColor(Color.WHITE));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("[CameraLauncher] The read failed: " + databaseError.getCode());
                }
            });



            // LOG OUT
            FloatingActionButton fab1 = findViewById(R.id.logOutButton);
            fab1.setOnClickListener(view -> {
                updateUser(null);
                DynamicToast.makeSuccess(this,"Signed Out!").show();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            });

            // OPEN CAMERA
            FloatingActionButton fab2 = findViewById(R.id.openCameraButton);
            fab2.setOnClickListener(view -> {
                openCamera();
                if(imgUri != null){
                    firebaseConnection.upload(imgUri.toString()); // TODO pendiente a Luca lo diga
                }
            });

            Spinner spinner = findViewById(R.id.userList);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    String selectedItem = parent.getItemAtPosition(position).toString();
                    if (selectedItem.isEmpty()) return;
                    Intent intent = new Intent(getApplicationContext(), PhotoListActivity.class);
                    User us = null;
                    for(User u : userArrayList) {
                        if (u.getName().equalsIgnoreCase(selectedItem)) us = u;
                    }
                    if (us == null) {
                        DynamicToast.makeError(getApplicationContext(), "Nothing selected!").show();
                    } else {
                        intent.putExtra("USER",us);
                        startActivity(intent);
                    }

                } // to close the onItemSelected
                public void onNothingSelected(AdapterView<?> parent)
                {

                }
            });

            // MY GALLERY
//            ImageView iv = findViewById(R.id.iv);
            Button my_photos = findViewById(R.id.my_photos);
            my_photos.setOnClickListener(v -> {
//                new DownLoadImageTask(iv).execute("https://skimdoo.com/img/logo.png");
//                requestPermissions(WRITE_SD_PERMISSION_CODE);

                Intent intent = new Intent(this, PhotoListActivity.class);
                intent.putExtra("USER",user);
                startActivity(intent);
            });
        }
    }

    @Override
    protected void onStop() {
        try {
            saveUser();
        } catch (IOException e) {
            requestPermissions(WRITE_SD_PERMISSION_CODE);
            DynamicToast.makeWarning(this,"CamAPP2 needs write SD permissions", 3).show();
            e.printStackTrace();
        } finally {
            super.onStop();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 10: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) DynamicToast.makeSuccess(this,"Permissions granted! We can help us to sign in/up!", 5).show();
                else DynamicToast.makeWarning(this,"Ok...").show();
            }
            case 20: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) DynamicToast.makeSuccess(this,"Permissions granted! We can help us to sign in/up!", 5).show();
                else DynamicToast.makeWarning(this,"Ok...").show();
            }
            case 30: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) DynamicToast.makeSuccess(this,"Permissions granted! We can save your session!", 5).show();
                else DynamicToast.makeWarning(this,"Saved session will not work! You must sign in always!", 5).show();
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void requestPermissions(int i) {
        switch (i) {
            case 10: {
                if (!checkPermission(LIST_CONTACTS_PERMISSION_CODE)) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.GET_ACCOUNTS},
                            LIST_CONTACTS_PERMISSION_CODE);
                }
            }
            case 20: {
                if (!checkPermission(READ_CONTACTS_PERMISSION_CODE)) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            READ_CONTACTS_PERMISSION_CODE);
                }
            }
            case 30: {
                if (!checkPermission(WRITE_SD_PERMISSION_CODE)) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_SD_PERMISSION_CODE);
                }
            }
        }
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
        DynamicToast.makeSuccess(this, "User saved!").show();

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
        DynamicToast.makeSuccess(this, "User load!").show();

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

    /* PRIVATE API */
    private boolean checkPermission(int requestCode) {
        DynamicToast.makeWarning(this, "Check permissions...").show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switch (requestCode) {
                case 10: {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    }
                }
                case 20: {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    }
                }
                case 30: {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    }
                }
            }
        }
        DynamicToast.makeWarning(this, "Your device is not compatible...").show();
        return true;
    }

    /*
        AsyncTask enables proper and easy use of the UI thread. This class
        allows to perform background operations and publish results on the UI
        thread without having to manipulate threads and/or handlers.
     */

    /*
        final AsyncTask<Params, Progress, Result>
            execute(Params... params)
                Executes the task with the specified parameters.
     */
    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
        }
    }
}

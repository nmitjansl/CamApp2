package com.escoladeltreball.org.camapp2;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.escoladeltreball.org.camapp2.api.firebase.FirebaseConnection;
import com.escoladeltreball.org.camapp2.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Pruebas";
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        storageRef = FirebaseStorage.getInstance().getReference();
        FirebaseConnection con = new FirebaseConnection("lpbove@gmail.com",storageRef);


        /*con.upload("com/escoladeltreball/org/camapp2/api/firebase/testAndroid.png");*//*
        //con.upload2();
        FirebaseConnection firebaseConnection = new FirebaseConnection();
        *//*firebaseConnection.insertUserDB("samuel@gmail.com","1", "cass","samuel");
        firebaseConnection.insertUserDB("daniel@gmail.com","2", "da","daniel");*/

        FirebaseConnection firebaseConnection = new FirebaseConnection("lpbove@gmail.com",storageRef);
        firebaseConnection.mAuth = FirebaseAuth.getInstance();
        // [START sign_in_with_email]
        firebaseConnection.mAuth.signInWithEmailAndPassword("prueba@gmail.com", "123456")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            firebaseConnection.userFire = firebaseConnection.mAuth.getCurrentUser();
                            firebaseConnection.userFire.getEmail();
                            Toast.makeText(getApplicationContext(), "Succesful signup with user: " + firebaseConnection.userFire.getEmail(), Toast.LENGTH_LONG).show();
                            Log.w(TAG, "signInWithEmail:succes");

                            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/testIMG.png";
                            firebaseConnection.upload(path);


                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());

                        }
                    }
                });



/*
        firebaseConnection.createUser("prueba@gmail.com","123456",getApplicationContext(),"samuel");



        for(User user:firebaseConnection.listUsers() ) {

            System.out.println(user.getEmail());
        }

        */


    }
}

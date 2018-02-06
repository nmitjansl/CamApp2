package com.escoladeltreball.org.camapp2.api.firebase;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.escoladeltreball.org.camapp2.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


public class FirebaseConnection {
    @VisibleForTesting
    public ProgressDialog mProgressDialog;
    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    private StorageReference storageRef;
    private String user;
    private String path;
    private ArrayList<String> allDir;
    private ArrayList<String> userDir;
    private Context context;


    public FirebaseConnection() {
    }

    public FirebaseConnection(String user, StorageReference storageRef) {
        this.storageRef = storageRef;
        this.user = user;
    }

    /**
     * Registra usuarios con el método de autentificación de firebase, el password tiene que tener un mínimo
     * de 6 carácteres y el email tiene que ser válido.
     */


    public boolean createUser(final String email, final String password, final Context context, final String name) {
        final boolean[] resultado = {false};
        this.context = context;
        mAuth = FirebaseAuth.getInstance();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    resultado[0] = true;
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success");
                    Toast toast = Toast.makeText(context, "Registration succesfull.", Toast.LENGTH_SHORT);
                    TextView v = toast.getView().findViewById(android.R.id.message);
                    v.setTextColor(Color.GREEN);
                    toast.show();
                    FirebaseUser user = mAuth.getCurrentUser();

                    insertUserDB(user.getEmail(), user.getUid(), password, name);

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast toast = Toast.makeText(context, "Registration failed.", Toast.LENGTH_SHORT);
                    TextView v = toast.getView().findViewById(android.R.id.message);
                    v.setTextColor(Color.RED);
                    toast.show();
                }

            }
        });
        return resultado[0];
    }

    /**
     * Comprueba si el usuario está registrado, devuelve un booleano indicando si existe o no
     */

    public boolean signIn(String email, String password, final Context context) {
        Log.d(TAG, "signIn:" + email);
        final boolean[] resultado = {false};


        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            resultado[0] = true;

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                        // [END_EXCLUDE]
                    }
                });


        // [END sign_in_with_email]
        return resultado[0];
    }

    public ArrayList<String> listDirectory() {
        allDir = (ArrayList<String>) Arrays.asList(new File(storageRef.getPath()).list());
        return allDir;
    }

    public ArrayList<String> listUserDirectory(String user) {
        userDir = (ArrayList<String>) Arrays.asList(new File(storageRef.getPath() + "/" + user).list());
        return userDir;
    }


    public void insertUserDB(String email, String uid, String pass, String name) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("users" + "/" + "users_data").child(uid);
        User user = new User(uid, name, email,  pass);
        myRef.setValue(user);
    }

    public ArrayList<User> listUsers() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("users" + "/" + "users_data");
        Query queryRef = myRef.endAt(null);
        //TODO
        //DataSnapshot dataSnapshot =
        return null;
    }

    public boolean upload(String img) {
        Uri file = Uri.fromFile(new File(img));
        if (img.contains("/")) {
            img = img.substring(img.lastIndexOf("/") + 1);
        }
        StorageReference imgRef = storageRef.child(user + "/" + img);


        imgRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
        return true;
    }

    public boolean download(StorageReference imgRef, String fileName) {
        File localFile = null;
        String[] split = fileName.split(".");
        try {
            localFile = File.createTempFile(split[0], split[1]);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        imgRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Successfully downloaded data to local file
                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle failed download
                // ...
            }
        });
        return true;
    }


}

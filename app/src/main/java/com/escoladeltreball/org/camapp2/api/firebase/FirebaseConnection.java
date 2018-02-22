package com.escoladeltreball.org.camapp2.api.firebase;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.escoladeltreball.org.camapp2.models.Image;
import com.escoladeltreball.org.camapp2.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class FirebaseConnection {
    @VisibleForTesting
    public ProgressDialog mProgressDialog;
    private static final String TAG = "EmailPassword";
    public FirebaseAuth mAuth;
    private StorageReference storageRef;
    private String user;
    private String path;
    private ArrayList<String> allDir;
    private ArrayList<String> userDir;
    private Context context;
    public FirebaseUser userFire;


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


    public void createUser(final String email, final String password, final Context context, final String name) {
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    userFire = mAuth.getCurrentUser();

                    insertUserDB(userFire.getEmail(), userFire.getUid(), password, name);

                } else {

                }

            }
        });
    }

    /**
     * Comprueba si el usuario está registrado, devuelve un booleano indicando si existe o no
     */

    public void signIn(String email, String password, final Context context) {
        Log.d(TAG, "signIn:" + email);
        mAuth = FirebaseAuth.getInstance();
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            userFire = mAuth.getCurrentUser();
                            Log.w(TAG, "signInWithEmail:succes");
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());

                        }
                    }
                });
    }

    public static User insertUserDB(String email, String uid, String pass, String name) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("users" + "/" + "users_data").child(uid);
        User user = new User(uid, name, email, pass);
        myRef.setValue(user);
        return user;
    }

    public void insertUserImage(String nombre, String direccio) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("users" + "/" + "users_images").child(nombre);
        Image image = new Image(userFire.getEmail(), direccio);
        myRef.setValue(image);
    }

    public ArrayList<User> listUsers() {
        //Copia desde aquí
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("users" + "/" + "users_data");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<User> userList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User post = snapshot.getValue(User.class);
                    System.out.println(post.toString());
                    userList.add(post);

                }
                //Haz tu código aquí
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        //Hasta aquí
        final ArrayList<User> userArrayList = new ArrayList<>();
        return userArrayList;

    }

    public ArrayList<Image> listImages(String uid) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("users" + "/" + "users_image" + "/" + uid);
        final ArrayList<Image> imageArrayList = new ArrayList<>();


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Image> imageList = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Image image = snapshot.getValue(Image.class);
                    System.out.println(image.toString());
                    imageList.add(image);
                }
                //Una vez tengas la lista, pon tu código aquí.

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        return imageArrayList;

    }



    public void upload(String path) {
        //copia metodo
        File file = new File(path);

        if (file.exists()) {

            String name = file.getName();
            StorageReference imgRef = storageRef.child(userFire.getEmail()).child(name);
            InputStream stream = null;

            try {
                stream = new FileInputStream(new File(path));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            imgRef.putStream(stream)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get a URL to the uploaded content
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        insertUserImage(userFire.getUid(), String.valueOf(taskSnapshot.getDownloadUrl()));
                        //pon aqui tu codigo
                    })
                    .addOnFailureListener(exception -> {
                        // Handle unsuccessful uploads
                        // ...
                    });
        }
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

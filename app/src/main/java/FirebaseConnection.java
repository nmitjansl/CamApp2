import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.escoladeltreball.org.camapp2.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class FirebaseConnection{
    private StorageReference storageRef;
    private String user;
    private ArrayList<String> allDir;
    private ArrayList<String> userDir;


    FirebaseConnection(String user, StorageReference storageRef){
        this.storageRef = storageRef;
        this.user = user;
    }


    public ArrayList<String> listDirectory(){
        allDir =(ArrayList<String>)Arrays.asList(new File(storageRef.getPath()).list());
        return allDir;
    }

    public ArrayList<String> listUserDirectory(String user){
        userDir = (ArrayList<String>) Arrays.asList(new File(storageRef.getPath()+"/"+user).list());
        return userDir;
    }


    public boolean upload(String img){
        Uri file = Uri.fromFile(new File(img));
        if(img.lastIndexOf("/") != -1){
            img = img.substring(img.lastIndexOf("/")+1);
        }
        StorageReference imgRef = storageRef.child(user+"/"+img);

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

    public boolean download(StorageReference imgRef, String fileName){
        File localFile = null;
        String[] split = fileName.split(".");
        try {
            localFile = File.createTempFile(split[0],split[1]);
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

    //GETTERS & SETTERS//


    public StorageReference getStorageRef() {
        return storageRef;
    }

    public void setStorageRef(StorageReference storageRef) {
        this.storageRef = storageRef;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public ArrayList<String> getAllDir() {
        return allDir;
    }

    public void setAllDir(ArrayList<String> allDir) {
        this.allDir = allDir;
    }

    public ArrayList<String> getUserDir() {
        return userDir;
    }

    public void setUserDir(ArrayList<String> userDir) {
        this.userDir = userDir;
    }
}

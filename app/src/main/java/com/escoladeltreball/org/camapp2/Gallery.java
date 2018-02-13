package com.escoladeltreball.org.camapp2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.List;

public class Gallery extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
    }
}

class Pic {
    private String url;

    public Pic(String url) {
        this.url = url;
    }
}

class PictureAdapter extends ArrayAdapter<Pic> {
    private Context context;
    private List<Pic> pictures;

    // TODO Constructor using super
}
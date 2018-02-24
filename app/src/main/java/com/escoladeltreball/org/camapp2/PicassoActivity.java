package com.escoladeltreball.org.camapp2;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.squareup.picasso.Picasso;

abstract class PicassoActivity extends FragmentActivity {
    private FrameLayout galleryContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_picasso);
        galleryContent = (FrameLayout) findViewById(R.id.gallery_content);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Picasso.with(this).cancelTag(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        getLayoutInflater().inflate(layoutResID, galleryContent);
    }

    @Override
    public void setContentView(View view) {
        galleryContent.addView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        galleryContent.addView(view, params);
    }
}

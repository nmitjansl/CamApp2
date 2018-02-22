package com.escoladeltreball.org.camapp2;

import android.os.Bundle;
import android.widget.GridView;

//public class PhotoListActivity extends AppCompatActivity {

public class PhotoListActivity extends PicassoActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);

        GridView gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setAdapter(new GridViewAdapter(this));
        gridView.setOnScrollListener(new ScrollListener(this));
    }
}


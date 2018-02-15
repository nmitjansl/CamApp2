package com.escoladeltreball.org.camapp2;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.List;

public class PhotoListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_list);
    }
}
// TODO
/*class PhotoListAdapter extends ArrayAdapter<Photo>{

    public PhotoListAdapter(Context context, List<Photo> photos) {
        super(context, resource);
    }
}*/

class Photo {

}
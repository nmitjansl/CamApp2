package com.escoladeltreball.org.camapp2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.escoladeltreball.org.camapp2.models.Image;
import com.escoladeltreball.org.camapp2.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

public class PhotoListActivity extends PicassoActivity {

    private ArrayList<Image> urls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        User user = (User) intent.getParcelableExtra("USER");

        // TODO remove fake user
        user = new User("pocholo", "pocholo", "pocholo@gmail.com", "pocholo");

        listImages(user.getUid());

        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.gallery_content, MasterFragment.newInstance())
                    .commit();
            Log.v("MYTAG","Commit fragment");
        }*/
    }

    void showDetails(String url) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.gallery_content, DetailFragment.newInstance(url))
                .addToBackStack(null)
                .commit();
    }

    // uid = pocholo
    public void listImages(String uid) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference().child("users" + "/" + "users_images");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Image> userImages = new ArrayList<>();
                DataSnapshot refUid = null;
                String realUID = null;
                try{
                    realUID = dataSnapshot.child("users_images").child(uid).getKey();
                    if(realUID == uid){
                        refUid = dataSnapshot.child(uid);
                    }
                }catch(Exception e){
                    refUid = null;
                }
                if(realUID == uid){
                    for(DataSnapshot item : refUid.getChildren()){
                        Object test = item.getValue();
                        Image image = item.getValue(Image.class);
                        if(image != null){
                            userImages.add(image);
                        }
                    }
                }

                // clear urls and addAll
                for (Image image : userImages) {
                    urls.add(image);
                }

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.gallery_content, MasterFragment.newInstance())
                        .commit();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public ArrayList<Image> getUrls() {
        return urls;
    }

    public static class MasterFragment extends Fragment {
        public static MasterFragment newInstance() {
            return new MasterFragment();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final PhotoListActivity activity = (PhotoListActivity) getActivity();
            final GridAdapter adapter = new GridAdapter(activity);

            GridView gridView = (GridView) LayoutInflater.from(activity)
                    .inflate(R.layout.activity_photo_list, container, false);
            gridView.setAdapter(adapter);
            gridView.setOnScrollListener(new ScrollListener(activity));
            gridView.setOnItemClickListener((adapterView, view, position, id) -> {
                String url = adapter.getItem(position).getDireccio();
                activity.showDetails(url);
            });
            return gridView;
        }
    }

    public static class DetailFragment extends Fragment {
        private static final String KEY_URL = "picasso:url";

        public static DetailFragment newInstance(String url) {
            Bundle arguments = new Bundle();
            arguments.putString(KEY_URL, url);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            Activity activity = getActivity();
            View view = LayoutInflater.from(activity)
                    .inflate(R.layout.activity_photo_list_detail, container, false);

            ImageView imageView = (ImageView) view.findViewById(R.id.photo);
            Button btn_like = (Button) view.findViewById(R.id.btn_like);
            // TODO set like listener

            Bundle arguments = getArguments();
            String url = arguments.getString(KEY_URL);

            Picasso.with(getContext())
                    .load(url)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.error)
                    .fit()
                    .tag(activity)
                    .into(imageView);

            return view;
        }
    }
}

final class GridAdapter extends BaseAdapter {
    private final Context context;
    private final PhotoListActivity activity;
    private final List<Image> images = new ArrayList<>();

    public GridAdapter(Context context) {
        this.context = context;
        activity = (PhotoListActivity) context;

        for (Image image : activity.getUrls()) {
            images.add(image);
        }

    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Image getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SquaredImageView view = (SquaredImageView) convertView;
        if (view == null) {
            view = new SquaredImageView(context);
            view.setScaleType(CENTER_CROP);
        }

        String url = getItem(position).getDireccio();
        Log.v("MYTAG","getView:"+url);
        Picasso.with(context)
                .load(url)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .fit()
                .tag(context)
                .into(view);

        return view;
    }

}

final class SquaredImageView extends android.support.v7.widget.AppCompatImageView {
    public SquaredImageView(Context context) {
        super(context);
    }

    public SquaredImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}

class ScrollListener implements AbsListView.OnScrollListener {
    private final Context context;

    public ScrollListener(Context context) {
        this.context = context;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        final Picasso picasso = Picasso.with(context);
        if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
            picasso.resumeTag(context);
        } else {
            picasso.pauseTag(context);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        // Do nothing.
    }
}

final class Data {
    static final String BASE = "http://i.imgur.com/";
    static final String EXT = ".jpg";
    static final String[] URLS = {
            BASE + "bXSam7h" + EXT, BASE + "KRatyV5" + EXT, BASE + "9hPHF4V" + EXT,
            BASE + "Gy4fExt" + EXT, BASE + "GIjuplT" + EXT, BASE + "GH4uFn5" + EXT,

    };

    private Data() {
        // No instances.
    }
}
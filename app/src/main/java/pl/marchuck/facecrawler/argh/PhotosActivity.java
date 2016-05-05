package pl.marchuck.facecrawler.argh;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.marchuck.facecrawler.R;

public class PhotosActivity extends AppCompatActivity {

    public static final String TAG = PhotosActivity.class.getSimpleName();
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_single_photo);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        List<String> items = new ArrayList<>();
        if (getIntent().hasExtra("DATA")) {
            String[] response = getIntent().getStringArrayExtra("DATA");
            items.addAll(Arrays.asList(response));
        }
        for (String s : items) Log.d(TAG, "next url\n" + s);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new PhotoAdapter(this, items));
    }

    public static class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.VH> {
        List<String> items;
        Context ctx;

        public PhotoAdapter(Context ctx, List<String> items) {
            this.items = items;
            this.ctx = ctx;
        }

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, null, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            final String url = items.get(position);
            Picasso.with(ctx).load(url)
                    .error(new ColorDrawable(Color.DKGRAY))
                    .fit()
                    .into(holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    ctx.startActivity(browserIntent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public static class VH extends RecyclerView.ViewHolder {
            public ImageView imageView;

            public VH(View v) {
                super(v);
                imageView = (ImageView) v.findViewById(R.id.image);
            }
        }
    }
}

package pl.marchuck.facecrawler.drawer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.marchuck.facecrawler.R;
import pl.marchuck.facecrawler.ifaces.Updatable;

public class DrawerFragment extends Fragment implements Updatable {

    public DrawerPresenter drawerPresenter;

    @Bind(R.id.recycler_view)
    public RecyclerView recyclerView;

    @Bind(R.id.text)
    public TextView text;

    @Bind(R.id.image)
    public ImageView image;

    public DrawerFragment() {
        // Required empty public constructor
    }

    public static DrawerFragment newInstance() {
        DrawerFragment fragment = new DrawerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drawer, container, false);
        ButterKnife.bind(this, view);
        drawerPresenter = new DrawerPresenter(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupPhotoAndText();
    }

    private void setupPhotoAndText() {
        drawerPresenter.setupPhotoAndText();
    }

    private void setupRecyclerView() {
        drawerPresenter.setupRecyclerView();
    }

    @Override
    public void update() {
        setupPhotoAndText();
    }
}

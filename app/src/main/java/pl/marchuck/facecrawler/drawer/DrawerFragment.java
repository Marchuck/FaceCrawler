package pl.marchuck.facecrawler.drawer;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import pl.marchuck.facecrawler.R;
import pl.marchuck.facecrawler.argh.FaceActivity;
import pl.marchuck.facecrawler.ifaces.Updatable;
import pl.marchuck.facecrawler.thirdPartyApis.Face4Java.Face4Java;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class DrawerFragment extends Fragment implements Updatable {
    public static final String TAG = DrawerFragment.class.getSimpleName();
    public DrawerPresenter drawerPresenter;

    @Bind(R.id.recycler_view)
    public RecyclerView recyclerView;

    @Bind(R.id.text)
    public TextView text;

    @Bind(R.id.image)
    public ImageView image;



    @Bind(R.id.login)
    public TextView loginTextView;

    @OnLongClick(R.id.login)
    public boolean onLongClick() {
        Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                new Face4Java().init();
            }
        }).subscribeOn(Schedulers.io()).subscribe(new Subscriber<Boolean>() {
            @Override
            public void onCompleted() {
                Log.i(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onNext(Boolean aBoolean) {
                Log.d(TAG, "onNext: ");
            }
        });
        return false;
    }

    @OnClick(R.id.login)
    public void log() {
        boolean loggedIn = activity().isUserLogged();
        if (loggedIn)
            activity().logout();
        else
            activity().login();
        loginTextView.setText(!loggedIn ? "login" : "logout");
    }

    public FaceActivity activity() {
        return (FaceActivity) getActivity();
    }

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
        drawerPresenter.setupPhotoAndMessage();
    }

    private void setupRecyclerView() {
        drawerPresenter.setupRecyclerView();
    }

    @Override
    public void update() {
        setupPhotoAndText();
        if (activity().isUserLogged()) loginTextView.setText("log out");
        else loginTextView.setText("login");
    }
}

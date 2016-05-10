package pl.marchuck.facecrawler;


import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.GraphResponse;

import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.marchuck.facecrawler.thirdPartyApis.common.GenericFacebookPoster;
import pl.marchuck.facecrawler.thirdPartyApis.common.GraphAPI;
import rx.functions.Action1;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LikeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LikeFragment extends Fragment {
    public static final String TAG = LikeFragment.class.getSimpleName();
    private LikeFragment This = this;

    boolean liked = false;
    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Bind(R.id.loginStatus)
    TextView loginStatus;

    @Bind(R.id.progress)
    ProgressBar progressBar;

    @OnClick(R.id.fab)
    public void onFabClick() {
        showProgressBar();
        if (!liked)
            GraphAPI.like("175256249539930_176382476093974").subscribe(new Action1<GraphResponse>() {
                @Override
                public void call(GraphResponse graphResponse) {
                    Log.d(TAG, "call: " + graphResponse.toString());
                    liked = true;
                    changeBtnInUi(liked);
                }
            });
        else
            GraphAPI.dislike("175256249539930_176382476093974").subscribe(new Action1<GraphResponse>() {
                @Override
                public void call(GraphResponse graphResponse) {
                    Log.d(TAG, "call: " + graphResponse.toString());
                    liked = false;
                    changeBtnInUi(liked);
                }
            });
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void changeBtnInUi(final boolean liked) {
        final String _loginStatus = "post " + (!liked ? "dis" : "") + "liked";
        Log.i(TAG, _loginStatus);
        final int colorA = getResources().getColor(R.color.colorAccent);
        final int colorB = getResources().getColor(R.color.colorPrimary);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideProgressBar();
                Log.i(TAG, "run: ");
                if (liked) fab.setBackgroundTintList(ColorStateList.valueOf(colorA));
                else fab.setBackgroundTintList(ColorStateList.valueOf(colorB));
                loginStatus.setText(_loginStatus);
                List<View> views = new LinkedList<>();
                views.add(loginStatus);
                views.add(fab);
                for (View v : views) {
                    v.setAlpha(0f);
                    v.animate().alpha(1f).setDuration(300).start();
                }
            }
        });
    }

    public LikeFragment() {
        // Required empty public constructor
    }

    public static LikeFragment newInstance() {
        LikeFragment fragment = new LikeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_like, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}

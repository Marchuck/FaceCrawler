package pl.marchuck.facecrawler;


import android.content.res.ColorStateList;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.GraphResponse;

import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.marchuck.facecrawler.argh.FaceActivity;
import pl.marchuck.facecrawler.thirdPartyApis.common.GraphAPI;
import pl.marchuck.facecrawler.utils.TagAdapter;
import rx.functions.Action1;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TagsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TagsFragment extends Fragment {
    public static final String TAG = TagsFragment.class.getSimpleName();

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;


    public TagsFragment() {
        // Required empty public constructor
    }

    public static TagsFragment newInstance() {
        TagsFragment fragment = new TagsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tags, container, false);
        ButterKnife.bind(this, view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        TagAdapter adapter = TagAdapter.getInstance();
        adapter.currentActivity = getActivity();
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onStop() {
      //  ((FaceActivity) (getActivity())).showFab();
        super.onStop();
    }
}

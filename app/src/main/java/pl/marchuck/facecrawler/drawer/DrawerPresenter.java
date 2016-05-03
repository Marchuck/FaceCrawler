package pl.marchuck.facecrawler.drawer;

import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.facebook.Profile;
import com.squareup.picasso.Picasso;

import pl.marchuck.facecrawler.App;
import pl.marchuck.facecrawler.FaceAdapter;
import pl.marchuck.facecrawler.MainActivity;
import pl.marchuck.facecrawler.argh.RetroFacebookActivity;
import pl.marchuck.facecrawler.utils.FBTarget;

/**
 * @author Lukasz Marczak
 * @since 03.05.16.
 */
public class DrawerPresenter {
    public static final String TAG = DrawerPresenter.class.getSimpleName();
    DrawerFragment drawerFragment;

    FaceAdapter adapter;
    FaceAdapter.Listener listener;

    public DrawerPresenter(DrawerFragment drawerFragment) {
        this.drawerFragment = drawerFragment;
    }

    public void setupRecyclerView() {

        listener = new FaceAdapter.Listener() {
            @Override
            public void onClicked(int j) {
                switchToFragment(j);
            }
        };
        adapter = new FaceAdapter(listener);
        drawerFragment.recyclerView.setLayoutManager(new LinearLayoutManager(drawerFragment.getActivity()));
        drawerFragment.recyclerView.setAdapter(adapter);
    }

    private void switchToFragment(int j) {
        RetroFacebookActivity activity = (RetroFacebookActivity) drawerFragment.getActivity();
        activity.switchFragment(j);
    }

    public void setupPhotoAndText() {
        String _id = App.instance.currentUserId;
        Log.d(TAG, "setupPhotoAndText: " + _id + "," + (drawerFragment.image != null));

        Profile.fetchProfileForCurrentAccessToken();//activity.currentProfile;
        Profile profile = Profile.getCurrentProfile();
        Picasso.with(drawerFragment.getActivity())
                .load("https://graph.facebook.com/" + _id + "/picture?type=large")
                .into(new FBTarget(drawerFragment.image));
        drawerFragment.text.setTextColor(Color.WHITE);
        if (profile != null)
            drawerFragment.text.setText("Hello, " + profile.getFirstName());
    }


}

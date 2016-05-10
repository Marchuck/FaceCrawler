package pl.marchuck.facecrawler.drawer;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.facebook.Profile;
import com.squareup.picasso.Picasso;

import pl.marchuck.facecrawler.App;
import pl.marchuck.facecrawler.FaceAdapter;
import pl.marchuck.facecrawler.argh.FaceActivity;
import pl.marchuck.facecrawler.utils.VerboseTarget;

/**
 * @author Lukasz Marczak
 * @since 03.05.16.
 */
public class DrawerPresenter {
    public static final String TAG = DrawerPresenter.class.getSimpleName();
    DrawerFragment drawerFragment;
    FaceAdapter adapter;

    public DrawerPresenter(DrawerFragment drawerFragment) {
        this.drawerFragment = drawerFragment;
    }

    public void setupRecyclerView() {
        adapter = new FaceAdapter(new FaceAdapter.Listener() {
            @Override
            public void onClicked(int j) {
                switchToFragment(j);
            }
        });
        RecyclerView.LayoutManager lm = new LinearLayoutManager(drawerFragment.getActivity());
        drawerFragment.recyclerView.setLayoutManager(lm);
        drawerFragment.recyclerView.setAdapter(adapter);
    }

    private void switchToFragment(int j) {
        FaceActivity activity = (FaceActivity) drawerFragment.getActivity();
        activity.switchFragment(j);
    }

    public void setupPhotoAndMessage() {
        String _id = App.instance.currentUserId;
        Log.d(TAG, "setupPhotoAndMessage: " + _id + "," + (drawerFragment.image != null));
        //get current profile
        Profile.fetchProfileForCurrentAccessToken();//activity.currentProfile;
        Profile profile = Profile.getCurrentProfile();
        //loading picture
        Picasso.with(drawerFragment.getActivity())
                .load("https://graph.facebook.com/" + _id + "/picture?type=large")
                .into(new VerboseTarget(drawerFragment.image));
        //set text for textView
        if (profile != null) {
            App.instance.userName = profile.getFirstName();
            String greet = "Hello, " + profile.getFirstName() + " " + profile.getLastName() + "!";
            drawerFragment.text.setText(greet);
        }
    }
}

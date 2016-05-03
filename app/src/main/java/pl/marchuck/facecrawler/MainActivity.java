package pl.marchuck.facecrawler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.marchuck.facecrawler.drawer.DrawerFragment;
import pl.marchuck.facecrawler.ifaces.Facebookable;

public class MainActivity extends AppCompatActivity implements Facebookable {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int SELECT_PHOTO = 203;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    public FacebookFlow facebookFlow;
    public Profile currentProfile;
    DrawerFragment drawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFacebook();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setupDrawer();
        setupDrawerLayout();
        setupCenterLayout();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
        facebookFlow.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Call the 'deactivateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onPause methods of the primary Activities that an app may be
        // launched into.
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {

        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        facebookFlow.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        drawerFragment.drawerPresenter.setupPhotoAndText();
    }

    private void setupFacebook() {
        Log.d(TAG, "setupFacebook: ");
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        facebookFlow = new FacebookFlow(this);
    }

    private void setupCenterLayout() {
        replaceFragment(R.id.center_content, LoginFragment.newInstance());
    }

    private void setupDrawerLayout() {
        drawerFragment = DrawerFragment.newInstance();
        replaceFragment(R.id.left_content, drawerFragment);
    }

    private void replaceFragment(@IdRes int res, Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(res, fragment)
                .commitAllowingStateLoss();
    }

    public void switchFragment(int id) {
        switch (id) {
            case 0:
                Toast.makeText(MainActivity.this, "0", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(MainActivity.this, "1", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                facebookFlow.onClickPostPhoto();
                break;
            case 3:
                facebookFlow.onClickPostStatusUpdate();
                break;
            case 4:
                android.os.Process.killProcess(Process.myPid());
        }
    }

    private Fragment getFragmentForId(int id) {
        return LoginFragment.newInstance();
    }

    private void setupDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, android.R.string.ok,
                android.R.string.no) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Log.d(TAG, "onDrawerOpened " + drawerView.getId());
                drawerFragment.drawerPresenter.setupPhotoAndText();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerLayout.setDrawerElevation(15);
        drawerLayout.setDrawerListener(toggle); // drawerLayout Listener set to the drawerLayout toggle
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void update() {
        drawerFragment.update();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void postPhoto() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    @Override
    public void postStatusUpdate() {

    }
}

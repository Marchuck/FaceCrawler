package pl.marchuck.facecrawler.argh;

import android.content.Intent;
import android.os.*;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.marchuck.facecrawler.App;
import pl.marchuck.facecrawler.FacebookFlow;
import pl.marchuck.facecrawler.MainActivity;
import pl.marchuck.facecrawler.R;
import pl.marchuck.facecrawler.drawer.DrawerFragment;
import pl.marchuck.facecrawler.thirdPartyApis.common.Friend;
import pl.marchuck.facecrawler.thirdPartyApis.common.GenericFacebookPoster;
import pl.marchuck.facecrawler.utils.FBTarget;
import retrofacebook.Facebook;
import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

public class RetroFacebookActivity extends AppCompatActivity {
    public static final String TAG = RetroFacebookActivity.class.getSimpleName();
    private RetroFacebookActivity This = this;
    private Facebook facebook;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.progress)
    ProgressBar progress;


    @OnClick(R.id.fab)
    public void setupFab() {
        dealWithResult(null);
    }

    DrawerFragment drawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retro_facebook);
        ButterKnife.bind(this);
        hideProgressBar();
        setSupportActionBar(toolbar);
        setupFacebook();
        setupDrawerLayout();

        TokenChangeObservable.startObservable(new TokenChangeObservable.TokenChangeListener() {
            @Override
            public void onChange() {
                Log.d(TAG, "onChange: ");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: ");
                        setupImage();
                    }
                });
            }
        });
    }

    private void setupDrawerLayout() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout, android.R.string.ok, android.R.string.no) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Log.d(TAG, "onDrawerOpened " + drawerView.getId());
                setupImage();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerLayout.setDrawerElevation(15);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
        drawerFragment = DrawerFragment.newInstance();
        replaceFragment(R.id.left_content, drawerFragment);
    }

    private void replaceFragment(@IdRes int res, Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(res, fragment)
                .commitAllowingStateLoss();
    }

    private void setupFacebook() {

        facebook = Facebook.create(this);
        facebook.initialize(this);
        LoginManager.getInstance().logInWithReadPermissions(this,
                Arrays.asList("public_profile", "user_friends"));
        LoginManager.getInstance().logInWithPublishPermissions(
                this,
                Arrays.asList(FacebookFlow.PUBLISH_ACTIONS));
        facebook.logIn().subscribe(new Subscriber<LoginResult>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "onCompleted: ");
                dealWithResult(result);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onNext(final LoginResult loginResult) {
                result = loginResult;
            }
        });
    }

    LoginResult result;

    private void dealWithResult(@Nullable LoginResult loginResult) {
        if (loginResult != null) {
            AccessToken token = loginResult.getAccessToken();
            if (token != null) {
                AccessToken.setCurrentAccessToken(token);
                App.instance.currentUserId = token.getUserId();
                App.instance.currentToken = token.getToken();
                Log.d(TAG, "access token: " + token);
            } else {
                Log.e(TAG, "nullable access token");
            }
        } else {
            AccessToken token = AccessToken.getCurrentAccessToken();
            if (token != null) {
                App.instance.currentUserId = AccessToken.getCurrentAccessToken().getUserId();
                App.instance.currentToken = AccessToken.getCurrentAccessToken().getToken();
            }
        }
        setupImage();
    }

    public void switchFragment(int id) {
        switch (id) {
            case 0:

                Toast.makeText(This, "0", Toast.LENGTH_SHORT).show();
                GenericFacebookPoster.getMyWall().subscribe(new Subscriber<GraphResponse>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onNext(GraphResponse graphResponse) {
                        Log.d(TAG, "onNext: " + graphResponse.toString());
                    }
                });
                break;
            case 1:
                Toast.makeText(This, "token is: " + App.instance.currentToken, Toast.LENGTH_SHORT).show();
                Observable.from(GenericFacebookPoster.getFriends())
                        .flatMap(new Func1<Friend, Observable<GraphResponse>>() {
                            @Override
                            public Observable<GraphResponse> call(Friend friend) {
                                return GenericFacebookPoster.postOnWall(friend.id, "Hello, " + friend.name + "!");
                            }
                        }).subscribe(new Subscriber<GraphResponse>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(GraphResponse res) {
                        Log.d(TAG, "onNext: " + res.toString());
                    }
                });
                break;
            case 2:
                //facebookFlow.onClickPostPhoto();
                break;
            case 3:
                //facebookFlow.onClickPostStatusUpdate();
                break;
            case 4:
                //facebookFlow.onClickPostStatusUpdate();
                break;
            case 5:
                android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    private void setupImage() {
        if (image == null) return;
        Log.d(TAG, "setupImage: " + App.instance.currentUserId);
        Picasso.with(this)
                .load("https://graph.facebook.com/" + App.instance.currentUserId + "/picture?type=large")
                .into(new FBTarget(image));
        drawerFragment.drawerPresenter.setupPhotoAndText();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebook.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult() called with: " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
//        if (App.instance.currentUserId == null) {
//            showProgressBar();
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    hideProgressBar();
//                    setupImage();
//                }
//            }, 10000);
//        }
    }

    private void showProgressBar() {
        progress.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progress.setVisibility(View.GONE);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d(TAG, "onPostResume: ");
        setupImage();

    }
}

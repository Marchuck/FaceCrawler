package pl.marchuck.facecrawler.argh;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.squareup.picasso.Picasso;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.marchuck.facecrawler.App;
import pl.marchuck.facecrawler.LikeFragment;
import pl.marchuck.facecrawler.R;
import pl.marchuck.facecrawler.TagsFragment;
import pl.marchuck.facecrawler.drawer.DrawerFragment;
import pl.marchuck.facecrawler.thirdPartyApis.ResearchgateApi;
import pl.marchuck.facecrawler.thirdPartyApis.common.Friend;
import pl.marchuck.facecrawler.thirdPartyApis.common.GenericFacebookPoster;
import pl.marchuck.facecrawler.thirdPartyApis.common.GraphAPI;
import pl.marchuck.facecrawler.utils.VerboseTarget;
import retrofacebook.Facebook;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class FaceActivity extends AppCompatActivity {
    public static final String TAG = FaceActivity.class.getSimpleName();
    private FaceActivity This = this;
    private Facebook facebook;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.image)
    ImageView image;

    @Bind(R.id.progress)
    ProgressBar progress;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    @OnClick(R.id.fab)
    public void setupFab() {
        dealWithResult(null);
    }

    DrawerFragment drawerFragment;
    boolean loggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retro_facebook);
        ButterKnife.bind(this);
        hideProgressBar();
        setSupportActionBar(toolbar);
        setupFacebook();
        setupDrawerLayout();
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
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.right_content, TagsFragment.newInstance())
                .commitAllowingStateLoss();
    }

    private void replaceFragment(@IdRes int res, Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(res, fragment)
                .commitAllowingStateLoss();
        drawerLayout.closeDrawers();
    }

    private void setupFacebook() {

        facebook = Facebook.create(this);
        facebook.initialize(this);
        LoginManager.getInstance().logInWithReadPermissions(this,
                Arrays.asList("public_profile", "user_friends"));
//        LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList(FacebookFlow.PUBLISH_ACTIONS));
        login();
    }


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
                    public void onNext(final GraphResponse graphResponse) {
                        Log.d(TAG, "onNext: " + graphResponse.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FaceActivity.this, graphResponse.getRawResponse(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                break;
            case 1:
                // Toast.makeText(This, "token is: " + App.instance.currentToken, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(FaceActivity.this, "Done", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case 2:
                GraphAPI.likeFirstPost();
                break;
            case 3:
                replaceFragment(R.id.center_content, LikeFragment.newInstance());
                break;
            case 4:
                getPhotos();
                break;
            case 5:
                GenericFacebookPoster.concatPost(ResearchgateApi.getAbstract(GenericFacebookPoster.getSubject())).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<GraphResponse>() {
                            @Override
                            public void onCompleted() {
                                Log.i(TAG, "onCompleted: ");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage());

                            }

                            @Override
                            public void onNext(GraphResponse response) {
                                Log.e(TAG, "onNext: " + response);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(FaceActivity.this, "Posted new abstract", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        });

                break;
            case 6:
                GraphAPI.postStarWars(new Action1<GraphResponse>() {
                    @Override
                    public void call(GraphResponse response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FaceActivity.this, "Posted star wars character", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                break;
            case 7:
                openRightDrawer(); break;
            default:
                android.os.Process.killProcess(android.os.Process.myPid());

        }
    }

    private void openRightDrawer() {
        Log.d(TAG, "openRightDrawer: ");
        drawerLayout.closeDrawers();
        drawerLayout.openDrawer(Gravity.RIGHT);
    }

    private void postNews() {
        Toast.makeText(FaceActivity.this, "posting...", Toast.LENGTH_SHORT).show();
        GraphAPI.postNews().flatMap(new Func1<String, Observable<GraphResponse>>() {
            @Override
            public Observable<GraphResponse> call(String s) {
                return GraphAPI.postMessage(s);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GraphResponse>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(final GraphResponse response) {
                        Log.i(TAG, "onNext: " + response.toString());
                    }
                });
    }

    private void setupImage() {
        if (image == null) return;
        Log.d(TAG, "setupImage: " + App.instance.currentUserId);
        Picasso.with(this)
                .load("https://graph.facebook.com/" + App.instance.currentUserId + "/picture?type=large")
                .into(new VerboseTarget(image));
        drawerFragment.drawerPresenter.setupPhotoAndMessage();
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

    public void logout() {
        Facebook.logOut();
        loggedIn = false;
        drawerFragment.update();
    }

    public void login() {
        facebook.logIn().subscribe(new Subscriber<LoginResult>() {
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
            public void onNext(final LoginResult loginResult) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dealWithResult(loginResult);
                        loggedIn = true;
                        drawerFragment.update();
                    }
                });
            }
        });
    }

    public boolean isUserLogged() {
        return loggedIn;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: " + getToken());
        if (Settings.actionsEnabled)
            GraphAPI.postMessageSilently(App.instance.userName + " stand on");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: " + getToken());
        if (Settings.actionsEnabled)
            GraphAPI.postMessageSilently(App.instance.userName + " sit down");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: " + getToken());
        if (Settings.actionsEnabled)
            GraphAPI.postMessageSilently(App.instance.userName + " is sleeping");
    }

    public void getPhotos() {
        Log.d(TAG, "getPhotos: ");
        GraphAPI.getPhotoLinks().observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<String>>() {
            @Override
            public void call(List<String> items) {
                Intent intent = new Intent(FaceActivity.this, PhotosActivity.class);
                String[] itt = new String[items.size()];
                for (int j = 0; j < items.size(); j++) {
                    itt[j] = items.get(j);
                }
                intent.putExtra("DATA", itt);
                startActivity(intent);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e(TAG, "call: " + throwable.getMessage());
                throwable.printStackTrace();
            }
        });
    }

    public void testuj() {
        Log.i(TAG, "testuj: ");
        GraphAPI.getJsoupDocument("https://www.facebook.com/adam.twarzowy")
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Document>() {
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
                    public void onNext(Document document) {
                        Log.d(TAG, "onNext: " + document.title());
                        Elements all = document.getAllElements();
                        GraphAPI.printElements(TAG, all);
                        Log.i(TAG, "onNext: done");
                    }
                });
    }

    public String getToken() {
        return AccessToken.getCurrentAccessToken() != null ?
                AccessToken.getCurrentAccessToken().getToken() : "nullable token ;(";
    }

    public void showFab() {
        fab.setVisibility(View.VISIBLE);
    }

    public void hideFab() {
        fab.setVisibility(View.GONE);
    }
}

package pl.marchuck.facecrawler;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.squareup.picasso.Picasso;

public class FActivity extends AppCompatActivity {
    public static final String TAG = FActivity.class.getSimpleName();
ImageView fbImageView;
private FActivity This=this;
    //ProfilePictureView profilePictureView;
    LoginButton loginButton;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext(), new FacebookSdk.InitializeCallback() {
            @Override
            public void onInitialized() {
                Log.d(TAG, "onInitialized: ");
                AccessToken token = AccessToken.getCurrentAccessToken();
                if (token == null) Log.e(TAG, "onInitialized: token is null");
                else Log.d(TAG, "onInitialized: token is " + token.getToken());
            }
        });
        setContentView(R.layout.activity_f);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fbImageView = (ImageView) findViewById(R.id.image);
        loginButton = (LoginButton) findViewById(R.id.loginButton);

        //loginButton.setReadPermissions("user_friends");
        loginButton.setReadPermissions("public_profile");
        callbackManager = CallbackManager.Factory.create();

        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.d(TAG, "onSuccess: " + loginResult.getAccessToken());
                        if (loginResult.getAccessToken() != null) {
                            String _id = loginResult.getAccessToken().getUserId();
                            Log.d(TAG, "onSuccess: " + _id);

                            Picasso.with(This)
                                    .load("https://graph.facebook.com/" + _id+ "/picture?type=large")
                                    .into(fbImageView);
                        } else {
                            Log.e(TAG, "onSuccess: access token is null! ");
                        }
                        Log.d(TAG, "onSuccess: ");
                        AccessToken.setCurrentAccessToken(loginResult.getAccessToken());
                        Profile currentProfile = Profile.getCurrentProfile();
                        if (currentProfile != null) {
                            String id = currentProfile.getId();
                        } else {
                            Log.e(TAG, "onSuccess: null profile");
                        }
                    }
                    @Override
                    public void onCancel() {
                        Log.d(TAG, "onCancel");
                    }
                    @Override
                    public void onError(FacebookException exception) {
                        Log.d(TAG, "onError " + exception.toString());
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

}

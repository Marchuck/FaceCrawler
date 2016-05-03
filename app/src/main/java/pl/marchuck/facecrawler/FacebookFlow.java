package pl.marchuck.facecrawler;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import pl.marchuck.facecrawler.ifaces.Facebookable;

/**
 * @author Lukasz Marczak
 * @since 03.05.16.
 */
public class FacebookFlow {
    private FacebookFlow This = this;
    public static final String TAG = FacebookFlow.class.getSimpleName();
    PendingAction pendingAction = PendingAction.NONE;
    private CallbackManager callbackManager;
    private Facebookable facebookable;
    private boolean canPresentShareDialogWithPhotos;
    private boolean canPresentShareDialog;
    private FacebookCallback<Sharer.Result> shareCallback;
    private ShareDialog shareDialog;
    private final int RESULT_OK = -1;
    public LoginButton loginButton;
    private FacebookCallback<LoginResult> loginCallback;
    public String _id;

    public boolean canBeEnabled() {

        boolean bool = AccessToken.getCurrentAccessToken() != null;
        Log.d(TAG, "canBeEnabled: " + bool);
        return bool;
    }

    public boolean canPostStatusUpdate() {
        return canBeEnabled() || canPresentShareDialog;
    }

    public boolean canPostPhoto() {
        return canBeEnabled() || canPresentShareDialogWithPhotos;
    }

    private void onCancel() {
        Log.d("HelloFacebook", "Canceled");
        Toast.makeText(facebookable.getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
    }

    private void onError(FacebookException error) {
        Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
        String title = "Error connecting to facebook ;(";
        String alertMessage = error.getMessage();
        showResult(title, alertMessage);
    }

    private void initShareCallback() {
        shareCallback = new FacebookCallback<Sharer.Result>() {
            @Override
            public void onCancel() {
                This.onCancel();
            }

            @Override
            public void onError(FacebookException error) {
                This.onError(error);
            }

            @Override
            public void onSuccess(Sharer.Result result) {
                This.onSuccess(result);
            }
        };
    }

    private void onSuccess(Sharer.Result result) {
        Log.d("HelloFacebook", "Success!");
        if (result.getPostId() != null) {
            String title = "Result success";
            String id = result.getPostId();
            String alertMessage = "Successfully posted " + id;
            showResult(title, alertMessage);
        }
    }

    private void showResult(String title, String alertMessage) {
        new android.app.AlertDialog.Builder(facebookable.getActivity())
                .setTitle(title)
                .setMessage(alertMessage)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    ProfileTracker profileTracker;

    public FacebookFlow(final Facebookable facebookable) {
        this.facebookable = facebookable;
        initShareCallback();
        initLoginCallback();
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                facebookable.update();
                // It's possible that we were waiting for Profile to be populated in order to
                // post a status update.
                handlePendingAction();
            }
        };
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(facebookable.getActivity());
        shareDialog.registerCallback(
                callbackManager,
                shareCallback);
        canPresentShareDialog = ShareDialog.canShow(
                ShareLinkContent.class);

        // Can we present the share dialog for photos?
        canPresentShareDialogWithPhotos = ShareDialog.canShow(
                SharePhotoContent.class);
        // LoginManager.getInstance().registerCallback(callbackManager, loginCallback);
    }

    private void initLoginCallback() {
        loginCallback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "onSuccess: " + loginResult.toString());
                AccessToken accessToken = loginResult.getAccessToken();
                AccessToken.setCurrentAccessToken(accessToken);
                if (accessToken != null) {
                    _id = accessToken.getUserId();
                    Log.i(TAG, "token expires in " + accessToken.getExpires());
                    Log.i(TAG, "token : " + accessToken.getToken());
                }

                handlePendingAction();
                facebookable.update();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel: ");
                if (pendingAction != PendingAction.NONE) {
                    showAlert();
                    pendingAction = PendingAction.NONE;
                }
                facebookable.update();
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, "onError: " + exception.getMessage());
                if (pendingAction != PendingAction.NONE && exception instanceof FacebookAuthorizationException) {
                    showAlert();
                    pendingAction = PendingAction.NONE;
                }
                facebookable.update();
            }

            private void showAlert() {
                new AlertDialog.Builder(facebookable.getActivity())
                        .setTitle("Cancelled")
                        .setMessage("Permission not granted")
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }
        };
    }

    public void handlePendingAction() {
        Log.i(TAG, "handlePendingAction: ");

        PendingAction previouslyPendingAction = pendingAction;
        // These actions may re-set pendingAction if they are still pending, but we assume they
        // will succeed.
        pendingAction = PendingAction.NONE;

        switch (previouslyPendingAction) {
            case NONE:
                break;
            case POST_PHOTO:
                facebookable.postPhoto();
                break;
            case POST_STATUS_UPDATE:
                postStatusUpdate();
                break;
        }
    }

    private void postStatusUpdate() {
        Profile profile = Profile.getCurrentProfile();
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle("title " + System.currentTimeMillis())
                .setContentDescription(
                        "message no " + UUID.randomUUID().toString())
//                .setContentUrl(Uri.parse("http://developers.facebook.com/docs/android"))
                .setContentUrl(Uri.parse("http://127.0.0.1:1337"))
                .build();


        if (canPresentShareDialog) {
            Log.i(TAG, "canPresentShareDialog: ");
            shareDialog.show(linkContent);
        } else if (profile != null && hasPublishPermission()) {
            Log.i(TAG, "profile is not null and can publish");
            ShareApi.share(linkContent, shareCallback);
        } else {
            Log.i(TAG, "pending action post status update");
            pendingAction = PendingAction.POST_STATUS_UPDATE;
        }
    }

    public void onClickPostPhoto() {
        performPublish(PendingAction.POST_PHOTO, canPresentShareDialogWithPhotos);
    }

    public void onClickPostStatusUpdate() {
        performPublish(PendingAction.POST_STATUS_UPDATE, canPresentShareDialog);
    }

    private void performPublish(PendingAction action, boolean allowNoToken) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            pendingAction = action;
            if (hasPublishPermission()) {
                // We can do the action right away.
                handlePendingAction();
                return;
            } else {
                // We need to get new permissions, then complete the action when we get called back.
                LoginManager.getInstance().logInWithPublishPermissions(
                        this.facebookable.getActivity(),
                        Arrays.asList(PERMISSION));
                return;
            }
        }

        if (allowNoToken) {
            pendingAction = action;
            handlePendingAction();
        }
    }

    private boolean hasPublishPermission() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && accessToken.getPermissions().contains(PERMISSION);
    }

    private static final String PERMISSION = "publish_actions";

    public void login(Fragment fragment) {

        LoginManager.getInstance()
                .logInWithReadPermissions(fragment, Arrays.asList("public_profile", "user_friends"));

//        logInWithPublishPermissions(fragment,
        //              Arrays.asList(PERMISSION));
//Arrays.asList("public_profile", "user_friends",);
    }

    private void postPhotoResult(Bitmap image) {
        Log.d(TAG, "postPhotoResult()");
        SharePhoto sharePhoto = new SharePhoto.Builder().setBitmap(image).build();
        ArrayList<SharePhoto> photos = new ArrayList<>();
        photos.add(sharePhoto);

        SharePhotoContent sharePhotoContent =
                new SharePhotoContent.Builder().setPhotos(photos).build();
        if (canPresentShareDialogWithPhotos) {
            shareDialog.show(sharePhotoContent);
        } else if (hasPublishPermission()) {
            ShareApi.share(sharePhotoContent, shareCallback);
        } else {
            pendingAction = PendingAction.POST_PHOTO;
        }
    }

    public void onDestroy() {
        profileTracker.stopTracking();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        callbackManager.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + ")");

        Bitmap yourSelectedImage = null;
        switch (requestCode) {
            case MainActivity.SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri selectedImage = imageReturnedIntent.getData();
                        InputStream imageStream = facebookable.getActivity()
                                .getContentResolver().openInputStream(selectedImage);
                        yourSelectedImage = BitmapFactory.decodeStream(imageStream);

                    } catch (Exception ex) {
                        Log.e(TAG, "error during decoding image");
                    }
                    if (yourSelectedImage != null) {
                        postPhotoResult(yourSelectedImage);

                    }
                }
        }
    }

    public void setLoginBtn(LoginButton loginButton) {
        this.loginButton = loginButton;
        this.loginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends"));
        this.loginButton.registerCallback(callbackManager, loginCallback);
    }

    private void onSuccessLogin(AccessToken accessToken) {
        Log.d(TAG, "onSuccessLogin: " + accessToken.getToken());
        AccessToken.setCurrentAccessToken(accessToken);
        Toast.makeText(FacebookFlow.this.facebookable.getActivity(), "success", Toast.LENGTH_SHORT).show();
        facebookable.update();
    }
}

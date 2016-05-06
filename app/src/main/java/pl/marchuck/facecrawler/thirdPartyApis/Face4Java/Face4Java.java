package pl.marchuck.facecrawler.thirdPartyApis.Face4Java;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Post;
import facebook4j.ResponseList;
import facebook4j.auth.AccessToken;
import facebook4j.conf.ConfigurationBuilder;

/**
 * @author Lukasz Marczak
 * @since 06.05.16.
 */
public class Face4Java {
    public static final String TAG = Face4Java.class.getSimpleName();
    public Facebook facebook;

    public Face4Java() {
    }

    public void init() {

        String oldToken = com.facebook.AccessToken.getCurrentAccessToken().getToken();

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthAppId("522302374637151")
                .setOAuthAppSecret("f1fb1a8adb6e87e5df07e6d572b6f28e")
                .setOAuthAccessToken(oldToken)
                .setOAuthPermissions("public_profile,user_friends,publish_actions");
        FacebookFactory ff = new FacebookFactory(cb.build());
        facebook = ff.getInstance();

        AccessToken extendedToken = null;
//        try {
//            extendedToken = facebook.extendTokenExpiration(oldToken);
//        } catch (FacebookException e) {
//            e.printStackTrace();
//            Log.e(TAG, "extending access token failed");
//        }
//        if (extendedToken != null) {
//            App.instance.currentToken = extendedToken.getToken();
//            Log.i(TAG, "verylong token exists! \n" + App.instance.currentToken);
//        }
        try {
            ResponseList<Post> p = facebook.getPosts();
        } catch (FacebookException e) {
            e.printStackTrace();
        }
    }
}

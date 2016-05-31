package pl.marchuck.facecrawler.thirdPartyApis.common;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.marchuck.facecrawler.App;
import pl.marchuck.facecrawler.utils.TagAdapter;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * @author Lukasz Marczak
 * @since 18.04.2016.
 */
public class GenericFacebookPoster {
    public static final String TAG = GenericFacebookPoster.class.getSimpleName();

    public static <T> Observable<GraphResponse> concatPost(@NonNull Observable<T> obso) {
        Log.i(TAG, "concatPost: ");

        return obso.flatMap(new Func1<T, Observable<GraphResponse>>() {
            @Override
            public Observable<GraphResponse> call(T t) {
                if (t == null) Log.e(TAG, "concat post nullable T");
                return GraphAPI.postMessage((t != null) ? t.toString() : "Have a nice day");
            }
        });
    }

    public static Observable<GraphResponse> searchUserOnFacebook(final String username) {
        return Observable.create(new Observable.OnSubscribe<GraphResponse>() {
            @Override
            public void call(final Subscriber<? super GraphResponse> subscriber) {
//                search?q=Lukasz Marczak&type=user
                Bundle bundle = new Bundle();
                bundle.putString("q", username);
                bundle.putString("type", "user");
                new GraphRequest(AccessToken.getCurrentAccessToken(),
                        "/search",
                        bundle,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                Log.d(TAG, "GraphResponse: " + response.toString());
                                subscriber.onNext(response);
                            }
                        }).executeAsync();
            }
        });
    }

    public static Observable<GraphResponse> getMyWall() {
        return Observable.create(new Observable.OnSubscribe<GraphResponse>() {
            @Override
            public void call(final Subscriber<? super GraphResponse> subscriber) {
                Bundle bundle = new Bundle();
                new GraphRequest(AccessToken.getCurrentAccessToken(),
                        "/me/feed",
                        bundle,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                Log.d(TAG, "onCompleted: " + response.toString());
                                subscriber.onNext(response);
                            }
                        }).executeAsync();
            }
        });
    }

    public static Observable<GraphResponse> postOnWall(String userId, final String message) {
        final String fixedUserId = userId == null ? "me" : userId;
        return Observable.create(new Observable.OnSubscribe<GraphResponse>() {
            @Override
            public void call(final Subscriber<? super GraphResponse> subscriber) {
                Bundle bundle = new Bundle();
                bundle.putString("message", message);
                bundle.putString("access_token", App.instance.longLivingAccessToken);
                new GraphRequest(AccessToken.getCurrentAccessToken(),
                        "/" + fixedUserId + "/feed",
                        bundle,
                        HttpMethod.POST,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                Log.d(TAG, "onCompleted: " + response.toString());
                                subscriber.onNext(response);
                            }
                        }).executeAsync();
            }
        });
    }

    public static List<Friend> getFriends() {
        List<Friend> friends = new ArrayList<>();
        friends.add(new Friend(null, "Adam"));
        friends.add(new Friend(IgorCzajkowskiID, "Igor"));
        friends.add(new Friend(LukaszMarczakID, "Lukasz"));
        friends.add(new Friend(PiotrPawelID, "Paweł"));
        return friends;
    }

    public static String IgorCzajkowskiID = "100002310747350";
    public static String LukaszMarczakID = "100000800501048";
    public static String PiotrPawelID = "100011868858052";


    public static String getSubject() {
        List<String> subjects = TagAdapter.getInstance().dataset;
        return subjects.get(new Random().nextInt(subjects.size()));
    }
}

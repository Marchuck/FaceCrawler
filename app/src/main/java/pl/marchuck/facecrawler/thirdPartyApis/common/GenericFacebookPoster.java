package pl.marchuck.facecrawler.thirdPartyApis.common;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import java.util.ArrayList;
import java.util.List;

import pl.marchuck.facecrawler.App;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * @author Lukasz Marczak
 * @since 18.04.2016.
 */
public class GenericFacebookPoster {
    public static final String TAG = GenericFacebookPoster.class.getSimpleName();
    public static final String FRONT_MESSAGE = "New item ! \n";

    public static <T> Observable<GraphResponse> concatPost(@NonNull Observable<T> obso) {
        return obso.flatMap(new Func1<T, Observable<GraphResponse>>() {
            @Override
            public Observable<GraphResponse> call(T t) {
                return postMessage(FRONT_MESSAGE + t.toString());
            }
        });
    }

    public static Observable<GraphResponse> postMessage(final String message) {
        return Observable.create(new Observable.OnSubscribe<GraphResponse>() {
            @Override
            public void call(final Subscriber<? super GraphResponse> subscriber) {
                Bundle bundle = new Bundle();
                bundle.putString("message", message);
                bundle.putString("access_token", AccessToken.getCurrentAccessToken().getToken());
                new GraphRequest(AccessToken.getCurrentAccessToken(),
                        "/me/feed",
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
    public static Observable<GraphResponse> getMyWall(){
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
                bundle.putString("access_token", App.instance.currentToken);
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
}

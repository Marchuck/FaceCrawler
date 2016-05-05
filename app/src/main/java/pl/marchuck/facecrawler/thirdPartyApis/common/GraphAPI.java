package pl.marchuck.facecrawler.thirdPartyApis.common;

import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pl.marchuck.facecrawler.App;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * @author Lukasz Marczak
 * @since 05.05.16.
 */
public class GraphAPI {
    //    me/photos?type=uploaded
    public static final String TAG = GraphAPI.class.getSimpleName();

    public static Observable<GraphResponse> getPhotos() {
        return getPhotos(null);
    }

    public static List<String> getIds(GraphResponse response) {
        List<String> ids = new ArrayList<>();
        try {
            JSONObject jsonObject = response.getJSONObject();
            JSONArray array = jsonObject.getJSONArray("data");
            for (int j = 0; j < array.length(); j++) {
                JSONObject oo = array.getJSONObject(j);
                String id = oo.get("id").toString();
                ids.add(id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return ids;
        }
        return ids;
    }

    public static Observable<List<String>> getPhotoLinks() {
        return getPhotoLinks(null);
    }

    public static Observable<List<String>> getPhotoLinks(String userId) {
        return getPhotos(userId).flatMap(new Func1<GraphResponse, Observable<String>>() {
            @Override
            public Observable<String> call(GraphResponse graphResponse) {
                List<String> ids = getIds(graphResponse);
                return Observable.from(ids);
            }
        }).flatMap(new Func1<String, Observable<String>>() {
            @Override
            public Observable<String> call(String s) {
                return getPhotoLinkFromId(s);
            }
        }).toList();
    }

    public static Observable<GraphResponse> getPhotos(final String userId) {
        return Observable.create(new Observable.OnSubscribe<GraphResponse>() {
            @Override
            public void call(final Subscriber<? super GraphResponse> subscriber) {
                Bundle bundle = new Bundle();
                bundle.putString("type", "uploaded");

                final String fuserId = (userId == null) ? "me" : userId;
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/" + fuserId + "/photos",
                        bundle,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                subscriber.onNext(response);
                                subscriber.onCompleted();
                            }
                        }
                ).executeAsync();
            }
        });
    }

    public static Observable<String> getPhotoLinkFromId(final String id) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                Bundle bundle =new Bundle();
                bundle.putString("fields","link");
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/" + id,
                        bundle,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                try {
                                    Log.d(TAG, "onCompleted: " + response.toString());
                                    Log.d(TAG, "jsonObject: " + response.getJSONObject().toString());
                                    JSONObject oo = response.getJSONObject();
                                    String link = oo.get("link").toString();
                                    subscriber.onNext(link);
                                    subscriber.onCompleted();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    subscriber.onError(e);
                                }
                            }
                        }
                ).executeAsync();
            }
        });
    }

    public static Observable<GraphResponse> postMessage(final String message) {
        return Observable.create(new Observable.OnSubscribe<GraphResponse>() {
            @Override
            public void call(final Subscriber<? super GraphResponse> subscriber) {
                Bundle bundle = new Bundle();
                bundle.putString("message", message);
                bundle.putString("access_token", App.instance.graphAPIToken);
                new GraphRequest(AccessToken.getCurrentAccessToken(),
                        "/me/feed",
                        bundle,
                        HttpMethod.POST,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                subscriber.onNext(response);
                            }
                        }).executeAsync();
            }
        });
    }

    public static Observable<GraphResponse> like(String postId) {
        return postLikeAction(postId, true);
    }

    public static Observable<GraphResponse> dislike(String postId) {
        return postLikeAction(postId, false);
    }

    private static Observable<GraphResponse> postLikeAction(final String postId, final boolean isLike) {
        return Observable.create(new Observable.OnSubscribe<GraphResponse>() {
            @Override
            public void call(final Subscriber<? super GraphResponse> subscriber) {
                new GraphRequest(AccessToken.getCurrentAccessToken(),
                        "/" + postId + "/likes",
                        null,
                        isLike ? HttpMethod.POST : HttpMethod.DELETE
                        ,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                //    Log.d(TAG, "onCompleted: " + response.toString());
                                subscriber.onNext(response);
                            }
                        }).executeAsync();
            }
        });
    }
}

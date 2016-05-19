package pl.marchuck.facecrawler.thirdPartyApis.common;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.annotation.Documented;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.marchuck.facecrawler.App;
import pl.marchuck.facecrawler.argh.FaceActivity;
import pl.marchuck.facecrawler.argh.Settings;
import pl.marchuck.facecrawler.thirdPartyApis.pokemon.PokemonClient;
import pl.marchuck.facecrawler.thirdPartyApis.swapi.SwapiClient;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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
                Bundle bundle = new Bundle();
                bundle.putString("fields", "link");
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
        Log.i(TAG, "postMessage: " + message);
        return Observable.create(new Observable.OnSubscribe<GraphResponse>() {
            @Override
            public void call(final Subscriber<? super GraphResponse> subscriber) {
                Bundle bundle = new Bundle();
                bundle.putString("message", message);
                //   if (Settings.useMockToken)
//                bundle.putString("access_token", App.instance.longLivingAccessToken);
                bundle.putString("access_token", getToken());
                //   else
                //     bundle.putString("access_token", AccessToken.getCurrentAccessToken().getToken());
                new GraphRequest(AccessToken.getCurrentAccessToken(),
                        "/me/feed",
                        bundle,
                        HttpMethod.POST,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                Log.i(TAG, "onCompleted: " + response.toString());
                                subscriber.onNext(response);
                            }
                        }).executeAsync();
            }
        });
    }

    private static String getToken() {
        AccessToken thisToken = AccessToken.getCurrentAccessToken();
        return thisToken != null ? thisToken.getToken() : App.instance.currentToken;
    }

    public static Observable<GraphResponse> like(String postId) {
        return postLikeAction(postId, true);
    }

    public static String newsPage = "http://wiadomosci.onet.pl";

    public static rx.Observable<Document> getJsoupDocument(final String url) {
        return Observable.create(new Observable.OnSubscribe<Document>() {

            @Override
            public void call(Subscriber<? super Document> subscriber) {
                Log.i(TAG, "getting new document...");
                Document document = null;
                try {
                    document = Jsoup.connect(url).get();
                } catch (IOException ignored) {
                    Log.e("JsoupProxy", "getDocument: " + ignored.getMessage());
                }
                subscriber.onNext(document);
                subscriber.onCompleted();
            }
        });
    }

    public static void printElement(String TAG, Element e) {
        Log.d(TAG, "element: html: " + e.html() + ", text: " + e.text() + ", val: " + e.val() + ", data: "
                + e.data() + ", id: " + e.id() + ", nodeName: " + e.nodeName() + ", tag: " + e.tag()
                + ", tagName: " + e.tagName() + ", \n" + e.outerHtml());
    }

    public static void printElements(String TAG, Elements elements) {
        for (Element el : elements) printElement(TAG, el);
    }

    public static Observable<GraphResponse> dislike(String postId) {
        return postLikeAction(postId, false);
    }

    public static Observable<String> postNews() {
        Log.i(TAG, "postNews: ");
        return getJsoupDocument(newsPage).flatMap(new Func1<Document, Observable<String>>() {
            @Override
            public Observable<String> call(Document document) {
                Elements newses = document.getElementsByClass("datePublished");
                Element news0 = newses.get(0);
                Log.e(TAG, "returning: " + news0.text());
                return Observable.just(news0.text());
            }
        });
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

    public static void postPoke(Action1<GraphResponse> callback) {
        Log.d(TAG, "postPoke: ");
        int randomId = new Random().nextInt(151);
        randomId = randomId < 0 ? -randomId : randomId;

        GenericFacebookPoster.concatPost(PokemonClient.getPokemonById(1 + randomId)).subscribe(callback, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e(TAG, "call: " + throwable.getMessage());
                throwable.printStackTrace();
            }
        });
    }

    public static void postStarWars(Action1<GraphResponse> callback) {
        Log.d(TAG, "postStarWars: ");
        int randomId = new Random().nextInt(80);
        randomId = randomId < 0 ? -randomId : randomId;
        GenericFacebookPoster.concatPost(SwapiClient.getSwapiCharacterById(1 + randomId)).subscribe(callback, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e(TAG, "call: " + throwable.getMessage());
                throwable.printStackTrace();
            }
        });
    }

    public static void postMessageSilently(String message) {
        final String TAG = FaceActivity.TAG;
        postMessage(message).subscribe(new Action1<GraphResponse>() {
            @Override
            public void call(GraphResponse response) {
                Log.d(TAG, response.toString());
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e(TAG, "Error occurred: " + throwable.getMessage());
                throwable.printStackTrace();
            }
        });
    }

    public static void likeFirstPost() {
        Log.d(TAG, "likeFirstPost: ");
        GenericFacebookPoster.getMyWall().flatMap(new Func1<GraphResponse, Observable<GraphResponse>>() {
            @Override
            public Observable<GraphResponse> call(GraphResponse response) {
                String postId = "175256249539930_182398955492326";//not first
                try {
                    JSONObject obj = response.getJSONObject();
                    JSONArray array = (JSONArray) obj.get("data");
                    JSONObject oo = (JSONObject) array.get(0);
                    postId = oo.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return like(postId);
            }
        }).subscribe(new Action1<GraphResponse>() {
            @Override
            public void call(GraphResponse response) {
                Log.d(TAG, "DONE ");
                Log.d(TAG, response.toString());
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e(TAG, throwable.getLocalizedMessage());
                throwable.printStackTrace();
            }
        });
    }

    public static Observable<GraphResponse> commentFirstPost(final String message) {
        Log.d(TAG, "likeFirstPost: ");
       return GenericFacebookPoster.getMyWall().flatMap(new Func1<GraphResponse, Observable<GraphResponse>>() {
            @Override
            public Observable<GraphResponse> call(GraphResponse response) {
                String postId = "175256249539930_182398955492326";//not first
                try {
                    JSONObject obj = response.getJSONObject();
                    JSONArray array = (JSONArray) obj.get("data");
                    JSONObject oo = (JSONObject) array.get(0);
                    postId = oo.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return comment(postId, message);
            }
        });
    }

    private static Observable<GraphResponse> comment(final String postId, final String message) {
        return Observable.create(new Observable.OnSubscribe<GraphResponse>() {
            @Override
            public void call(final Subscriber<? super GraphResponse> subscriber) {
                Bundle bundle = new Bundle();
                bundle.putString("message", message);
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/" + postId + "/comments",
                        bundle,
                        HttpMethod.POST,
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
}

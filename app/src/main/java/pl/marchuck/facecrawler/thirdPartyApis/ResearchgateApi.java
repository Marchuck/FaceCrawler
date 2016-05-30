package pl.marchuck.facecrawler.thirdPartyApis;

import android.util.Log;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.marchuck.facecrawler.thirdPartyApis.swapi.Researcher;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * @author Lukasz Marczak
 * @since 18.05.16.
 */
public class ResearchgateApi {
    public static final String TAG = ResearchgateApi.class.getSimpleName();

    public interface ResearchGate {

        @GET("/search.SearchContent.html")
        void getAbstract(@Query("query") String query, @Query("type") String type, Callback<JsonElement> callback);
    }

    static String endpoint = "https://researchgate.net";
    public static Observable<ResearchGateResponse> completeResearchResponse(final String query){
        return getAbstract(query).flatMap(new Func1<ResearchGateResponse, Observable<ResearchGateResponse>>() {
            @Override
            public Observable<ResearchGateResponse> call(ResearchGateResponse researchGateResponse) {
                Log.d(TAG, "call: ");
                return ResearcherGetter.fillResearchersFBUrls(researchGateResponse);
            }
        });
    }

    public static Observable<ResearchGateResponse> getAbstract(final String query) {
        Log.d(TAG, "getAbstract: ");

        final RestAdapter retrofit = buildResearchGateRetrofit();

        return Observable.create(new Observable.OnSubscribe<ResearchGateResponse>() {
            @Override
            public void call(final Subscriber<? super ResearchGateResponse> subscriber) {
                retrofit.create(ResearchGate.class)
                        .getAbstract(query, "publication", new Callback<JsonElement>() {
                            @Override
                            public void success(JsonElement jsonElement, Response response) {
                                Log.d(TAG, "success: " + jsonElement.toString());
                                try {
                                    subscriber.onNext(parseResponse(jsonElement.toString()));
                                } catch (JSONException x) {
                                    Log.e(TAG, "json parsing excepion: " + x.getMessage());
                                    x.printStackTrace();
                                    subscriber.onNext(new ResearchGateResponse("Still looking for new abstracts"));

                                }
                                subscriber.onCompleted();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                subscriber.onError(error);
                            }
                        });
            }
        });
    }

    private static RestAdapter buildResearchGateRetrofit() {
        return new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("x-push-state", "1");
                        request.addHeader("Accept-Encoding: gzip, deflate", "br");
                        request.addHeader("X-Requested-With", "XMLHttpRequest");
                        request.addHeader("Host", "www.researchgate.net");
                        request.addHeader("Referer", "https://www.researchgate.net/search.Search.html?query=programming&type=publication");
                        request.addHeader("Accept", "application/json");
                        request.addHeader("Cookie", "_ga=GA1.2.1091146910.1463592871; __gads=ID=7edcd9066211dc1f:T=1463592872:S=ALNI_Ma0rkdvLDiBM6alEdvCpw8zGfNl6A; c1=6h%8D%E0%BA%9Du%FC%1B%D3f%82%D5%11%99%26%3A45%A5P%B0%27I%F1PTz%87%9F%D5UW%10%7C%05%88%0F%90.e%1C%1F%B5C%8B%87%CE%B7hn%AD%80%FC%BD%94i%7F%03%1C%88%C2%23%25; c2=%F4%19%B1%90%E1I%03%00%8Dd%8A%8FI%B5%29%C6%DF%18-%B8%DD%F7%837%A7%D1%0F%5D%DE%BE%09c-A%5D%F0X%D6%0C%81G%15%2C%164%BFv%98o%A3oO%C9%0F%7F%9C%81%7B%1CoT%C6%DB%A3fj%FC%A6%18%B0_%5D%9B%E7%F3%13%B0aD%BB%A5E0%FF%BE%DDu%D0%A2%23X%C1A%E9%9F%3F; cirgu=1; sid=XFcVuRMdfKA4qav0Al0DsoTLD10T6C6enI5o0rjjr6BFGtq00lj5ngiT8REg90oIDpaBPM8efQJOzhQKIdBTEv2bWFIV5ewjWOfZbkWb0Mp6o5t10jq2OoUqEpSHwekK; cili=_2_OTM4YTc1NmUyMTIzYTg5ZWVhYmY4YjFkODBjZTlmMjFhZDZkMzkzY2MwYTNiODAxNmM1YmQ4YWY4ZWY2YmJkNl8xMTEyMjU2Nzsw; _gat=1; _gat_UA-58591210-1=1");
                        request.addHeader("Accept-Language", "pl,en-US;q=0.7,en;q=0.3");
                        request.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.11; rv:46.0) Gecko/20100101 Firefox/46.0");
                    }
                }).build();
    }

    private static ResearchGateResponse parseResponse(String s) throws JSONException {
        List<Researcher> researchers = new ArrayList<>();
        JSONObject object = new JSONObject(s);
        JSONObject result = object.getJSONObject("result");
        JSONObject data0 = result.getJSONObject("data");
        JSONObject searchListWidget = data0.getJSONObject("searchListWidget");
        JSONObject data1 = searchListWidget.getJSONObject("data");
        JSONArray listItems = data1.getJSONArray("listItems");
        //   int index = new Random().nextInt(listItems.length());
        String _abstract = null;
        String url = null;
        List<Integer> indexes = new ArrayList<>();
        for (int j = 0; j < listItems.length(); j++) {
            indexes.add(j);
        }
        Collections.shuffle(indexes);
        for (Integer x : indexes) {
            JSONObject data3 = listItems.getJSONObject(x).getJSONObject("data");
            if (data3.has("abstract") && data3.has("publicationUrl") && data3.has("authors")) {
                /**get url of publication*/
                url = endpoint + "/" + data3.getString("publicationUrl");

                /**get content of publication abstract*/
                _abstract = data3.getString("abstract");

                /**get authors of this publication*/
                JSONArray authorsArray = data3.getJSONArray("authors");
                if (authorsArray.length() != 0) {

                    for (int k = 0; k < authorsArray.length(); k++) {
                        String researchGateUrl = endpoint + "/" + authorsArray.getJSONObject(k).getString("url");
                        String authorFullname = authorsArray.getJSONObject(k).getString("fullname");
                       // printedAuthors += authorFullname + ": " + researchGateUrl + "\n\n";
                        researchers.add(new Researcher(authorFullname, researchGateUrl));
                    }
                }
                break;
            }
        }
        if (_abstract == null) return new ResearchGateResponse();

        else {
            return new ResearchGateResponse(_abstract, researchers, url);
        }
    }
}

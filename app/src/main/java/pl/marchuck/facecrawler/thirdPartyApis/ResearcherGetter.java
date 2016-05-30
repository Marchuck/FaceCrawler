package pl.marchuck.facecrawler.thirdPartyApis;

import android.util.Log;

import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import pl.marchuck.facecrawler.thirdPartyApis.common.GenericFacebookPoster;
import pl.marchuck.facecrawler.thirdPartyApis.swapi.Researcher;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.functions.Func4;
import rx.functions.Func5;

/**
 * @author Lukasz Marczak
 * @since 30.05.16.
 */
public class ResearcherGetter {
    public static final String TAG = ResearcherGetter.class.getSimpleName();

    public static ResearchGateResponse fillUrl(ResearchGateResponse gateResponse, GraphResponse response, int index) {
        String id = "";
        try {
            JSONObject root = new JSONObject(response.getRawResponse());
            JSONArray data = root.getJSONArray("data");
            id = data.getJSONObject(0).getString("id");
        } catch (Exception x) {
            Log.e(TAG, "failed to extract facebook id.  " + x.getMessage());
            x.printStackTrace();
        }
        Researcher fixed = gateResponse.authors.get(index);
        fixed.facebookUrl = "https://www.facebook.com/" + id;
        gateResponse.authors.set(0, fixed);
        return gateResponse;
    }

    public static rx.Observable<ResearchGateResponse> fillResearchersFBUrls(final ResearchGateResponse researchGateResponse) {
        final List<Researcher> researchers = researchGateResponse.authors;
        if (researchers == null || researchers.size() == 0) return Observable.empty();
        Log.d(TAG, "fillResearchersFBUrls: "+researchers.size());

        switch (researchers.size()) {

            case 2:
                return Observable.zip(GenericFacebookPoster.searchUserOnFacebook(researchers.get(0).fullName),
                        GenericFacebookPoster.searchUserOnFacebook(researchers.get(1).fullName), new Func2<GraphResponse,
                                GraphResponse, ResearchGateResponse>() {
                            @Override
                            public ResearchGateResponse call(GraphResponse response, GraphResponse response2) {
                                return fillUrl(fillUrl(researchGateResponse, response, 0), response2, 1);
                            }
                        });
            case 3:
                return Observable.zip(GenericFacebookPoster.searchUserOnFacebook(researchers.get(0).fullName),
                        GenericFacebookPoster.searchUserOnFacebook(researchers.get(1).fullName),
                        GenericFacebookPoster.searchUserOnFacebook(researchers.get(2).fullName), new Func3<GraphResponse,
                                GraphResponse, GraphResponse, ResearchGateResponse>() {
                            @Override
                            public ResearchGateResponse call(GraphResponse response,
                                                             GraphResponse response2, GraphResponse response3) {
                                return fillUrl(fillUrl(fillUrl(researchGateResponse, response, 0), response2, 1),
                                        response3, 2);
                            }
                        });
            case 4:
                return Observable.zip(GenericFacebookPoster.searchUserOnFacebook(researchers.get(0).fullName),
                        GenericFacebookPoster.searchUserOnFacebook(researchers.get(1).fullName),
                        GenericFacebookPoster.searchUserOnFacebook(researchers.get(2).fullName),
                        GenericFacebookPoster.searchUserOnFacebook(researchers.get(3).fullName), new Func4<GraphResponse,
                                GraphResponse, GraphResponse, GraphResponse, ResearchGateResponse>() {
                            @Override
                            public ResearchGateResponse call(GraphResponse response, GraphResponse response2,
                                                             GraphResponse response3, GraphResponse response4) {
                                return fillUrl(fillUrl(fillUrl(fillUrl(researchGateResponse, response, 0),
                                        response2, 1), response3, 2), response4, 3);
                            }
                        });
            case 5:
                return Observable.zip(GenericFacebookPoster.searchUserOnFacebook(researchers.get(0).fullName),
                        GenericFacebookPoster.searchUserOnFacebook(researchers.get(1).fullName),
                        GenericFacebookPoster.searchUserOnFacebook(researchers.get(2).fullName),
                        GenericFacebookPoster.searchUserOnFacebook(researchers.get(3).fullName),
                        GenericFacebookPoster.searchUserOnFacebook(researchers.get(4).fullName),
                        new Func5<GraphResponse, GraphResponse, GraphResponse, GraphResponse, GraphResponse,
                                ResearchGateResponse>() {
                            @Override
                            public ResearchGateResponse call(GraphResponse response, GraphResponse response2,
                                                             GraphResponse response3, GraphResponse response4,
                                                             GraphResponse response5) {
                                return fillUrl(fillUrl(fillUrl(fillUrl(fillUrl(researchGateResponse, response, 0),
                                        response2, 1), response3, 2), response4, 3), response5, 4);
                            }
                        }
                );

            case 1:
            default:
                return GenericFacebookPoster.searchUserOnFacebook(researchers.get(0).fullName)
                        .map(new Func1<GraphResponse, ResearchGateResponse>() {
                            @Override
                            public ResearchGateResponse call(GraphResponse graphResponse) {
                                return fillUrl(researchGateResponse, graphResponse, 0);
                            }
                        });


        }

    }

}

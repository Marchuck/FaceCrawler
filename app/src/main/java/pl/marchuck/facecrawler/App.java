package pl.marchuck.facecrawler;

import android.app.Application;
import android.util.Log;

import pl.marchuck.facecrawler.thirdPartyApis.ResearchGateResponse;
import pl.marchuck.facecrawler.thirdPartyApis.swapi.Researcher;

/**
 * @author Lukasz Marczak
 * @since 03.05.16.
 */
public class App extends Application {

    public static App instance;
    public String currentToken;
    public String currentUserId;
    public String userName = "Adam";
    public String longLivingAccessToken;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        longLivingAccessToken = getResources().getString(R.string.long_living_access_token);
    }

    public ResearchGateResponse researchGateResponse;

    public void clearCurrentResponse() {

        researchGateResponse = null;
    }

    public void updateResearcher(Researcher researcher) {
        if (researchGateResponse == null) return;
        for (int j = 0; j < researchGateResponse.authors.size(); j++) {
            if (researcher.fullName.equals(researchGateResponse.authors.get(j).fullName)) {
                Researcher ress = researchGateResponse.authors.get(j);
                ress.facebookUrl = researcher.facebookUrl;
                researchGateResponse.authors.set(j, ress);
            }
        }
    }

    public static final String TAG = App.class.getSimpleName();

    public String prepareMessage() {
        Log.d(TAG, "prepareMessage: ");
        String authors = "";
        for (Researcher researcher : researchGateResponse.authors) {
            authors += researcher.fullName + ": " + researcher.researchGateUrl + "\n"
                    + researcher.facebookUrl + "\n\n";
        }

        return researchGateResponse.url + "\n\n"
                + researchGateResponse._abstract + "\n\n"
                + authors;
    }
}

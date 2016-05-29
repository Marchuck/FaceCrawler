package pl.marchuck.facecrawler.thirdPartyApis;

import java.util.ArrayList;
import java.util.List;

import pl.marchuck.facecrawler.thirdPartyApis.swapi.Researcher;

/**
 * @author Lukasz Marczak
 * @since 29.05.16.
 */
public class ResearchGateResponse {
    public String _abstract;
    public List<Researcher> authors = new ArrayList<>();
    public String url;

    public ResearchGateResponse() {
    }

    public ResearchGateResponse(String _abstract) {
        this._abstract = _abstract;
    }

    public ResearchGateResponse(String _abstract, List<Researcher> authors, String url) {
        this._abstract = _abstract;
        this.authors = authors;
        this.url = url;
    }

    @Override
    public String toString() {
        Researcher r=authors.get(0);
        return url + "\n\n" +
                _abstract +
                "\n\nAuthor: " + r.fullName +
                ", " + r.researchGateUrl + "\n\n" +
                r.facebookUrl;
    }
}

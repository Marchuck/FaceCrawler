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
        StringBuilder stringBuilder = new StringBuilder();
        for (Researcher r : authors) {
            buildNextAuthor(stringBuilder, r);
        }
        String authorss = authors.size() == 1 ? "Author: " : "Authors: ";
        return url + "\n" +
                fixedAbstract(_abstract) + "\n\n" + authorss + stringBuilder.toString();

    }

    private String fixedAbstract(String _abstract) {
        return (_abstract == null || _abstract.isEmpty() || _abstract.equals("false") ? "" : shortenedAbstract(_abstract));
    }

    private String shortenedAbstract(String anAbstract) {
        return (anAbstract.length() > 200 ? anAbstract.substring(0, 500) : anAbstract) + "...";
    }

    private void buildNextAuthor(StringBuilder stringBuilder, Researcher r) {
        stringBuilder.append(r.fullName).append(", ").append(r.researchGateUrl).append("\n")
                .append(r.facebookUrl == null ? "" : r.facebookUrl).append("\n\n");
    }
}

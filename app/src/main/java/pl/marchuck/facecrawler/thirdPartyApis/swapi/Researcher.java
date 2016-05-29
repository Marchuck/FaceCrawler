package pl.marchuck.facecrawler.thirdPartyApis.swapi;

/**
 * @author Lukasz Marczak
 * @since 29.05.16.
 */
public class Researcher {
    public String fullName;
    public String researchGateUrl;
    public String facebookUrl;

    public Researcher() {
    }

    public Researcher(String researchGateUrl, String fullName, String facebookUrl) {
        this.researchGateUrl = researchGateUrl;
        this.fullName = fullName;
        this.facebookUrl = facebookUrl;
    }

    public Researcher(String fullName, String researchGateUrl) {
        this.fullName = fullName;
        this.researchGateUrl = researchGateUrl;
    }
}

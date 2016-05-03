package pl.marchuck.facecrawler.thirdPartyApis.pokemon;

/**
 * @author Lukasz Marczak
 * @since 18.04.2016.
 */
public class Pokemon {
    public String name;
    public String height;
    public String weight;
    public String base_experience;

    @Override
    public String toString() {
        return name + ", height: " + height + ", weight: " + weight + ", " + base_experience + " EXP";
    }
}

package pl.marchuck.facecrawler.thirdPartyApis.common;

/**
 * @author Lukasz Marczak
 * @since 03.05.16.
 */
public class Friend {
    public String id;
    public String name;

    public Friend(String friendId, String friendName) {
        this.id = friendId;
        this.name = friendName;
    }
}

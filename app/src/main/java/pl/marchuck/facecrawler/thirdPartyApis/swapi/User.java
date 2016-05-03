package pl.marchuck.facecrawler.thirdPartyApis.swapi;

import com.google.gson.annotations.Expose;

/**
 * @author Lukasz Marczak
 * @since 19.04.2016.
 */
public class User {
    @Expose
    private String firstName;
    @Expose(serialize = false)
    private String lastName;
    @Expose(serialize = false, deserialize = false)
    private String emailAddress;
    @Expose
    private String password;

    @Override
    public String toString() {
        return firstName + "," + lastName + "," + emailAddress + "," + password;
    }
}

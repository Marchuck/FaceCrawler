package pl.marchuck.facecrawler.thirdPartyApis.swapi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * @author Lukasz Marczak
 * @since 18.04.2016.
 */
public interface Swapi {

    String endpoint = "http://swapi.co/api/";

    @GET("people/{id}")
    Call<SwapiCharacter> getCharacter(@Path("id") int id);
}

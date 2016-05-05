package pl.marchuck.facecrawler;

import android.app.Application;
import android.content.pm.PackageInstaller;
import android.support.design.widget.AppBarLayout;

import com.facebook.FacebookSdk;

/**
 * @author Lukasz Marczak
 * @since 03.05.16.
 */
public class App extends Application {

    public static App instance;
    public String currentToken;
    public String currentUserId;

    public String graphAPIToken = "EAAHbBZCZBBbl8BAOZAyREhryDfnfZABwkuAlws5zDLYBk8IOhoz7DBbFKS6FIIZAFI27BOEIxOWjD8HBauJtDaojfafGhsAAiGhoBebaesZAKBDpw0rZA0yyrqXKsEcxxD1BS3tsMEFxL5Qei3hOXf0q8ZCxZBFN9ZCZBhDSxVOS25REgZDZD";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}

package pl.marchuck.facecrawler.ifaces;

import android.app.Activity;

/**
 * @author Lukasz Marczak
 * @since 03.05.16.
 */
public interface Facebookable extends Updatable {
    Activity getActivity();

    void postPhoto();

    void postStatusUpdate();
}

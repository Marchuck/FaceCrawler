package pl.marchuck.facecrawler.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * @author Lukasz Marczak
 * @since 03.05.16.
 */
public class FBTarget implements Target {
    public static final String TAG = FBTarget.class.getSimpleName();

    public FBTarget(ImageView imageView) {
        this.imageView = imageView;
    }

    ImageView imageView;

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        Log.i(TAG, "onBitmapLoaded: ");
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        Log.e(TAG, "onBitmapFailed: ");
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        Log.i(TAG, "onPrepareLoad: ");
    }
}

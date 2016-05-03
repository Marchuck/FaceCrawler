package pl.marchuck.facecrawler.argh;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.facebook.AccessToken;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * @author Lukasz Marczak
 * @since 03.05.16.
 */
public class TokenChangeObservable {
    public static final String TAG = TokenChangeObservable.class.getSimpleName();

    public interface TokenChangeListener {
        void onChange();
    }

    private static rx.Observable<Boolean> tokenChangedObservable() {

        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            boolean notChanged = true;

            @Override
            public void call(Subscriber<? super Boolean> subscriber) {

                //Handler handler = new Handler(Looper.getMainLooper());
                while (notChanged && !subscriber.isUnsubscribed()) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    notChanged = AccessToken.getCurrentAccessToken() == null;
                }
                if (!subscriber.isUnsubscribed()) subscriber.onNext(true);
                subscriber.onCompleted();
            }
        });
    }

    public static void startObservable(final TokenChangeListener listener) {
        tokenChangedObservable().subscribeOn(Schedulers.trampoline()).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                listener.onChange();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e(TAG, "call: " + throwable.getMessage());
                throwable.printStackTrace();
            }
        });
    }

}

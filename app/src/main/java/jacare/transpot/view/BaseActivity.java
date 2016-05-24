/**
 * Created by: Tomek Spędzia
 * Date: 5/22/2016
 * Email: tomek.milosz.spedzia@gmail.com
 */

package jacare.transpot.view;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import rx.Observable;

public abstract class BaseActivity extends FragmentActivity {
    public static final String TAG = "Trnspt.Base";

    protected boolean started;
    protected boolean addressTyped;
    protected boolean addresInputLocked;

    protected Observable<Boolean> onInputLeft(EditText view){
        return Observable.just(view)
                .map(this::doOnInputLeft);
    }

    private boolean doOnInputLeft(EditText view){
        Log.i(TAG, String.format("Quitting input %s", view.getId()));

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        view.setCursorVisible(false);
        addressTyped = false;
        view.clearFocus();
        return false;
    }

    protected void onInputEntered(EditText view){
        Log.i(TAG, String.format("Entering input %s", view.getId()));
        view.setCursorVisible(true);
        addressTyped = true;
    }
}

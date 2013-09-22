package com.example.screenwriter;

import android.app.Activity;
import android.os.StrictMode;
import android.util.Log;

public class BaseActivity extends Activity {

protected static final int NO_FLAGS = 0;
protected static final String EXTRA_TEXT = "extra_text";

private static final String TAG = "ScreenWriter";
protected void LOG(String msg) {
	if (BuildConfig.DEBUG) {
		Log.i(TAG, msg);
	}
}
protected void CONFIG_STRICT_MODE() {
	if (BuildConfig.DEBUG) {
		StrictMode.enableDefaults();
	}
}

}

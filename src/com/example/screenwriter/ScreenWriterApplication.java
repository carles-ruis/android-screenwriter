package com.example.screenwriter;

import android.app.Application;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;

public class ScreenWriterApplication extends Application {

private GestureLibrary gestureLib;

public ScreenWriterApplication() {}

/*- ********************************************************************************** */
/*- *********** PUBLIC ************* */
/*- ********************************************************************************** */
public GestureLibrary getGestureLib() {
	if (gestureLib==null) {
		gestureLib = GestureLibraries.fromRawResource(this, R.raw.gestures);
		gestureLib.load();
	}
	return gestureLib;
}

}

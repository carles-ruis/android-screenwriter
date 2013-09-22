package com.example.screenwriter;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ImatgeGesturesActivity extends Activity {

/*- ********************************************************************************** */
/*- *********** OVERRIDE ************* */
/*- ********************************************************************************** */
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_imatge_gestures);
}

@Override
public boolean onCreateOptionsMenu(Menu menu) {
	return true;
}

}

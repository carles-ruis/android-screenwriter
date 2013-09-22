package com.example.screenwriter;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends BaseActivity implements OnGesturePerformedListener {

private static final double MIN_PREDICTION_SCORE = 1.0d;
private static final String SAVED_TEXT = "saved_text";
private static final String SAVED_IS_MAJUSCULES = "saved_is_majuscules";
private static final String SAVED_NEXT_MAJUSCULES = "saved_next_majuscules";

private GestureLibrary gestureLib;
private boolean isMajuscules;
private boolean isNextMajuscules;
private TextView text;

/*- ********************************************************************************** */
/*- *********** OVERRIDE ************* */
/*- ********************************************************************************** */
@Override
protected void onCreate(Bundle bundle) {
	super.onCreate(bundle);
	CONFIG_STRICT_MODE();
	setContentView(R.layout.activity_main);

	gestureLib = ((ScreenWriterApplication)getApplicationContext()).getGestureLib();
	if (gestureLib==null) finish();
	isNextMajuscules = true;
	isMajuscules = false;
	this.iniText(bundle);
	this.setEventHandlers();
}

@Override
public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
}

@Override
protected void onSaveInstanceState(Bundle bundle) {
    super.onSaveInstanceState(bundle);
    bundle.putString(SAVED_TEXT, text.getText().toString());
    bundle.putBoolean(SAVED_NEXT_MAJUSCULES, isNextMajuscules);
    bundle.putBoolean(SAVED_IS_MAJUSCULES, isMajuscules);
}

@Override
public boolean onOptionsItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case R.id.menu_llista_gestures:
		mostraLlistaDeGestures();
		return true;
	}
	return super.onOptionsItemSelected(item);
}

@Override
public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
	ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
	if (predictions.size() > 0) {
		Prediction p = predictions.get(0);
		if (p.score > MIN_PREDICTION_SCORE) {
			escriuLletra(p.name);
		}
	}
}

@Override
public void onBackPressed() {}

@Override
public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
	super.onCreateContextMenu(menu, v, menuInfo);
	getMenuInflater().inflate(R.menu.main_context, menu);
}

@Override
public boolean onContextItemSelected(MenuItem item) {
	switch (item.getItemId()) {
	case R.id.menu_context_esborrar:
		text.setText("");
		return true;
	case R.id.menu_context_editar:
		editarTextManual();
		return true;
	}
	return super.onContextItemSelected(item);
}

/*- ********************************************************************************** */
/*- *********** PRIVATE ************* */
/*- ********************************************************************************** */
private void setEventHandlers() {
	((ImageButton) findViewById(R.id.boto_delete)).setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			esborrarLletra();
		}
	});
	GestureOverlayView gestureView = (GestureOverlayView) findViewById(R.id.gestures_overlay);
	gestureView.addOnGesturePerformedListener(this);
	gestureView.setOnLongClickListener(new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			mostraImatgeGestures();
			return true;
		}
	});
	((ToggleButton) findViewById(R.id.boto_majuscules)).setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			isMajuscules = !isMajuscules;
		}
	});
	((Button) findViewById(R.id.boto_coma)).setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			escriuLletra(",");
		}
	});
	((ImageButton) findViewById(R.id.boto_espai)).setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			escriuLletra(" ");
		}
	});
	((ImageButton) findViewById(R.id.boto_enter)).setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			escriuLletra(".");
			escriuSaltDeLinia();
			isNextMajuscules = true;
		}
	});
	((ImageButton) findViewById(R.id.boto_enviar)).setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			enviarText();
		}
	});
}

@SuppressLint("DefaultLocale")
private void escriuLletra(CharSequence lletra) {
	if (isMajuscules || isNextMajuscules) {
		lletra = lletra.toString().toUpperCase();
	}
	Toast.makeText(this, lletra, Toast.LENGTH_SHORT).show();
	text.append(lletra);
	isNextMajuscules = false;
}

private void esborrarLletra() {
	int length = text.getText().length();
	if (length > 0) {
		text.setText(text.getText().subSequence(0, length - 1));
	}
}

private void escriuSaltDeLinia() {
	text.append(System.getProperty("line.separator"));
	isNextMajuscules = true;
}

private void iniText(Bundle savedState) {
	text = (TextView) findViewById(R.id.text_main);
	Bundle intentExtras = getIntent().getExtras();
	if (intentExtras != null) {
		text.setText(intentExtras.getString(EXTRA_TEXT));
	} else if (savedState!=null) {
		text.setText(savedState.getString(SAVED_TEXT));
		isNextMajuscules = savedState.getBoolean(SAVED_NEXT_MAJUSCULES);
		isMajuscules = savedState.getBoolean(SAVED_IS_MAJUSCULES);
	}		
	this.registerForContextMenu(text);
}

/*- ********************************************************************************** */
/*- *********** SEND INTENTS ************* */
/*- ********************************************************************************** */
private void editarTextManual() {
	Intent intent = new Intent(this, EditTextActivity.class);
	intent.putExtra(EXTRA_TEXT, text.getText().toString());
	startActivity(intent);
}

private void enviarText() {
	Intent intent = new Intent(Intent.ACTION_SEND);
	intent.putExtra(Intent.EXTRA_TEXT, text.getText().toString());
	intent.setType("text/plain");
	startActivity(Intent.createChooser(intent, getResources().getText(R.string.enviar_text)));
	
}

private void mostraLlistaDeGestures() {
	startActivity(new Intent(this,LlistaDeGesturesActivity.class));
}

private void mostraImatgeGestures() {
	startActivity(new Intent(this,ImatgeGesturesActivity.class));
}
}

package com.example.screenwriter;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EditTextActivity extends BaseActivity {

private EditText text;

/*- ********************************************************************************** */
/*- *********** OVERRIDE ************* */
/*- ********************************************************************************** */
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_edit_text);
	
	text = (EditText) findViewById(R.id.text_edit);
	text.setText(getIntent().getStringExtra(EXTRA_TEXT));
	this.setEventHandlers();
	}

@Override
public boolean onCreateOptionsMenu(Menu menu) {
	return true;
}

@Override
public void onBackPressed() {}

/*- ********************************************************************************** */
/*- *********** PRIVATE ************* */
/*- ********************************************************************************** */
private void setEventHandlers() {
	((Button) findViewById(R.id.boto_ok)).setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			guardarTextEditat();
		}
	});
	((Button) findViewById(R.id.boto_cancelar)).setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			cancelarTextEditat();
		}
	});
	}

private void guardarTextEditat() {
	startActivity(new Intent(this,MainActivity.class).putExtra(EXTRA_TEXT, text.getText().toString()));      
}
private void cancelarTextEditat() {
	startActivity(new Intent(this,MainActivity.class).putExtra(EXTRA_TEXT, getIntent().getStringExtra(EXTRA_TEXT)));
}

}

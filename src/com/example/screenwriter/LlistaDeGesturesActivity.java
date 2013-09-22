package com.example.screenwriter;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.gesture.Gesture;
import android.gesture.GestureLibrary;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class LlistaDeGesturesActivity extends ListActivity {

private GestureLibrary gestureLib;
private GesturesLoadTask mTask;
private static GesturesAdapter mAdapter;

private final Comparator<NamedGesture> mSorter = new Comparator<NamedGesture>() {
    public int compare(NamedGesture object1, NamedGesture object2) {
        return object1.name.compareTo(object2.name);
    }
};

/*- ********************************************************************************** */
/*- *********** OVERRIDE ************* */
/*- ********************************************************************************** */
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	gestureLib = ((ScreenWriterApplication) getApplicationContext()).getGestureLib();
	if (gestureLib == null) finish();
	if (mAdapter == null) {
		mAdapter = new GesturesAdapter(this);
		loadGestures();
	}
	setListAdapter(mAdapter);
}

@Override
public boolean onCreateOptionsMenu(Menu menu) {
	return true;
}

/*- ********************************************************************************** */
/*- *********** PRIVATE ************* */
/*- ********************************************************************************** */
private void loadGestures() {
    if (mTask != null && mTask.getStatus() != GesturesLoadTask.Status.FINISHED) {
        mTask.cancel(true);
    }        
    mTask = (GesturesLoadTask) new GesturesLoadTask().execute();
}

/*- ********************************************************************************** */
/*- *********** GESTURES LOAD TASK ************* */
/*- ********************************************************************************** */
private class GesturesLoadTask extends AsyncTask<Void, NamedGesture, Integer> {
private int mThumbnailSize;
private int mThumbnailInset;
private int mPathColor;
private ProgressDialog dialog;
private static final int STATUS_SUCCESS = 0;
private static final int STATUS_CANCELLED = 1;
private static final int STATUS_NO_STORAGE = 2;

@Override
protected void onPreExecute() {
	super.onPreExecute();
	final Resources resources = getResources();
	mPathColor = resources.getColor(R.color.gesture_color);
	mThumbnailInset = (int) resources.getDimension(R.dimen.gesture_thumbnail_inset);
	mThumbnailSize = (int) resources.getDimension(R.dimen.gesture_thumbnail_size);
	mAdapter.setNotifyOnChange(false);
	dialog = ProgressDialog.show(LlistaDeGesturesActivity.this, (CharSequence) "",
		(CharSequence) resources.getString(R.string.espera_msg));
//	mAdapter.clear();
}

@Override
protected Integer doInBackground(Void... params) {
	if (isCancelled()) return STATUS_CANCELLED;
	if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) { return STATUS_NO_STORAGE; }

	final GestureLibrary store = gestureLib;
	for (String name : store.getGestureEntries()) {
		if (isCancelled()) break;
		for (Gesture gesture : store.getGestures(name)) {
			final Bitmap bitmap = gesture.toBitmap(mThumbnailSize, mThumbnailSize, mThumbnailInset, mPathColor);
			final NamedGesture namedGesture = new NamedGesture();
			namedGesture.gesture = gesture;
			namedGesture.name = name;
			mAdapter.addBitmap(namedGesture.gesture.getID(), bitmap);
			publishProgress(namedGesture);
		}
	}
	return STATUS_SUCCESS;
}

@Override
protected void onProgressUpdate(NamedGesture... gesture) {
	super.onProgressUpdate(gesture);
	final GesturesAdapter adapter = mAdapter;
//	adapter.setNotifyOnChange(false);
	adapter.add(gesture[0]);
//	adapter.sort(mSorter); /*-la llista no s'ordena fins al final*/
//	adapter.notifyDataSetChanged();
}

@Override
protected void onPostExecute(Integer result) {
	super.onPostExecute(result);
    if (dialog.isShowing()) {
        dialog.dismiss();
    }
	mAdapter.sort(mSorter);
	mAdapter.notifyDataSetChanged(); /*-perque es mostri la llista a la View*/
}
}

/*- ********************************************************************************** */
/*- *********** GESTURES ADAPTER ************* */
/*- ********************************************************************************** */
static class NamedGesture {
String name;
Gesture gesture;
}

class GesturesAdapter extends ArrayAdapter<NamedGesture> {
private final LayoutInflater mInflater;
private final Map<Long, Drawable> mThumbnails = Collections.synchronizedMap(new HashMap<Long, Drawable>());

public GesturesAdapter(Context context) {
	super(context, 0);
	mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
}

void addBitmap(Long id, Bitmap bitmap) {
	mThumbnails.put(id, new BitmapDrawable(bitmap));
}

@Override
public View getView(int position, View convertView, ViewGroup parent) {
	if (convertView == null) {
		convertView = mInflater.inflate(R.layout.gestures_item, parent, false);
	}
	final NamedGesture gesture = getItem(position);
	final TextView label = (TextView) convertView;
	label.setTag(gesture);
	label.setText(gesture.name);
	label.setCompoundDrawablesWithIntrinsicBounds(mThumbnails.get(gesture.gesture.getID()), null, null, null);
	return convertView;
}
}
}

package ch.cern.maps.services;

import ch.cern.maps.adapters.MapAdapter;
import ch.cern.www.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class DialogBox extends Activity {

	private Context context;

	public DialogBox(Context c) {
		context = c;
	}

	public Dialog startInfoBox(Typeface tf) {
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View mLayout = mInflater.inflate(R.layout.map_selector_dialog, null);
		TextView tv = (TextView) mLayout.findViewById(R.id.boxTitle);
		tv.setTypeface(tf);

		AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
		mBuilder.setView(mLayout);
		ListView drawerListView = (ListView) mLayout
				.findViewById(R.id.selectMap);
		MapAdapter customAdapter = new MapAdapter(context);
		drawerListView.setAdapter(customAdapter);
		return mBuilder.create();
	}
}

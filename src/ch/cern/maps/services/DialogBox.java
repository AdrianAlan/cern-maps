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

	private Context mContext;

	public DialogBox(Context c) {
		mContext = c;
	}

	public Dialog startInfoBox(Typeface tf) {
		LayoutInflater mInflater = (LayoutInflater) mContext
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View mLayout = mInflater.inflate(R.layout.dialog_box, null);
		TextView tv = (TextView) mLayout.findViewById(R.id.boxTitle);
		tv.setTypeface(tf);

		AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
		mBuilder.setView(mLayout);
		ListView drawerListView = (ListView) mLayout
				.findViewById(R.id.selectMap);
		MapAdapter customAdapter = new MapAdapter(mContext);
		drawerListView.setAdapter(customAdapter);
		return mBuilder.create();
	}

	public Dialog startTramBox(String t18, String tY1, String tY2) {
		TextView tram18, tramY1, tramY2;

		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialog_trams, null);
		tram18 = (TextView) layout.findViewById(R.id.box18);
		if (t18 != null) {
			tram18.setText(mContext.getString(R.string.NextTramIn) + t18);
		}
		tramY1 = (TextView) layout.findViewById(R.id.boxY1);
		if (tY1 != null) {
			tramY1.setText(mContext.getString(R.string.NextBusIn) + tY1);
		}
		tramY2 = (TextView) layout.findViewById(R.id.boxY2);
		if (tY2 != null) {
			tramY2.setText(mContext.getString(R.string.NextBusIn) + tY2);
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setView(layout).setNeutralButton(R.string.OK, null);
		return builder.create();
	}

}

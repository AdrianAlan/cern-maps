package ch.cern.maps.services;

import ch.cern.maps.utils.Constants;
import ch.cern.maps.utils.Utils;
import ch.cern.www.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class DialogBox extends Activity {

	private Context mContext;

	public DialogBox(Context _context) {
		mContext = _context;
	}

	public Dialog startInfoBox() {
		TextView boxTitle, boxInfo;

		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialog_box, null);
		boxTitle = (TextView) layout.findViewById(R.id.boxTitle);
		boxTitle.setTypeface(Utils.getTypeface(mContext,
				Constants.TYPEFONT_NEOSANS));
		boxInfo = (TextView) layout.findViewById(R.id.boxInfo);
		boxInfo.setTypeface(Utils.getTypeface(mContext,
				Constants.TYPEFONT_NEOSANS));
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setView(layout).setNeutralButton("OK", null);
		return builder.create();
	}

	public Dialog startTramBox(String t18, String tY1, String tY2) {
		TextView boxTitle, tram18, tramY1, tramY2, textView18, textViewY1, textViewY2;

		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialog_trams, null);
		boxTitle = (TextView) layout.findViewById(R.id.boxTitle);
		boxTitle.setTypeface(Utils.getTypeface(mContext,
				Constants.TYPEFONT_NEOSANS));
		textView18 = (TextView) layout.findViewById(R.id.textView18);
		textView18.setTypeface(Utils.getTypeface(mContext,
				Constants.TYPEFONT_NEOSANS));
		textViewY1 = (TextView) layout.findViewById(R.id.textViewY1);
		textViewY1.setTypeface(Utils.getTypeface(mContext,
				Constants.TYPEFONT_NEOSANS));
		textViewY2 = (TextView) layout.findViewById(R.id.textViewY2);
		textViewY2.setTypeface(Utils.getTypeface(mContext,
				Constants.TYPEFONT_NEOSANS));
		tram18 = (TextView) layout.findViewById(R.id.box18);
		tram18.setTypeface(Utils.getTypeface(mContext,
				Constants.TYPEFONT_NEOSANS));
		if (t18 != null) {
			tram18.setText("Next tram in: " + t18);
		}
		tramY1 = (TextView) layout.findViewById(R.id.boxY1);
		tramY1.setTypeface(Utils.getTypeface(mContext,
				Constants.TYPEFONT_NEOSANS));
		if (tY1 != null) {
			tramY1.setText("Next bus in: " + tY1);
		}
		tramY2 = (TextView) layout.findViewById(R.id.boxY2);
		tramY2.setTypeface(Utils.getTypeface(mContext,
				Constants.TYPEFONT_NEOSANS));
		if (tY2 != null) {
			tramY2.setText("Next bus in: " + tY2);
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setView(layout).setNeutralButton("OK", null);
		return builder.create();
	}

}

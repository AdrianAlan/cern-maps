package ch.cern.maps.adapters;

import ch.cern.www.R;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ShuttleAdapter extends BaseAdapter {

	private Typeface typeface;
	private String[] shuttle;

	public ShuttleAdapter(Context c, String[] t) {
		this.shuttle = t;
		this.typeface = Typeface.createFromAsset(c.getAssets(),
				"DroidSans.ttf");
	}

	@Override
	public int getCount() {
		return shuttle.length;
	}

	@Override
	public Object getItem(int i) {
		return shuttle[i];
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(final int index, View view, final ViewGroup parent) {

		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			view = inflater.inflate(R.layout.shuttle_item, parent, false);
		}
		TextView nextShuttle = (TextView) view.findViewById(R.id.textViewNext);
		nextShuttle.setTypeface(typeface);

		TextView nextShuttleMinutes = (TextView) view.findViewById(R.id.textViewMinutes);
		nextShuttleMinutes.setTypeface(typeface);
		nextShuttleMinutes.setText(shuttle[index]);
		return view;
	}
}

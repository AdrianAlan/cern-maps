package ch.cern.maps.adapters;

import java.util.ArrayList;

import ch.cern.maps.models.TPGView;
import ch.cern.www.R;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TPGAdapter extends BaseAdapter {

	private Typeface typeface;
	private ArrayList<TPGView> tpg;

	public TPGAdapter(Context c, ArrayList<TPGView> t) {
		this.tpg = t;
		this.typeface = Typeface.createFromAsset(c.getAssets(),
				"DroidSans.ttf");
	}

	@Override
	public int getCount() {
		return tpg.size();
	}

	@Override
	public Object getItem(int i) {
		return tpg.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(final int index, View view, final ViewGroup parent) {

		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			view = inflater.inflate(R.layout.trams_item, parent, false);
		}

		final TPGView dataModel = tpg.get(index);

		final TextView textViewLine = (TextView) view.findViewById(R.id.textViewLine);
		textViewLine.setTypeface(typeface);

		final TextView imageView = (TextView) view.findViewById(R.id.imageViewLine);
		imageView.setTypeface(typeface);
		imageView.setText(dataModel.getLineNumber());
		imageView.setBackground(dataModel.getLineColor());

		final TextView next = (TextView) view.findViewById(R.id.textViewNext);
		next.setTypeface(typeface);

		final TextView minutes = (TextView) view.findViewById(R.id.textViewMinutes);
		minutes.setTypeface(typeface);
		minutes.setText(dataModel.getNext()[0].getWaiting() + "");

		final TextView direction = (TextView) view.findViewById(R.id.textViewDirection);
		direction.setTypeface(typeface);
		direction.setText("minutes in direction " + dataModel.getNext()[0].getDirection());

		final TextView next2 = (TextView) view.findViewById(R.id.textViewNext2);
		next2.setTypeface(typeface);

		final TextView minutes2 = (TextView) view.findViewById(R.id.textViewMinutes2);
		minutes2.setTypeface(typeface);
		minutes2.setText(dataModel.getNext()[1].getWaiting() + "");

		final TextView direction2 = (TextView) view.findViewById(R.id.textViewDirection2);
		direction2.setTypeface(typeface);
		direction2.setText("minutes in direction " + dataModel.getNext()[1].getDirection());
		
		return view;
	}
}

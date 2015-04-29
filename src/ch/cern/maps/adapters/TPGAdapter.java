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

	private Typeface mTypeface;
	private Context mContext;
	private ArrayList<TPGView> mTPG;
	private TextView tv;

	public TPGAdapter(Context c, ArrayList<TPGView> t) {
		this.mContext = c;
		this.mTPG = t;
		this.mTypeface = Typeface.createFromAsset(mContext.getAssets(),
				"DroidSans.ttf");
	}

	@Override
	public int getCount() {
		return mTPG.size();
	}

	@Override
	public Object getItem(int i) {
		return mTPG.get(i);
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

		final TPGView dataModel = mTPG.get(index);

		tv = (TextView) view.findViewById(R.id.textViewLine);
		tv.setTypeface(mTypeface);
		
		tv = (TextView) view.findViewById(R.id.imageViewLine);
		tv.setTypeface(mTypeface);
		tv.setText(dataModel.getLineNumber());
		tv.setBackgroundDrawable(dataModel.getLineColor());
		
		tv = (TextView) view.findViewById(R.id.textViewNext);
		tv.setTypeface(mTypeface);
		
		tv = (TextView) view.findViewById(R.id.textViewMinutes);
		tv.setTypeface(mTypeface);
		tv.setText(dataModel.getNext()[0].getWaiting() + "");
		
		tv = (TextView) view.findViewById(R.id.textViewDirection);
		tv.setTypeface(mTypeface);
		tv.setText("minutes in direction " + dataModel.getNext()[0].getDirection());
		
		tv = (TextView) view.findViewById(R.id.textViewNext2);
		tv.setTypeface(mTypeface);
		
		tv = (TextView) view.findViewById(R.id.textViewMinutes2);
		tv.setTypeface(mTypeface);
		tv.setText(dataModel.getNext()[1].getWaiting() + "");
		
		tv = (TextView) view.findViewById(R.id.textViewDirection2);
		tv.setTypeface(mTypeface);
		tv.setText("minutes in direction " + dataModel.getNext()[1].getDirection());
		
		return view;
	}
}

package ch.cern.maps.adapters;

import java.util.ArrayList;
import java.util.Locale;

import ch.cern.maps.models.DataNavigation;
import ch.cern.maps.utils.Constants;
import ch.cern.www.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MapAdapter extends BaseAdapter {

	private ArrayList<DataNavigation> navObjects;
	private Typeface typeface;
	private Context context;

	public MapAdapter(Context c) {
		context = c;
		String[] navTitles = c.getResources().getStringArray(
				R.array.MapSelectorTitles);
		String[] navDescription = c.getResources().getStringArray(
				R.array.MapSelectorDescriptions);
		int[] navIcons = Constants.mapSelectorIcons;
		navObjects = new ArrayList<>();
		for (int i = 0; i < navTitles.length; i++) {
			navObjects.add(new DataNavigation(navTitles[i], navDescription[i],
					navIcons[i]));
		}
		typeface = Typeface.createFromAsset(context.getAssets(),
				"DroidSans.ttf");
	}

	@Override
	public int getCount() {
		return navObjects.size();
	}

	@Override
	public Object getItem(int i) {
		return navObjects.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(final int index, @Nullable View view, final ViewGroup parent) {

		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			view = inflater.inflate(R.layout.navigation_item, parent, false);
		}

		final DataNavigation dataModel = navObjects.get(index);

		final TextView title = (TextView) view.findViewById(R.id.navTitle);
		title.setTypeface(typeface);
		title.setText(dataModel.getTitle());
		final TextView description = (TextView) view.findViewById(R.id.navDescription);
		description.setTypeface(typeface);
		description.setText(dataModel.getaDescription());
		final ImageView icon = (ImageView) view.findViewById(R.id.navIcon);
		icon.setImageDrawable(context.getResources().getDrawable(
				dataModel.getImage()));

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				DataNavigation mp = (DataNavigation) getItem(index);
				sendTypeIntent(mp.getTitle().toLowerCase(Locale.ENGLISH));	
			}
		});

		return view;
	}
	
	public void sendTypeIntent(String mType) {
		Intent typeIntent = new Intent();
		typeIntent.setAction(Constants.MapTypeActionTag);
		typeIntent.putExtra(Constants.MapType, mType);
		context.sendBroadcast(typeIntent);
	}
}

package ch.cern.maps.adapters;

import java.util.ArrayList;

import ch.cern.maps.AboutActivity;
import ch.cern.maps.PhonebookActivity;
import ch.cern.maps.ShuttleScheduleActivity;
import ch.cern.maps.StartActivity;
import ch.cern.maps.TPGScheduleActivity;
import ch.cern.maps.models.DataNavigation;
import ch.cern.maps.utils.Constants;
import ch.cern.www.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigationAdapter extends BaseAdapter {

	private ArrayList<DataNavigation> navObjects;
	private Typeface typeface;
	private Context context;

	public NavigationAdapter(Context c) {
		context = c;
		String[] navTitles = c.getResources().getStringArray(
				R.array.NavigationTitles);
		String[] navDescription = c.getResources().getStringArray(
				R.array.NavigationDescriptions);
		int[] navIcons = Constants.navIcons;
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
	public View getView(final int index, View view, final ViewGroup parent) {

		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			view = inflater.inflate(R.layout.navigation_item, parent, false);
		}

		final DataNavigation dataModel = navObjects.get(index);

		TextView title = (TextView) view.findViewById(R.id.navTitle);
		title.setTypeface(typeface);
		title.setText(dataModel.getTitle());
		TextView description = (TextView) view.findViewById(R.id.navDescription);
		description.setTypeface(typeface);
		description.setText(dataModel.getaDescription());
		ImageView icon = (ImageView) view.findViewById(R.id.navIcon);
		icon.setImageDrawable(context.getResources().getDrawable(
				dataModel.getImage()));

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (index == 0) {
					Intent i = new Intent(context, StartActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(i);
				}

				if (index == 1) {
					Intent i = new Intent(context, PhonebookActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(i);
				}

				if (index == 2) {
					Intent i = new Intent(context, TPGScheduleActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(i);
				}

				if (index == 3) {
					Intent i = new Intent(context, ShuttleScheduleActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(i);
				}

				if (index == 4) {
					Intent i = new Intent(context, AboutActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(i);
				}
			}
		});

		return view;
	}
}

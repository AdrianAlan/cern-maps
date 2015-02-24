package ch.cern.maps.navigation;

import java.util.ArrayList;

import ch.cern.maps.AboutActivity;
import ch.cern.maps.StartActivity;
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
import android.widget.Toast;

public class NavigationAdapter extends BaseAdapter {

	private ArrayList<DataNavigation> navObjects;
	private Typeface mTypeface;
	private Context mContext;
	private TextView tv;
	private ImageView iv;

	public NavigationAdapter(Context c) {
		mContext = c;
		String[] navTitles = c.getResources().getStringArray(
				R.array.NavigationTitles);
		String[] navDescription = c.getResources().getStringArray(
				R.array.NavigationDescriptions);
		int[] navIcons = Constants.navIcons;
		navObjects = new ArrayList<DataNavigation>();
		for (int i = 0; i < navTitles.length; i++) {
			navObjects.add(new DataNavigation(navTitles[i], navDescription[i],
					navIcons[i]));
		}
		mTypeface = Typeface.createFromAsset(mContext.getAssets(),
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

		tv = (TextView) view.findViewById(R.id.navTitle);
		tv.setTypeface(mTypeface);
		tv.setText(dataModel.getTitle());
		tv = (TextView) view.findViewById(R.id.navDescription);
		tv.setTypeface(mTypeface);
		tv.setText(dataModel.getaDescription());
		iv = (ImageView) view.findViewById(R.id.navIcon);
		iv.setImageDrawable(mContext.getResources().getDrawable(
				dataModel.getImage()));

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				
				if (index == 0) {
					Intent i = new Intent(mContext, StartActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mContext.startActivity(i);
				}
				
				if (index == 4) {
					Intent i = new Intent(mContext, AboutActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mContext.startActivity(i);
				}
				
				Toast.makeText(parent.getContext(),
						"view clicked: " + index + dataModel.getaDescription(),
						Toast.LENGTH_SHORT).show();
			}
		});

		return view;
	}
}

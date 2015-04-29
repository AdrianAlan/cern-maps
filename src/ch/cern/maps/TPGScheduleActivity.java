package ch.cern.maps;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import ch.cern.maps.adapters.NavigationAdapter;
import ch.cern.maps.adapters.TPGAdapter;
import ch.cern.maps.models.TPGView;
import ch.cern.maps.models.Trams;
import ch.cern.maps.utils.Constants;
import ch.cern.maps.utils.ImageHelper;
import ch.cern.maps.utils.JSONParser;
import ch.cern.maps.utils.Utils;
import ch.cern.www.R;
import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class TPGScheduleActivity extends Activity {

	private ActionBarDrawerToggle actionBarDrawerToggle;
	private int[] mTVs = { R.id.action_bar_title, R.id.stopName };
	private Typeface mTypeface;
	private ProgressBar loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Show Action Bar
		this.requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_start);

		// Take care of navigation drawer and action bar
		ListView drawerListView = (ListView) findViewById(R.id.left_drawer);
		NavigationAdapter customAdapter = new NavigationAdapter(
				getApplicationContext());
		drawerListView.setAdapter(customAdapter);
		DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_drawer, R.string.nool, R.string.nool);

		// Set actionBarDrawerToggle as the DrawerListener
		drawerLayout.setDrawerListener(actionBarDrawerToggle);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		// Inflate your custom layout
		final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater()
				.inflate(R.layout.action_bar, null);

		// Set up your ActionBar
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setCustomView(actionBarLayout);

		// Customization of the top bar
		final Drawable actionBarColor = getResources().getDrawable(
				R.drawable.top_lines);
		actionBar.setBackgroundDrawable(actionBarColor);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		LinearLayout parentLayout = (LinearLayout) findViewById(R.id.content_frame);
		View childLayout = inflater.inflate(R.layout.tpg_activity,
				(ViewGroup) findViewById(R.layout.tpg_activity));
		parentLayout.addView(childLayout);
		TextView tv = (TextView) findViewById(R.id.action_bar_title);
		tv.setText(getResources().getString(R.string.tpg));

		// Layout start
		LinearLayout ll = (LinearLayout) findViewById(R.id.searchLayout);
		ll.setVisibility(View.INVISIBLE);
		mTypeface = Typeface.createFromAsset(getAssets(), "DroidSans.ttf");
		for (int i = 0; i < mTVs.length; i++) {
			setFontsOnTextViews(mTVs[i]);
		}
		ImageView iv = (ImageView) findViewById(R.id.tpgimg);
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		iv.setImageBitmap(ImageHelper
				.decodeSampledBitmapFromResource(getResources(),
						R.drawable.tpg, displaymetrics.widthPixels, 150));
		loading = (ProgressBar) findViewById(R.id.progressBarTPG);
		
		// Get the trams
		loading.setVisibility(View.VISIBLE);
		readMyTramsFromJSON();
		loading.setVisibility(View.INVISIBLE);
	}

	private void readMyTramsFromJSON() {
		JSONParser jsonParser;
		try {
			ArrayList<Trams> t18 = new ArrayList<Trams>();
			ArrayList<Trams> bY = new ArrayList<Trams>();

			InputStream is = getAssets().open(Constants.JSONTram);
			jsonParser = new JSONParser(is);

			for (Iterator<Trams> i = jsonParser.readSchedule("18").iterator(); i
					.hasNext();) {
				Trams tram = (Trams) i.next();
				t18.add(tram);
			}

			for (Iterator<Trams> i = jsonParser.readSchedule("Y").iterator(); i
					.hasNext();) {
				Trams tram = (Trams) i.next();
				bY.add(tram);
			}

			Trams[] nextTrams = Utils.getNextTrains(t18);
			Trams[] nextBuses = Utils.getNextTrains(bY);

			ArrayList<TPGView> tpg = new ArrayList<TPGView>();
			tpg.add(new TPGView(nextTrams[0].getLine(), getResources()
					.getDrawable(R.drawable.trams_18), nextTrams));
			tpg.add(new TPGView(nextBuses[0].getLine(), getResources()
					.getDrawable(R.drawable.trams_y), nextBuses));
			
			ListView tpgList = (ListView) findViewById(R.id.tpg_list);
			TPGAdapter customAdapter = new TPGAdapter(
					getApplicationContext(), tpg);
			tpgList.setAdapter(customAdapter);

		} catch (IOException e) {
			Log.e(Constants.TAG, e.getMessage());
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		actionBarDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		actionBarDrawerToggle.syncState();
	}

	private void setFontsOnTextViews(int arg) {
		TextView tv = (TextView) findViewById(arg);
		tv.setTypeface(mTypeface);
	}
}

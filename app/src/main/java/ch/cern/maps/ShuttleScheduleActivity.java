package ch.cern.maps;

import java.io.IOException;
import java.io.InputStream;
import ch.cern.maps.adapters.NavigationAdapter;
import ch.cern.maps.adapters.ShuttleAdapter;
import ch.cern.maps.models.Circuit;
import ch.cern.maps.utils.Constants;
import ch.cern.maps.utils.ImageHelper;
import ch.cern.maps.utils.JSONParser;
import ch.cern.www.R;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class ShuttleScheduleActivity extends Activity {

	private ActionBarDrawerToggle actionBarDrawerToggle;
	private int[] tvs = { R.id.action_bar_title, R.id.circuitName,
			R.id.textSubTitle };
	private Circuit circuit;
	private Typeface typeface;
	private ShowCircuitSelector showCircuitSelector;
	private ShowTimeSelector showTimeSelector;
	private ShowStopSelector showStopSelector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Show Action Bar
		this.requestWindowFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.drawer_layout);

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
		View childLayout = inflater.inflate(R.layout.shuttle_activity,
				(ViewGroup) findViewById(R.id.shuttle_activity));
		parentLayout.addView(childLayout);
		TextView tv = (TextView) findViewById(R.id.action_bar_title);
		tv.setText(getResources().getString(R.string.CERNShuttleService));

		// Layout start
		LinearLayout ll = (LinearLayout) findViewById(R.id.searchLayout);
		ll.setVisibility(View.INVISIBLE);
		typeface = Typeface.createFromAsset(getAssets(), "DroidSans.ttf");
		for (int mTV : tvs) {
			setFontsOnTextViews(mTV);
		}
		ImageView iv = (ImageView) findViewById(R.id.imageShuttle);
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		iv.setImageBitmap(ImageHelper.decodeSampledBitmapFromResource(
				getResources(), R.drawable.img_leading_shuttle,
				displaymetrics.widthPixels, 150));

		ImageButton selectCircuit = (ImageButton) findViewById(R.id.buttonSelectCircuit);
		selectCircuit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showCircuitSelector = new ShowCircuitSelector();
				showCircuitSelector.show(getFragmentManager(), Constants.APP_NAME);
			}
		});

		ImageButton selectTime = (ImageButton) findViewById(R.id.buttonSelectTime);
		selectTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showTimeSelector = new ShowTimeSelector();
				showTimeSelector.show(getFragmentManager(), Constants.APP_NAME);
			}
		});

		ImageButton selectStop = (ImageButton) findViewById(R.id.buttonSelectStop);
		selectStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showStopSelector = new ShowStopSelector();
				showStopSelector.show(getFragmentManager(), Constants.APP_NAME);
			}
		});

		String s = Constants.shuttleCircuits[0];
		setCircuitName(s);
		readShuttleFromJSON(s);
		String t = circuit.getTimes().get(0);
		circuit.setSelectedTime(t);
		circuit.setSelectedStop(circuit.getStops(t).get(0));
		setFromWhenText();
		readShuttleSchedule();
	}

	@Override
	protected void onStop() {
		super.onStop();

		destroyDialogFragment(showCircuitSelector);
		destroyDialogFragment(showTimeSelector);
		destroyDialogFragment(showStopSelector);

		showCircuitSelector = null;
		showTimeSelector = null;
		showStopSelector = null;
	}

	private void destroyDialogFragment(@Nullable DialogFragment fragment) {
		if (fragment != null && fragment.isVisible()) {
			fragment.dismiss();
		}
	}

	public class ShowCircuitSelector extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Select circuit").setItems(
					Constants.shuttleCircuits,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							String s = Constants.shuttleCircuits[which];
							setCircuitName(s);
							readShuttleFromJSON(s);
							String t = circuit.getTimes().get(0);
							circuit.setSelectedTime(t);
							circuit.setSelectedStop(circuit.getStops(t).get(0));
							setFromWhenText();
							readShuttleSchedule();
						}
					});
			return builder.create();
		}
	}

	public class ShowTimeSelector extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			final String[] arr = new String[circuit.getTimes().size()];
			builder.setTitle("Select time").setItems(
					circuit.getTimes().toArray(arr),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							circuit.setSelectedTime(circuit.getTimes()
									.toArray(arr)[which]);
							setFromWhenText();
							readShuttleSchedule();
						}
					});
			return builder.create();
		}
	}

	public class ShowStopSelector extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			final String time = circuit.getSelectedTime();
			final String[] arr = new String[circuit.getStops(time).size()];
			builder.setTitle("Select stop").setItems(
					circuit.getStops(time).toArray(arr),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							circuit.setSelectedStop(circuit.getStops(time)
									.toArray(arr)[which]);
							setFromWhenText();
							readShuttleSchedule();
						}
					});
			return builder.create();
		}
	}

	private void readShuttleFromJSON(String c) {
		JSONParser jsonParser;
		try {
			InputStream is = getAssets().open(Constants.JSONCERNShuttle);
			jsonParser = new JSONParser(is);
			circuit = jsonParser.readShuttle(c);
		} catch (IOException e) {
			Log.e(Constants.TAG, e.getMessage(), e);
		}
	}

	private void readShuttleSchedule() {
		ListView mShuttleList = (ListView) findViewById(R.id.listShuttles);
		try {
			InputStream is = getAssets().open(Constants.JSONCERNShuttle);
			JSONParser jsonParser = new JSONParser(is);
			String[] s = jsonParser.readShuttleSchedule(circuit);
			ShuttleAdapter customAdapter = new ShuttleAdapter(
					getApplicationContext(), s);
			mShuttleList.setAdapter(customAdapter);
		} catch (IOException e) {
			Log.e(Constants.TAG, e.getMessage(), e);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		actionBarDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		actionBarDrawerToggle.syncState();
	}

	private void setFontsOnTextViews(int arg) {
		TextView tv = (TextView) findViewById(arg);
		tv.setTypeface(typeface);
	}

	private void setFromWhenText() {
		final TextView mSubTitle = (TextView) findViewById(R.id.textSubTitle);

		mSubTitle.setText("Transport in the " + circuit.getSelectedTime()
				+ " from " + circuit.getSelectedStop());
	}

	private void setCircuitName(String s) {
		final TextView mCircuitName = (TextView) findViewById(R.id.circuitName);
		mCircuitName.setText(s);
	}
}

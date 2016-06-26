package ch.cern.maps;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import ch.cern.maps.adapters.NavigationAdapter;
import ch.cern.maps.adapters.PhonebookAdapter;
import ch.cern.maps.models.Person;
import ch.cern.maps.services.CheckConnection;
import ch.cern.maps.utils.Constants;
import ch.cern.maps.utils.GetContentByURL;
import ch.cern.maps.utils.JSONParser;
import ch.cern.www.R;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class PhonebookActivity extends Activity {

	private ActionBarDrawerToggle actionBarDrawerToggle;
	private boolean isInternet;
	private int[] tvs = { R.id.action_bar_title, R.id.editPersonSearch,
			R.id.textViewNoInternet };
	private Handler uCantHandleThat = new Handler();
	private PhonebookReceiver phonebookReceiver;
	private Typeface typeface;

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

		// Customization
		final Drawable actionBarColor = getResources().getDrawable(
				R.drawable.top_lines);
		actionBar.setBackgroundDrawable(actionBarColor);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		LinearLayout parentLayout = (LinearLayout) findViewById(R.id.content_frame);
		View childLayout = inflater.inflate(R.layout.phonebook_activity,
				null); // Will null work here?
		parentLayout.addView(childLayout);

		TextView tv = (TextView) findViewById(R.id.action_bar_title);
		tv.setText(getResources().getString(R.string.phonebook));
		LinearLayout ll = (LinearLayout) findViewById(R.id.searchLayout);
		ll.setVisibility(View.INVISIBLE);
		typeface = Typeface.createFromAsset(getAssets(), "DroidSans.ttf");
		for (int mTV : tvs) {
			setFontsOnTextViews(mTV);
		}

		ImageView iv = (ImageView) findViewById(R.id.phonebookimg);
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		/*iv.setImageBitmap(ImageHelper.decodeSampledBitmapFromResource(
				getResources(), R.drawable.cern, displaymetrics.widthPixels,
				150));
				TODO: Find cern drawable resource
				*/

		final TextView editTextSearch = (TextView) findViewById(R.id.editPersonSearch);
		editTextSearch.setOnKeyListener(new OnKeyListener()
		{
			@Override
		    public boolean onKey(View v, int keyCode, KeyEvent event)
		    {
		        if (event.getAction() == KeyEvent.ACTION_DOWN)
		        {
		            switch (keyCode)
		            {
		                case KeyEvent.KEYCODE_DPAD_CENTER:
		                case KeyEvent.KEYCODE_ENTER:
		                	startQuery();
		                    return true;
		                default:
		                    break;
		            }
		        }
		        return false;
		    }
		});

		final ImageButton imageButtonPhonebook = (ImageButton) findViewById(R.id.imageButtonSearchPhonebook);
		imageButtonPhonebook.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startQuery();
			}
		});
	}

	protected void startQuery() {
		final TextView editTextSearch = (TextView) findViewById(R.id.editPersonSearch);
		final ProgressBar loading = (ProgressBar) findViewById(R.id.progressBarPhonebook);

		if (editTextSearch.getText() == null || editTextSearch.getText().equals("")
				|| editTextSearch.getText().toString().equals("")) {
			ToastResult(getString(R.string.OnEmptySearch));
		} else {
			if (isInternet) {
				loading.setVisibility(View.VISIBLE);
				uCantHandleThat.postDelayed(runForYourLife,
						Constants.LocateMeThreshold);
				new GetContentByURL(getApplicationContext())
						.execute(editTextSearch.getText().toString());
			}
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

	private class PhonebookReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent mReceivedIntent) {
			final ProgressBar loading = (ProgressBar) findViewById(R.id.progressBarPhonebook);
			final ListView phonebookList = (ListView) findViewById(R.id.people_list);

			loading.setVisibility(View.INVISIBLE);
			uCantHandleThat.removeCallbacks(runForYourLife);
			if (mReceivedIntent.getAction()
					.equals(Constants.PhonebookActionTag)) {
				String mt = mReceivedIntent
						.getStringExtra(Constants.PhonebookResponse);
				if (mt != null) {
					JSONParser jsonParser = new JSONParser(
							new ByteArrayInputStream(mt.getBytes()));
					ArrayList<Person> p = jsonParser.readPhonebook();
					if (p.size() == 0) {
						ToastResult("No results");
					}
					PhonebookAdapter customAdapter = new PhonebookAdapter(
							getApplicationContext(), p);
					phonebookList.setAdapter(customAdapter);
				} else {
					isInternetAvailable();
				}
			}
			if (mReceivedIntent.getAction().equals(
					Constants.InternetConnectionActionTag)) {
				isInternet = mReceivedIntent.getBooleanExtra(
						Constants.InternetConnectionStatus, false);
				hideLayouts(isInternet);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		phonebookReceiver = new PhonebookReceiver();
		IntentFilter intentPhonebookFilter = new IntentFilter();
		intentPhonebookFilter.addAction(Constants.PhonebookActionTag);
		intentPhonebookFilter.addAction(Constants.InternetConnectionActionTag);
		registerReceiver(phonebookReceiver, intentPhonebookFilter);
		isInternetAvailable();
	}

	public void hideLayouts(Boolean isInternet) {
		final ProgressBar loading = (ProgressBar) findViewById(R.id.progressBarPhonebook);
		final RelativeLayout noConnection = (RelativeLayout) findViewById(R.id.layoutNoConnection);
		final LinearLayout isConnection = (LinearLayout) findViewById(R.id.layoutSearch);
		final ListView phonebookList = (ListView) findViewById(R.id.people_list);

		if (isInternet) {
			uCantHandleThat.removeCallbacks(checkConnection);
			loading.setVisibility(View.INVISIBLE);
			noConnection.setVisibility(View.INVISIBLE);
			isConnection.setVisibility(View.VISIBLE);
			phonebookList.setVisibility(View.VISIBLE);
		} else {
			phonebookList.setVisibility(View.INVISIBLE);
			isConnection.setVisibility(View.INVISIBLE);
			loading.setVisibility(View.VISIBLE);
			noConnection.setVisibility(View.VISIBLE);
			uCantHandleThat.postDelayed(checkConnection,
					Constants.LocateMeThreshold);
		}
	}

	private Runnable runForYourLife = new Runnable() {
		public void run() {
			final ProgressBar loading = (ProgressBar) findViewById(R.id.progressBarPhonebook);
			loading.setVisibility(View.INVISIBLE);
			ToastResult(getString(R.string.UnsuccessfulPhonebook));
		}
	};

	private Runnable checkConnection = new Runnable() {
		public void run() {
			uCantHandleThat.removeCallbacks(checkConnection);
			isInternetAvailable();
			uCantHandleThat.postDelayed(checkConnection, Constants.LocateMeThreshold);
		}
	};

	@Override
	protected void onPause() {
		unregisterReceiver(phonebookReceiver);
		super.onPause();
	}

	private void ToastResult(String s) {
		Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
	}

	public void isInternetAvailable() {
		new CheckConnection(getApplicationContext()).execute(Constants.PingURLForConnection);
	}
}

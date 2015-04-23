package ch.cern.maps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import ch.cern.maps.adapters.NavigationAdapter;
import ch.cern.maps.geo.LocationService;
import ch.cern.maps.geo.OrientationService;
import ch.cern.maps.models.Building;
import ch.cern.maps.models.Trams;
import ch.cern.maps.services.*;
import ch.cern.maps.utils.*;
import ch.cern.www.R;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.webkit.WebView.PictureListener;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Picture;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

@SuppressWarnings("deprecation")
public class StartActivity extends Activity {

	private ActionBarDrawerToggle actionBarDrawerToggle;
	private boolean mProviders = false;
	private double mLatitude, mLongitude, mAccuracy, mAzimuth;
	private double[] GPSPixel = { 0, 0 }, mPositionPixel = { 0, 0 },
			initialScroll = { 2641, 4833 };
	private GenerateHTMLContent getHTML;
	private Dialog myDialog;
	private Handler uCantHandleThat = new Handler();
	private Intent mIntentOrientation, mIntentLocation;
	private ImageButton imageButtonLocateMe, imageButtonSearch,
			imageButtonMapType;
	private ProgressBar progressBar;
	private MapScroller myMapScroll;
	private SensorsReceiver mStateReceiver;
	private MapTypeReceiver mMapTypeReceiver;
	private SharedPreferences mPreferences;
	private String t18, tY1, tY2;
	private TextView editTextSearch;
	private Typeface mTypeface;
	private WebView webView;

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

		// You customizationaction_bar
		TextView tv = (TextView) findViewById(R.id.action_bar_title);
		tv.setText(getResources().getString(R.string.none));

		final Drawable actionBarColor = getResources().getDrawable(
				R.drawable.top_lines);
		actionBar.setBackgroundDrawable(actionBarColor);

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);

		/*
		 * View childLayout = inflater.inflate(R.layout.header_bar, (ViewGroup)
		 * findViewById(R.layout.header_bar));
		 */
		LinearLayout parentLayout = (LinearLayout) findViewById(R.id.content_frame);
		// parentLayout.addView(childLayout);
		View childLayout = inflater.inflate(R.layout.start_map,
				(ViewGroup) findViewById(R.layout.start_map));
		parentLayout.addView(childLayout);

		// Initiate activity elements
		mTypeface = Typeface.createFromAsset(this.getAssets(), "DroidSans.ttf");
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		webView = (WebView) findViewById(R.id.mapWebView);
		editTextSearch = (TextView) findViewById(R.id.editTextSearch);
		editTextSearch.setTypeface(mTypeface);

		// Initiate intents
		mIntentOrientation = new Intent(getApplicationContext(),
				OrientationService.class);
		mIntentLocation = new Intent(getApplicationContext(),
				LocationService.class);

		// Initiate shared preferences
		mPreferences = getApplicationContext().getSharedPreferences(
				Constants.SharedPreferences, 0);

		initializeVariables();

		// Setup
		setWebView(mPreferences.getString(Constants.MapType, ""), 0, 0);
		setLocateMeFuction();
		setSearchBuilding();
		setMapTypeSelector();
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		editTextSearch.setWidth(displaymetrics.widthPixels);
	}

	private void setMapTypeSelector() {
		imageButtonMapType = (ImageButton) findViewById(R.id.imageButtonMapType);
		imageButtonMapType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialogBox(1);
			}
		});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		actionBarDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// call ActionBarDrawerToggle.onOptionsItemSelected(), if it returns
		// true
		// then it has handled the app icon touch event
		if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		actionBarDrawerToggle.syncState();
	}

	/*
	 * Initialize location variables. Also used when provider disappears
	 */

	private void initializeVariables() {
		mLatitude = 0;
		mLongitude = 0;
		mAccuracy = 0;
		mAzimuth = 0;
		mPositionPixel[0] = 0;
		mPositionPixel[1] = 0;
	}

	private void setSearchBuilding() {
		imageButtonSearch = (ImageButton) findViewById(R.id.imageButtonSearch);
		imageButtonSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchForMe();
			}
		});
	}

	/*
	 * Gets position of the building and converts to proper scroll coordinates
	 */
	protected void searchForMe() {
		if (editTextSearch.getText().toString().equals(null)
				|| editTextSearch.getText().toString().equals("")) {
			ToastResult(getString(R.string.OnEmptySearch));
		} else {
			double[] search = getPositionOfTheBuildingFromJSON(editTextSearch
					.getText().toString());

			if (search == null) {
				ToastResult(getString(R.string.NoSearchResults) + " "
						+ editTextSearch.getText());
			} else if (search[1] < Constants.MAP_WEST
					|| search[1] > Constants.MAP_EAST
					|| search[0] > Constants.MAP_NORTH
					|| search[0] < Constants.MAP_SOUTH) {
				ToastResult(getString(R.string.MapToSmall)
						+ editTextSearch.getText());
				GPSPixel[0] = 0;
				GPSPixel[1] = 0;
			} else {

				progressBar.setVisibility(View.VISIBLE);
				GPSPixel = Utils.getPixel(search[1], search[0]);
				onShowGPS((int) GPSPixel[0], (int) GPSPixel[1],
						webView.getScale());
				scrollMe(GPSPixel);
				progressBar.setVisibility(View.INVISIBLE);
			}
		}
	}

	private double[] getPositionOfTheBuildingFromJSON(String userInput) {
		try {
			JSONParser jsonParser = new JSONParser(getAssets().open(
					Constants.JSON_BUILDINGS));
			Building b = jsonParser.readBuildingCoordinants(userInput
					.toLowerCase(Locale.getDefault()));
			if (b != null) {
				double[] returnCoordinants = { b.getNS(), b.getWE() };
				return returnCoordinants;
			}
		} catch (IOException e) {
			Log.e(Constants.TAG, e.getMessage());
		}
		return null;
	}

	public void showDialogBox(int i) {
		myDialog = new Dialog(this);
		DialogBox customDialogBox = new DialogBox(this);
		switch (i) {
		case 1:
			myDialog = customDialogBox.startInfoBox(mTypeface);
			break;
		case 2:
			progressBar.setVisibility(View.VISIBLE);
			readMyTramsFromJSON();
			myDialog = customDialogBox.startTramBox(t18, tY1, tY2);
			progressBar.setVisibility(View.INVISIBLE);
			break;
		default:
			break;
		}
		myDialog.show();
	}

	private void setLocateMeFuction() {
		imageButtonLocateMe = (ImageButton) findViewById(R.id.imageButtonLocateMe);

		imageButtonLocateMe.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					imageButtonLocateMe
							.setImageResource(R.drawable.ic_action_locate);
					imageButtonLocateMe
							.setBackgroundResource(R.drawable.locatemebg);
					return false;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					imageButtonLocateMe
							.setImageResource(R.drawable.ic_action_locate);
					imageButtonLocateMe.setBackgroundColor(getResources()
							.getColor(R.color.trans));
					return false;
				}
				return false;
			}
		});

		imageButtonLocateMe.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				progressBar.setVisibility(View.VISIBLE);
				ToastResult(getString(R.string.WaitForLocation));
				setLocateMe();
				uCantHandleThat.postDelayed(runForYourLife,
						Constants.LocateMeTreshold);
			}
		});
	}

	private void setLocateMe() {
		startService(mIntentLocation);
		startService(mIntentOrientation);
	}

	protected void processGPS(double longitude, double latitude, double accuracy) {
		mPositionPixel = Utils.getPixel(longitude, latitude);
		if (mPositionPixel[1] < 0 || mPositionPixel[1] > Constants.MAP_HEIGHT
				|| mPositionPixel[0] > Constants.MAP_WIDTH
				|| mPositionPixel[0] < 0) {
			ToastResult(getString(R.string.OutOfBound));
			if (isMyServiceRunning(LocationService.class.getName())) {
				stopService(mIntentLocation);
			}
			if (isMyServiceRunning(OrientationService.class.getName())) {
				stopService(mIntentOrientation);
			}
			initializeVariables();
			onLocationGone();
		} else {
			progressBar.setVisibility(View.VISIBLE);
			mAccuracy = accuracy * Constants.MeterToPixelRatio;
			onShowPos((int) mPositionPixel[0], (int) mPositionPixel[1],
					(int) mAccuracy, webView.getScale());
			scrollMe(mPositionPixel);
			progressBar.setVisibility(View.INVISIBLE);
		}
	}

	private void scrollMe(double[] initialScroll) {
		double[] mMap = { webView.getMeasuredWidth() / webView.getScale(),
				webView.getMeasuredHeight() / webView.getScale() };
		myMapScroll = new MapScroller(initialScroll, mMap);
		onScrollPage((int) myMapScroll.getScroll()[0],
				(int) myMapScroll.getScroll()[1]);
	}

	@SuppressLint({ "SetJavaScriptEnabled" })
	private void setWebView(String mMapType, double f, double g) {
		if (f != 0 && g != 0) {
			initialScroll[0] = f;
			initialScroll[1] = g;
		}

		getHTML = new GenerateHTMLContent(mMapType);
		webView.loadDataWithBaseURL("file:///android_asset/images/",
				getHTML.setHtml(), "text/html", "utf-8", null);

		webView.getSettings().setJavaScriptEnabled(true);

		webView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (GPSPixel[0] != 0) {
					onShowGPS((int) GPSPixel[0], (int) GPSPixel[1],
							webView.getScale());
				}
				if (mPositionPixel[0] != 0) {
					onShowPos((int) mPositionPixel[0], (int) mPositionPixel[1],
							(int) mAccuracy, webView.getScale());
				}
				return false;
			}
		});

		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				progressBar.setVisibility(View.INVISIBLE);
				// Those might be required later
				webView.getSettings().setUseWideViewPort(true);
				webView.getSettings().setLoadWithOverviewMode(true);
				webView.getSettings().setSupportZoom(true);
				webView.getSettings().setBuiltInZoomControls(true);
				webView.getSettings().setUseWideViewPort(true);
				webView.setScrollbarFadingEnabled(true);
				webView.getSettings().setDisplayZoomControls(false);
				webView.setPadding(0, 0, 0, 0);
			}
		});

		// When content is loaded, scroll map to desired location
		webView.setPictureListener(new PictureListener() {
			@Override
			public void onNewPicture(WebView view, Picture picture) {
				progressBar.setVisibility(View.INVISIBLE);
				webView.getSettings().setSupportZoom(true);
				webView.getSettings().setBuiltInZoomControls(true);
				// Let us scroll to Main Building
				scrollMe(initialScroll);
				webView.setPictureListener(null);
			}
		});
	}

	private void readMyTramsFromJSON() {
		JSONParser jsonParser;
		try {
			ArrayList<Trams> trams18 = new ArrayList<Trams>();
			ArrayList<Trams> tramsY1 = new ArrayList<Trams>();
			ArrayList<Trams> tramsY2 = new ArrayList<Trams>();
			InputStream is = getAssets().open(Constants.JSONTram);
			jsonParser = new JSONParser(is);
			for (Iterator<Trams> i = jsonParser.readSchedule().iterator(); i
					.hasNext();) {
				Trams tram = (Trams) i.next();
				if (tram.getLine().equals("18")) {
					trams18.add(tram);
				} else if (tram.getLine().equals("Y1")) {
					tramsY1.add(tram);
				} else if (tram.getLine().equals("Y2")) {
					tramsY2.add(tram);
				}
			}
			t18 = Utils.getNextTrains(trams18);
			tY1 = Utils.getNextTrains(tramsY1);
			tY2 = Utils.getNextTrains(tramsY2);

		} catch (IOException e) {
			Log.e(Constants.TAG, e.getMessage());
		}

	}

	private void onScrollPage(int mLeft, int mTop) {
		webView.loadUrl("javascript:pageScroll(" + mLeft + ", " + mTop + ")");
	}

	private void onShowGPS(int mLeft, int mTop, double mScale) {
		webView.loadUrl("javascript:mGPSPosition(" + mLeft + ", " + mTop + ", "
				+ mScale + ")");
	}

	private void onShowPos(int mLeft, int mTop, int mRadius, double mScale) {
		webView.loadUrl("javascript:mPosPosition(" + mLeft + ", " + mTop + ", "
				+ mRadius + ", " + mScale + ")");
	}

	private void onLocationGone() {
		webView.loadUrl("javascript:mHidePos()");
	}

	private void onAzimuthChange(double d) {
		webView.loadUrl("javascript:mRotationPos(" + (int) d + ")");
	}

	private void setAzimuth(double mAzimuth) {
		this.mAzimuth = mAzimuth;
	}

	private class SensorsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent mReceivedIntent) {

			if (mReceivedIntent.getAction().equals(Constants.LocationActionTag)) {
				mAccuracy = mReceivedIntent.getFloatExtra(
						Constants.LocationFlagAccuracy, 0);
				mLongitude = mReceivedIntent.getDoubleExtra(
						Constants.LocationFlagLongitude, 0);
				mLatitude = mReceivedIntent.getDoubleExtra(
						Constants.LocationFlagLatitude, 0);

				if (mLatitude != 0 && mLatitude != 0) {
					uCantHandleThat.removeCallbacks(runForYourLife);
					progressBar.setVisibility(View.INVISIBLE);
					processGPS(mLongitude, mLatitude, mAccuracy);
				}
			}

			if (mReceivedIntent.getAction().equals(
					Constants.OrientationActionTag)) {
				setAzimuth(mReceivedIntent.getDoubleExtra(
						Constants.OrientationFlagAzimuth, 0));
				onAzimuthChange(mAzimuth);
			}

			if (mReceivedIntent.getAction()
					.equals(Constants.ProvidersActionTag)) {
				mProviders = mReceivedIntent.getBooleanExtra(
						Constants.GPSProvider, false)
						|| mReceivedIntent.getBooleanExtra(
								Constants.NetworkProvider, false);

				if (!mProviders) {
					if (!mPreferences.getBoolean(Constants.GPSRequest, false)) {
						buildAlertMessage(
								getString(R.string.GPSOff),
								android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS,
								Constants.GPSRequest);
					}
				}
			}
		}
	}

	private class MapTypeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent mReceivedIntent) {
			if (mReceivedIntent.getAction().equals(Constants.MapTypeActionTag)) {
				String mt = mReceivedIntent.getStringExtra(Constants.MapType);

				myDialog.cancel();
				mPreferences = getApplicationContext().getSharedPreferences(
						Constants.SharedPreferences, 0);
				Editor mEditor = mPreferences.edit();
				mEditor.putString(Constants.MapType, mt);
				mEditor.commit();
				double x = mPositionPixel[0];
				double y = mPositionPixel[1];
				if (x == 0 && y == 0) {
					x = GPSPixel[0];
					y = GPSPixel[1];
				}
				if (x == 0 && y == 0) {
					x = 2641;
					y = 4833;
				}
				setWebView(mt, x, y);
			}
		}
	}

	private Runnable runForYourLife = new Runnable() {
		public void run() {
			progressBar.setVisibility(View.INVISIBLE);
			ToastResult(getString(R.string.UnsuccessfulLocatization));
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		mStateReceiver = new SensorsReceiver();
		IntentFilter intentLocationFilter = new IntentFilter();
		intentLocationFilter.addAction(Constants.LocationActionTag);
		intentLocationFilter.addAction(Constants.OrientationActionTag);
		intentLocationFilter.addAction(Constants.ProvidersActionTag);
		registerReceiver(mStateReceiver, intentLocationFilter);
		mMapTypeReceiver = new MapTypeReceiver();
		IntentFilter intentTypeFilter = new IntentFilter();
		intentTypeFilter.addAction(Constants.MapTypeActionTag);
		registerReceiver(mMapTypeReceiver, intentTypeFilter);
		initializeVariables();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause() Stop services and unregister
	 * Broadcast Receiver
	 */
	@Override
	protected void onPause() {
		if (isMyServiceRunning(LocationService.class.getName())) {
			stopService(mIntentLocation);
		}
		if (isMyServiceRunning(OrientationService.class.getName())) {
			stopService(mIntentOrientation);
		}
		unregisterReceiver(mStateReceiver);
		unregisterReceiver(mMapTypeReceiver);
		super.onPause();
	}

	/*
	 * Loop through the running services and check if the input one is running
	 */
	private boolean isMyServiceRunning(String s) {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if (s.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	private void buildAlertMessage(String mMessage, final String mIntent,
			final String mKey) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
				.setMessage(mMessage)
				.setCancelable(false)
				.setPositiveButton(getString(R.string.enable),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent callSettingIntent = new Intent(mIntent);
								startActivity(callSettingIntent);
							}
						});
		alertDialogBuilder.setNeutralButton(getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		alertDialogBuilder.setNegativeButton(getString(R.string.stopasking),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						SharedPreferences mPreferences = getApplicationContext()
								.getSharedPreferences(
										Constants.SharedPreferences, 0);
						Editor mEditor = mPreferences.edit();
						mEditor.putBoolean(mKey, true);
						mEditor.commit();
					}
				});
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}

	private void ToastResult(String s) {
		Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
	}
}
package ch.cern.maps;

import java.io.IOException;
import java.util.Locale;

import ch.cern.maps.adapters.NavigationAdapter;
import ch.cern.maps.geo.LocationService;
import ch.cern.maps.geo.OrientationService;
import ch.cern.maps.models.Building;
import ch.cern.maps.services.*;
import ch.cern.maps.utils.*;
import ch.cern.www.R;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
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

public class StartActivity extends Activity {

	private ActionBarDrawerToggle actionBarDrawerToggle;
	private double latitude, longitude, accuracy, azimuth;
	private double[] GPSPixel = { 0, 0 }, positionPixel = { 0, 0 },
			initialScroll = { 2641, 4833 };
	private Dialog mapSelectorDialog;
	private Handler uCantHandleThat = new Handler();
	private Intent intentOrientation, intentLocation;
	private SensorsReceiver stateReceiver;
	private MapTypeReceiver mapTypeReceiver;
	private SharedPreferences preferences;
	private Typeface typeface;
	private static final int LOCATION_REQUEST = 1241;

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

		// You customizationaction_bar
		TextView tv = (TextView) findViewById(R.id.action_bar_title);
		tv.setText(getResources().getString(R.string.nool));

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
		View childLayout = inflater.inflate(R.layout.map_activity,
				(ViewGroup) findViewById(R.id.map));
		parentLayout.addView(childLayout);

		// Initiate activity elements
		typeface = Typeface.createFromAsset(this.getAssets(), "DroidSans.ttf");
		final WebView webView = (WebView) findViewById(R.id.mapWebView);

		webView.setWebChromeClient(new WebChromeClient() {
			public boolean onConsoleMessage(ConsoleMessage cm) {
				Log.d("Web console: ", cm.message() + " -- From line "
						+ cm.lineNumber() + " of "
						+ cm.sourceId());
				return true;
			}
		});

		final TextView editTextSearch = (TextView) findViewById(R.id.editTextSearch);
		editTextSearch.setTypeface(typeface);

		// Initiate intents
		intentOrientation = new Intent(getApplicationContext(),
				OrientationService.class);
		intentLocation = new Intent(getApplicationContext(),
				LocationService.class);

		// Initiate shared preferences
		preferences = getApplicationContext().getSharedPreferences(
				Constants.SharedPreferences, 0);

		initializeVariables();

		// Setup

		Intent i = getIntent();
		String mOffice = i.getStringExtra(Constants.ScrollToTag);
		if (mOffice != null && !mOffice.equals("")) {
			mOffice = mOffice.split("-")[0];
			setWebView(preferences.getString(Constants.MapType, ""), -1, -1, mOffice);
		} else {
			setWebView(preferences.getString(Constants.MapType, ""), 0, 0, null);
		}
		setLocateMeFuction();
		setSearchBuilding();
		setMapTypeSelector();
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		editTextSearch.setWidth(displaymetrics.widthPixels);
	}

	private void setMapTypeSelector() {
		final ImageButton imageButtonMapType = (ImageButton) findViewById(R.id.imageButtonMapType);
		imageButtonMapType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialogBox();
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
		return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
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
		latitude = 0;
		longitude = 0;
		accuracy = 0;
		azimuth = 0;
		positionPixel[0] = 0;
		positionPixel[1] = 0;
	}

	private void setSearchBuilding() {
		final ImageButton imageButtonSearch = (ImageButton) findViewById(R.id.imageButtonSearch);
		imageButtonSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchBuilding();
			}
		});
	}

	private void searchBuilding() {
		final TextView editTextSearch = (TextView) findViewById(R.id.editTextSearch);
		searchForMe(editTextSearch.getText().toString());
	}

	/*
	 * Gets position of the building and converts to proper scroll coordinates
	 */
	protected void searchForMe(String s) {
		if (s == null || s.equals("")) {
			ToastResult(getString(R.string.OnEmptySearch));
		} else {
			double[] search = getPositionOfTheBuildingFromJSON(s);
			if (search == null) {
				ToastResult(getString(R.string.NoSearchResults) + " " + s);
			} else if (search[1] < Constants.MAP_WEST
					|| search[1] > Constants.MAP_EAST
					|| search[0] > Constants.MAP_NORTH
					|| search[0] < Constants.MAP_SOUTH) {
				ToastResult(getString(R.string.MapToSmall) + s);
				GPSPixel[0] = 0;
				GPSPixel[1] = 0;
			} else {
				final WebView webView = (WebView) findViewById(R.id.mapWebView);
				final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

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
				return new double[]{ b.getNS(), b.getWE() };
			}
		} catch (IOException e) {
			Log.e(Constants.TAG, e.getMessage(), e);
		}
		return null;
	}

	public void showDialogBox() {
		mapSelectorDialog = new Dialog(this);
		DialogBox customDialogBox = new DialogBox(this);
		mapSelectorDialog = customDialogBox.startInfoBox(typeface);
		mapSelectorDialog.show();
	}

	private void setLocateMeFuction() {
		final ImageButton imageButtonLocateMe = (ImageButton) findViewById(R.id.imageButtonLocateMe);
		imageButtonLocateMe.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
				progressBar.setVisibility(View.VISIBLE);
				ToastResult(getString(R.string.WaitForLocation));
				setLocateMe();
				uCantHandleThat.postDelayed(runForYourLife,
						Constants.LocateMeThreshold);
			}
		});
	}

	private void setLocateMe() {
		if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.ACCESS_COARSE_LOCATION }, LOCATION_REQUEST );
		} else {
			startService(intentLocation);
			startService(intentOrientation);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (requestCode == LOCATION_REQUEST) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				startService(intentLocation);
				startService(intentOrientation);
			} else {
				ToastResult(getString(R.string.location_permission_denied_result));
				final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
				progressBar.setVisibility(View.INVISIBLE);
			}
		}
	}

	protected void processGPS(double longitude, double latitude, double accuracy) {
		positionPixel = Utils.getPixel(longitude, latitude);
		if (positionPixel[1] < 0 || positionPixel[1] > Constants.MAP_HEIGHT
				|| positionPixel[0] > Constants.MAP_WIDTH
				|| positionPixel[0] < 0) {
			ToastResult(getString(R.string.OutOfBound));
			if (isMyServiceRunning(LocationService.class.getName())) {
				stopService(intentLocation);
			}
			if (isMyServiceRunning(OrientationService.class.getName())) {
				stopService(intentOrientation);
			}
			initializeVariables();
			onLocationGone();
		} else {
			final WebView webView = (WebView) findViewById(R.id.mapWebView);
			final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

			progressBar.setVisibility(View.VISIBLE);
			this.accuracy = accuracy * Constants.MeterToPixelRatio;
			onShowPos((int) positionPixel[0], (int) positionPixel[1],
					(int) this.accuracy, webView.getScale());
			scrollMe(positionPixel);
			progressBar.setVisibility(View.INVISIBLE);
		}
	}

	private void scrollMe(double[] initialScroll) {
		final WebView webView = (WebView) findViewById(R.id.mapWebView);
		final double[] mMap = { webView.getMeasuredWidth() / webView.getScale(),
				webView.getMeasuredHeight() / webView.getScale() };

		final MapScroller myMapScroll = new MapScroller(initialScroll, mMap);
		onScrollPage((int) myMapScroll.getScroll()[0],
				(int) myMapScroll.getScroll()[1]);
	}

	@SuppressLint({ "SetJavaScriptEnabled" })
	private void setWebView(String mMapType, double f, double g, final String s) {
		Log.e("TAG", "main");
		if (f != 0 && g != 0) {
			initialScroll[0] = f;
			initialScroll[1] = g;
		}

		final WebView webView = (WebView) findViewById(R.id.mapWebView);

		webView.loadUrl("file:///android_asset/images/map.html?type=" + mMapType);
		webView.getSettings().setJavaScriptEnabled(true);

		webView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (GPSPixel[0] != 0) {
					onShowGPS((int) GPSPixel[0], (int) GPSPixel[1],
							webView.getScale());
				}
				if (positionPixel[0] != 0) {
					onShowPos((int) positionPixel[0], (int) positionPixel[1],
							(int) accuracy, webView.getScale());
				}
				return false;
			}
		});

		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
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
				final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
				progressBar.setVisibility(View.INVISIBLE);
				webView.getSettings().setSupportZoom(true);
				webView.getSettings().setBuiltInZoomControls(true);
				if (initialScroll[0] != -1) {
					// Let us scroll to Main Building
					scrollMe(initialScroll);
				} else {
					searchForMe(s);
				}
				webView.setPictureListener(null);
			}
		});
	}

	private void onScrollPage(int mLeft, int mTop) {
		final WebView webView = (WebView) findViewById(R.id.mapWebView);
		webView.loadUrl("javascript:pageScroll(" + mLeft + ", " + mTop + ")");
	}

	private void onShowGPS(int mLeft, int mTop, double mScale) {
		final WebView webView = (WebView) findViewById(R.id.mapWebView);
		webView.loadUrl("javascript:mGPSPosition(" + mLeft + ", " + mTop + ", "
				+ mScale + ")");
	}

	private void onShowPos(int mLeft, int mTop, int mRadius, double mScale) {
		final WebView webView = (WebView) findViewById(R.id.mapWebView);
		webView.loadUrl("javascript:mPosPosition(" + mLeft + ", " + mTop + ", "
				+ mRadius + ", " + mScale + ")");
	}

	private void onLocationGone() {
		final WebView webView = (WebView) findViewById(R.id.mapWebView);
		webView.loadUrl("javascript:mHidePos()");
	}

	private void onAzimuthChange(double d) {
		final WebView webView = (WebView) findViewById(R.id.mapWebView);
		webView.loadUrl("javascript:mRotationPos(" + (int) d + ")");
	}

	private void setAzimuth(double mAzimuth) {
		this.azimuth = mAzimuth;
	}

	private class SensorsReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent mReceivedIntent) {

			if (mReceivedIntent.getAction().equals(Constants.LocationActionTag)) {
				accuracy = mReceivedIntent.getFloatExtra(
						Constants.LocationFlagAccuracy, 0);
				longitude = mReceivedIntent.getDoubleExtra(
						Constants.LocationFlagLongitude, 0);
				latitude = mReceivedIntent.getDoubleExtra(
						Constants.LocationFlagLatitude, 0);

				if (latitude != 0 && longitude != 0) {
					final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
					uCantHandleThat.removeCallbacks(runForYourLife);
					progressBar.setVisibility(View.INVISIBLE);
					processGPS(longitude, latitude, accuracy);
				}
			}

			if (mReceivedIntent.getAction().equals(
					Constants.OrientationActionTag)) {
				setAzimuth(mReceivedIntent.getDoubleExtra(
						Constants.OrientationFlagAzimuth, 0));
				onAzimuthChange(azimuth);
			}

			if (mReceivedIntent.getAction()
					.equals(Constants.ProvidersActionTag)) {
				boolean mProviders = mReceivedIntent.getBooleanExtra(
						Constants.GPSProvider, false)
						|| mReceivedIntent.getBooleanExtra(
						Constants.NetworkProvider, false);

				if (!mProviders) {
					if (!preferences.getBoolean(Constants.GPSRequest, false)) {
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

				mapSelectorDialog.cancel();
				preferences = getApplicationContext().getSharedPreferences(
						Constants.SharedPreferences, 0);
				Editor mEditor = preferences.edit();
				mEditor.putString(Constants.MapType, mt);
				mEditor.apply();
				double x = positionPixel[0];
				double y = positionPixel[1];
				if (x == 0 && y == 0) {
					x = GPSPixel[0];
					y = GPSPixel[1];
				}
				if (x == 0 && y == 0) {
					x = 2641;
					y = 4833;
				}
				setWebView(mt, x, y, null);
			}
		}
	}

	private Runnable runForYourLife = new Runnable() {
		public void run() {
			final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
			progressBar.setVisibility(View.INVISIBLE);
			ToastResult(getString(R.string.UnsuccessfulLocatization));
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		stateReceiver = new SensorsReceiver();
		IntentFilter intentLocationFilter = new IntentFilter();
		intentLocationFilter.addAction(Constants.LocationActionTag);
		intentLocationFilter.addAction(Constants.OrientationActionTag);
		intentLocationFilter.addAction(Constants.ProvidersActionTag);
		registerReceiver(stateReceiver, intentLocationFilter);
		mapTypeReceiver = new MapTypeReceiver();
		IntentFilter intentTypeFilter = new IntentFilter();
		intentTypeFilter.addAction(Constants.MapTypeActionTag);
		registerReceiver(mapTypeReceiver, intentTypeFilter);
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
			stopService(intentLocation);
		}
		if (isMyServiceRunning(OrientationService.class.getName())) {
			stopService(intentOrientation);
		}
		unregisterReceiver(stateReceiver);
		unregisterReceiver(mapTypeReceiver);
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

	private void buildAlertMessage(final String mMessage, final String mIntent,
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
						mEditor.apply();
					}
				});
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}

	private void ToastResult(String s) {
		Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
	}
}
package ch.cern.maps;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import ch.cern.maps.geo.LocationService;
import ch.cern.maps.geo.OrientationService;
import ch.cern.maps.services.*;
import ch.cern.maps.utils.*;
import ch.cern.www.R;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.webkit.WebView.PictureListener;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomButtonsController;
import android.annotation.SuppressLint;
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
import android.graphics.Picture;

@SuppressWarnings("deprecation")
public class StartActivity extends Activity {

	private boolean mProviders = false;
	private double mLatitude, mLongitude, mAccuracy, mAzimuth;
	private GenerateHTMLContent getHTML;
	private Handler uCantHandleThat = new Handler();
	private Intent mIntentOrientation, mIntentLocation;
	private ImageButton imageButtonLocateMe, imageButtonInfo, imageButtonTrams,
			imageButtonSearch;
	private ProgressBar progressBar;
	private MapScroller myMapScroll;
	private SensorsReceiver mStateReceiver;
	private SharedPreferences mPreferences;
	private String t18, tY1, tY2;
	private TextView editTextSearch;
	private WebView webView;

	// TODO Place Accuracy::Location Testing

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Don't show title
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_start);

		// Initiate activity elements
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		webView = (WebView) findViewById(R.id.mapWebView);
		editTextSearch = (TextView) findViewById(R.id.editTextSearch);

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
		setWebView();
		setInfoBox();
		setLocateMeFuction();
		setSearchBuilding();
	}

	/*
	 * Initialize location variables. Also used when
	 */

	private void initializeVariables() {
		mLatitude = 0;
		mLongitude = 0;
		mAccuracy = 0;
		mAzimuth = 0;
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

	protected void searchForMe() {
		if (editTextSearch.getText().toString().equals(null)
				|| editTextSearch.getText().toString().equals("")) {
			ToastResult(getString(R.string.NoSearchResults));
		} else {
			double[] search = getPositionOfTheBuildingFromJSON(editTextSearch
					.getText().toString());
			if (search == null) {
				ToastResult(getString(R.string.OnEmptySearch)
						+ editTextSearch.getText());
			} else if (search[1] < Constants.MAP_WEST
					|| search[1] > Constants.MAP_EAST
					|| search[0] > Constants.MAP_NORTH
					|| search[0] < Constants.MAP_SOUTH) {
				ToastResult(getString(R.string.MapToSmall)
						+ editTextSearch.getText());
			} else {
				progressBar.setVisibility(View.VISIBLE);
				double[] locationPixel = Utils.getPixel(search[0], search[1]);
				onShowGPS((int) locationPixel[0], (int) locationPixel[1]);
				scrollMe(locationPixel);
				progressBar.setVisibility(View.INVISIBLE);
			}
		}
	}

	private double[] getPositionOfTheBuildingFromJSON(String userInput) {
		JSONParser jsonParser;
		try {
			InputStream is = getAssets().open("buildings.JSON");
			jsonParser = new JSONParser(is);
			for (Iterator<Building> i = jsonParser.readBuildingCoordinants()
					.iterator(); i.hasNext();) {
				Building b = (Building) i.next();
				if (b.getBuildingName().equals(userInput)) {
					double[] returnCoordinants = { b.getNS(), b.getWE() };
					return returnCoordinants;
				}
			}
		} catch (IOException e) {
			Log.e(Constants.TAG, "Error. " + e);
		}
		return null;
	}

	private void setInfoBox() {
		imageButtonInfo = (ImageButton) findViewById(R.id.buttonInfo);
		imageButtonInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialogBox(1);
			}
		});
		imageButtonTrams = (ImageButton) findViewById(R.id.buttonTrams);
		imageButtonTrams.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialogBox(2);
			}
		});

	}

	public void showDialogBox(int i) {
		Dialog myDialog = new Dialog(this);
		DialogBox customDialogBox = new DialogBox(this);
		switch (i) {
		case 1:
			myDialog = customDialogBox.startInfoBox();
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
		mStateReceiver = new SensorsReceiver();
		IntentFilter intentLocationFilter = new IntentFilter();
		intentLocationFilter.addAction(Constants.LocationActionTag);
		intentLocationFilter.addAction(Constants.OrientationActionTag);
		intentLocationFilter.addAction(Constants.ProvidersActionTag);
		registerReceiver(mStateReceiver, intentLocationFilter);
		startService(mIntentLocation);
		startService(mIntentOrientation);
	}

	protected void processGPS(double longitude, double latitude, double accuracy) {
		double[] locationPixel = Utils.getPixel(longitude, latitude);
		if (locationPixel[1] < 0 || locationPixel[1] > Constants.MAP_HEIGHT
				|| locationPixel[0] > Constants.MAP_WIDTH
				|| locationPixel[0] < 0) {
			ToastResult(getString(R.string.OutOfBound));
			if (isMyServiceRunning(LocationService.class.getName())) {
				stopService(mIntentLocation);
			}
			if (isMyServiceRunning(OrientationService.class.getName())) {
				stopService(mIntentOrientation);
			}
			unregisterReceiver(mStateReceiver);
			onLocationGone();
		} else {
			progressBar.setVisibility(View.VISIBLE);
			scrollMe(locationPixel);
		}
	}

	private void scrollMe(double[] initialScroll) {
		double[] mMap = { webView.getMeasuredWidth() / webView.getScale(),
				webView.getMeasuredHeight() / webView.getScale() };
		myMapScroll = new MapScroller(initialScroll, mMap);
		onScrollPage((int) myMapScroll.getScroll()[0],
				(int) myMapScroll.getScroll()[1]);
	}

	@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
	private void setWebView() {
		getHTML = new GenerateHTMLContent();
		webView.loadDataWithBaseURL("file:///android_asset/images/",
				getHTML.setHtml(80, 80, 20, 20), "text/html", "utf-8", null);
		webView.getSettings().setJavaScriptEnabled(true);
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
				webView.setPadding(0, 0, 0, 0);
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
					// Use the API 11+ calls to disable the controls
					new Runnable() {
						@SuppressLint("NewApi")
						public void run() {
							webView.getSettings().setDisplayZoomControls(false);
						}
					}.run();
				} else {
					ZoomButtonsController zoom_controll;
					try {
						zoom_controll = (ZoomButtonsController) webView
								.getClass()
								.getMethod("getZoomButtonsController")
								.invoke(webView, null);
						zoom_controll.getContainer().setVisibility(View.GONE);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		// When content is loaded, scroll map to desired location
		webView.setPictureListener(new PictureListener() {
			@Override
			public void onNewPicture(WebView view, Picture picture) {
				progressBar.setVisibility(View.INVISIBLE);
				// Let us scroll to Main Building
				double[] initialScroll = { 2473, 5658 };
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
			InputStream is = getAssets().open("trams.JSON");
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
			Log.i(Constants.TAG, e.getMessage());
		}

	}

	private void onScrollPage(int mLeft, int mTop) {
		webView.loadUrl("javascript:pageScroll(" + mLeft + ", " + mTop + ")");
	}

	private void onShowGPS(int mLeft, int mTop) {
		webView.loadUrl("javascript:mGPSPosition(" + mLeft + ", " + mTop + ")");
	}

	private void onLocationGone() {
		webView.loadUrl("javascript:mHidePos()");
	}

	private void onAzimuthChange(double d) {
		webView.loadUrl("javascript:mRotationPos(" + (int) d + ")");
	}

	private void onLocationChange(int mLeft, int mTop) {
		webView.loadUrl("javascript:mPosPosition(" + mLeft + ", " + mTop + ")");
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
					onLocationChange(100, 100);
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
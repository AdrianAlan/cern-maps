package ch.cern.maps;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

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
import android.app.Dialog;
import android.graphics.Picture;

@SuppressWarnings("deprecation")
public class StartActivity extends Activity {

	private TextView headerTitle, editTextSearch;
	private ImageButton imageButtonLocateMe, imageButtonInfo, imageButtonTrams,
			imageButtonSearch;
	private String t18, tY1, tY2;
	private WebView webView;
	private ProgressBar progressBar;
	private LocationService myLocation;
	private MapScroller myMapScroll;
	private Random diceRoller = new Random();
	private int[] myPosition = { -1, -1 };
	private int[] searchPosition = { -1, -1 };
	private int[] scrollPosition = { -1, -1 };
	private int currentScale = 150;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_start);

		initView();
		setInfoBox();
		setLocateMeFuction();
		setSearchBuilding();
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
			Toast.makeText(getApplicationContext(), "What? Type in something!",
					Toast.LENGTH_LONG).show();
		} else {
			double[] search = getPositionOfTheBuildingFromJSON(editTextSearch
					.getText().toString());
			if (search == null) {
				Toast.makeText(getApplicationContext(),
						"Can't find anything like " + editTextSearch.getText(),
						Toast.LENGTH_LONG).show();
			} else if (search[1] < Constants.MAP_WEST
					|| search[1] > Constants.MAP_EAST
					|| search[0] > Constants.MAP_NORTH
					|| search[0] < Constants.MAP_SOUTH) {
				Toast.makeText(getApplicationContext(),
						"Map to small to show " + editTextSearch.getText(),
						Toast.LENGTH_LONG).show();
			} else {
				progressBar.setVisibility(View.VISIBLE);
				double[] pixel = Utils.getPixel(search[0], search[1]);
				scrollPosition[0] = searchPosition[0] = (int) pixel[0];
				scrollPosition[1] = searchPosition[1] = (int) pixel[1];
				currentScale = (int) (100 * webView.getScale());
				setWebView();
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
			Log.i(Constants.TAG, e.getMessage());
		}
		return null;
	}

	private void initView() {
		headerTitle = (TextView) findViewById(R.id.barTitle);
		headerTitle.setTypeface(Utils.getTypeface(this,
				Constants.TYPEFONT_NEOSANS));
		headerTitle.setText(Constants.APP_NAME);
		editTextSearch = (TextView) findViewById(R.id.editTextSearch);
		editTextSearch.setTypeface(Utils.getTypeface(this,
				Constants.TYPEFONT_NEOSANS));
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		webView = (WebView) findViewById(R.id.mapWebView);
		setWebView();
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
					imageButtonLocateMe.setImageResource(R.drawable.location);
					imageButtonLocateMe
							.setBackgroundResource(R.drawable.locatemebg);
					return false;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					imageButtonLocateMe.setImageResource(R.drawable.location);
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
				getLocateMe();
			}
		});

		setLocateMe();
	}

	private void setLocateMe() {
		myLocation = new LocationService(this);
	}

	private void getLocateMe() {
		if (myLocation.getMyCurrentPosition()[0] == -1) {
			// No GPS enabled
			int notBoringToasts = diceRoller.nextInt(10) + 1;
			if (notBoringToasts < 6) {
				Toast.makeText(getApplicationContext(),
						"Enable your GPS, please!", Toast.LENGTH_LONG).show();
			} else if (notBoringToasts >= 6 && notBoringToasts < 9) {
				Toast.makeText(getApplicationContext(), "GPS is off, dude...",
						Toast.LENGTH_LONG).show();
			} else if (notBoringToasts >= 9) {
				Toast.makeText(getApplicationContext(),
						"Man, I'm not a fortune teller! Gimme some GPS first!",
						Toast.LENGTH_LONG).show();
			}
		} else if (myLocation.getMyCurrentPosition()[0] == 0) {
			progressBar.setVisibility(View.VISIBLE);
			// No signal
			int notBoringToasts = diceRoller.nextInt(10) + 1;
			if (notBoringToasts < 6) {
				Toast.makeText(getApplicationContext(), "Waiting for location",
						Toast.LENGTH_LONG).show();
			} else if (notBoringToasts >= 6 && notBoringToasts < 9) {
				Toast.makeText(getApplicationContext(),
						"Locating such a nice person is a pleasure",
						Toast.LENGTH_LONG).show();
			} else if (notBoringToasts >= 9) {
				Toast.makeText(getApplicationContext(),
						"Wait. Trying my best to find you!", Toast.LENGTH_LONG)
						.show();
			}

			// Waiting 5s to refresh
			Handler mHandler = new Handler();
			Runnable mRunnable = new Runnable() {
				@Override
				public void run() {
					if (myLocation.getMyCurrentPosition()[0] == 0) {
						progressBar.setVisibility(View.INVISIBLE);
						int notBoringToasts = diceRoller.nextInt(10) + 1;
						if (notBoringToasts < 6) {
							Toast.makeText(getApplicationContext(),
									"Can't localize you. Are you real?",
									Toast.LENGTH_LONG).show();
						} else if (notBoringToasts >= 6 && notBoringToasts < 9) {
							Toast.makeText(getApplicationContext(),
									"Ufff... no joy. Can't find you",
									Toast.LENGTH_LONG).show();
						} else if (notBoringToasts >= 9) {
							Toast.makeText(
									getApplicationContext(),
									"Can't get your position now. Try to jump a few times",
									Toast.LENGTH_LONG).show();
						}
					} else if (myLocation.getMyCurrentPosition()[0] > 0) {
						progressBar.setVisibility(View.INVISIBLE);
						processGPS(myLocation.getMyCurrentPosition()[0],
								myLocation.getMyCurrentPosition()[1]);
					}
				}
			};
			mHandler.postDelayed(mRunnable, 5000);
		} else if (myLocation.getMyCurrentPosition()[0] > 0) {
			processGPS(myLocation.getMyCurrentPosition()[0],
					myLocation.getMyCurrentPosition()[1]);
		}
	}

	protected void processGPS(double d, double e) {
		double[] locationPixel = Utils.getPixel(d, e);
		if (locationPixel[1] < 0 || locationPixel[1] > Constants.MAP_HEIGHT
				|| locationPixel[0] > Constants.MAP_WIDTH
				|| locationPixel[0] < 0) {
			Toast.makeText(
					getApplicationContext(),
					"Don't think you're close to CERN... Tried to test me, right :P",
					Toast.LENGTH_LONG).show();
		} else {
			progressBar.setVisibility(View.VISIBLE);
			scrollPosition[0] = myPosition[0] = (int) locationPixel[0];
			scrollPosition[1] = myPosition[1] = (int) locationPixel[1];
			currentScale = (int) (100 * webView.getScale());
			setWebView();
		}
	}

	private void scrollMe(int initialScrollX, int initialScrollY) {
		myMapScroll = new MapScroller(initialScrollX, initialScrollY,
				webView.getMeasuredWidth(), webView.getMeasuredHeight(),
				webView.getScale());
		webView.scrollTo(myMapScroll.getScrollToX(), myMapScroll.getScrollToY());
	}

	@SuppressLint("NewApi")
	private void setWebView() {

		webView.loadDataWithBaseURL("file:///android_asset/images/", getHtml(),
				"text/html", "utf-8", null);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				progressBar.setVisibility(View.INVISIBLE);
				// Those might be required later
				webView.getSettings().setUseWideViewPort(true);
				webView.getSettings().setLoadWithOverviewMode(true);
				webView.setInitialScale(currentScale);
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
				if (scrollPosition[0] > 0) {
					scrollMe(scrollPosition[0], scrollPosition[1]);
				} else {
					int[] initialScroll = { 2473, 5658 };
					scrollMe(initialScroll[0], initialScroll[1]);
				}
			}
		});

		// When scroll is finished, any gesture user makes should not scroll
		// back to initial location
		webView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				webView.setPictureListener(null);
				return false;
			}
		});
	}

	private String getHtml() {
		String htmlString = "<body style='width:3644px;margin:0; padding:0;'>"
				+ "<div style='float:left;background-image:url(MAP1A1.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP1A2.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP1B1.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP1B2.png);height:798px;width:911px;'></div>"
				+ "<div style='float:left;background-image:url(MAP1A3.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP1A4.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP1B3.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP1B4.png);height:798px;width:911px;'></div>"
				+ "<div style='float:left;background-image:url(MAP2A1.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP2A2.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP2B1.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP2B2.png);height:798px;width:911px;'></div>"
				+ "<div style='float:left;background-image:url(MAP2A3.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP2A4.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP2B3.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP2B4.png);height:798px;width:911px;'></div>"
				+ "<div style='float:left;background-image:url(MAP3A1.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP3A2.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP3B1.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP3B2.png);height:798px;width:911px;'></div>"
				+ "<div style='float:left;background-image:url(MAP3A3.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP3A4.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP3B3.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP3B4.png);height:798px;width:911px;'></div>"
				+ "<div style='float:left;background-image:url(MAP4A1.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP4A2.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP4B1.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP4B2.png);height:798px;width:911px;'></div>"
				+ "<div style='float:left;background-image:url(MAP4A3.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP4A4.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP4B3.png);height:798px;width:911px;'></div><div style='float:left;background-image:url(MAP4B4.png);height:798px;width:911px;'></div>";
		if (myPosition[0] >= 0) {
			htmlString += "<div style='background-image:url(circle.png);position:absolute;height:40px;width:40px;z-index:98;top:"
					+ (myPosition[1] - 20)
					+ "px;left:"
					+ (myPosition[0] - 20)
					+ "px;'></div>";
		}
		if (searchPosition[0] >= 0) {
			htmlString += "<div style='background-image:url(pin.png);position:absolute;height:84px;width:100px;z-index:99;top:"
					+ (searchPosition[1] - 75)
					+ "px;left:"
					+ (searchPosition[0] - 30) + "px;'></div>";
		}
		htmlString += "</body>";
		return htmlString;
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
}

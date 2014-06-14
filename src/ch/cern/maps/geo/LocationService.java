package ch.cern.maps.geo;

import ch.cern.maps.utils.Constants;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LocationService extends Service {

	private String holdErrorInformation = "LocationListener error";
	private Location clientsLastLocation;
	private LocationManager mLocationManager = null;
	private String[] mProvidersStrings = { LocationManager.NETWORK_PROVIDER,
			LocationManager.GPS_PROVIDER };
	private LocationListener[] mLocationListeners = new LocationListener[] {
			new LocationListener(LocationManager.NETWORK_PROVIDER),
			new LocationListener(LocationManager.GPS_PROVIDER) };
	private static final int LOCATION_INTERVAL = 10000;
	private static final float LOCATION_DISTANCE = 10f;
	private double mLatitude = 0, mLongitude = 0;
	private float mAccuracy = 0;

	@Override
	public void onCreate() {
		super.onCreate();

		initLocationManager();

		for (int i = 0; i < mLocationListeners.length; i++) {
			try {
				mLocationManager.requestLocationUpdates(mProvidersStrings[i],
						LOCATION_INTERVAL, LOCATION_DISTANCE,
						mLocationListeners[1]);
			} catch (Exception e) {
				Log.e(Constants.TAG, holdErrorInformation + e);
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mLocationManager != null) {
			for (int i = 0; i < mLocationListeners.length; i++) {
				try {
					mLocationManager.removeUpdates(mLocationListeners[i]);
				} catch (Exception e) {
					Log.e(Constants.TAG, holdErrorInformation + e);
				}
			}
		}
	}

	public void sendGPSIntent() {
		Intent GPSIntent = new Intent();
		GPSIntent.setAction(Constants.LocationActionTag);
		GPSIntent.putExtra(Constants.LocationFlagLatitude, getLatitude());
		GPSIntent.putExtra(Constants.LocationFlagLongitude, getLongitude());
		GPSIntent.putExtra(Constants.LocationFlagAccuracy, getAccuracy());
		sendBroadcast(GPSIntent);
	}

	public void sendProviderInfoIntent() {
		Intent ProviderInfoIntent = new Intent();
		ProviderInfoIntent.setAction(Constants.ProvidersActionTag);
		ProviderInfoIntent.putExtra(Constants.GPSProvider,
				isProvider(LocationManager.GPS_PROVIDER));
		ProviderInfoIntent.putExtra(Constants.NetworkProvider,
				isProvider(LocationManager.NETWORK_PROVIDER));
		sendBroadcast(ProviderInfoIntent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		sendGPSIntent();
		sendProviderInfoIntent();
		return super.onStartCommand(intent, flags, startId);
	}

	private double getLatitude() {
		return mLatitude;
	}

	private double getLongitude() {
		return mLongitude;
	}

	private float getAccuracy() {
		return mAccuracy;
	}

	private void setLatitude(double latitudeON) {
		this.mLatitude = latitudeON;
	}

	private void setLongitude(double longitudeON) {
		this.mLongitude = longitudeON;
	}

	private void setAccuracy(float accuracyON) {
		this.mAccuracy = accuracyON;
	}

	private class LocationListener implements android.location.LocationListener {

		public LocationListener(String mProvider) {
			clientsLastLocation = new Location(mProvider);
		}

		@Override
		public void onLocationChanged(Location currentLocation) {
			if (clientsLastLocation == null
					|| mAgeCheck(currentLocation) < mAgeCheck(clientsLastLocation)) {
				clientsLastLocation.set(currentLocation);
				setLongitude(clientsLastLocation.getLongitude());
				setLatitude(clientsLastLocation.getLatitude());
				setAccuracy(clientsLastLocation.getAccuracy());
				sendGPSIntent();
			}
		}

		@Override
		public void onProviderDisabled(String mProvider) {
			sendProviderInfoIntent();
		}

		@Override
		public void onProviderEnabled(String mProvider) {
		}

		@Override
		public void onStatusChanged(String mProvider, int status, Bundle extras) {
		}
	}

	private void initLocationManager() {
		if (mLocationManager == null) {
			mLocationManager = (LocationManager) getApplicationContext()
					.getSystemService(Context.LOCATION_SERVICE);
		}
	}

	public boolean isProvider(String mType) {
		return mLocationManager.isProviderEnabled(mType);
	}

	private long mAgeCheck(Location l) {
		return System.currentTimeMillis() - l.getTime();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
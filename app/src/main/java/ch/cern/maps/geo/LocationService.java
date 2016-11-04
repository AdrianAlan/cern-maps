package ch.cern.maps.geo;

import ch.cern.maps.utils.Constants;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

public class LocationService extends Service {

	private String holdErrorInformation = "LocationListener error";
	private Location clientsLastLocation;
	private LocationManager locationManager = null;
	private String[] locationProviders = {LocationManager.NETWORK_PROVIDER,
			LocationManager.GPS_PROVIDER};
	private LocationListener[] locationListeners = new LocationListener[]{
			new LocationListener(LocationManager.NETWORK_PROVIDER),
			new LocationListener(LocationManager.GPS_PROVIDER)};
	private static final int LOCATION_INTERVAL = 10000;
	private static final float LOCATION_DISTANCE = 10f;
	private double latitude = 0, longitude = 0;
	private float accuracy = 0;

	@Override
	public void onCreate() {
		super.onCreate();

		initLocationManager();

		if (hasPermissions()) {
			for (int i = 0; i < locationListeners.length; i++) {
				try {
					locationManager.requestLocationUpdates(locationProviders[i],
							LOCATION_INTERVAL, LOCATION_DISTANCE,
							locationListeners[1]);
				} catch (Exception e) {
					Log.e(Constants.TAG, holdErrorInformation + e.getMessage(), e);
				}
			}
		} else {
			Log.i(Constants.TAG, "Location disabled by the user");
		}
	}

	private boolean hasPermissions() {
		return !(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
				ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (locationManager != null && hasPermissions()) {
			for (LocationListener mLocationListener : locationListeners) {
				try {
					locationManager.removeUpdates(mLocationListener);
				} catch (Exception e) {
					Log.e(Constants.TAG, holdErrorInformation + e.getMessage(), e);
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
		return latitude;
	}

	private double getLongitude() {
		return longitude;
	}

	private float getAccuracy() {
		return accuracy;
	}

	private void setLatitude(double latitudeON) {
		this.latitude = latitudeON;
	}

	private void setLongitude(double longitudeON) {
		this.longitude = longitudeON;
	}

	private void setAccuracy(float accuracyON) {
		this.accuracy = accuracyON;
	}

	private class LocationListener implements android.location.LocationListener {

		public LocationListener(String mProvider) {
			clientsLastLocation = new Location(mProvider);
		}

		@Override
		public void onLocationChanged(Location currentLocation) {
			if (clientsLastLocation == null
					|| mAgeCheck(currentLocation) < mAgeCheck(clientsLastLocation)) {
				clientsLastLocation = currentLocation;
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
		if (locationManager == null) {
			locationManager = (LocationManager) getApplicationContext()
					.getSystemService(Context.LOCATION_SERVICE);
		}
	}

	public boolean isProvider(String mType) {
		return locationManager.isProviderEnabled(mType);
	}

	private long mAgeCheck(Location l) {
		return System.currentTimeMillis() - l.getTime();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
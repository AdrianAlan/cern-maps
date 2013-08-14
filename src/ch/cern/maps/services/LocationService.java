package ch.cern.maps.services;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationService {

	private Context context;
	private double latitude, longitude;
	private LocationManager locationManager;
	private LocationListener locationListener;

	public LocationService(Context _c) {
		context = _c;
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		locationListener = new MyLocationListener();
		
	}
	
	public double[] getMyCurrentPosition() {
		double[] ret = {-1,-1};
	    if ( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
	    	locationManager.removeUpdates(locationListener);
	    } else {
	    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					1000, 0, locationListener);
	    	ret[0] = getLatitude();
	    	ret[1] = getLongitude();
	    }
	    return ret;
	}

	private double getLatitude() {
		return latitude;
	}

	private void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	private double getLongitude() {
		return longitude;
	}

	private void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location cernLocation) {
			setLongitude(cernLocation.getLongitude());
			setLatitude(cernLocation.getLatitude());
		}

		@Override
		public void onProviderDisabled(String provider) {
			locationManager.removeUpdates(locationListener);
		}

		@Override
		public void onProviderEnabled(String provider) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					1000, 0, locationListener);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
}

package ch.cern.maps.services;

import java.net.InetAddress;

import ch.cern.maps.utils.Constants;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class CheckConnection extends AsyncTask<String, Void, Boolean> {

	private Context context;

	public CheckConnection(Context c) {
		this.context = c;
	}

	protected Boolean doInBackground(String... urls) {
		try {
			InetAddress ipAddr = InetAddress.getByName("cern.ch");
			return !ipAddr.equals("");
		} catch (Exception e) {
			Log.e(Constants.TAG, e.getMessage(), e);
			return false;
		}
	}

	protected void onPostExecute(Boolean res) {
		Intent typeIntent = new Intent();
		typeIntent.setAction(Constants.InternetConnectionActionTag);
		typeIntent.putExtra(Constants.InternetConnectionStatus, res);
		context.sendBroadcast(typeIntent);
	}
}

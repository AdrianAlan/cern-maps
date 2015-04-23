package ch.cern.maps.services;

import java.net.InetAddress;

import ch.cern.maps.utils.Constants;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class CheckConnection extends AsyncTask<String, Void, Boolean> {

	private Context mContext;

	public CheckConnection(Context c) {
		this.mContext = c;
	}

	protected Boolean doInBackground(String... urls) {
		try {
			InetAddress ipAddr = InetAddress.getByName("cern.ch");
			if (ipAddr.equals("")) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}

	protected void onPostExecute(Boolean res) {
		Intent typeIntent = new Intent();
		typeIntent.setAction(Constants.InternetConnectionActionTag);
		typeIntent.putExtra(Constants.InternetConnectionStatus, res);
		mContext.sendBroadcast(typeIntent);
	}
}

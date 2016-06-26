package ch.cern.maps.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class GetContentByURL extends AsyncTask<String, Void, String> {

	private Context context;
	
	public GetContentByURL(Context c) {
		this.context = c;
	}
	
	protected String doInBackground(String... urls) {

		String searchString = urls[0].toLowerCase(Locale.ENGLISH);
		try {
			searchString = URLEncoder.encode(searchString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Log.e(Constants.TAG, e.getMessage(), e);
			return null;
		}

		try {
			URL url = new URL(Constants.PhonebookURL + searchString);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

			InputStream mInputStream = urlConnection.getInputStream();
			BufferedReader mReader = new BufferedReader(new InputStreamReader(
					mInputStream, "iso-8859-1"), 8);
			StringBuilder mBuilder = new StringBuilder();
			String l;
			while ((l = mReader.readLine()) != null) {
				mBuilder.append(l);
			}

			mInputStream.close();

			return mBuilder.toString();
		} catch (IOException e) {
			Log.e(Constants.TAG, e.getMessage(), e);
			return null;
		}
	}

	protected void onPostExecute(String res) {
		Intent typeIntent = new Intent();
		typeIntent.setAction(Constants.PhonebookActionTag);
		typeIntent.putExtra(Constants.PhonebookResponse, res);
		context.sendBroadcast(typeIntent);
	}
}

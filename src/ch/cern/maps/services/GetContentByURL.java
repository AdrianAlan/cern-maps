package ch.cern.maps.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import ch.cern.maps.utils.Constants;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class GetContentByURL extends AsyncTask<String, Void, String> {

	private Context mContext;
	
	public GetContentByURL(Context c) {
		this.mContext = c;
	}
	
	protected String doInBackground(String... urls) {
		String resString = null;

		String searchString = urls[0].toLowerCase(Locale.ENGLISH);
		try {
			searchString = URLEncoder.encode(searchString, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			searchString.replace(" ", "+");
			e1.printStackTrace();
		}

		try {
			HttpClient mHTTPClient = new DefaultHttpClient();
			HttpResponse mHTTPResponse = mHTTPClient.execute(new HttpGet(
					Constants.PhonebookURL + searchString));
			InputStream mInputStream = mHTTPResponse.getEntity().getContent();
			BufferedReader mReader = new BufferedReader(new InputStreamReader(
					mInputStream, "iso-8859-1"), 8);
			StringBuilder mBuilder = new StringBuilder();
			String l = null;
			while ((l = mReader.readLine()) != null) {
				mBuilder.append(l);
			}
			resString = mBuilder.toString();
			mInputStream.close();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resString;
	}

	protected void onPostExecute(String res) {
		Intent typeIntent = new Intent();
		typeIntent.setAction(Constants.PhonebookActionTag);
		typeIntent.putExtra(Constants.PhonebookResponse, res);
		mContext.sendBroadcast(typeIntent);
	}
}

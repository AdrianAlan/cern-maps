package ch.cern.maps.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.cern.maps.models.Building;
import ch.cern.maps.models.Trams;
import android.util.Log;

public class JSONParser {

	private String json = "";
	private JSONObject jObj = null;

	public JSONParser(InputStream is) {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String readLine = null;
		StringBuilder stringBuilder = new StringBuilder();

		try {
			// while the BufferedReader readLine is not null
			while ((readLine = br.readLine()) != null) {
				stringBuilder.append(readLine + "\n");
			}
			is.close();
			json = stringBuilder.toString();
		} catch (Exception e) {
			Log.e(Constants.TAG, "Error converting result to " + e.toString());
		}
		// try parsing the string to an object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e(Constants.TAG, "Error parsing data " + e.toString());
		}
	}

	public ArrayList<Trams> readSchedule() {
		ArrayList<Trams> trams = new ArrayList<Trams>();
		try {
			JSONArray jsonTrams = jObj.getJSONArray(Constants.JSON_TAG_TRAMS);
			for (int i = 0; i < jsonTrams.length(); i++) {
				JSONObject tram = jsonTrams.getJSONObject(i);
				String line = tram.getString(Constants.JSON_TAG_TRAMS_LINE);
				String time = tram.getString(Constants.JSON_TAG_TRAMS_TIME);
				trams.add(new Trams(line, time));
			}
		} catch (JSONException e) {
			Log.e(Constants.TAG, "Error parsing data " + e.toString());
			e.printStackTrace();
		}
		return trams;
	}

	public Building readBuildingCoordinants(String searchingFor) {
		try {
			JSONObject theBuilding = jObj.getJSONObject(searchingFor);
			return new Building(searchingFor.toLowerCase(Locale.getDefault()),
					theBuilding
							.getString(Constants.JSON_TAG_BUILDINGS_LATITUDE),
					theBuilding
							.getString(Constants.JSON_TAG_BUILDINGS_LONGITUDE),
					"".toLowerCase(Locale.getDefault()));
		} catch (JSONException e) {
			return null;
		}
	}
}
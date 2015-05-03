package ch.cern.maps.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.cern.maps.models.Building;
import ch.cern.maps.models.Circuit;
import ch.cern.maps.models.Person;
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

	public ArrayList<Person> readPhonebook() {
		ArrayList<Person> people = new ArrayList<Person>();
		try {
			JSONArray jsonPeople = jObj
					.getJSONArray(Constants.JSON_TAG_Phonebook);
			for (int i = 0; i < jsonPeople.length(); i++) {
				JSONObject person = jsonPeople.getJSONObject(i);
				people.add(new Person(
						person.getString(Constants.JSON_TAG_Phonebook_Firstname),
						person.getString(Constants.JSON_TAG_Phonebook_Familyname),
						person.getString(Constants.JSON_TAG_Phonebook_Email),
						person.getString(Constants.JSON_TAG_Phonebook_Group),
						person.getString(Constants.JSON_TAG_Phonebook_Office)));
			}
		} catch (JSONException e) {
			Log.e(Constants.TAG, "Error parsing data " + e.toString());
			e.printStackTrace();
		}
		return people;
	}

	public ArrayList<Trams> readSchedule(String lineNumber) {
		ArrayList<Trams> trams = new ArrayList<Trams>();
		try {
			JSONArray jsonTrams = jObj.getJSONArray(lineNumber);
			for (int i = 0; i < jsonTrams.length(); i++) {
				JSONObject tram = jsonTrams.getJSONObject(i);
				String time = tram.getString(Constants.JSON_TAG_Trams_Time);
				String direction = tram
						.getString(Constants.JSON_TAG_Trams_Direction);
				String day = tram.getString(Constants.JSON_TAG_Trams_Day);
				trams.add(new Trams(lineNumber, time, direction, day));
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

	public Circuit readShuttle(String lineName) {
		Circuit line = null;
		try {
			JSONObject jsonShuttle = jObj.getJSONObject(lineName);
			ArrayList<String> timeList = new ArrayList<String>();
			Map<String, ArrayList<String>> stopList = new HashMap<String, ArrayList<String>>();
			for (Iterator<String> iterator = jsonShuttle.keys(); iterator
					.hasNext();) {
				String st = (String) iterator.next();
				JSONArray jsonStops = jsonShuttle.getJSONObject(st)
						.getJSONArray("Stops");
				ArrayList<String> al = new ArrayList<String>();
				for (int i = 0; i < jsonStops.length(); i++) {
					al.add(jsonStops.getString(i));
				}
				stopList.put(st, al);
				timeList.add(st);
			}
			line = new Circuit(lineName, timeList, stopList);
		} catch (JSONException e) {
			Log.e(Constants.TAG, "Error parsing data " + e.toString());
			e.printStackTrace();
		}
		return line;
	}

	public String[] readShuttleSchedule(Circuit mCircuit) {
		try {
			JSONObject jsonShuttle = jObj.getJSONObject(mCircuit.getName());
			JSONObject jsonShuttle2 = jsonShuttle.getJSONObject(mCircuit
					.getSelectedTime());
			JSONObject jsonShuttle3 = jsonShuttle2.getJSONObject("Time");
			JSONArray jsonShuttle4 = jsonShuttle3.getJSONArray(mCircuit
					.getSelectedStop());
			String[] s = new String[jsonShuttle4.length()];
			for (int i = 0; i < jsonShuttle4.length(); i++) {
				s[i] = jsonShuttle4.optString(i);
			}
			return s;
		} catch (JSONException e) {
			Log.e(Constants.TAG, "Error parsing data " + e.toString());
			e.printStackTrace();
			
		}
		return null;
	}
}
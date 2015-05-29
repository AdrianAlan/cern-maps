package ch.cern.maps.utils;

import ch.cern.www.R;

public class Constants {

	public static final String APP_NAME = "CERN Maps";
	public static final String TAG = "CERNMAPS";

	public static final String JSONTram = "tpg.json";
	public static final String JSON_TAG_Trams = "trams";
	public static final String JSON_TAG_Trams_Time = "time";
	public static final String JSON_TAG_Trams_Direction = "direction";
	public static final String JSON_TAG_Trams_Day = "day";

	public static final String JSON_BUILDINGS = "buildings.json";
	public static final String JSON_TAG_BUILDINGS_LATITUDE = "lat";
	public static final String JSON_TAG_BUILDINGS_LONGITUDE = "lon";
	
	public static final String JSONCERNShuttle = "shuttle.json";
	

	public static final int MAP_HEIGHT = 5376;
	public static final int MAP_WIDTH = 5376;

	public static final double MAP_NORTH = 46.267124;
	public static final double MAP_SOUTH = 46.227409;
	public static final double MAP_WEST = 6.025761;
	public static final double MAP_EAST = 6.083785;

	// Calculated by: MAP_HEIGHT / ((MAP_NORTH-MAP_SOUTH) * 111111)
	// 111111 is a ratio between degrees and meters
	public static final double MeterToPixelRatio = 1.218281465;

	public static final int LocateMeTreshold = 20000;
	public static final String LocationActionTag = "LocationRequest";
	public static final String LocationFlagLatitude = "LatitudeRequest";
	public static final String LocationFlagLongitude = "LongitudeRequest";
	public static final String LocationFlagAccuracy = "AccuracyRequest";
	public static final String ProvidersActionTag = "ProvidersRequest";
	public static final String GPSProvider = "GPSProvider";
	public static final String NetworkProvider = "NetworkProvider";
	public static final String OrientationActionTag = "OrientatonRequest";
	public static final String OrientationFlagAzimuth = "AzimuthRequest";
	public static final String GPSRequest = "SharedPreferencesGPS";
	public static final String SharedPreferences = "SharedPreferences";

	public static final int[] navIcons = { R.drawable.ic_action_map,
			R.drawable.ic_action_call,
			R.drawable.ic_action_transport_tram_icon,
			R.drawable.ic_action_group, R.drawable.ic_action_about };

	public static final int[] mapSelectorIcons = { R.drawable.ic_standard,
			R.drawable.ic_cycle, R.drawable.ic_transport };

	public static final String[] shuttleCircuits = { "Circuit 1 - Meyrin",
		"Circuit 2 - Prévessin", "Circuit 3 - Shift", 
		"Circuit 4 - Airport" };
	
	public static final int UpdatePeriod = 2592000;
	
	public static final String MapTypeActionTag = "MapTypeActionTag";
	public static final String MapType = "MapType";

	public static final String PhonebookURL = "http://m.web.cern.ch/m/ajax-util.php?f=phonebook&search=";
	public static final String PhonebookActionTag = "PhonebookActionTag";
	public static final String PhonebookResponse = "PhonebookResponse";
	public static final String JSON_TAG_Phonebook = "results";
	public static final String JSON_TAG_Phonebook_Firstname = "firstname";
	public static final String JSON_TAG_Phonebook_Familyname = "familyname";
	public static final String JSON_TAG_Phonebook_Email = "email";
	public static final String JSON_TAG_Phonebook_Group = "group";
	public static final String JSON_TAG_Phonebook_Office = "office";
	public static final String InternetConnectionActionTag = "InternetConnectionActionTag";
	public static final String InternetConnectionStatus = "InternetConnectionStatus";
	public static final String PingURLForConnection = "cern.ch";
	
}

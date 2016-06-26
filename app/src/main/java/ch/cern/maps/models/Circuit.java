package ch.cern.maps.models;

import java.util.ArrayList;
import java.util.Map;

public class Circuit {

	private String name;
	private ArrayList<String> times;
	private Map<String, ArrayList<String>> stops;
	private String selectedTime, selectedStop;
	
	public Circuit(String name, ArrayList<String> times, Map<String, ArrayList<String>> stops){
		this.setName(name);
		this.setTimes(times);
		this.setStops(stops);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<String> getTimes() {
		return times;
	}

	public void setTimes(ArrayList<String> times) {
		this.times = times;
	}

	public ArrayList<String> getStops(String key) {
		return stops.get(key);
	}

	public void setStops(Map<String, ArrayList<String>> stops) {
		this.stops = stops;
	}
	
	public void setSelectedTime(String selectedTime) {
		this.selectedTime = selectedTime;
	}

	public String getSelectedTime() {
		return selectedTime;
	}

	public String getSelectedStop() {
		return selectedStop;
	}

	public void setSelectedStop(String selectedStop) {
		this.selectedStop = selectedStop;
	}
}
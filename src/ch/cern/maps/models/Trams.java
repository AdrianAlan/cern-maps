package ch.cern.maps.models;

public class Trams {

	private String line, time, direction, day;
	private int waiting;

	public Trams(String line, String time, String direction, String day) {
		this.setLine(line);
		this.setTime(time);
		this.setDay(day);
		this.setDirection(direction);
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public int getWaiting() {
		return waiting;
	}

	public void setWaiting(int waiting) {
		this.waiting = waiting;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

}

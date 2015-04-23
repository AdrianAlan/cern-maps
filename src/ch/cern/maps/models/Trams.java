package ch.cern.maps.models;

public class Trams {

	private String line;
	private String time;
	private String direction;
	private int waiting;

	public Trams(String line, String time, String direction) {
		this.setLine(line);
		this.setTime(time);
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

}

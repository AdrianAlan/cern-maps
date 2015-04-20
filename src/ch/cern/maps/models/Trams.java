package ch.cern.maps.models;

public class Trams {

	private String line;
	private String time;

	public Trams(String line, String time) {
		this.setLine(line);
		this.setTime(time);
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

}

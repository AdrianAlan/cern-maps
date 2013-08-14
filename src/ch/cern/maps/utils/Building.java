package ch.cern.maps.utils;

public class Building {

	private double NS, WE;
	private String buildingName;

	public Building(String name, String NS, String WE) {
		this.setNS(Double.parseDouble(NS));
		this.setWE(Double.parseDouble(WE));
		this.setBuildingName(name);
	}

	public double getNS() {
		return NS;
	}

	public void setNS(double nS) {
		NS = nS;
	}

	public double getWE() {
		return WE;
	}

	public void setWE(double wE) {
		WE = wE;
	}
	
	public String getBuildingName() {
		return buildingName;
	}

	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}
}

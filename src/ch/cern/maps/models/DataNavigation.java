package ch.cern.maps.models;

public class DataNavigation {

	private String navTitle, navDescription;
	private int navImage;

	public DataNavigation(String title, String description, int image) {
		this.navTitle = title;
		this.navDescription = description;
		this.navImage = image;
	}

	public String getTitle() {
		return navTitle;
	}

	public String getaDescription() {
		return navDescription;
	}

	public int getImage() {
		return navImage;
	}
}
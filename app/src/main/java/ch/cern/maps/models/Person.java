package ch.cern.maps.models;

public class Person {

	private String firstName, familyName, email, group, office;
	
	public Person(String firstName, String familyName, String email, String group, String office) {
		this.setFirstName(firstName);
		this.setFamilyName(familyName);
		this.setEmail(email);
		this.setGroup(group);
		this.setOffice(office);
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}
	
}

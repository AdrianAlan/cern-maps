package ch.cern.maps.models;

public class Person {

	private String firstname, familyname, email, group, office;
	
	public Person(String firstname, String familyname, String email, String group, String office) {
		this.setFirstname(firstname);
		this.setFamilyname(familyname);
		this.setEmail(email);
		this.setGroup(group);
		this.setOffice(office);
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getFamilyname() {
		return familyname;
	}

	public void setFamilyname(String familyname) {
		this.familyname = familyname;
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

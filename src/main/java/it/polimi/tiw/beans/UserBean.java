package it.polimi.tiw.beans;

public class UserBean {
	private int id;
	private String mail;
	private String course;

	public int getId() {
		return id;
	}

	public String getMail() {
		return mail;
	}
	
	public String getCourse() {
		return course;
	}
	
	public void setId(int i) {
		id = i;
	}

	public void setMail(String m) {
		mail = m;
	}
	
	public void setCourse(String c) {
		course = c;
	}

}

package it.polimi.tiw.beans;

import java.time.LocalDate;
import java.time.LocalTime;

public class Exam{
	private int courseID;
	private LocalDate date;
	private int verbalID;
	private LocalDate verbalDate;
    private LocalTime verbalHour;

	public int getCourseID() {
		return courseID;
	}

	public LocalDate getDate() {
		return date;
	}
	
	public int getVerbalID() {
		return verbalID;
	}
	
	public LocalDate getVerbalDate() {
		return verbalDate;
	}
	
	public LocalTime getVerbalHour() {
		return verbalHour;
	}
	
	public void setCourseID(int i) {
		courseID = i;
	}

	public void setDate(LocalDate d) {
		date = d;
	}
	
	public void setVerbalID(int i) {
		verbalID = i;
	}
	
	public void setVerbalDate(LocalDate dv) {
		verbalDate =  dv;
	}
	
	public void setVerbalHour(LocalTime ov) {
		verbalHour = ov;
	}
	

}

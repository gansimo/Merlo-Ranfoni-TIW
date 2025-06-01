package it.polimi.tiw.beans;

import java.time.LocalDate;
import java.time.LocalTime;

public class Exam{
	private int courseID;
	private LocalDate date;

	public int getCourseID() {
		return courseID;
	}

	public LocalDate getDate() {
		return date;
	}
	
	public void setCourseID(int i) {
		courseID = i;
	}

	public void setDate(LocalDate d) {
		date = d;
	}

}

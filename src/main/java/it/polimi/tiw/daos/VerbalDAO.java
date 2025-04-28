package it.polimi.tiw.daos;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import it.polimi.tiw.beans.RegisteredStudent;
import it.polimi.tiw.beans.VerbalBean;

public class VerbalDAO {
private Connection con;
	
	public VerbalDAO(Connection connection) {
		this.con = connection;
	}

	public VerbalBean createVerbal(int courseID, String examDate) throws SQLException {
		
		String query = "INSERT INTO Verbale (data_verbale, ora_verbale, id_corso, data) VALUES (?, ?, ?, ?)" ;
		VerbalBean verb = new VerbalBean();
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			String actualDate = Date.valueOf(LocalDate.now()).toString();
			String actualHour = Timestamp.valueOf(LocalDateTime.now()).toString();
			pstatement.setDate(1, Date.valueOf(LocalDate.now()));
			pstatement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
			pstatement.setInt(3, courseID);
			pstatement.setString(4, examDate);
			pstatement.executeUpdate();
			
			
			verb.setCourseID(courseID);
			verb.setExamDate(examDate);
			verb.setHour(actualHour);
			verb.setID(Statement.RETURN_GENERATED_KEYS);
			verb.setDate(actualDate);
			return verb;
		}
	}
	
	public void insertNewVerbalizedStudents(List<RegisteredStudent> students, VerbalBean v) throws SQLException {
		int verbID = v.getID();
		for(RegisteredStudent stud : students) {
			String query = "INSERT INTO Studenti_Verbale (id_verbale, id_studente) VALUES (?, ?)";
			try (PreparedStatement pstatement = con.prepareStatement(query);) {
				pstatement.setInt(1, verbID);
				pstatement.setInt(2, stud.getId());

				pstatement.executeUpdate();
			}
		}
	}
	
}

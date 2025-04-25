package it.polimi.tiw.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Exam;

public class CourseDAO{
	private Connection con;
	private int id;

	public CourseDAO(Connection connection, int i) {
		this.con = connection;
		this.id = i;
	}
	
	public List<Exam> findExams() throws SQLException {
		List<Exam> exams = new ArrayList<Exam>();
		String query = "SELECT data, id_verbale, data_verbale, ora_verbale FROM Appello WHERE id_corso = ? ORDER BY data ASC;";
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
	        pstatement.setInt(1, this.id);
	        
	        try (ResultSet result = pstatement.executeQuery()) {
	            while (result.next()) {
	                Exam exam = new Exam();
	                exam.setCourseID(this.id);
	                exam.setDate(result.getDate("data").toLocalDate());
	                
	                int verbID = result.getInt("id_verbale");
	                if (!result.wasNull()) {
	                    exam.setVerbalID(verbID);
	                    exam.setVerbalDate(result.getDate("data_verbale").toLocalDate());
	                    exam.setVerbalHour(result.getTime("ora_verbale").toLocalTime());
	                }
	                
	                exams.add(exam);
	            }
	        }
	    }
		return exams;
	}
	
}
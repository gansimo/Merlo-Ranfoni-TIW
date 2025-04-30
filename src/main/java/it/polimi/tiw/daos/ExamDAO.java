package it.polimi.tiw.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;


import it.polimi.tiw.beans.ExamResult;

public class ExamDAO {
	private Connection con;
    private int studentID;
    private String date;
    private int courseID;
    
    public ExamDAO(Connection connection, int i, String date, int cid) {
        this.con = connection;
        this.studentID = i;
        this.date = date;
        this.courseID = cid;
    }
    
    public ExamResult findExamData() throws SQLException {
        ExamResult examResult = new ExamResult();
        String query = "SELECT id_corso, data, id_studente, voto, stato " +
                       "FROM Iscrizioni_Appello " +
                       "WHERE id_corso = ? AND data = ? AND id_studente = ? " +
                       ";";

        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setInt(3, this.studentID);
            pstatement.setString(2, (this.date));
            pstatement.setInt(1, courseID);
            
            
            
            try (ResultSet result = pstatement.executeQuery()) {
                while (result.next()) {
                    examResult.setCourseId(this.courseID);
                    examResult.setDate(this.date);
                    examResult.setStudentId(this.studentID);
                    examResult.setMark(result.getString("voto"));
                    examResult.setState(result.getString("stato"));
                	}
            }
        }     
        return examResult;
    }
    
    public String findExamName(int c) throws SQLException{
		String name = null;
		String query = "SELECT nome FROM Corso WHERE id = ?;";
		 try (PreparedStatement pstatement = con.prepareStatement(query)) {
			 pstatement.setInt(1, c);
			 try (ResultSet result = pstatement.executeQuery()) {
				 if (result.next()) {
				 name = result.getString("nome");
				 }
			 }
		 }
		 return name;
	}
    
    public void rejectMark(int c, String d, int u) throws SQLException{
    	String query = "UPDATE Iscrizioni_Appello SET stato='rifiutato', voto='rimandato' WHERE id_corso = ? AND data = ? AND id_studente = ? ;";
    	try (PreparedStatement pstatement = con.prepareStatement(query)) {
			 pstatement.setInt(1, c);
			 pstatement.setString(2, (d));
			 pstatement.setInt(3, u);
			 int updated = pstatement.executeUpdate();
			 if (updated == 0) {
			    throw new SQLException("Nessuna riga aggiornata");
			 }
		}
    }
}

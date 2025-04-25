package it.polimi.tiw.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.RegisteredStudent;

public class StudentTableDAO {
	private Connection con;
	
	public StudentTableDAO(Connection connection) {
		this.con = connection;
	}
	
	public List<RegisteredStudent> getStudentTable(int courseID, String date) throws SQLException {
		List<RegisteredStudent> students = new ArrayList<>();
		
		String query = "SELECT u.id, u.matricola, u.cognome, u.nome, u.mail, u.corso_laurea, i.voto, i.stato FROM Iscrizioni_Appello AS i JOIN Utente AS u ON i.id_studente = u.id WHERE i.id_corso = ? AND i.data = ? ";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, courseID);
			pstatement.setString(2, date);
			try (ResultSet result = pstatement.executeQuery()) {
                while (result.next()) {
                    RegisteredStudent stud = new RegisteredStudent();
                    stud.setId(result.getInt("id"));
                    stud.setMatr(result.getString("matricola"));
                    stud.setSurname(result.getString("cognome"));
                    stud.setName(result.getString("nome"));
                    stud.setMail(result.getString("mail"));
                    stud.setCourse(result.getString("corso_laurea"));
                    stud.setGrade(result.getString("voto"));
                    stud.setState(result.getString("stato"));
                    
                    students.add(stud);
                }
            }
		return students;
		}
	}
	
	public List<RegisteredStudent> getOrderedStudentTable(int courseID, String date, String orderByColumn, String orderByDirection) throws SQLException {
		List<RegisteredStudent> students = new ArrayList<>();
		
		String query = "SELECT u.id, u.matricola, u.cognome, u.nome, u.mail, u.corso_laurea, i.voto, i.stato FROM Iscrizioni_Appello AS i JOIN Utente AS u ON i.id_studente = u.id WHERE i.id_corso = ? AND i.data = ? ORDER BY " + orderByColumn + " " + orderByDirection;
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, courseID);
			pstatement.setString(2, date);
			try (ResultSet result = pstatement.executeQuery()) {
                while (result.next()) {
                    RegisteredStudent stud = new RegisteredStudent();
                    stud.setId(result.getInt("id"));
                    stud.setMatr(result.getString("matricola"));
                    stud.setSurname(result.getString("cognome"));
                    stud.setName(result.getString("nome"));
                    stud.setMail(result.getString("mail"));
                    stud.setCourse(result.getString("corso_laurea"));
                    stud.setGrade(result.getString("voto"));
                    stud.setState(result.getString("stato"));
                    
                    students.add(stud);
                }
            }
		return students;
		}
	}
	
	public RegisteredStudent getRegisteredStudent(int courseID, String date, int studID) throws SQLException {
		RegisteredStudent stud;
		String query = "SELECT u.matricola, u.cognome, u.nome, u.mail, u.corso_laurea, i.voto, i.stato FROM Iscrizioni_Appello AS i JOIN Utente AS u ON i.id_studente = u.id WHERE i.id_corso = ? AND i.data = ? AND i.id_studente = ?";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setInt(1, courseID);
			pstatement.setString(2, date);
			pstatement.setInt(3, studID);
			try (ResultSet result = pstatement.executeQuery()) {
				stud = new RegisteredStudent();
				if(result.next()) {
	                stud.setId(studID);
	                stud.setMatr(result.getString("matricola"));
	                stud.setSurname(result.getString("cognome"));
	                stud.setName(result.getString("nome"));
	                stud.setMail(result.getString("mail"));
	                stud.setCourse(result.getString("corso_laurea"));
	                stud.setGrade(result.getString("voto"));
	                stud.setState(result.getString("stato"));
				}
            }
		return stud;
		}
	}
	
	public void updateGrade(int courseID, String date, int studID, String grade, String state) throws SQLException {
		String query = "UPDATE Iscrizioni_Appello SET voto = ?, stato = ? WHERE id_studente = ? AND id_corso = ? AND data = ?";
		
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, grade);
			pstatement.setString(2, state);
			pstatement.setInt(3, studID);
			pstatement.setInt(4, courseID);
			pstatement.setString(5, date);
			int updated = pstatement.executeUpdate();
		    if (updated == 0) {
		      throw new SQLException("Nessuna riga aggiornata");
		    }
		}
	}
	
	public void publishGrades(int courseID, String date) throws SQLException {
		String query = "UPDATE Iscrizioni_Appello SET stato = ? WHERE id_corso = ? AND data = ? AND stato = 'inserito'";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, "pubblicato");
			pstatement.setInt(2, courseID);
			pstatement.setString(3, date);
			pstatement.executeUpdate();
		}
	}
	
	public void verbalizeGrades(int courseID, String date) throws SQLException {
		String query = "UPDATE Iscrizioni_Appello SET stato = ? WHERE id_corso = ? AND data = ? AND stato = 'pubblicato'";
		try (PreparedStatement pstatement = con.prepareStatement(query);) {
			pstatement.setString(1, "verbalizzato");
			pstatement.setInt(2, courseID);
			pstatement.setString(3, date);
			pstatement.executeUpdate();
		}
	}
	
}

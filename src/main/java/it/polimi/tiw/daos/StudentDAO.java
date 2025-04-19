package it.polimi.tiw.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Course;

public class StudentDAO {
    private Connection con;
    private int studentID;

    public StudentDAO(Connection connection, int i) {
        this.con = connection;
        this.studentID = i;
    }

    public List<Course> findStudentCourses() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT c.id, c.nome, u.nome AS prof_nome, u.cognome AS prof_cognome " +
                       "FROM Corso AS c " +
                       "JOIN Iscrizioni_corsi AS ic ON ic.id_corso = c.id " +
                       "JOIN Utente AS u ON c.id_prof = u.id " +
                       "WHERE ic.id_studente = ? " +
                       "ORDER BY c.nome ASC;";

        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setInt(1, this.studentID);
            try (ResultSet result = pstatement.executeQuery()) {
                while (result.next()) {
                    Course course = new Course();
                    course.setId(result.getInt("id"));
                    course.setCourseName(result.getString("nome"));
                    course.setProfName(result.getString("prof_nome"));
                    course.setProfSurname(result.getString("prof_cognome"));
                    courses.add(course);
                }
            }
        }
        return courses;
    }

    public int findFirstCourse() throws SQLException {
        String query = "SELECT id_corso FROM Iscrizioni_corsi WHERE id_studente = ? ORDER BY id_corso ASC LIMIT 1";
        int r = 0;
        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setInt(1, this.studentID);
            try (ResultSet result = pstatement.executeQuery()) {
                if (result.next()) {
                    r = result.getInt("id_corso");
                }
            }
        }
        return r;
    }
}
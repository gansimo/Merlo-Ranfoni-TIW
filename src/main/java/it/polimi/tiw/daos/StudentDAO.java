package it.polimi.tiw.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Corso;

public class StudentDAO {
    private Connection con;
    private int id;

    public StudentDAO(Connection connection, int i) {
        this.con = connection;
        this.id = i;
    }

    public List<Corso> findCorsi() throws SQLException {
        List<Corso> corsi = new ArrayList<>();
        String query = "SELECT c.id, c.nome, u.nome AS prof_nome, u.cognome AS prof_cognome " +
                       "FROM Corso AS c " +
                       "JOIN Iscrizioni_corsi AS ic ON ic.id_corso = c.id " +
                       "JOIN Utente AS u ON c.id_prof = u.id " +
                       "WHERE ic.id_studente = ? " +
                       "ORDER BY c.nome ASC;";

        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setInt(1, this.id);
            try (ResultSet result = pstatement.executeQuery()) {
                while (result.next()) {
                    Corso corso = new Corso();
                    corso.setId(result.getInt("id"));
                    corso.setNomeCorso(result.getString("nome"));
                    corso.setNomeProf(result.getString("prof_nome"));
                    corso.setCognomeProf(result.getString("prof_cognome"));
                    corsi.add(corso);
                }
            }
        }
        return corsi;
    }

    public int findDefaultProject() throws SQLException {
        String query = "SELECT id_corso FROM Iscrizioni_corsi WHERE id_studente = ? ORDER BY id_corso ASC LIMIT 1";
        int r = 0;
        try (PreparedStatement pstatement = con.prepareStatement(query)) {
            pstatement.setInt(1, this.id);
            try (ResultSet result = pstatement.executeQuery()) {
                if (result.next()) {
                    r = result.getInt("id_corso");
                }
            }
        }
        return r;
    }
}
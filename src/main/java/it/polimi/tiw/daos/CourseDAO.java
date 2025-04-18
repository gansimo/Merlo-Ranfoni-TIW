package it.polimi.tiw.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Appello;

public class CourseDAO{
	private Connection con;
	private int id;

	public CourseDAO(Connection connection, int i) {
		this.con = connection;
		this.id = i;
	}
	
	public List<Appello> findAppelli() throws SQLException {
		List<Appello> appelli = new ArrayList<Appello>();
		String query = "SELECT data, id_verbale, data_verbale, ora_verbale FROM Appello WHERE id_corso = ? ORDER BY data ASC;";
		try (PreparedStatement pstatement = con.prepareStatement(query)) {
	        pstatement.setInt(1, this.id);
	        
	        try (ResultSet result = pstatement.executeQuery()) {
	            while (result.next()) {
	                Appello appello = new Appello();
	                appello.setIdCorso(this.id);
	                appello.setData(result.getDate("data").toLocalDate());
	                
	                int idVerbale = result.getInt("id_verbale");
	                if (!result.wasNull()) {
	                    appello.setIdVerbale(idVerbale);
	                    appello.setDataVerbale(result.getDate("data_verbale").toLocalDate());
	                    appello.setOraVerbale(result.getTime("ora_verbale").toLocalTime());
	                }
	                
	                appelli.add(appello);
	            }
	        }
	    }
		return appelli;
	}
}
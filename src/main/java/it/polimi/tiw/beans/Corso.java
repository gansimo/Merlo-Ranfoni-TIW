package it.polimi.tiw.beans;

public class Corso {
	private int id;
	private String nome_corso;
	private String nome_prof;
	private String cognome_prof;

	public int getId() {
		return id;
	}

	public String getNomeCorso() {
		return nome_corso;
	}
	
	public String getNomeProf() {
		return nome_prof;
	}
	
	public String getCognomeProf() {
		return cognome_prof;
	}
	
	public void setId(int i) {
		id = i;
	}

	public void setNomeCorso(String nc) {
		nome_corso = nc;
	}
	
	public void setNomeProf(String np) {
		nome_prof = np;
	}
	
	public void setCognomeProf(String cp) {
		cognome_prof =  cp;
	}
	

}

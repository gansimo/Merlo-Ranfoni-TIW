package it.polimi.tiw.beans;

import java.time.LocalDate;
import java.time.LocalTime;

public class Appello{
	private int id_corso;
	private LocalDate data;
	private int id_verbale;
	private LocalDate dataVerbale;
    private LocalTime oraVerbale;

	public int getIdCorso() {
		return id_corso;
	}

	public LocalDate getData() {
		return data;
	}
	
	public int getIdVerbale() {
		return id_verbale;
	}
	
	public LocalDate getDataVerbale() {
		return dataVerbale;
	}
	
	public LocalTime getOraVerbale() {
		return oraVerbale;
	}
	
	public void setIdCorso(int i) {
		id_corso = i;
	}

	public void setData(LocalDate d) {
		data = d;
	}
	
	public void setIdVerbale(int i) {
		id_verbale = i;
	}
	
	public void setDataVerbale(LocalDate dv) {
		dataVerbale =  dv;
	}
	
	public void setOraVerbale(LocalTime ov) {
		oraVerbale = ov;
	}
	

}

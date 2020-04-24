package it.polito.tdp.meteo.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {
	MeteoDAO dao;
	private List <Rilevamento> bestSoluzione;
	private List <Citta> cittaElenco;
	private double bestCosto;
	private int k=0;
	private int x=0;
	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;

	public Model() {
		dao = new MeteoDAO();
		this.bestSoluzione = null;
		this.bestCosto = 0.0;
		this.cittaElenco = new ArrayList<>();
	}

	// of course you can change the String output with what you think works best
	public String getUmiditaMedia(int mese) {
		return "TODO!";
	}
	
	// of course you can change the String output with what you think works best
	public List<Rilevamento> trovaSequenza(int mese) {
		
		List<Rilevamento> parziale = new ArrayList<>();
		Citta c;
		for(String s : this.citta()) {
			c = new Citta(s);
			c.setRilevamenti((dao.getAllRilevamentiLocalitaMese(mese, s)));
			c.setCounter(dao.getAllRilevamentiLocalitaMese(mese, s).size());
			cittaElenco.add(c);
		}
		//caso iniziale
		cerca(parziale, 0, 0, 0);
		return bestSoluzione;
	}
	
	private void cerca(List<Rilevamento> parziale, int l, double costo, int cntMax) {
		//caso terminale
		if (cntMax>=NUMERO_GIORNI_CITTA_MAX) 
			return;
		if(l==this.NUMERO_GIORNI_TOTALI) {
			Set<String> cittaVisitate = new HashSet<>();
			for (Rilevamento r : parziale) {
				cittaVisitate.add(r.getLocalita());
			}
			if (cittaVisitate.size()!=this.citta().size())
				return;
			k++;
			if(k==1) {
				bestSoluzione = new ArrayList<>(parziale);
				bestCosto = costo;}
			if (costo<=bestCosto) {
				bestSoluzione = new ArrayList<>(parziale);
				bestCosto = costo;
			}
			return; }
		
		else {
			//caso normale
			//creo una lista contenente i rilevamenti eseguiti il giorno l per ogni città
			List<Rilevamento> rilevamentiGiornoL = new ArrayList<>();
			for(Citta c : cittaElenco)
				rilevamentiGiornoL.add(c.getRilevamenti().get(l));
			//prova ad aggiungere ogni rilevamento eseguito in ogni città il giorno l
			if (l>0 && (cntMax==0 || cntMax==1)) {
				for(Rilevamento r : rilevamentiGiornoL) {
					if (r.getLocalita().equals(parziale.get(parziale.size()-1).getLocalita())) {
						parziale.add(r);
						cerca(parziale, l+1, costo+r.getUmidita(), cntMax+1); 
						parziale.remove(parziale.size()-1); }}}
			else {
				for(Rilevamento r : rilevamentiGiornoL) {
				parziale.add(r);
				if (l==0)	
					cerca(parziale, l+1, costo+r.getUmidita(), 0); 
				else if(l>0 && !parziale.get(parziale.size()-1).getLocalita().equals(parziale.get(parziale.size()-2).getLocalita())) {
						cerca(parziale, l+1, costo+r.getUmidita()+COST, 0);	
						}
					else {
						cerca(parziale, l+1, costo+r.getUmidita(), cntMax+1); 
							 }
				parziale.remove(parziale.size()-1);
			}}}
		}
	
	public double getAvgRilevamentiLocalitaMese (int mese, String localita) {
		return dao.getAvgRilevamentiLocalitaMese(mese, localita);
	}
	
	public Set <String> citta() {
		Set<String> citta = new HashSet<>();
		for (Rilevamento r : dao.getAllRilevamenti())
			citta.add(r.getLocalita());
		return citta;
	}
}

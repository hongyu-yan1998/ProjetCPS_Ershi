package cps.regles.bouchons;

import java.util.ArrayList;

import cps.cep.samu.HealthCorrelatorI;
import cps.cep.samu.evenements.SAMUDemandeIntervention;
import cps.evenements.ComplexEvent;
import cps.evenements.interfaces.ComplexEventI;
import cps.evenements.interfaces.EventBaseI;
import cps.evenements.interfaces.EventI;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;

public class HealthCorrelator 
implements HealthCorrelatorI {
	
	private boolean medecinDispo;
	private boolean ambulanceDispo;
	
	public HealthCorrelator() {
		super();
		medecinDispo = true;
		ambulanceDispo = true;
	}

	@Override
	public boolean estDansCentre(AbsolutePosition position) {
		return true;
	}

	@Override
	public void setAmbulDispo(boolean amb) {
		this.ambulanceDispo = amb;
	}
	
	@Override
	public boolean estAmbulDispo() {
		return ambulanceDispo;
	}

	
	@Override
	public void setMedecinAvailable(boolean med) {
		this.medecinDispo = med;
	}

	@Override
	public boolean estMedecinDispo() {
		return medecinDispo;
	}


	/**
	 * Le SAMU courant propage un evenement complexe {@link ComplexEvent} au SAMU suivant le plus proche,
	 * Les evenements propages sont donc ajoutés au SAMU suivant.  
	 **/
	@Override
	public void triggerDemande(ArrayList<EventI> matchedEvents) {
		ComplexEventI di = new SAMUDemandeIntervention(matchedEvents);
	}

	@Override
	public void triggerAmbulance(AbsolutePosition pos) throws Exception {
		System.out.println("ambulance go -> " + pos.toString());		
	}

	@Override
	public void triggerMedecin(AbsolutePosition pos) throws Exception {
		System.out.println("medecin go -> " + pos.toString());			
	}

	@Override
	public void triggerMedecin(AbsolutePosition pos, String personId) throws Exception {
		System.out.println("ambulance go -> " + pos.toString() + "de personne " + personId);			
	}

	@Override
	public void triggerMedicCall(AbsolutePosition pos, String personId) throws Exception {
		System.out.println("MedicCall a -> " + pos.toString() + "de personne " + personId);			
	}

	@Override
	public void triggerPasserSamuSuiv(EventI e) throws Exception {
		System.out.println("Passer prochain Samu");
	}

	@Override
	public EventBaseI getEb() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}

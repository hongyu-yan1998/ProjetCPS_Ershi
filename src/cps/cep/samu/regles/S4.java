package cps.cep.samu.regles;

import java.util.ArrayList;

import cps.cep.interfaces.CorrelatorStateI;
import cps.cep.samu.CorrelateurSAMU;
import cps.cep.samu.HealthCorrelatorI;
import cps.evenements.interfaces.EventBaseI;
import cps.evenements.interfaces.EventI;
import cps.cep.samu.evenements.SAMUAlarmeSante;
import cps.cep.samu.evenements.SAMUDemandeIntervention;

public class S4 extends S3 {

	public S4() {}
	
	@Override
	public boolean filter(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		HealthCorrelatorI samuState = (CorrelateurSAMU)c;

		//  && samuState.estDansCentre("p") && 
		// 		samuState.estSuivantPlusPres(samuState.getSamuPlusProche()) &&
		// 		samuState.getSamuPlusProche().estAmbulDispo();
		EventI e = matchedEvents.get(0);
		try {
			return  samuState.estDansCentre(((SAMUAlarmeSante) e).getPosition()) && !samuState.estMedecinDispo();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return false;
	}
	
	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		HealthCorrelatorI samuState = (CorrelateurSAMU)c;
		//A corriger
		SAMUDemandeIntervention demande = new SAMUDemandeIntervention(matchedEvents);
		try {
			samuState.triggerPasserSamuSuiv(demande);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		eb.removeEvent(matchedEvents.get(0));
	}

}

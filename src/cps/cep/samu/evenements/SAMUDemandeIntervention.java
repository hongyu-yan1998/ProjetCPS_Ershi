package cps.cep.samu.evenements;

import java.util.ArrayList;

import cps.evenements.ComplexEvent;
import cps.evenements.interfaces.EventI;

public class SAMUDemandeIntervention extends ComplexEvent {

	private static final long serialVersionUID = 1L;
	
	public SAMUDemandeIntervention(ArrayList<EventI> CorrelatedEvents) {
		super(CorrelatedEvents);
	}


}

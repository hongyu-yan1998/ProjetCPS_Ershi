package cps.cep.samu.regles;

import java.util.ArrayList;

import cps.cep.interfaces.CorrelatorStateI;
import cps.cep.samu.CorrelateurSAMU;
import cps.cep.samu.HealthCorrelatorI;
import cps.cep.samu.evenements.SAMUAlarmeSante;
import cps.evenements.interfaces.EventBaseI;
import cps.evenements.interfaces.EventI;
import cps.regles.interfaces.RuleI;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfHealthAlarm;

public class S1 implements RuleI {

	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		
		EventI as = null;
		for(int i = 0; i < eb.numberOfEvents() && (as == null) ; i++) {
			EventI e = eb.getEvent(i);
			if(e instanceof SAMUAlarmeSante && e.hasProperty("type") && e.hasProperty("position") 
				&& ((TypeOfHealthAlarm)e.getPropertyValue("type")).equals(TypeOfHealthAlarm.EMERGENCY)
				&& ((AbsolutePosition)e.getPropertyValue("position")).equals(((SAMUAlarmeSante) e).getPosition()))
			{
				as = e;
			}
		}
		
		if(as != null) {
			ArrayList<EventI> matchedEvents = new ArrayList<EventI>();
			matchedEvents.add(as);
			return matchedEvents;
		} else {
			return null;
		}

	}

	@Override
	public boolean correlate(ArrayList<EventI> matchedEvents) {
		return true;
	}

	@Override
	public boolean filter(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		HealthCorrelatorI samuState = (CorrelateurSAMU)c;
		EventI e = matchedEvents.get(0);
		try {
			return samuState.estDansCentre(((SAMUAlarmeSante) e).getPosition()) && samuState.estAmbulDispo();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return false;
	}

	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		HealthCorrelatorI samuState = (CorrelateurSAMU)c;
		EventI e = matchedEvents.get(0);
		try {
			samuState.triggerAmbulance(((SAMUAlarmeSante) e).getPosition());
			System.out.println("S1 triggerAmbulance");
		} catch (Exception e1) {
			e1.printStackTrace();
		};
	}

	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		eb.removeEvent(matchedEvents.get(0));
	}

}

package cps.cep.samu.regles;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import cps.cep.interfaces.CorrelatorStateI;
import cps.cep.samu.CorrelateurSAMU;
import cps.cep.samu.HealthCorrelatorI;
import cps.cep.samu.evenements.SAMUAlarmeSante;
import cps.cep.samu.evenements.SAMUChuteDePersonne;
import cps.cep.samu.evenements.SAMUSignalOk;
import cps.evenements.interfaces.EventBaseI;
import cps.evenements.interfaces.EventI;
import cps.regles.interfaces.RuleI;
import fr.sorbonne_u.cps.smartcity.interfaces.TypeOfHealthAlarm;

/**
 * The class <code>S8.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 2022年4月10日</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public class S8 
implements RuleI
{
	@Override
	public ArrayList<EventI> match(EventBaseI eb) {
		EventI as = null;
		EventI s = null;
		for(int i = 0; i < eb.numberOfEvents() && (as == null || s == null); i++) {
			EventI e = eb.getEvent(i);
			if(e instanceof SAMUAlarmeSante && e.hasProperty("type") && e.hasProperty("personId") 
				&& ((TypeOfHealthAlarm)e.getPropertyValue("type")).equals(TypeOfHealthAlarm.TRACKING))
			{
				as = e;
			}
			if(e instanceof SAMUSignalOk && e.hasProperty("personId") ) {
				s = e;
			}
		}
		if(as != null && s != null) {
			ArrayList<EventI> matchedEvents = new ArrayList<EventI>();
			matchedEvents.add(as);
			matchedEvents.add(s);
			return matchedEvents;
		} else {
			return null;
		}
	}
	
	@Override
	public boolean correlate(ArrayList<EventI> matchedEvents) {
		return matchedEvents.get(0).getPropertyValue("personId").equals(
				matchedEvents.get(1).getPropertyValue("personId")) && 
				matchedEvents.get(0).getTimeStamp().isBefore(
						matchedEvents.get(1).getTimeStamp()) &&
				matchedEvents.get(0).getTimeStamp().plus(
						Duration.of(10, ChronoUnit.MINUTES)).isAfter(
								matchedEvents.get(1).getTimeStamp());
	}
	
	@Override
	public boolean filter(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		HealthCorrelatorI samuState = (CorrelateurSAMU)c;
		try {
			System.out.println("propager un evenement au samu plus proche");
			return !samuState.estMedecinDispo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	public void act(ArrayList<EventI> matchedEvents, CorrelatorStateI c) {
		HealthCorrelatorI samuState = (CorrelateurSAMU)c;
		SAMUChuteDePersonne trackingAlarm = new SAMUChuteDePersonne(matchedEvents);
		try {
			samuState.triggerPasserSamuSuiv(trackingAlarm);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(ArrayList<EventI> matchedEvents, EventBaseI eb) {
		eb.removeEvent(matchedEvents.get(0));
		eb.removeEvent(matchedEvents.get(1));
	}
	
}

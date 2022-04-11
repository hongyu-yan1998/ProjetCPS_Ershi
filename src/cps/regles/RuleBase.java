package cps.regles;

import java.util.ArrayList;

import cps.cep.interfaces.CorrelatorStateI;
import cps.evenements.interfaces.EventBaseI;
import cps.evenements.interfaces.EventI;
import cps.regles.interfaces.RuleI;

public class RuleBase {
	
	public ArrayList<RuleI> rules;
	
	public RuleBase() {
		this.rules = new ArrayList<RuleI>();
	}

	public void addRule(RuleI r) {
		this.rules.add(r);
	}
	
	public boolean fireFirstOn(EventBaseI eb, CorrelatorStateI c) {
		for (RuleI r : rules) {
			ArrayList<EventI> matchedEvents = r.match(eb);
			if(matchedEvents != null && 
			   r.correlate(matchedEvents) && 
			   r.filter(matchedEvents, c)) 
			{
				r.act(matchedEvents, c);
				r.update(matchedEvents, eb);
				return true;
			}
		}
		return false;
	}
	
	public boolean fireAllOn(EventBaseI eb, CorrelatorStateI c) {
		boolean flag = false;
		for (RuleI r : rules) {
			ArrayList<EventI> matchedEvents = r.match(eb);
			if(matchedEvents != null && 
			   r.correlate(matchedEvents) && 
			   r.filter(matchedEvents, c)) 
			{
				r.act(matchedEvents, c);
				r.update(matchedEvents, eb);
				flag = true;
			}
		}
		return flag;
	}
}

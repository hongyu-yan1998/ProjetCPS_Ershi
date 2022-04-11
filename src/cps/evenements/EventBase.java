package cps.evenements;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;

import cps.evenements.interfaces.EventBaseI;
import cps.evenements.interfaces.EventI;

public class EventBase implements EventBaseI {

	private ArrayList<EventI> events;
	
	public EventBase() {
		events = new ArrayList<EventI>();
	}

	@Override
	public void addEvent(EventI e) {
		events.add(e);
	}

	@Override
	public void removeEvent(EventI e) {
		events.remove(e);
	}

	@Override
	public EventI getEvent(int i) {
		return events.get(i);
	}

	@Override
	public int numberOfEvents() {
		return events.size();
	}

	@Override
	public boolean appearsIn(EventI e) {
		return events.contains(e);
	}

	@Override
	public void clearEvents(Duration d) {
		if(d == null) {
			events.removeAll(events);
		}else {
			LocalTime now = LocalTime.now();
			events.removeIf(e -> 
				d.getNano() < Duration.between(now, e.getTimeStamp()).getNano());
		}
	}

}

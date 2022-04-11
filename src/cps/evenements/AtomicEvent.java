package cps.evenements;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.HashMap;

import cps.evenements.interfaces.AtomicEventI;
import fr.sorbonne_u.cps.smartcity.utils.TimeManager;

public class AtomicEvent implements AtomicEventI {

	private static final long serialVersionUID = 1L;
	private LocalTime createTime;
	private HashMap<String, Serializable> properties; 
	
	public AtomicEvent() {
		properties = new HashMap<String, Serializable>();
		createTime = TimeManager.get().getCurrentLocalTime();
	}

	@Override
	public LocalTime getTimeStamp() {
		return createTime;
	}
	
	public void setCreateTime(LocalTime createTime) {
		this.createTime = createTime;
	}

	@Override
	public boolean hasProperty(String name) {
		return properties.containsKey(name);
	}

	@Override
	public Serializable getPropertyValue(String name) {
		return properties.get(name);
	}

	@Override
	public Serializable putProperty(String name, Serializable value) {
		return properties.put(name, value);
	}

	@Override
	public void removeProperty(String name) {
		properties.remove(name);
	}

}

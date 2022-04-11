package cps.cep.connections;

import cps.cep.interfaces.EventReceptionCI;
import cps.evenements.interfaces.EventI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class EventReceptionOutboundPort 
extends AbstractOutboundPort 
implements EventReceptionCI {

	private static final long serialVersionUID = 1L;
	
	public EventReceptionOutboundPort(ComponentI owner)
	throws Exception 
	{
		super(EventReceptionCI.class, owner);
	}

	public EventReceptionOutboundPort(String uri, ComponentI owner)
	throws Exception 
	{
		super(uri, EventReceptionCI.class, owner);
	}
	
	@Override
	public void receiveEvent(String emitterURI, EventI event) throws Exception {
		((EventReceptionCI)this.getConnector()).
			receiveEvent(emitterURI, event);

	}

	@Override
	public void receiveEvents(String emitterURI, EventI[] events) throws Exception {
		((EventReceptionCI)this.getConnector()).
		receiveEvents(emitterURI, events);
	}

}

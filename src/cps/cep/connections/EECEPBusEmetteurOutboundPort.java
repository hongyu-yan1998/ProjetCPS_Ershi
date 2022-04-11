package cps.cep.connections;

import cps.cep.interfaces.EventEmissionCI;
import cps.evenements.interfaces.EventI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class EECEPBusEmetteurOutboundPort 
extends AbstractOutboundPort 
implements EventEmissionCI {
	
	private static final long serialVersionUID = 1L;

	public EECEPBusEmetteurOutboundPort(ComponentI owner)
			throws Exception {
		super(EventEmissionCI.class, owner);
	}

	public EECEPBusEmetteurOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, EventEmissionCI.class, owner);
	}

	@Override
	public void sendEvent(String emitterURI, EventI event) throws Exception {
		((EventEmissionCI)this.getConnector()).
			sendEvent(emitterURI, event);
	}

	@Override
	public void sendEvents(String emitterURI, EventI[] events) throws Exception {
		((EventEmissionCI)this.getConnector()).
			sendEvents(emitterURI, events);
	}

}

package cps.cep.connector;

import cps.cep.interfaces.EventEmissionCI;
import cps.evenements.interfaces.EventI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * The class <code>CBCOEventEmissionConnector.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 2022年3月11日</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public class CBCOEventEmissionConnector 
extends AbstractConnector 
implements EventEmissionCI 
{

	@Override
	public void sendEvent(String emitterURI, EventI event) throws Exception {
		((EventEmissionCI)this.offering).sendEvent(emitterURI, event);
	}

	@Override
	public void sendEvents(String emitterURI, EventI[] events) throws Exception {
		((EventEmissionCI)this.offering).sendEvents(emitterURI, events);
	}

}

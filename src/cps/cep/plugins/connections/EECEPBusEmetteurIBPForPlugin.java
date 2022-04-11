package cps.cep.plugins.connections;

import cps.cep.interfaces.EventEmissionCI;
import cps.cep.interfaces.EventEmissionI;
import cps.evenements.interfaces.EventI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import cps.cep.plugins.BusPlugin;

/**
 * The class <code>EECEPBusEmetteurIBPForPlugin.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 2022年4月4日</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public class EECEPBusEmetteurIBPForPlugin 
extends AbstractInboundPort
implements EventEmissionCI {

	private static final long serialVersionUID = 1L;

	public EECEPBusEmetteurIBPForPlugin(
			ComponentI owner,
			String pluginURI
			)throws Exception {
		super(EventEmissionCI.class, owner, pluginURI, null);
		assert owner instanceof EventEmissionI;
	}

	public EECEPBusEmetteurIBPForPlugin(
			String uri, 
			ComponentI owner,
			String pluginURI
			)throws Exception {
		super(uri, EventEmissionCI.class, owner, pluginURI, null);
		assert owner instanceof EventEmissionI;
	}


	@Override
	public void sendEvent(String emitterURI, EventI event) throws Exception {
		this.getOwner().handleRequest(
			new AbstractComponent.AbstractService<Void>(this.getPluginURI()) {
				@Override
				public Void call() throws Exception {
					((BusPlugin)this.getServiceProviderReference()).
												sendEvent(emitterURI, event);
					return null;
				}
			});		
	}

	@Override
	public void sendEvents(String emitterURI, EventI[] events) throws Exception {
		this.getOwner().handleRequest(
			new AbstractComponent.AbstractService<Void>(this.getPluginURI()) {
				@Override
				public Void call() throws Exception {
					((BusPlugin)this.getServiceProviderReference()).
												sendEvents(emitterURI, events);
					return null;
				}
			});			
	}

}

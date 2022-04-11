package cps.cep.connections;

import cps.cep.interfaces.CEPBusManagementCI;
import cps.cep.components.CEPBus;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

/**
*
* <p><strong>Description</strong></p>
* 
* The class <code>BMCEPBusEmetteurInboundPort</code> implements the
 * inbound port for the {@code CEPBusManagementCI} interface. 
 * It's the port of CEPBusFacade
* 
* 
* @author	 <p>Hongyu YAN & Liuyi CHEN</p>
*/
public class BMCEPBusEmetteurInboundPort 
extends AbstractInboundPort 
implements CEPBusManagementCI 
{
	private static final long serialVersionUID = 1L;

	public BMCEPBusEmetteurInboundPort(ComponentI owner)
			throws Exception {
		super(CEPBusManagementCI.class, owner);
		assert owner instanceof CEPBus;
	}

	public BMCEPBusEmetteurInboundPort(String uri, ComponentI owner)
			throws Exception { 
		super(uri, CEPBusManagementCI.class, owner);
		assert owner instanceof CEPBus;
	}

	@Override
	public String registerEmitter(String uri) throws Exception {
		return this.getOwner().handleRequest(
				b -> ((CEPBus)b).registerEmitter(uri));
	}

	@Override
	public void unregisterEmitter(String uri) throws Exception {
		this.getOwner().handleRequest(
				b -> { ((CEPBus)b).unregisterEmitter(uri);
						return null;
				});
	}

	@Override
	public String registerCorrelator(String uri, String inboundPortURI) throws Exception {
		return this.getOwner().handleRequest(
				b -> ((CEPBus)b).registerCorrelator(uri, inboundPortURI));
	}

	@Override
	public void unregisterCorrelator(String uri) throws Exception {
		this.getOwner().handleRequest(
				b -> { ((CEPBus)b).unregisterCorrelator(uri);
						return null;
				});
	}

	@Override
	public void registerExecutor(String uri, String inboundPortURI) throws Exception {
		this.getOwner().handleRequest(
				b -> { ((CEPBus)b).registerExecutor(uri, inboundPortURI);
					return null;
				});
	}

	@Override
	public String getExecutorInboundPortURI(String uri) throws Exception {
		return this.getOwner().handleRequest(
				b -> ((CEPBus)b).getExecutorInboundPortURI(uri));
	}

	@Override
	public void unregisterExecutor(String uri) throws Exception {
		this.getOwner().handleRequest(
				b -> { ((CEPBus)b).unregisterExecutor(uri);
						return null;
				});
	}

	@Override
	public void subscribe(String subscriberURI, String emitterURI) throws Exception {
		this.getOwner().handleRequest(
				b -> { ((CEPBus)b).subscribe(uri, emitterURI);
						return null;
				});
	}

	@Override
	public void unsubscribe(String subscriberURI, String emitterURI) throws Exception {
		this.getOwner().handleRequest(
				b -> { ((CEPBus)b).unsubscribe(uri, emitterURI);
						return null;
				});
	}

}

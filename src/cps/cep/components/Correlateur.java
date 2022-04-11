package cps.cep.components;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cps.cep.connections.BMCEPBusCorrOutboundPort;
import cps.cep.connections.EECEPBusCorreOutboundPort;
import cps.cep.connections.EventReceptionInboundPort;
import cps.cep.connector.CBCOBusManagerConnector;
import cps.cep.connector.CBEMEventEmissionConnector;
import cps.cep.interfaces.CEPBusManagementCI;
import cps.cep.interfaces.CorrelatorStateI;
import cps.cep.interfaces.EventEmissionCI;
import cps.cep.interfaces.EventReceptionCI;
import cps.evenements.EventBase;
import cps.evenements.interfaces.EventBaseI;
import cps.evenements.interfaces.EventI;
import cps.regles.RuleBase;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

/**
 * The class <code>Correlateur.java</code>
 *
 * <p>
 * <strong>Description</strong>
 * </p>
 * 
 * <p>
 * Created on : 2022年3月10日
 * </p>
 * 
 * @author
 *         <p>
 *         Hongyu YAN & Liuyi CHEN
 *         </p>
 */
@OfferedInterfaces(offered = { EventReceptionCI.class })
@RequiredInterfaces(required = { EventEmissionCI.class, CEPBusManagementCI.class })
public class Correlateur 
extends AbstractComponent 
implements CorrelatorStateI
{
	/** identifier of the corresponding Correlateur SAMU. */
	protected String corre_id;
	/** URI of the event emission inbound port. */
	protected String eventEmissionInboundPortURI;
	/** URI of the cep bus inbound port. */
	protected String cepBusManageInboundPortURI;
	/** URI of the executor inbound port. */
	protected String actionExecutionInboundPortURI;
	/** URI of the samu notification inbound port. */
	protected String notificationInboundPortURI;

	/** eventEmission outbound port. */
	protected EECEPBusCorreOutboundPort eeOBP;
	/** cepBus outbound port. */
	protected BMCEPBusCorrOutboundPort cbcOBP;
	
	/** eventEmission inbound port. */
	protected EventReceptionInboundPort erIBP;

	protected EventBaseI eventBase = new EventBase();
	protected RuleBase ruleBase = new RuleBase();
	
	protected ArrayList<String> executorURIs = new ArrayList<String>();
	protected ArrayList<String> emitterURIs = new ArrayList<String>();
	
	/** the URI that will be used for the plug-in (assumes a singleton).	*/
	protected final static String	CORRE_PLUGIN_URI = "corre-plugin-uri";
	
	public static final int N = 2;
	public static final int CAPACITY = 5;
	protected ThreadPoolExecutor executor_event = 
			new ThreadPoolExecutor(N, N, 0L, TimeUnit.MILLISECONDS, 
					new ArrayBlockingQueue<Runnable>(CAPACITY),
					new ThreadPoolExecutor.CallerRunsPolicy());
	protected ThreadPoolExecutor executor_rule = 
			new ThreadPoolExecutor(N, N, 0L, TimeUnit.MILLISECONDS, 
					new ArrayBlockingQueue<Runnable>(CAPACITY),
					new ThreadPoolExecutor.CallerRunsPolicy());

	public Correlateur(
			String corre_id, 
			String cepBusManageInboundPortURI, 
			String eventReceptionInboundPortURI,
			ArrayList<String> executorURIs,
			ArrayList<String> emitterURIs,
			RuleBase ruleBase) throws Exception 
	{
		super(2, 0);

		assert eventReceptionInboundPortURI != null && !eventReceptionInboundPortURI.isEmpty();
		assert !executorURIs.isEmpty();
		
		this.corre_id = corre_id;
		this.executorURIs = executorURIs;
		this.emitterURIs = emitterURIs;
		this.cepBusManageInboundPortURI = cepBusManageInboundPortURI;

		this.erIBP = new EventReceptionInboundPort(eventReceptionInboundPortURI, this);
		this.erIBP.publishPort();

		this.eeOBP = new EECEPBusCorreOutboundPort(this);
		this.eeOBP.publishPort();

		this.cbcOBP = new BMCEPBusCorrOutboundPort(this);
		this.cbcOBP.publishPort();
		
		this.ruleBase = ruleBase;
	}

	@Override
	public synchronized void start() throws ComponentStartException {
		super.start();
		try {
			this.doPortConnection(this.cbcOBP.getPortURI(), this.cepBusManageInboundPortURI,
					CBCOBusManagerConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
	}
	
	@Override
	public synchronized void execute() throws Exception {
		super.execute();
		
		this.eventEmissionInboundPortURI = 
				this.cbcOBP.registerCorrelator(corre_id, erIBP.getPortURI());
		/** Connection entre Correlator et Bus (EventEmission) */
		this.doPortConnection(this.eeOBP.getPortURI(), this.eventEmissionInboundPortURI,
				CBEMEventEmissionConnector.class.getCanonicalName());
		for (String uri : emitterURIs) {
			this.cbcOBP.subscribe(corre_id, uri);
		}
	}

	@Override
	public synchronized void finalise() throws Exception {
		this.doPortDisconnection(this.eeOBP.getPortURI());
		this.doPortDisconnection(this.cbcOBP.getPortURI());
		super.finalise();
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.eeOBP.unpublishPort();
			this.cbcOBP.unpublishPort();
			this.erIBP.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	/**
	 * 
	 * @param emitterURI l'uri d'émetteur
	 * @param event      un évènement à ajouter dans la base des évènements
	 * @throws Exception
	 */
	public void receiveEvent(String emitterURI, EventI event) throws Exception {
		Runnable RegisterTask_event = ()->{
			try {
				this.eventBase.addEvent(event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		executor_event.submit(RegisterTask_event);
		
		Runnable RegisterTask_rule = ()->{
			try {
				this.ruleBase.fireFirstOn(eventBase, this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		executor_rule.submit(RegisterTask_rule);
		
	}

	/**
	 * 
	 * @param emitterURI l'uri d'émetteur
	 * @param events     des évènements à ajouter dans la base des évènements
	 */
	public void receiveEvents(String emitterURI, EventI[] events) throws Exception {
		Runnable RegisterTask_event = ()->{
			try {
				for (EventI event : events) {
					this.eventBase.addEvent(event);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		executor_event.submit(RegisterTask_event);
		
		Runnable RegisterTask_rule = ()->{
			try {
				this.ruleBase.fireAllOn(eventBase, this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		executor_rule.submit(RegisterTask_rule);
		
		
	}
	
	/**
	 * 
	 * @param emitterURI l'uri d'émetteur
	 * @param event      
	 */
	public void sendEvent(String emitterURI, EventI event) throws Exception {
		this.eeOBP.sendEvent(emitterURI, event);
	}

	/**
	 * 
	 * @param emitterURI l'uri d'émetteur
	 * @param events     
	 */
	public void sendEvents(String emitterURI, EventI[] events) throws Exception {
		this.eeOBP.sendEvents(emitterURI, events);
	}

}

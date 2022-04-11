package cps.cep.components;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cps.cep.connections.BMCEPBusCorrInboundPort;
import cps.cep.connections.BMCEPBusEmetteurInboundPort;
import cps.cep.connections.EECEPBusCorrInboundPort;
import cps.cep.connections.EventReceptionOutboundPort;
import cps.cep.connector.EventReceptionConnector;
import cps.cep.interfaces.CEPBusManagementCI;
import cps.cep.interfaces.EventEmissionCI;
import cps.cep.interfaces.EventEmissionI;
import cps.cep.interfaces.EventReceptionCI;
import cps.cep.plugins.BusPlugin;
import cps.evenements.interfaces.EventI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

@RequiredInterfaces(required = { EventReceptionCI.class })
@OfferedInterfaces(offered = { CEPBusManagementCI.class, EventEmissionCI.class })
public class CEPBus 
extends AbstractComponent
implements EventEmissionI {

	public static final String CEPBUS_URI = "cepbus";
	
	public static final String EEcepBusEmetteurIP_URI = "cepbus-emetteur-ip-uri-event-emission";
	public static final String BMcepBusEmetteurIP_URI = "cepbus-emetteur-ip-uri-cep-bus-management";
	public static final String EEcepBusCorreIP_URI = "cepbus-corre-ip-uri-event-emission";
	public static final String BMcepBusCorreIP_URI = "cepbus-corre-ip-uri-cep-bus-management";
	public static final String EROP_URI = "cepbus-op-uri-event-reception";
	
	protected final static String	BUS_PLUGIN_URI = "bus-plugin-uri";

	protected BMCEPBusEmetteurInboundPort bmCepBusEmmIBP;
	protected BMCEPBusCorrInboundPort bmCepBusCorrIBP;
	protected EECEPBusCorrInboundPort eeCepBusCorrIBP;;
	protected EventReceptionOutboundPort erop;

	// liste du uri des émetteurs
	protected Set<String> uriEmetteurs;
	// associations entre executeurs et son inboundPort
	protected ConcurrentHashMap<String, String> executeurs;
	// associations entre correlateurs et son inboundPort
	protected ConcurrentHashMap<String, String> correlateurs;
	// abonne correlateurs aux emetteurs ou correlateurs
	protected ConcurrentHashMap<String, HashSet<String>> abonneCorrelateurs;
	
	public static final int N = 3;
	public static final int CAPACITY = 5;
	protected ThreadPoolExecutor executor_ee = 
			new ThreadPoolExecutor(N, N, 0L, TimeUnit.MILLISECONDS, 
					new ArrayBlockingQueue<Runnable>(CAPACITY),
					new ThreadPoolExecutor.CallerRunsPolicy());
	protected ThreadPoolExecutor exec_subsribe = 
			new ThreadPoolExecutor(N, N, 0L, TimeUnit.MILLISECONDS, 
					new ArrayBlockingQueue<Runnable>(CAPACITY),
					new ThreadPoolExecutor.CallerRunsPolicy());

	protected CEPBus() throws Exception {
		super(2, 0);
		uriEmetteurs = new HashSet<>();
		executeurs = new ConcurrentHashMap<>();
		correlateurs = new ConcurrentHashMap<>();
		abonneCorrelateurs = new ConcurrentHashMap<>();
		
		this.bmCepBusEmmIBP = new BMCEPBusEmetteurInboundPort(BMcepBusEmetteurIP_URI, this);
		
		this.bmCepBusCorrIBP = new BMCEPBusCorrInboundPort(BMcepBusCorreIP_URI, this);
		this.eeCepBusCorrIBP = new EECEPBusCorrInboundPort(EEcepBusCorreIP_URI, this);
		
		this.erop = new EventReceptionOutboundPort(EROP_URI, this);
		
		this.bmCepBusEmmIBP.publishPort();
		this.bmCepBusCorrIBP.publishPort();
		this.eeCepBusCorrIBP.publishPort();
		this.erop.publishPort();
		
		BusPlugin plugin = new BusPlugin(EEcepBusEmetteurIP_URI);
		plugin.setPluginURI(BUS_PLUGIN_URI);
		this.installPlugin(plugin);
	}
	
	
	
	@Override
	public synchronized void start() throws ComponentStartException {
		super.start();
	}

	@Override
	public synchronized void execute() throws Exception {
		super.execute();
	}

	@Override
	public synchronized void finalise() throws Exception {
		super.finalise();
		this.doPortDisconnection(this.erop.getPortURI());
	}

	@Override
	public synchronized void shutdown() throws ComponentShutdownException {
		try {
			this.bmCepBusEmmIBP.unpublishPort();
			this.bmCepBusCorrIBP.unpublishPort();
			this.eeCepBusCorrIBP.unpublishPort();
			this.erop.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e);
		}
		super.shutdown();
	}

	/**
	 * enregistre l'émetteur d'événements
	 * 
	 * @param uri URI de l'émetteur
	 * @return URI de son port entrant offrant l'interface EventEmissionCI
	 *         (EEIP_URI)
	 */
	public String registerEmitter(String uri) throws Exception {
		synchronized (this) {
			uriEmetteurs.add(uri);
			return EEcepBusEmetteurIP_URI;
		}
	}

	/**
	 * supprime l'emetteur d'événements
	 * 
	 * @param uri URI de l'émetteur
	 */
	public void unregisterEmitter(String uri) throws Exception {
		synchronized (this) {
			uriEmetteurs.remove(uri);
		}
	}

	/**
	 * enregistre le correlator avec l'URI de son port entrant
	 * 
	 * @param uri            URI du correlator
	 * @param inboundPortURI port entrant de ce correlator
	 * @return URI de son port entrant offrant l'interface EventEmissionCI
	 *         (EEIP_URI)
	 */
	public String registerCorrelator(String uri, String inboundPortURI) throws Exception {
		correlateurs.put(uri, inboundPortURI);
		return EEcepBusCorreIP_URI;
	}

	/**
	 * supprime le correlator donné
	 * 
	 * @param uri URI du correlator
	 */
	public void unregisterCorrelator(String uri) throws Exception {
		correlateurs.remove(uri);
	}

	/**
	 * enregistrer l'association de uri de l'exécuteur et l'uri de port entrant de
	 * cet exécuteur
	 * 
	 * @param uri            URI de l'exécuteur
	 * @param inboundPortURI port entrant de l'exécuteur
	 */
	public void registerExecutor(String uri, String inboundPortURI) throws Exception {
		synchronized (this) {
			executeurs.put(uri, inboundPortURI);
		}
	}

	/**
	 * obtenir l'uri de port entrant de l'exécuteur
	 * 
	 * @param uri URI de l'exécuteur
	 * @return inboundPortURI port entrant de cet exécuteur
	 */
	public String getExecutorInboundPortURI(String uri) throws Exception {
		return executeurs.get(uri);
	}

	/**
	 * supprime l'exécuteur donné
	 * 
	 * @param uri URI de l'exécuteur
	 */
	public void unregisterExecutor(String uri) throws Exception {
		executeurs.remove(uri);
	}

	/**
	 * abonne emitteur aux correlateurs
	 * 
	 * @param subscriberURI l'uri de correlateur à abonner
	 * @param emitterURI    l'uri d'émetteur ou de correlateur
	 */
	public void subscribe(String subscriberURI, String emitterURI) throws Exception {
		Runnable RegisterTask = ()->{
			try {
				if (abonneCorrelateurs.containsKey(subscriberURI)) {
					abonneCorrelateurs.get(subscriberURI).add(emitterURI);
				} else {
					HashSet<String> abonne = new HashSet<String>();
					abonne.add(emitterURI);
					abonneCorrelateurs.put(subscriberURI, abonne);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		
		exec_subsribe.submit(RegisterTask);
		
	}

	/**
	 * desabonne le correlateur aux evenements emis par l'émetteur ou le correlateur
	 * 
	 * @param subscriberURI l'uri de correlateur à desabonner
	 * @param emitterURI    emitterURI l'uri d'émetteur ou de correlateur
	 */
	public void unsubscribe(String subscriberURI, String emitterURI) throws Exception {
		if (abonneCorrelateurs.containsKey(subscriberURI)) {
			abonneCorrelateurs.get(subscriberURI).remove(emitterURI);
		}
	}

	/**
	 * émettre un évènement à propager vers les destinataires abonnés aux évènements
	 * en provence de l'émetteur
	 * 
	 * @param emitterURI l'uri d'émetteur
	 * @param event      l'évènement à propager
	 */
	public void correReceiveEvent(String emitterURI, EventI event) throws Exception {
		for (String uri : abonneCorrelateurs.keySet()) {
			for (String uriAbonnement : abonneCorrelateurs.get(uri)) {
				if (emitterURI.equals(uriAbonnement)) {
					String inboundPortCorre = correlateurs.get(uri);
					this.doPortConnection(this.erop.getPortURI(), inboundPortCorre, EventReceptionConnector.class.getCanonicalName());
					this.erop.receiveEvent(emitterURI, event);
				}
			}
		}
		
	}

	/**
	 * envoyer un tableau d'évènements à propager vers les destinataires abonnés aux
	 * évènements en provence de l'émetteur
	 * 
	 * @param emitterURI l'uri d'émetteur
	 * @param events     l'évènement à propager
	 */
	public void correReceiveEvents(String emitterURI, EventI[] events) throws Exception {
		for (String uri : abonneCorrelateurs.keySet()) {
			for (String uriAbonnement : abonneCorrelateurs.get(uri)) {
				if (emitterURI.equals(uriAbonnement)) {
					String inboundPortCorre = correlateurs.get(uri);
					this.doPortConnection(this.erop.getPortURI(), inboundPortCorre, EventReceptionConnector.class.getCanonicalName());
					this.erop.receiveEvents(emitterURI, events);
					this.doPortDisconnection(this.erop.getPortURI());
				}
			}
		}
	}
	
	/** methodes offertes */
	
	/**
	 * recevoir un évènement et l'envoie aux corrélateurs
	 */
	@Override
	public void sendEvent(String emitterURI, EventI event) throws Exception {
		Runnable RegisterTask = ()->{
			try {
				this.correReceiveEvent(emitterURI, event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		
		executor_ee.submit(RegisterTask);
	}

	/**
	 * recevoir les évènements et les envoie aux corrélateurs
	 */
	@Override
	public void sendEvents(String emitterURI, EventI[] events) throws Exception {
		Runnable RegisterTask = ()->{
			try {
				this.correReceiveEvents(emitterURI, events);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		
		executor_ee.submit(RegisterTask);
	}
	
}

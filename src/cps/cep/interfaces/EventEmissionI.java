package cps.cep.interfaces;

import cps.evenements.interfaces.EventI;

/**
 * The class <code>EventEmissionI.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 2022年4月4日</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public interface EventEmissionI {
	public void sendEvent(String emitterURI, EventI event) throws Exception ;
	public void sendEvents(String emitterURI, EventI[] events) throws Exception ;

}

package cps.cep.trafficLight;

import cps.cep.interfaces.ActionI;

/**
 * The class <code>ActionsTrafficLight.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 2022年3月12日</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public enum ActionsTrafficLight implements ActionI{
	/**	passer l'intersection en mode priorité indiqué					*/
	ChangePriority,
	/**	passer l'intersection en mode normal				*/
	ReturnToNormalMode
	
}

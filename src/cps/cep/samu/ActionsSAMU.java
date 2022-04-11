package cps.cep.samu;

import cps.cep.interfaces.ActionI;

/**
 * The class <code>ActionsSAMU.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : 2022年3月11日</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public enum ActionsSAMU implements ActionI{

	/**	intervention d'ambulance					*/
	InterventionAmbulance,
	/**	intervention de medecin					*/
	InterventionMedecin,
	/**	un appel du medecin a personne specifique	*/
	InterventionTeleMedecin
}

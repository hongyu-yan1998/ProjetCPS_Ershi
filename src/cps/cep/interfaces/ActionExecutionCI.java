package cps.cep.interfaces;

import java.io.Serializable;

import fr.sorbonne_u.components.interfaces.OfferedCI;
import fr.sorbonne_u.components.interfaces.RequiredCI;

/**
 * The class <code>CVM.java</code> 
 *
 * <p><strong>Description</strong></p>
 * 
 * <p>Created on : date2022年2月21日 下午3:32:18</p>
 * 
 * @author	<p>Hongyu YAN & Liuyi CHEN</p>
 */
public interface ActionExecutionCI 
extends RequiredCI, OfferedCI, ActionExecutionI 
{
	@Override
	public ResponseI execute(ActionI a, Serializable[] params) throws Exception;
}

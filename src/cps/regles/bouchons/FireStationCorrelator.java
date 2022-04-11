package cps.regles.bouchons;

import java.util.ArrayList;

import cps.cep.fireStation.FireStationCorrelatorI;
import cps.evenements.interfaces.EventBaseI;
import cps.evenements.interfaces.EventI;
import fr.sorbonne_u.cps.smartcity.grid.AbsolutePosition;

public class FireStationCorrelator 
implements FireStationCorrelatorI {

	public static final double N = 100.0;
	/**disponibilite de grande echelle 				*/
	private boolean echelleDispo;
	/**disponibilite de camion stantard 			*/
	private boolean camionDispo;
	
	public FireStationCorrelator(EventBaseI eb) {
		this.echelleDispo = true;
		this.camionDispo = true;
	}
	
	@Override
	public boolean estDansZone(AbsolutePosition p) {
		return inGrid(p.getX()) && inGrid(p.getY());
	}
	
	/**
	 * return true if {@code v} in in the smart city grid, false otherwise.
	 * @param v	coordinate to be tested.
	 * @return	true if {@code v} is in the smart city grid, false otherwise.
	 */
	public static boolean inGrid(double v)
	{
		return v >= 0.0 && v <= N;
	}
	
	@Override
	public boolean estEchelleDispo() {
		return echelleDispo;
	}
	
	@Override
	public boolean estCamionDispo() {
		return true;
	}

	@Override
	public void triggerPremiereAlarmeImmeuble(AbsolutePosition pos, ArrayList<EventI> matchedEvents) throws Exception {
		System.out.println("First alarm triggered at " + pos +
				   " using resource grande echelle\n");
	}

	@Override
	public void triggerPremiereAlarmeMaison(AbsolutePosition pos, ArrayList<EventI> matchedEvents) throws Exception {
		System.out.println("First alarm triggered at " + pos +
				   " using resource stantard camion\n");
	}

	@Override
	public void triggerGeneraleAlarme(ArrayList<EventI> matchedEvents) throws Exception {
		System.out.println("General alarm triggered");
	}

	@Override
	public void triggerGeneraleAlarmePosition(AbsolutePosition pos, ArrayList<EventI> matchedEvents) throws Exception {
		System.out.println("General alarm triggered at " + pos + "\n");
	}

	@Override
	public void triggerSecondeAlarme(AbsolutePosition pos, ArrayList<EventI> matchedEvents) throws Exception {
		System.out.println("Second alarm triggered at " + pos + "\n");
	}

	@Override
	public void setEchelleDispo(boolean ed) throws Exception {
		this.echelleDispo = ed;
	}

	@Override
	public void setCamionDispo(boolean ecd) throws Exception {
		this.camionDispo = ecd;
	}

	@Override
	public EventBaseI getEb() throws Exception {
		return null;
	}
	
}

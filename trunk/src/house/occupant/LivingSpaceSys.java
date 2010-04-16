package house.occupant;

import java.util.ArrayList;

import util.BoundedRand;
import house.Consumer;
import house.simulation.ThermalSys;
import house.thermostat.ThermostatSys;
import TranRunJLite.TrjSys;
import TranRunJLite.TrjTime;

/**
 * Implements all tasks that occupy the living spaces
 * 
 * @author bill
 * 
 */
public class LivingSpaceSys extends TrjSys implements Consumer {

	private String Name;
	private ArrayList<OccupantTask> occupantList;
	private ThermalSys therm;
	private ThermostatSys tStat;
	private int numHome = 0;

	public LivingSpaceSys(String name, TrjTime tm, BoundedRand rand,
			ArrayList<OccupantParams> paramList, ThermalSys therm,
			ThermostatSys tStat) {
		super(tm);

		// initialize the occupant list
		occupantList = new ArrayList<OccupantTask>(paramList.size());
		// construct the occupants and put them inside list
		for (int i = 0; i <= paramList.size(); i++) {
			String tName = "Occupant " + 0;
			occupantList.add(new OccupantTask(tName, this, paramList.get(i), i,
					rand));
		}
		
		this.therm = therm;
		this.tStat = tStat;
	}

	@Override
	public double getP() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	double getTempInside(){
		return therm.getTempInside();
	}
	
	double getSetpointTemp(){
		return tStat.getSetpointTemp();
	}
	
	void specifyHome(){
		numHome += 1;
	}
	
	void specifyAway(){
		numHome -= 1;
	}
	
	int getTotalHome(){
		return numHome;
	}
	
	int getDRState(){
		return 0;
	}
	
	void adjustSetpoint(double dT){
		System.out.println("Attempt to change the setpoint.  This is not implemented");
	}
}

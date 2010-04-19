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
		for (int i = 0; i < paramList.size(); i++) {
			String tName = "Occupant " + 0;
			occupantList.add(new OccupantTask(tName, this, paramList.get(i), i,
					rand));
			specifyHome();
		}

		this.therm = therm;
		this.tStat = tStat;
	}

	public void setThermalSys(ThermalSys sys) {
		this.therm = sys;
	}

	public void setThermostatSys(ThermostatSys sys) {
		this.tStat = sys;
	}

	@Override
	public double getP() {
		// TODO Auto-generated method stub
		return 0;
	}

	double getTempInside() {
		return therm.getTempInside();
	}

	double getSetpointTemp() {
		return tStat.getSetpointTemp();
	}

	void specifyHome() {
		numHome += 1;
	}

	void specifyAway() {
		numHome -= 1;
	}

	int getTotalHome() {
		return numHome;
	}

	int getDRState() {
		return 0;
	}

	void adjustSetpoint(double dT) {
		System.out
				.println("Attempt to change the setpoint.  This is not implemented");
	}

	public int getNumWorking() {
		int numWorking = 0;
		for (OccupantTask task : occupantList) {
			if (task.getWorking())
				numWorking++;
		}
		return numWorking;
	}

	public int getNumDayShift() {
		int numDay = 0;
		for (OccupantTask task : occupantList) {
			if (task.getDayShift())
				numDay++;
		}
		return numDay;
	}

	public double getAvgWakeTime() {
		double out = 0.;
		for (OccupantTask task : occupantList) {
			out += task.getWakeTime();
		}
		return out / occupantList.size();
	}

	public double getAvgSleepTime() {
		double out = 0.;
		for (OccupantTask task : occupantList) {
			out += task.getSleepTime();
		}
		return out / occupantList.size();
	}

	public double getAvgLeaveTime() {
		double out = 0.;
		for (OccupantTask task : occupantList) {
			out += task.getLeaveTime();
		}
		return out / occupantList.size();
	}

	public double getAvgArriveTime() {
		double out = 0.;
		for (OccupantTask task : occupantList) {
			out += task.getArriveTime();
		}
		return out / occupantList.size();
	}

	public double getAvgComfortTemp() {
		double out = 0.;
		for (OccupantTask task : occupantList) {
			out += task.getComfortTemp();
		}
		return out / occupantList.size();
	}

	public double getAvgSleepTemp() {
		double out = 0.;
		for (OccupantTask task : occupantList) {
			out += task.getSleepTemp();
		}
		return out / occupantList.size();
	}

	public double getAvgAwayTemp() {
		double out = 0.;
		for (OccupantTask task : occupantList) {
			out += task.getAwayTemp();
		}
		return out / occupantList.size();
	}
}

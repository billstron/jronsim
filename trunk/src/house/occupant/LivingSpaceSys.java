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
	private ArrayList<ApplianceAutoTask> autoList;
	private ArrayList<ApplianceTimedCycleTask> timedList;
	private ArrayList<ApplianceManualTask> manualList;
	private ThermalSys therm;
	private ThermostatSys tStat;
	private int numHome = 0;
	private double[] PFridgeHL = { 94., 343.};
	private double[] PDryerHL = { 1800., 5000.};
	private double[] dryerCycle = { 45. * 60., 5. * 60. };
	private double[] PCompHL = { 160., 240. };

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
		}

		// initialize the automatic appliance list
		autoList = new ArrayList<ApplianceAutoTask>(3);
		// construct a refrigerator task
		double dtFridge = 5.;
		double Pfridge = rand.getBoundedRand(PFridgeHL[0], PFridgeHL[1]);
		double[] fridgeCycle = { Double.POSITIVE_INFINITY, 0 };
		double[] fridgeOff = { 0., 0. };
		autoList.add(new ApplianceAutoTask("Refrigerator", this, dtFridge,
				Pfridge, 0., fridgeCycle, fridgeOff, rand));

		// initialize the timed cycle list
		timedList = new ArrayList<ApplianceTimedCycleTask>(3);
		// construct the electric clothes dryer task
		double dtDryer = 5.;
		double Pdryer = rand.getBoundedRand(PDryerHL[0], PDryerHL[1]);
		timedList.add(new ApplianceTimedCycleTask("Clothes Dryer", this,
				dtDryer, Pdryer, 0., dryerCycle, rand));

		// initialize the timed cycle list
		manualList = new ArrayList<ApplianceManualTask>(3);
		// construct a computer task
		double dtComputer = 5.;
		double Pcomputer = rand.getBoundedRand(PCompHL[0], PCompHL[1]);
		manualList.add(new ApplianceManualTask("Computer", this, dtComputer,
				Pcomputer, 0., rand));

		this.therm = therm;
		this.tStat = tStat;
	}

	public void switchOnApplianceTimedCycle(int i) {
		//System.out.println("switchOnApplianceTimedCycle( " + i + " )");
		timedList.get(i).switchOn();
	}

	public boolean isOnApplianceTimedCycle(int i) {
		return timedList.get(i).isOn();
	}

	public void switchOnApplianceManual(int i) {
		//System.out.println("switchOnApplianceManual( " + i + " )");
		manualList.get(i).switchOn();
	}

	public void switchOffApplianceManual(int i) {
		//System.out.println("switchOffApplianceManual( " + i + " )");
		manualList.get(i).switchOff();
	}

	public boolean isOnApplianceManual(int i) {
		return manualList.get(i).isOn();
	}

	public void setThermalSys(ThermalSys sys) {
		this.therm = sys;
	}

	public void setThermostatSys(ThermostatSys sys) {
		this.tStat = sys;
	}

	@Override
	public double getP() {
		double P = 0;
		for(ApplianceAutoTask tsk : autoList)
			P += tsk.getP();
		for(ApplianceTimedCycleTask tsk : timedList)
			P += tsk.getP();
		for(ApplianceManualTask tsk : manualList)
			P += tsk.getP();
		return P;
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
		//System.out.println("adjustSetpoint( " + dT + " )");
		tStat.setSetpointChange(dT);
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

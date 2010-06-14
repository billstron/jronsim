/*
 * Copyright (c) 2010, Regents of the University of California
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *  * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *  * Neither the name of the University of California, Berkeley
 * nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package edu.berkeley.me.jRonSim.house.occupant;

import java.util.ArrayList;

import edu.berkeley.me.jRonSim.house.Consumer;
import edu.berkeley.me.jRonSim.house.simulation.ThermalSys;
import edu.berkeley.me.jRonSim.house.thermostat.ThermostatSys;
import edu.berkeley.me.jRonSim.util.BoundedRand;

import TranRunJLite.TrjSys;
import TranRunJLite.TrjTime;

/**
 * Implements all tasks that occupy the living spaces
 * 
 * @author William Burke <billstron@gmail.com>
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

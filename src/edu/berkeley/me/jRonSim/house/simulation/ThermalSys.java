/*
 * Copyright (c) 2009, Regents of the University of California
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
package edu.berkeley.me.jRonSim.house.simulation;

import edu.berkeley.me.jRonSim.house.Consumer;
import TranRunJLite.*;

/**
 * The thermal simulation system.
 * 
 * @author William Burke <billstron@gmail.com>
 */
public class ThermalSys extends TrjSys implements HouseIO, Consumer {

	String name;
	private HouseThermalSimTask thermSim;
	private HvacUnitTask acTask;
	private HvacUnitTask heaterTask;

	/**
	 * construct the thermal simulation using default parameters.
	 * 
	 * @param name
	 *            -- name of the simulation
	 * @param tm
	 *            -- time structure to be used
	 */
	public ThermalSys(String name, TrjTime tm) {
		super(tm);
		// Create some default parameters
		ThermalParams params = new ThermalParams();
		ThermalSysInit(name, params);
	}

	/**
	 * Construct the thermal simulation using specified parameters.
	 * 
	 * @param name
	 * @param tm
	 * @param params
	 */
	public ThermalSys(String name, TrjTime tm, ThermalParams params) {
		super(tm);
		ThermalSysInit(name, params);
	}

	/**
	 * Initalize the Thermal System
	 * 
	 * @param name
	 * @param params
	 */
	private void ThermalSysInit(String name, ThermalParams params) {
		this.name = name;

		ThermalUnit[] unitList = new ThermalUnit[5];
		// Construct the cooler unit
		HvacThermalUnit cooler = new HvacThermalUnit(HvacThermalUnit.COOLER,
				params.coolerMass, params.coolerFanMax,
				params.coolerFanEfficiency, params.coolerHeatInputMax,
				params.coolerHeatEfficiency);
		unitList[HouseThermalSimTask.COOLER_I] = cooler;

		// Construct the heater unit
		HvacThermalUnit heater = new HvacThermalUnit(HvacThermalUnit.HEATER,
				params.heaterMass, params.heaterfanMax,
				params.heaterFanEfficiency, params.heaterHeatInputMax,
				params.heaterHeatEfficiency);
		unitList[HouseThermalSimTask.HEATER_I] = heater;

		// Construct the external wall unit
		WallThermalUnit extWall = new WallThermalUnit(params.extWallMass,
				HouseThermalSimTask.EXTWALL_I, params.extWallKair,
				params.extWallKamb);
		unitList[HouseThermalSimTask.EXTWALL_I] = extWall;

		// construct the internal wall unit
		WallThermalUnit intWall = new WallThermalUnit(params.intWallMass,
				HouseThermalSimTask.INTWALL_I, params.intWallKair);
		unitList[HouseThermalSimTask.INTWALL_I] = intWall;

		// construct the air unit
		AirThermalUnit air = new AirThermalUnit(params.airMass,
				params.windowArea, params.infiltrationFlow,
				params.internalInput, extWall, intWall, cooler, heater);
		unitList[HouseThermalSimTask.AIR_I] = air;

		thermSim = new HouseThermalSimTask("House Simulation", this, unitList,
				params.initTemp, true);
		thermSim.setOutSideTemp(100);
		thermSim.setSolarRadiation(0);

		double dtUnit = 1;
		acTask = new HvacUnitTask("AC Opperatons Task", this, dtUnit,
				false /* not a heater */, 60, 60, thermSim);
		heaterTask = new HvacUnitTask("Heater Opperatons Task", this, dtUnit,
				true /* a heater */, 90, 90, thermSim);
	}

	/**
	 * get inside temperature
	 * 
	 * @return
	 */
	public double getTempInside() {
		return thermSim.getTin();
	}

	/**
	 * get heater state
	 * 
	 * @return heater on state
	 */
	public boolean getHeaterOnState() {
		return heaterTask.getOnState();
	}

	/**
	 * set the heater on state
	 * 
	 * @param state
	 */
	public void setHeaterOnState(boolean state) {
		// if the desired state is on then send the turn on command
		if (state) {
			heaterTask.SetCommand(HvacUnitTask.TURN_ON);
		}
		// if the desired state is off then send the turn off command
		else {
			heaterTask.SetCommand(HvacUnitTask.TURN_OFF);
		}
	}

	/**
	 * get the cooler state
	 * 
	 * @return
	 */
	public boolean getCoolerOnState() {
		return acTask.getOnState();
	}

	/**
	 * set the cooler state
	 * 
	 * @param state
	 */
	public void setCoolerOnState(boolean state) {
		// if the desired state is on then send the turn on command
		if (state) {
			acTask.SetCommand(HvacUnitTask.TURN_ON);
		}
		// if the desired state is off then send the turn off command
		else {
			acTask.SetCommand(HvacUnitTask.TURN_OFF);
		}
	}

	/**
	 * get the outside temperature
	 * 
	 * @return
	 */
	public double getOutsideTemp() {
		return thermSim.getTout();
	}

	/**
	 * Set the current outside temperature
	 * 
	 * @param Tout
	 */
	public void setOutsideTemp(double Tout) {
		thermSim.setOutSideTemp(Tout);
	}

	/**
	 * Set the current solar radiation.
	 * 
	 * @param sRad
	 */
	public void setSolarRadiation(double sRad) {
		thermSim.setSolarRadiation(sRad);
	}

	/**
	 * Return the power consumption of the edu.berkeley.me.jRonSim.house.
	 * 
	 * @return
	 */
	public double getP() {
		double p = thermSim.getP();
		return p;
	}
}

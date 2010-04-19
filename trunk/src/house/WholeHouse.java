/*
Copyright (c) 2009, Regents of the University of California
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
 * Neither the name of the University of California, Berkeley
nor the names of its contributors may be used to endorse or promote
products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package house;

import TranRunJLite.*;
import aggregator.environment.Envelope;
import house.appliances.AutoAppliancesSys;
import house.occupant.LivingSpaceSys;
import house.occupant.OccupantParams;
import house.simulation.*;
import java.io.PrintWriter;
import house.thermostat.*;
import java.util.ArrayList;

import util.BoundedRand;

/**
 * The house object contains a thermostat and a thermal simulation of a house.
 * 
 * @author William Burke <billstron@gmail.com>
 */
public class WholeHouse implements Envelope, House {

	private String name;
	private int idNum;
	private ArrayList<TrjSys> sysList = new ArrayList<TrjSys>();
	private ArrayList<Consumer> consumList = new ArrayList<Consumer>();
	private double Pdemand = 0; // aggregate demand

	// indicates the position of the systems in the sysList
	private final static int TSTAT_I = 0;
	private final static int THERM_I = 1;
	private final static int APPLI_I = 2;
	private final static int SPACE_I = 3;

	/**
	 * Construct a Pct House using specified thermal parameters
	 * 
	 * @param name
	 * @param tm
	 * @param idNum
	 * @param params
	 */
	public WholeHouse(String name, TrjTime tm, int idNum,
			ThermalParams thermPar, ThermostatParams tstatPar,
			ArrayList<OccupantParams> occParList, BoundedRand rand) {

		// create the living spaces system
		LivingSpaceSys space = new LivingSpaceSys("Room and Space", tm, rand,
				occParList, null, null);

		// Create the thermal system with the default parameters
		ThermalSys therm = new ThermalSys("Specific House", tm, thermPar);

		// Create the thermostat system
		ThermostatSys tstat = new ThermostatSys("Basic Thermostat", tm, therm,
				tstatPar);

		// Create the automatic appliance system
		AutoAppliancesSys appliances = new AutoAppliancesSys(
				"Automatic Appliances", tm, rand);

		// Add the systems into the living space
		space.setThermalSys(therm);
		space.setThermostatSys(tstat);

		// Initialize the house variables
		initWholeHouse(name, tm, idNum, therm, tstat, appliances, space);
	}

	/**
	 * Initialize the Pct House variables
	 * 
	 * @param name
	 * @param tm
	 * @param idNum
	 * @param therm
	 * @param tstat
	 */
	private void initWholeHouse(String name, TrjTime tm, int idNum,
			ThermalSys therm, ThermostatSys tstat,
			AutoAppliancesSys appliances, LivingSpaceSys space) {
		// Initialize the simulation
		this.name = name;
		this.idNum = idNum;

		sysList.add(tstat); // this one is index 0
		sysList.add(therm); // this one is index 1
		sysList.add(appliances);
		sysList.add(space);

		consumList.add(therm);
		consumList.add(appliances);
		consumList.add(space);
	}

	/**
	 * runs the house system states and simulation.
	 * 
	 * @return indicates the need to stop the program (true, false)
	 */
	public boolean run() {
		boolean stop = false;
		// for each system in the list
		for (TrjSys sys : sysList) {
			// run the system
			if (stop = sys.RunTasks()) {
				break; // break out if the system indicates.
			}
		}
		// get the power consumption
		Pdemand = 0; // initialize
		// for each system in the consumer list
		for (Consumer cc : consumList) {
			// get the power and add it to the aggregate
			Pdemand += cc.getP();
		}
		return stop;
	}

	/**
	 * Log function for the house system.
	 * 
	 */
	public void log(PrintWriter logFile) {
		ThermalSys therm = (ThermalSys) sysList.get(THERM_I);
		ThermostatSys tstat = (ThermostatSys) sysList.get(TSTAT_I);
		if (logFile != null) {
			logFile.printf("%d\t %.6f\t %.2f\t %.2f\t", idNum, therm
					.getTempInside(), tstat.getSetpointTemp(), this.getP());
		}
	}

	/**
	 * Get the name of the house.
	 * 
	 * @return
	 */
	String getName() {
		return name;
	}

	/**
	 * Get the ID number of this house.
	 * 
	 * @return
	 */
	int getIdNumber() {
		return idNum;
	}

	/**
	 * Set the solar radiation experienced by the house.
	 * 
	 * @param rad
	 */
	public void setSolarRadiation(double rad) {
		ThermalSys therm = (ThermalSys) sysList.get(THERM_I);
		therm.setSolarRadiation(rad);
	}

	/**
	 * Set the outside temperature experienced by the house.
	 * 
	 * @param Tout
	 */
	public void setOutsideTemp(double Tout) {
		ThermalSys therm = (ThermalSys) sysList.get(THERM_I);
		therm.setOutsideTemp(Tout);
	}

	/**
	 * Return the total energy
	 * 
	 * @return
	 */
	public double getP() {
		double P = Pdemand;

		return Pdemand;
	}
}

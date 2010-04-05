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
package house.simulation;

import TranRunJLite.*;
import ODEsolver.*;

/**
 * Simulates the house thermal dynamics
 * 
 * @author William Burke <billstron@gmail.com>
 */
public class HouseThermalSimTask extends TrjTask {

	// house parameters
	private ThermalUnit houseList[];
	private int nStates;
	private int nInputs = 2;
	private double P;
	private double Tout; // Outside Tempetature
	private double RadSolar; // Solar Radiation
	private double[] states;
	private double[] u; // Current inputs
	// Ode parameters.
	double tLast; // Time at which simulation was last run
	double tCur; // Current time
	double lastStep; // For adaptive ODE solvers
	double stepSize;
	double stepMin;
	boolean useAdaptiveSolver;
	HouseODE hs = null; // Simulation object
	double[] x0;
	double[] abstol; // Absolute and relative tolerances
	double reltol;
	// State indices
	public static final int COOLER_I = 0;
	public static final int HEATER_I = 1;
	public static final int EXTWALL_I = 2;
	public static final int INTWALL_I = 3;
	public static final int AIR_I = 4;
	// Input Indices
	public static final int TOUT_I = 0;
	public static final int RAD_I = 1;

	/**
	 * Construct the House Thermal Simulation Task
	 * 
	 * @param name
	 * @param sys
	 * @param houseList
	 * @param Tinit
	 * @param useAdaptiveSolver
	 */
	public HouseThermalSimTask(String name, TrjSys sys,
			ThermalUnit houseList[], double Tinit, boolean useAdaptiveSolver) {
		super(name, sys, 0 /* initial state */, true /* taskActive */);

		this.houseList = houseList;
		this.useAdaptiveSolver = useAdaptiveSolver;
		this.nStates = houseList.length;
		this.states = new double[nStates];
		this.x0 = new double[nStates];
		this.abstol = new double[nStates];
		this.u = new double[nInputs];

		tLast = 0.0;
		// System.out.println("Tinit = " + Tinit);
		// Create an ODE (simulation) object
		// State variables:
		for (int i = 0; i < nStates; i++) {
			x0[i] = Tinit; // State variable initial values
			abstol[i] = 1.e-4; // Absolute tolerance for adaptive solvers
		}
		reltol = 1.e-4;

		hs = new HouseODE(nStates, // int nn,
				x0, // double xx0[],
				0.0, // double t0,
				abstol, // double [] abstol,
				reltol // double reltol
		);
		stepMin = 1.e-6; // Smallest allowable adaptive step size
		stepSize = 1.e-2; // Nominal step size
	}

	/**
	 * Set the heater fan state
	 * 
	 * @param state
	 */
	public void setHeaterFanState(boolean state) {
		// get the heater therma unit
		HvacThermalUnit heater = (HvacThermalUnit) houseList[HEATER_I];
		heater.setFanState(state);
	}

	/**
	 * Set the heater input state
	 * 
	 * @param state
	 */
	public void setHeaterInputState(boolean state) {
		HvacThermalUnit heater = (HvacThermalUnit) houseList[HEATER_I];
		heater.setHeaterState(state);
	}

	/**
	 * Set the cooler fan state
	 * 
	 * @param state
	 */
	public void setCoolerFanState(boolean state) {
		// get the heater therma unit
		HvacThermalUnit cooler = (HvacThermalUnit) houseList[COOLER_I];
		cooler.setFanState(state);
	}

	/**
	 * Set the Cooler input state
	 * 
	 * @param state
	 */
	public void setCoolerInputState(boolean state) {
		HvacThermalUnit cooler = (HvacThermalUnit) houseList[COOLER_I];
		cooler.setHeaterState(state);
	}

	/**
	 * The the current inside temperature
	 * 
	 * @return
	 */
	double getTin() {
		return states[AIR_I];
	}

	/**
	 * Get the heater duct temperature
	 * 
	 * @return
	 */
	double getHeaterDuctTemp() {
		HvacThermalUnit heater = (HvacThermalUnit) houseList[HEATER_I];
		return heater.getFanOutletTemp();
	}

	/**
	 * Get the cooler duct temperature
	 * 
	 * @return
	 */
	double getCoolerDuctTemp() {
		HvacThermalUnit cooler = (HvacThermalUnit) houseList[COOLER_I];
		return cooler.getFanOutletTemp();
	}

	/**
	 * Set the outside Temperature
	 * 
	 * @param Tout
	 */
	public void setOutSideTemp(double Tout) {
		this.Tout = Tout;
	}

	/**
	 * Set the Solar Radiation
	 * 
	 * @param sRad
	 */
	public void setSolarRadiation(double sRad) {
		this.RadSolar = sRad;
	}

	/**
	 * Get the current house power consumption.
	 * 
	 * @return
	 */
	public double getP() {
		return P;
	}

	/**
	 * Find out when to run the task
	 * 
	 * @param sys
	 * @return
	 */
	public boolean RunTaskNow(TrjSys sys) {
		// The simulation runs all the time and has no states.
		tCur = sys.GetRunningTime();
		if (tCur <= tLast) {
			// Make sure time has moved forward
			tLast = tCur;
			return false;
		}
		return true;
	}

	/**
	 * Run the task
	 * 
	 * @param sys
	 * @return
	 */
	public boolean RunTask(TrjSys sys) {

		// Update the inputs
		u[TOUT_I] = Tout;
		u[RAD_I] = RadSolar;
		// Run the simulation to the current time
		tCur = sys.GetRunningTime();
		if (useAdaptiveSolver) {
			lastStep = hs.multiStepAdaptive(tCur - tLast, stepSize, stepMin);
			stepSize = lastStep; // For the next iteration
		} else {
			hs.multiStepFixed(tCur - tLast, stepSize);
		}
		P = 0;
		for (ThermalUnit unit : houseList) {
			P += unit.getP();
		}
		tLast = tCur;
		return false;
	}

	/**
	 * Return the outside temperature
	 * 
	 * @return
	 */
	double getTout() {
		return this.Tout;
	}

	/**
	 * Inner class that operates the differential equation solver
	 * 
	 */
	public class HouseODE extends RKF45 {

		/**
		 * Constructs the ODE solver class
		 * 
		 * @param nn
		 * @param xx0
		 * @param t0
		 * @param abstol
		 * @param reltol
		 */
		public HouseODE(int nn, double xx0[], double t0, double[] abstol,
				double reltol) {
			super(nn, xx0, t0, abstol, reltol);
		}

		/**
		 * Compute the derivative
		 * 
		 */
		public void deriv() {
			states = x;
			// update the information for each state.
			for (int k = 0; k < nStates; k++) {
				ThermalUnit unit = houseList[k];
				unit.setInputs(u);
				unit.setStates(x);
			}
			// Compute the derivatives for each state
			for (int k = 0; k < nStates; k++) {
				ThermalUnit unit = houseList[k];
				dx[k] = unit.getDeriv();
			}
		}
	}
}

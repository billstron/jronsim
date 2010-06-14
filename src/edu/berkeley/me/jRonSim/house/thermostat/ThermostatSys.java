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
package edu.berkeley.me.jRonSim.house.thermostat;

import TranRunJLite.*;
import javax.swing.SwingUtilities;

import edu.berkeley.me.jRonSim.house.simulation.HouseIO;
import edu.berkeley.me.jRonSim.house.thermostat.goalSeeker.GoalSeekerTask;
import edu.berkeley.me.jRonSim.house.userInterface.UserInterfaceJFrame;

/**
 * The TrjSys that implements a thermostat.
 * 
 * @author William Burke <billstron@gmail.com>
 */
public class ThermostatSys extends TrjSys {

	String name;
	private HvacHystControlTask heaterHystCont;
	private HvacHystControlTask coolerHystCont;
	private HvacPIDControlTask heaterPidCont;
	private HvacPIDControlTask coolerPidCont;
	private TinFilterTask TinFilt;
	private HvacPwmTask heaterPwm;
	private HvacPwmTask coolerPwm;
	private CoordinatorTask coordinator;
	private SupervisorTask supervisor;
	private UserInterfaceTask userInterface;
	private ComTask com;
	private GoalSeekerTask goalSeeker;

	/**
	 * Construct the Thermostat System.
	 * 
	 * @param name
	 * @param tm
	 * @param therm
	 *            -- Where to send the input/output data.
	 */
	public ThermostatSys(String name, TrjTime tm, HouseIO therm) {
		super(tm);

		initThermostatSys(name, tm, therm, null);
	}

	public ThermostatSys(String name, TrjTime tm, HouseIO therm,
			ThermostatParams params) {
		super(tm);

		initThermostatSys(name, tm, therm, params);
	}

	/**
	 * Construct the Thermostat System with a GUI.
	 * 
	 * @param name
	 * @param tm
	 * @param therm
	 *            -- where to send the input/output data.
	 * @param uiFlag
	 *            -- create a gui?
	 */
	public ThermostatSys(String name, TrjTime tm, HouseIO therm, boolean uiFlag) {
		super(tm);

		initThermostatSys(name, tm, therm, null);

		if (uiFlag) {
			UserInterfaceJFrame gui = new UserInterfaceJFrame(userInterface);
			SwingUtilities.invokeLater(gui);
		}
	}

	private void initThermostatSys(String name, TrjTime tm, HouseIO therm,
			ThermostatParams params) {

		this.name = name;

		double dtBox = 15 * 60;
		double dtFilter = 1;
		TinFilt = new TinFilterTask("Inside Temp Filter", this, therm, dtBox,
				dtFilter);

		double dtPeriod = 15 * 60;
		double dtPwm = 1;
		heaterPwm = new HvacPwmTask("Heater PWM", this, dtPeriod, true, dtPwm,
				false/* trigger mode */, therm);
		coolerPwm = new HvacPwmTask("Cooler PWM", this, dtPeriod, false, dtPwm,
				false/* trigger mode */, therm);

		double dtPid = 15 * 60;
		double kp = 1;
		double ki = 1;
		double kd = 0;
		heaterPidCont = new HvacPIDControlTask("Heater PID Controller", this,
				true, kp, ki, kd, heaterPwm, TinFilt, dtPid);
		coolerPidCont = new HvacPIDControlTask("Cooler PID Controller", this,
				false, kp, ki, kd, coolerPwm, TinFilt, dtPid);

		heaterHystCont = new HvacHystControlTask("Heater Control Task", this,
				true, therm, 5.0);
		coolerHystCont = new HvacHystControlTask("Cooler Control Task", this,
				false, therm, 5.0);

		coordinator = new CoordinatorTask("Coordinator Task", this,
				heaterHystCont, coolerHystCont, heaterPidCont, coolerPidCont,
				5.0);
		coordinator.setMode(ThermostatMode.COOLING);
		coordinator.SetCommand(coordinator.START_HYST_CONTROL);

		supervisor = new SupervisorTask("Supervisor Task", this, 5.0);
		if (params != null)
			supervisor.setSetpointTable(params.setpoints);

		userInterface = new UserInterfaceTask("User Interface Task", this, 0.5);

		com = new ComTask("Communications Task", this, 1.0);

		goalSeeker = new GoalSeekerTask("Goal Seeker Task", this, supervisor,
				coordinator, userInterface, com, 5.0);
		if (params != null)
			goalSeeker.setCostTolerance(params.costTolerance);
	}

	/**
	 * Return the current setpoint temp
	 * 
	 * @return
	 */
	public double getSetpointTemp() {
		return goalSeeker.getSetpointTemp();
	}

	/**
	 * Adjust the current setpoint by the specified amount.
	 * 
	 * @param dT
	 */
	public void setSetpointChange(double dT) {
		userInterface.setSetpointChange(dT);
	}

	/**
	 * Set the current setpoint to the specified value.
	 * 
	 * @param Tsp
	 */
	public void setSetpoint(double Tsp) {
		userInterface.setTsp(Tsp);
	}
}

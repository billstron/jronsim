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
package house.thermostat.goalSeeker;

import house.thermostat.*;
import TranRunJLite.*;
import comMessage.Message;
import java.util.ArrayList;

/**
 * The Goal Seeker Task coordinates and implements all of the higher functions
 * of the intelligent thermostat. It takes in infomation from helper tasks, and
 * decides what to do with it.
 * 
 * @author William Burke <billstron@gmail.com>
 */
public class GoalSeekerTask extends TrjTask {

	private ArrayList<TrjState> states = new ArrayList<TrjState>();
	private double dt;
	double Tin;
	double Tsp;
	double TspTable;
	double TspMod;
	double TspDrMod;
	boolean newSp;
	boolean holdOn;
	boolean overrideOn;
	boolean heaterOn;
	boolean coolerOn;
	double costTolerance;
	int rxBufferSize = 0;
	Message nextMsg = null;
	ThermostatMode uiMode = ThermostatMode.COOLING;
	ThermostatMode tstatMode = ThermostatMode.COOLING;

	/**
	 * Construct the Goal Seeker Task.
	 * 
	 * @param name
	 * @param sys
	 * @param supervisor
	 *            -- supervisor task
	 * @param coordinator
	 *            -- coordinator task
	 * @param ui
	 *            -- user interface task.
	 * @param dt
	 */
	public GoalSeekerTask(String name, TrjSys sys, SupervisorTask supervisor,
			CoordinatorTask coordinator, UserInterfaceTask ui, ComTask com,
			double dt) {
		super(name, sys, 0 /* initial state */, true /* start active */);

		initGoalSeekerTask(name, sys, supervisor, coordinator, ui, com, dt);
	}

	private void initGoalSeekerTask(String name, TrjSys sys,
			SupervisorTask supervisor, CoordinatorTask coordinator,
			UserInterfaceTask ui, ComTask com, double dt) {
		// add the states
		GoalSeekerStateNormal normal = new GoalSeekerStateNormal(
				"Normal State", this, supervisor, coordinator, ui, com);
		states.add(normal);
		stateNames.add("Normal State");
		stateNames.add("Economic Setpoint State");
		this.dtNominal = dt;

		// initialize the variables
		this.Tin = 75;
		this.Tsp = 75;
		this.newSp = false;
		this.dt = dt;
		this.costTolerance = 1;
	}

	/**
	 * State definitions.
	 * 
	 */
	final int normalState = 0;
	final int ecoSpState = 1;

	/**
	 * Return the setpoint temperature
	 * 
	 * @return
	 */
	public double getSetpointTemp() {
		return this.Tsp;
	}

	/**
	 * Set the cost tolerance.
	 * 
	 * @param tol
	 */
	public void setCostTolerance(double tol) {
		this.costTolerance = tol;
	}

	/**
	 * Calculates the next transition based on a message.
	 * 
	 * @param msg
	 * @return
	 */
	int nextTransition(Message msg) {
		int next = -1; // default is no transition.
		if (msg != null) {
			switch (msg.getType()) {
			case INFO:
				break;
			case DR_SETPOINT:
				break;
			case DR_COSTRATIO:
				break;
			case DR_RELIABILITY:
				break;
			}
		}
		return next;
	}

	/**
	 * Check to see if this task is ready to run
	 * 
	 * @param sys
	 *            The system in which this task is embedded
	 * @return "true" if this task is ready to run
	 */
	public boolean RunTaskNow(TrjSys sys) {
		// System.out.println("<GoalSeekerTask> RunTaskNow");
		return CheckTime(sys.GetRunningTime());
	}

	/**
	 * Run the Goal Seeker Task.
	 * 
	 * @param sys
	 * @return
	 */
	public boolean RunTask(TrjSys sys) {
		// System.out.println("here");
		// run the state defined by the tran run system
		states.get(this.currentState).run(sys.GetRunningTime());

		return false;
	}
}

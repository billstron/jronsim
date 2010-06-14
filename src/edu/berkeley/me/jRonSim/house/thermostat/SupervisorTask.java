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

/**
 * The Supervisor Task stores and returns setpoint information.
 * 
 * @author William Burke <billstron@gmail.com>
 */
public class SupervisorTask extends TrjTask {

	private int mode;
	private SetpointTable table;
	private double Tsp;
	private double TspTable;
	private boolean newSp;
	/**
	 * State identifiers
	 */
	private final int sHold = 0;
	private final int sTables = 1;
	/**
	 * Public mode identifiers.
	 */
	public final int HOLD_MODE = 0;
	public final int TABLES_MODE = 1;

	/**
	 * Constructs the supervisor task.
	 * 
	 * @param name
	 * @param sys
	 * @param dt
	 */
	public SupervisorTask(String name, TrjSys sys, double dt) {
		super(name, sys, 0 /* Initial State */, true /* Start Active */);

		stateNames.add("Hold State");
		stateNames.add("Tables State");
		this.dtNominal = dt;
		this.table = new SetpointTable();
		this.TspTable = table.getTsp(sys.GetCalendar());
		this.Tsp = this.TspTable;
		this.newSp = true;
		this.mode = TABLES_MODE;
	}

	/**
	 * Set the setpoint table
	 * 
	 * @param table
	 */
	void setSetpointTable(SetpointTable table) {
		this.table = table;
	}

	/**
	 * Indicates if the current setpoint is new.
	 * 
	 * @return
	 */
	public boolean isNewSetpoint() {
		return this.newSp;
	}

	/**
	 * Returns the current setpoint. If the setpoint is new, the flag is reset
	 * as well.
	 * 
	 * @return
	 */
	public double getSetpoint() {
		this.newSp = false;
		return this.Tsp;
	}

	/**
	 * Tells if the hold state is currently active.
	 * 
	 * @return
	 */
	public boolean isHoldOn() {
		boolean flag = false;
		if (mode == HOLD_MODE) {
			flag = true;
		}
		return flag;
	}

	/**
	 * Sets the Supervisor into the hold state.
	 * 
	 * @param holdOn
	 */
	public void setHoldOn(boolean holdOn) {
		if (holdOn) {
			mode = HOLD_MODE;
		} else {
			mode = TABLES_MODE;
		}
	}

	/**
	 * Check to see if this task is ready to run
	 * 
	 * @param sys
	 *            The system in which this task is embedded
	 * @return "true" if this task is ready to run
	 */
	public boolean RunTaskNow(TrjSys sys) {
		return CheckTime(sys.GetRunningTime());
	}

	/**
	 * Runs the Supervisor task.
	 * 
	 * @param sys
	 * @return
	 */
	@Override
	public boolean RunTask(TrjSys sys) {

		switch (this.currentState) {
		case sHold: // no setpoint changes.
			// Upon entry, don't indicate a new setpoint
			if (this.runEntry) {
				// System.out.println("SupervisorState: sHold");
				// newSp = false;
			}
			// Determine the state transition based on the mode variable
			this.nextState = -1;
			if (mode != HOLD_MODE) {
				this.nextState = sTables;
			}
			break;
		case sTables: // changes based on the setpoint tables
			// Upon entry, always indicate a new setpoint
			if (this.runEntry) {
				// System.out.println("SupervisorState: sTables");
				// newSp = true;
			}
			// Get the new setpoint and compare it to the old one.
			// if the setpoint changed, indicate as such.
			TspTable = table.getTsp(sys.GetCalendar());
			if (Tsp != TspTable) {
				newSp = true;
				Tsp = TspTable;
			}
			// determine the state transtion based on the mode flag and
			// the override flag
			this.nextState = -1;
			if (mode != TABLES_MODE) {
				this.nextState = sHold;
			}
			break;
		} // case
		return false;
	}
}

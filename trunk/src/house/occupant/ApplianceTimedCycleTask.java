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

package house.occupant;

import util.BoundedRand;
import TranRunJLite.TrjSys;
import TranRunJLite.TrjTask;

/**
 * 
 * @author William Burke <billstron@gmail.com>
 *
 */
public class ApplianceTimedCycleTask extends TrjTask {

	private double Pon; // on power
	private double Poff; // off power
	private double Pcurrent; // current operating power
	private double[] dtCycle = new double[2];
	private double tOff = 0;
	private BoundedRand rand;

	// the task's states
	private final static int STATE_ON = 1;
	private final static int STATE_OFF = 0;

	// the commands
	public final static int CMD_NONE = -1;
	public final static int CMD_OFF = 0;
	public final static int CMD_ON = 1;

	/**
	 * construct n appliance with a time cycle length and manual start.
	 * 
	 * @param name
	 * @param sys
	 * @param dt
	 * @param Pon
	 * @param dtCycle
	 * @param rand
	 */
	public ApplianceTimedCycleTask(String name, TrjSys sys, double dt,
			double Pon, double Poff, double[] dtCycle, BoundedRand rand) {
		super(name, sys, 0/* Initial State */, true/* active */);

		this.Pon = Pon;
		this.Poff = Poff;
		this.Pcurrent = 0;
		this.dtNominal = dt;
		this.dtCycle = dtCycle;
		stateNames.add("Off");
		stateNames.add("On");

		if (rand == null) {
			this.rand = new BoundedRand();
		} else {
			this.rand = rand;
		}
	}

	/**
	 * Return the current power consumption
	 * 
	 * @return
	 */
	double getP() {
		return Pcurrent;
	}

	/**
	 * Switch the appliance on
	 * 
	 */
	void switchOn() {
		command = CMD_ON;
	}

	/**
	 * Switch the appliance off
	 * 
	 */
	void switchOff() {
		command = CMD_OFF;
	}

	/**
	 * Check to see if the appliance is on.
	 * 
	 * @return on = true, off = false
	 */
	boolean isOn() {
		boolean on = false;
		if (currentState == STATE_ON || command == CMD_ON)
			on = true;
		return on;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see TranRunJLite.TrjTask#RunTask(TranRunJLite.TrjSys)
	 */
	@Override
	public boolean RunTask(TrjSys sys) {
		// get the current time
		double t = sys.GetRunningTime();
		// apply the current state
		switch (currentState) {
		case STATE_OFF: // the appliance is off
			// set the current power to the off power
			Pcurrent = Poff;
			nextState = -1;
			if (command == CMD_ON) {
				command = CMD_NONE;
				nextState = STATE_ON;
				System.out.println("\tTimed ON at " + t/3600);
			}
			break;
		case STATE_ON: // the appliance is on
			if (runEntry) {
				// set the off time based on the cycle time
				tOff = t
						+ rand.getBoundedRand(dtCycle[0], dtCycle[0]
								+ dtCycle[1]);
			}
			// set the current power to the off power
			Pcurrent = Pon;
			nextState = -1;
			if (t > tOff) {
				nextState = STATE_OFF;
				System.out.println("\tTimed OFF at " + t/3600);
			}
			if (command == CMD_OFF) {
				command = CMD_NONE;
				tOff = Double.NEGATIVE_INFINITY;
				nextState = STATE_OFF;
			}
			break;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see TranRunJLite.TrjTask#RunTaskNow(TranRunJLite.TrjSys)
	 */
	@Override
	public boolean RunTaskNow(TrjSys sys) {
		return CheckTime(sys.GetRunningTime());
	}
}

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
 * @author William Burke <billstron@gmail.com>
 * @date Mar 11, 2010
 */
public class ApplianceAutoTask extends TrjTask {
	private double Pon;
	private double Poff;
	private double Pcurrent;
	private double[] dtCycle = new double[2];
	private double[] dtOff = new double[2];
	private double tOn = 0;
	private double tOff = 0;
	private BoundedRand rand;

	// the task's states
	private final static int STATE_ON = 1;
	private final static int STATE_OFF = 0;

	/**
	 * Construct the task
	 * 
	 * @param name
	 * @param sys
	 * @param dt
	 * @param Pon
	 * @param Poff
	 * @param dtCycle
	 * @param dtOff
	 * @param rand
	 */
	public ApplianceAutoTask(String name, TrjSys sys, double dt, double Pon,
			double Poff, double[] dtCycle, double[] dtOff, BoundedRand rand) {

		super(name, sys, 0/* Initial State */, true/* active */);

		// initialize the parameters based on the inputs
		this.Pon = Pon;
		this.Poff = Poff;
		this.dtCycle = dtCycle;
		this.dtOff = dtOff;
		this.Pcurrent = 0;
		this.dtNominal = dt;
		// add the state names
		stateNames.add("Off");
		stateNames.add("On");
		// initialize the random number generator
		if (rand == null) {
			this.rand = new BoundedRand();
		} else {
			this.rand = rand;
		}
		tOn = rand.getBoundedRand(dtOff[0], dtOff[0] + dtOff[1]);
	}

	/**
	 * Return the current power consumption
	 * 
	 * @return
	 */
	double getP() {
		return Pcurrent;
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
		// run the current state
		switch (currentState) {
		case STATE_OFF: // appliance off
			Pcurrent = Poff;
			// compute the transition
			nextState = -1;
			// transition if the time is past the on time
			if (t > tOn) {
				nextState = STATE_ON;
			}
			break;
		case STATE_ON: // appliance on
			// if this is the first time in the state
			if (runEntry) {
				// compute the on time for the next cycle
				tOn = t + rand.getBoundedRand(dtOff[0], dtOff[0] + dtOff[1]);
				// compute the off time for this cycle
				tOff = t
						+ rand.getBoundedRand(dtCycle[0], dtCycle[0]
								+ dtCycle[1]);
			}
			Pcurrent = Pon;
			// compute the transition
			nextState = -1;
			// transition if the time is past the off time
			if (t > tOff)
				nextState = STATE_OFF;
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

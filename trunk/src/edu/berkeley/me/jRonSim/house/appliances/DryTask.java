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
package edu.berkeley.me.jRonSim.house.appliances;

import edu.berkeley.me.jRonSim.util.BoundedRand;
import TranRunJLite.TrjSys;
import TranRunJLite.TrjTask;

/**
 * This task operates a clothes dryer. Turning it on automatically based on
 * randomization.
 * 
 * @author William Burke <billstron@gmail.com>
 * @date Mar 11, 2010
 */
public class DryTask extends TrjTask {
	private double Pon;
	private double Pcurrent;
	private double tStart;
	private double dtCycle = (0.75 * 3600.0);
	private double dtOffMax = (7. * 24.0 * 3600.0);
	private double tNext = 0;
	private BoundedRand rand;

	// the task's states
	private final static int STATE_ON = 1;
	private final static int STATE_OFF = 0;

	/**
	 * Create the DryTask
	 * 
	 * @param name
	 * @param sys
	 * @param dt
	 * @param Pon
	 *            : Power consumed while on
	 */
	public DryTask(String name, TrjSys sys, double dt, double Pon) {
		super(name, sys, 0/* Initial State */, true/* active */);

		// initialize the class
		initDryTask(dt, Pon, null);
	}

	/**
	 * Create the DryTask
	 * 
	 * @param name
	 * @param sys
	 * @param dt
	 * @param Pon
	 *            : Power consumed while on
	 * @param rand
	 *            : Random number generator
	 */
	public DryTask(String name, TrjSys sys, double dt, double Pon,
			BoundedRand rand) {
		super(name, sys, 0/* Initial State */, true/* active */);

		// initialize the class
		initDryTask(dt, Pon, rand);
	}

	/**
	 * Initialize the DryTask
	 * 
	 * @param dt
	 * @param Pon
	 * @param rand
	 */
	private void initDryTask(double dt, double Pon, BoundedRand rand) {
		this.Pon = Pon;
		this.Pcurrent = 0;
		this.dtNominal = dt;
		currentState = 0;
		stateNames.add("Off");
		stateNames.add("On");

		if (rand == null) {
			this.rand = new BoundedRand();
		} else {
			this.rand = rand;
		}
		tNext = rand.getBoundedRand(0, dtOffMax);
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
		// System.out.println("here " + currentState);
		double t = sys.GetRunningTime();
		switch (currentState) {
		case STATE_OFF:
			if (runEntry) {
				tStart = t;
				// System.out.println("off transition");
			}
			Pcurrent = 0.0;
			// System.out.println("off");
			// get a random number
			nextState = -1;
			// if the number is larger than the time remaining, transition
			if (t > tNext) {
				// System.out.println("here " + num);
				nextState = STATE_ON;
			}

			break;
		case STATE_ON:
			if (runEntry) {
				tStart = t;
				tNext = t + rand.getBoundedRand(0, dtOffMax);
				// System.out.println("on transition");
			}
			Pcurrent = Pon;
			nextState = -1;
			if ((t - tStart) > dtCycle)

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

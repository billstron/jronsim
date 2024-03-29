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
package edu.berkeley.me.jRonSim.house.thermostat;

import java.util.ArrayList;

/**
 * @author William Burke <billstron@gmail.com>
 * 
 */
public class ThermostatParams {
	double DRuserProfile;
	double costTolerance;
	SetpointTable setpoints;

	/**
	 * Construct the ThermostatParams Class.
	 * 
	 */
	public ThermostatParams() {

		costTolerance = 5;
		DRuserProfile = 1;
		setpoints = new SetpointTable();

		// create a new setpoint day
		ArrayList<Setpoint> day = new ArrayList<Setpoint>(5);
		day.add(new Setpoint(75, 6, 0, Setpoint.Label.MORNING));
		day.add(new Setpoint(79, 9, 0, Setpoint.Label.DAY));
		day.add(new Setpoint(75, 18, 0, Setpoint.Label.EVENING));
		day.add(new Setpoint(77, 22, 0, Setpoint.Label.NIGHT));

		for (int i = 0; i < 7; i++) {
			setpoints.ReplaceSetpointDay(i, day);
		}
	}
}

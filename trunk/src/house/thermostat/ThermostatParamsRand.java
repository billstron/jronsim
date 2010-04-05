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
package house.thermostat;

import java.util.ArrayList;


import util.BoundedRand;

/**
 * @author William Burke <billstron@gmail.com>
 * @date Feb 5, 2010
 */
public class ThermostatParamsRand extends ThermostatParams {

	private BoundedRand rand;
	private double holdProb = .5;
	private double avgComfortTemp = 75.0;
	private double avgWakeTime = 6.0;
	private double avgAwayTemp = 79.0;
	private double avgLeaveTime = 8.0;
	private double avgArriveTime = 17.0;
	private double avgSleepTemp = 76.0;
	private double avgSleepTime = 22.0;
	private boolean everyoneWorks = true;
	
	private double[] comfortTemp = {72, 74};
	private double[] wakeTime = {6.0, 8.0};
	private double[] awayTemp = {74.0, 76.0};
	private double[] leaveTime = {8.0, 10.0};
	private double[] arriveTime = {16.0, 19.0};
	private double[] sleepTemp = {74.0, 73.0};
	private double[] sleepTime = {20.0, 23.0};

	public ThermostatParamsRand() throws Exception {
		rand = new BoundedRand();
		drawComfortParams();
		setSetpoints();
	}
	
	public ThermostatParamsRand(BoundedRand rand) throws Exception {
		this.rand = rand;
		drawComfortParams();
		setSetpoints();
	}
	
	private void drawComfortParams(){
		avgComfortTemp = rand.getBoundedRand(comfortTemp[0], comfortTemp[1]);
		avgWakeTime = rand.getBoundedRand(wakeTime[0], wakeTime[1]);
		avgAwayTemp = rand.getBoundedRand(awayTemp[0], awayTemp[1]);
		avgLeaveTime = rand.getBoundedRand(leaveTime[0], leaveTime[1]);
		avgArriveTime = rand.getBoundedRand(arriveTime[0], arriveTime[1]);
		avgSleepTemp = rand.getBoundedRand(sleepTemp[0], sleepTemp[1]);
		avgSleepTime = rand.getBoundedRand(sleepTime[0], sleepTime[1]);
	}

	private void setSetpoints() throws Exception {

		ArrayList<Setpoint> tableDay = new ArrayList<Setpoint>();
		// decide if the thermostat is left in hold mode.
		if (rand.getBoundedRand(0.0, 1.0) < holdProb) // left in hold
		{
			// create the hold day
			tableDay.add(new Setpoint(avgComfortTemp, 6, 0,
					Setpoint.Label.MORNING));

		} else // uses tables
		{
			// look to see if everyone is working and on the day shift
			if (everyoneWorks) {
				tableDay.add(new Setpoint(avgComfortTemp, avgWakeTime,
						Setpoint.Label.MORNING));
				tableDay.add(new Setpoint(avgAwayTemp, avgLeaveTime,
						Setpoint.Label.DAY));
				tableDay.add(new Setpoint(avgComfortTemp, avgArriveTime,
						Setpoint.Label.EVENING));
				tableDay.add(new Setpoint(avgSleepTemp, avgSleepTime,
						Setpoint.Label.NIGHT));
			}
			// somebody is home all day but everyone else works the day shift
			else {
				tableDay.add(new Setpoint(avgComfortTemp, avgWakeTime,
						Setpoint.Label.MORNING));
				tableDay.add(new Setpoint(avgSleepTemp, avgSleepTime,
						Setpoint.Label.NIGHT));
			}
		}

		// fill the table with the hold day
		for (int i = 0; i < 7; i++) {
			setpoints.ReplaceSetpointDay(i, tableDay);
		}

	}
}

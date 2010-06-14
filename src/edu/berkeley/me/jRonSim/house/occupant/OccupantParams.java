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
package edu.berkeley.me.jRonSim.house.occupant;

/**
 * @author William Burke <billstron@gmail.com>
 * 
 */
public class OccupantParams {

	// personal comfort preferences
	double[] comfortTemp = { 76.15, 80.6, 84.4, 71.5, 67.5, 80.0, 81.9 };
	double[] DRcomfortTemp = { 76.15, 80.6, 84.4, 71.5, 67.5, 80.0, 81.9 };
	// Likelihood of acting
	double[] motivationProb = { 0.0, 0.6, 0.9, 0.6, 0.9, 0.6, 0.6 };
	double[] DRmotivationProb = { 0.0, 0.3, 0.5, 0.3, 0.5, 0.6, 0.6 };
	// personal schedule preferences
	double[] wakeTime = { 6.125, 0.5 };
	double[] sleepTime = { 22.0, 0, 5 };
	double[] leaveTime = { 7.25, 0.5 };
	double[] arriveTime = { 16.25, 0.5 };
	// time to wait before another touch (s)
	double tPatience = 10. * 60.; 

	// indicates the array indices
	public static final int COMFORT = 0;
	public static final int WARM = 1;
	public static final int HOT = 2;
	public static final int COOL = 3;
	public static final int COLD = 4;
	public static final int SLEEPING = 5;
	public static final int AWAY = 6;

	boolean working = true;
	boolean dayShift = true;

	int resNum = 0;
	int numWorking = 0;
	int numDayShift = 0;
	int resHomeAllDay = -1;
	int resWorks = 0;
	double avgWakeTime = 0;
	double avgSleepTime = 0;
	double avgLeaveTime = 0;
	double avgArriveTime = 0;
	double avgComfortTemp = 0;
	double avgSleepTemp = 0;
	double avgAwayTemp = 0;

	public boolean getWorking() {
		return working;
	}

	public boolean getDayShift() {
		return dayShift;
	}

	public double getWakeTime() {
		return wakeTime[0];
	}

	public double getSleepTime() {
		return sleepTime[0];
	}

	public double getLeaveTime() {
		return leaveTime[0];
	}

	public double getArriveTime() {
		return arriveTime[0];
	}

	public double getComfortTemp() {
		return comfortTemp[COMFORT];
	}

	public double getSleepTemp() {
		return comfortTemp[SLEEPING];
	}

	public double getAwayTemp() {
		return comfortTemp[AWAY];
	}
}

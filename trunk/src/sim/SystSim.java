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
package sim;

import TranRunJLite.*;
import aggregator.SystemicSys;
import house.PctHouse;
import house.simulation.ThermalParams;
import house.simulation.ThermalParamsRand;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import util.BoundedRand;

/**
 * 
 * @author William Burke <billstron@gmail.com>
 */
public class SystSim {

	static String logNames[] = { "Neighborhood.out", "Measure.out",
			"SystemicControl.out" };
	static PrintWriter[] logs = new PrintWriter[3];
	public static final int HOODLOG_I = 0;
	public static final int MEASURELOG_I = 1;
	public static final int CONTROLLOG_I = 2;
	static String inputFiles[] = { "./test/ThermalParam.in"};
	public static final int THERMALPARAMS = 0;

	public static void main(String[] args) throws Exception {
		double dt = 5.0; // Used for samples that need a time delta
		double tFinal = 24 * 60 * 60; // sec

		// initialize the random number generator
		int seed = 35621;
		BoundedRand rn = new BoundedRand(seed);

		// Create the calendar and timer
		GregorianCalendar cal = new GregorianCalendar(2007, 6, 28, 0, 0);
		TrjTimeSim tm = new TrjTimeSim(cal, 0.0); // Create the log files
		try {
			for (int i = 0; i < logNames.length; i++) {
				FileWriter fw = new FileWriter(logNames[i]);
				logs[i] = new PrintWriter(fw);
			}
		} catch (IOException e) {
			System.out.println("IO Error " + e);
			System.exit(1); // File error -- quit
		}

		// Create the list of houses.
		ArrayList<PctHouse> houseList = new ArrayList<PctHouse>();
		for (int i = 0; i < 5; i++) {
			// generate a new set of random house parameters
			ThermalParams params = new ThermalParamsRand(rn, inputFiles[THERMALPARAMS]);
			// generate a new house
			PctHouse hs = new PctHouse("House", tm, i, params);
			houseList.add(hs);
		}

		double dtLog = 60;
		SystemicSys ssys = new SystemicSys("Systemic Simulation", tm,
				houseList, dtLog, logs[HOODLOG_I], logs[CONTROLLOG_I],
				logs[MEASURELOG_I]);

		// Run the systemic system.
		boolean stop = false;
		while (tm.getRunningTime() <= tFinal && !stop) {
			if (stop = ssys.RunTasks()) {
				break; // Run all of the tasks
			}
			tm.incrementRunningTime(dt);
		}
		// close the logfile
		for (PrintWriter pw : logs) {
			pw.close();
		}
		// Finish up and exit
		System.out.println("Simulation Completed");
		System.exit(0);
	}
}

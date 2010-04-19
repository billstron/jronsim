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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import util.BoundedRand;

/**
 * @author William Burke <billstron@gmail.com>
 * @date Feb 5, 2010
 */
public class OccupantParamsRand extends OccupantParams {

	private static final double[] comfortTemp_min = { 73.5, 78.8, 82.4, 69.5,
			65.5, 79.0, 78.8 };
	private static final double[] comfortTemp_max = { 78.8, 82.4, 86.4, 73.5,
			69.5, 81.0, 85.0 };
	private double hold_prob = 0.50; // from Therese Peffer
	private double work_prob = 0.639; // census data for 2000
	private double dayshift_prob = 0.525; // 1990 census data

	private static final int max_res = 2;

	/**
	 * The most basic constructor that generates randomized parameters based on
	 * default distributions
	 * 
	 */
	public OccupantParamsRand() {
		super();
		// generate the randomized parameters.
		intOccupantParamsRand(new BoundedRand());
	}

	/**
	 * Construct the randomized parameters based on specified distributions and
	 * random number generator
	 * 
	 * @param path
	 * @param rand
	 */
	public OccupantParamsRand(String path, BoundedRand rand) {
		super();
		// get the distributions from file.
		if (path != null)
			this.getRangeFromFile(path);
		// generate the randomized parameters.
		this.intOccupantParamsRand(rand);
	}

	/**
	 * Produce the randomized parameters
	 * 
	 * @param rand
	 */
	private void intOccupantParamsRand(BoundedRand rand) {

		// everyone has the same patience time
		this.tPatience = 10 * 3600;
		// motivation probabilities are all the same
		for (int k = 0; k < 7; k++)
			this.motivationProb[k] = motivationProb[k];
		// different comfort temps.

		for (int k = 0; k < 5; k++)
			this.comfortTemp[k] = rand.getBoundedRand(comfortTemp_min[k],
					comfortTemp_max[k]);
		this.comfortTemp[AWAY] = rand.getBoundedRand(this.comfortTemp[COMFORT],
				comfortTemp_max[AWAY]);
		this.comfortTemp[SLEEPING] = rand.getBoundedRand(
				this.comfortTemp[COMFORT], comfortTemp_max[SLEEPING]);

		// set the DR motivation and comfort temps
		// different probs
		for (int k = 0; k < 7; k++)
			this.DRmotivationProb[k] = DRmotivationProb[k];
		// same comfort temps for now.
		for (int k = 0; k < 7; k++)
			this.DRcomfortTemp[k] = this.comfortTemp[k];

		// decide if the person works or not
		if (rand.getBoundedRand(0, 1) < work_prob) // they work
		{
			this.working = true;
			// set the work hours
			if (rand.getBoundedRand(0, 1) < dayshift_prob) // work day shift
			{
				this.dayShift = true;
				// decide when they work
				this.leaveTime[0] = rand.getBoundedRand(6.5, 8);
				this.leaveTime[1] = 0.5;
				this.arriveTime[0] = this.leaveTime[0]
						+ rand.getBoundedRand(7, 11);
				this.arriveTime[1] = 0.5;
				// decide about their wake and sleep time.
				this.wakeTime[0] = this.leaveTime[0]
						- rand.getBoundedRand(.25, 2);
				this.wakeTime[1] = 0.5;
				this.sleepTime[0] = rand.getBoundedRand(21, 23);
				this.sleepTime[1] = 0.5;
			} else // work other shift
			{
				// decide when they work
				/*
				 * this.leaveTime[0] = 23.9; this.leaveTime[1] = 0.5;
				 * this.arriveTime[0] = 8; this.arriveTime[1] = 0.5; // decide
				 * about their wake and sleep time. this.wakeTime[0] = 20;
				 * this.wakeTime[1] = 0.5; this.sleepTime[0] = 10;
				 * this.sleepTime[1] = 0.5;
				 */
				this.dayShift = false;
				this.leaveTime[0] = rand.getBoundedRand(8.5, 30);
				if (this.leaveTime[0] >= 24)
					this.leaveTime[0] -= 24;
				this.leaveTime[1] = 0.5;
				this.arriveTime[0] = this.leaveTime[0]
						+ rand.getBoundedRand(7, 11);
				if (this.arriveTime[0] >= 24)
					this.arriveTime[0] -= 24;
				this.arriveTime[1] = 0.5;
				// decide about their wake and sleep time.
				this.wakeTime[0] = this.leaveTime[0]
						- rand.getBoundedRand(.25, 2);
				if (this.wakeTime[0] < 0)
					this.wakeTime[0] = 24 - this.wakeTime[0];
				this.wakeTime[1] = 0.5;
				this.sleepTime[0] = this.wakeTime[0]
						+ rand.getBoundedRand(15, 17);
				if (this.sleepTime[0] >= 24)
					this.sleepTime[0] -= 24;
				this.sleepTime[1] = 0.5;

			}
		} else // they don't work
		{
			this.working = false;
			this.dayShift = true;
			// decide about their wake and sleep time.
			this.wakeTime[0] = rand.getBoundedRand(4.5, 9);
			this.wakeTime[1] = 0.5;
			this.sleepTime[0] = rand.getBoundedRand(21, 23);
			this.sleepTime[1] = 0.5;
			// they are home all day
			this.leaveTime[0] = Double.POSITIVE_INFINITY;
			this.leaveTime[1] = Double.POSITIVE_INFINITY;
			this.arriveTime[0] = -1;
			this.arriveTime[1] = -1;
			// Burn a couple of random numbers to ensure that we get the same
			// housing stock regardless of the people preferences.
			// This does not help when the number of people are different though
			rand.getBoundedRand(0, 1);
			rand.getBoundedRand(0, 1);
			rand.getBoundedRand(0, 1);
		}
	}

	/**
	 * Get the parameter distributions from file.
	 * 
	 * @param path
	 */
	private void getRangeFromFile(String path) {

		// Try to read the file and put the stuff in it
		try {
			// Open the file that is the first
			// command line parameter
			FileInputStream fstream = new FileInputStream(path);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			int i;
			String name, value;
			StringTokenizer st;

			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				if (strLine != "\n") {
					// get the next command identifier.
					st = new StringTokenizer(strLine, ", ");
					name = st.nextToken();
					// process the rest of the line based on the command
					// identifier
					// step through the rest of the line based on the number of
					// entries that should be on the line.
					// If the line is underfilled, then we return an error
					// message.
					if (name.startsWith("#")) {
						; // do nothing with this line
					} else if (name.equalsIgnoreCase("motiv_prob")) {
						for (i = 0; i < 7; i++) {
							value = st.nextToken();
							if (value == null)
								System.err.printf(
										"<OccupantParamsRand> bad input: %s\n",
										name);
							else
								motivationProb[i] = Double.parseDouble(value);
						}
					} else if (name.equalsIgnoreCase("drmotiv_prob")) {
						for (i = 0; i < 7; i++) {
							value = st.nextToken();
							if (value == null)
								System.err.printf(
										"<OccupantParamsRand> bad input: %s\n",
										name);
							else
								DRmotivationProb[i] = Double.parseDouble(value);
						}
					} else if (name.equalsIgnoreCase("work_prob")) {
						value = st.nextToken();
						if (value == null)
							System.err.printf(
									"<OccupantParamsRand> bad input: %s\n",
									name);
						else
							work_prob = Double.parseDouble(value);
						// System.err.printf("work prob = %3.3f\n", work_prob);
					} else if (name.equalsIgnoreCase("hold_prob")) {
						value = st.nextToken();
						if (value == null)
							System.err.printf(
									"<OccupantParamsRand> bad input: %s\n",
									name);
						else
							hold_prob = Double.parseDouble(value);
					} else if (name.equalsIgnoreCase("dayshift_prob")) {
						value = st.nextToken();
						if (value == null)
							System.err.printf(
									"<OccupantParamsRand> bad input: %s\n",
									name);
						else
							dayshift_prob = Double.parseDouble(value);
					} else
						System.err
								.printf("<OccupantParamsRand> UNKNOWN: '%s'\n",
										strLine);
				}
			}
			// Close the input stream
			in.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("<OccupantParamsRand> Error: " + e.getMessage());
		}

	}

	public static ArrayList<OccupantParams> RandomList(String path,
			BoundedRand rand) {
		int totalRes = (int) Math.ceil(rand.getBoundedRand(0, max_res));
		ArrayList<OccupantParams> list = new ArrayList<OccupantParams>(totalRes);

		for (int i = 0; i < totalRes; i++) {
			list.add(new OccupantParamsRand(path, rand));
		}
		return list;
	}

}

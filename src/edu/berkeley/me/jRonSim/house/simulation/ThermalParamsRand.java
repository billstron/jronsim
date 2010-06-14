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
package edu.berkeley.me.jRonSim.house.simulation;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.StringTokenizer;

import edu.berkeley.me.jRonSim.util.BoundedRand;


/**
 * @author William Burke <billstron@gmail.com>
 * 
 */
public class ThermalParamsRand extends ThermalParams {

	private double[] deltaHouseSize = { 0.0, 1.0 };
	private double[] deltaWindowSize = { 0.0, 0.0 };
	private double[] deltaInfiltration = { 0.0, 0.0 };
	private double[] deltaUnitSize = { -0.5, 0.5 };
	private double[] deltaInsulationQuality = { 0.0, 1.0 };
	private double[] deltaSeer = { coolerHeatEfficiency, coolerHeatEfficiency };
	private double slabSize = 1.25;
	private double slab_prob = 0.5;
	private static final double UNIT_SAT_MIN = 1.5;
	private static final double UNIT_SAT_MAX = 5.0;
	private static final double DEL_WINDOWAREA = 91;
	private static final double DEL_INFILTRATIONFLOW = (3375 * 0.075) / 3600;
	private static final double DEL_EXTWALLK1 = 0.2625;
	private static final double DEL_EXTWALLK2 = 0.2625;
	private BoundedRand rn;

	/**
	 * Construct the random thermal parameters
	 * 
	 */
	public ThermalParamsRand() {
		// get the default parameters
		super();
		// construct a random number generator.
		this.rn = new BoundedRand();
		// set the thermal parameters
		setThermalParams();
	}

	/**
	 * Construct the random thermal parameters using a specified random number
	 * generator
	 * 
	 * @param rn
	 */
	public ThermalParamsRand(BoundedRand rn) {
		// get the default parameters
		super();
		// set the random number generator
		this.rn = rn;
		// Set the thermal params
		setThermalParams();
	}

	/**
	 * Construct the ThermalParams with a random selection controlled by an
	 * external file.
	 * 
	 * @param path
	 * @throws Exception
	 */
	public ThermalParamsRand(String path) throws Exception {
		// get the default params
		super();
		// construct a random number generator
		this.rn = new BoundedRand();
		// get the parameter range from file
		getRangeFromFile(path);
		// set the thermal params
		setThermalParams();
	}

	public ThermalParamsRand(BoundedRand rn, String path) throws Exception {
		// get the default params
		super();
		// set the random number generator
		this.rn = rn;
		// get the parameter range from file
		getRangeFromFile(path);
		// set the thermal params
		setThermalParams();
	}

	/**
	 * Set the thermal params based on the ranges provided in the class
	 * 
	 */
	private void setThermalParams() {

		// generate the global random variables and the weighting variables.
		double SizeHouse = 1 + rn.getBoundedRand(deltaHouseSize[0],
				deltaHouseSize[1]);
		double SizeUnit = 1 + rn.getBoundedRand(deltaUnitSize[0],
				deltaUnitSize[1]);
		double QualInsulate = rn.getBoundedRand(deltaInsulationQuality[0],
				deltaInsulationQuality[1]);
		double SizeWindow = 1 + rn.getBoundedRand(deltaWindowSize[0],
				deltaWindowSize[1]);
		double SizeInfiltration = 1 + rn.getBoundedRand(deltaInfiltration[0],
				deltaInfiltration[1]);
		double AcSeer = rn.getBoundedRand(deltaSeer[0], deltaSeer[1]);
		double Slab = 1;
		if (rn.getBoundedRand(0, 1) < slab_prob)
			Slab = slabSize;

		double Wany = 0.05;

		// modify the parameters.
		// TODO: add comments with unit value
		// Calculate the heater input and saturate so that it isn't too big.
		double baseHeater = heaterHeatInputMax;
		heaterHeatInputMax = baseHeater * SizeHouse * SizeUnit;
		if (heaterHeatInputMax > (12000 * UNIT_SAT_MAX) / 3600)
			heaterHeatInputMax = (12000 * UNIT_SAT_MAX) / 3600;
		else if (heaterHeatInputMax < (12000 * UNIT_SAT_MIN) / 3600)
			heaterHeatInputMax = (12000 * UNIT_SAT_MIN) / 3600;
		double heaterScale = heaterHeatInputMax / (baseHeater * SizeHouse);
		heaterMass = heaterMass * SizeHouse * heaterScale
				* (1 + rn.getBoundedRand(-1.0, 1.0) * Wany);
		heaterfanMax = heaterfanMax * SizeHouse * heaterScale;

		// Calculate the cooler input and saturate so that it isn't too big.
		double baseCooler = coolerHeatInputMax;
		coolerHeatInputMax = baseCooler * SizeHouse * SizeUnit;
		if (coolerHeatInputMax < (-12000 * UNIT_SAT_MAX) / 3600)
			coolerHeatInputMax = (-12000 * UNIT_SAT_MAX) / 3600;
		else if (coolerHeatInputMax > (-12000 * UNIT_SAT_MIN) / 3600)
			coolerHeatInputMax = (-12000 * UNIT_SAT_MIN) / 3600;
		
		double coolerScale = coolerHeatInputMax / (baseCooler * SizeHouse);
		//System.out.println("Cooler Size = " + SizeUnit + ", " + coolerHeatInputMax);
		coolerMass = coolerMass * SizeHouse * coolerScale
				* (1 + rn.getBoundedRand(-1.0, 1.0) * Wany);
		coolerFanMax = coolerFanMax * SizeHouse * coolerScale;

		coolerHeatEfficiency = AcSeer;

		airMass = airMass * SizeHouse
				* (1 + rn.getBoundedRand(-1.0, 1.0) * Wany);
		extWallMass = extWallMass * SizeHouse
				* (1 + rn.getBoundedRand(-1.0, 1.0) * Wany);
		intWallMass = intWallMass * SizeHouse * Slab
				* (1 + rn.getBoundedRand(-1.0, 1.0) * Wany);
		internalInput = internalInput * SizeHouse
				* (1 + rn.getBoundedRand(-1.0, 1.0) * Wany);

		// NOTE: the simulation was responding opposite of what would be
		// expected
		// with changes in QualInsulate. So I changed the sign in front.
		extWallKair = (extWallKair - QualInsulate * DEL_EXTWALLK1)
				* (1 + rn.getBoundedRand(-1.0, 1.0) * Wany);
		if (extWallKair <= 0)
			extWallKair = 0;
		extWallKamb = (extWallKamb - QualInsulate * DEL_EXTWALLK2)
				* (1 + rn.getBoundedRand(-1.0, 1.0) * Wany);
		if (extWallKamb <= 0)
			extWallKamb = 0;

		windowArea = (windowArea - QualInsulate * DEL_WINDOWAREA) * SizeHouse
				* SizeWindow * (1 + rn.getBoundedRand(-1.0, 1.0) * Wany);
		if (windowArea <= 0)
			windowArea = 0;
		infiltrationFlow = (infiltrationFlow - QualInsulate
				* DEL_INFILTRATIONFLOW)
				* SizeHouse
				* SizeInfiltration
				* (1 + rn.getBoundedRand(-1.0, 1.0) * Wany);
		if (infiltrationFlow <= 0)
			infiltrationFlow = 0;

	}

	/**
	 * Get the range values from a file.
	 * 
	 * @param path
	 * @throws Exception
	 */
	private void getRangeFromFile(String path) throws Exception {
		try {
			// Open the file that is the first
			FileInputStream fstream = new FileInputStream(path);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			int i; // initialize the counting number
			// the following will be used to parse the line
			StringTokenizer st;
			String strLine, name, value;

			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				// turn the line into a token
				st = new StringTokenizer(strLine, ", ");
				// System.out.println("here0");
				if (st.hasMoreTokens()) {
					// get the name from the first position in the string
					name = st.nextToken();
					// process the rest of the line based on the command
					// identifier
					// step through the rest of the line based on the number of
					// entries that should be on the line.
					// If the line is underfilled, then we return an error
					// message.
					if (name.startsWith("#")) {
						; // do nothing with this line
					} else if (name.equalsIgnoreCase("house_size")) {
						// fill the vector
						for (i = 0; i < 2; i++) {
							value = st.nextToken();
							if (value == null)
								System.err.printf(
										"<ThermalParamRand> bad input: %s\n",
										name);
							else
								deltaHouseSize[i] = Double.parseDouble(value);
						}
					} else if (name.equalsIgnoreCase("unit_size")) {
						for (i = 0; i < 2; i++) {
							value = st.nextToken();
							if (value == null)
								System.err.printf(
										"<ThermalParamRand> bad input: %s\n",
										name);
							else
								deltaUnitSize[i] = Double.parseDouble(value);
						}
					} else if (name.equalsIgnoreCase("insu_qual")) {
						for (i = 0; i < 2; i++) {
							value = st.nextToken();
							if (value == null)
								System.err.printf(
										"<ThermalParamRand> bad input: %s\n",
										name);
							else
								deltaInsulationQuality[i] = Double
										.parseDouble(value);
						}
					} else if (name.equalsIgnoreCase("window_size")) {
						for (i = 0; i < 2; i++) {
							value = st.nextToken();
							if (value == null)
								System.err.printf(
										"<ThermalParamRand> bad input: %s\n",
										name);
							else
								deltaWindowSize[i] = Double.parseDouble(value);
						}
					} else if (name.equalsIgnoreCase("infiltration")) {
						for (i = 0; i < 2; i++) {
							value = st.nextToken();
							if (value == null)
								System.err.printf(
										"<ThermalParamRand> bad input: %s\n",
										name);
							else
								deltaInfiltration[i] = Double
										.parseDouble(value);
						}
					} else if (name.equalsIgnoreCase("seer_range")) {
						for (i = 0; i < 2; i++) {
							value = st.nextToken();
							if (value == null)
								System.err.printf(
										"<ThermalParamRand> bad input: %s\n",
										name);
							else
								deltaSeer[i] = Double.parseDouble(value);
						}
					} else if (name.equalsIgnoreCase("slab_size")) {
						value = st.nextToken();
						if (value == null)
							System.err.printf(
									"<ThermalParamRand> bad input: %s\n", name);
						else
							slabSize = Double.parseDouble(value);
					} else if (name.equalsIgnoreCase("slab_prob")) {
						value = st.nextToken();
						if (value == null)
							System.err.printf(
									"<ThermalParamRand> bad input: %s\n", name);
						else
							slab_prob = Double.parseDouble(value);
					} else
						System.err.printf("<ThermalParamRand> UNKNOWN: '%s'\n",
								strLine);
				}
			}
			// Close the input stream
			in.close();
		} catch (Exception ex) {
			throw new Exception("ThermalParamRand.getRangeFromFile Exception",
					ex);
		}
	}

	public String toStringOnRange() {
		// initialize the writers
		StringWriter strWriter = new StringWriter();
		PrintWriter prt = new PrintWriter(strWriter);

		prt.printf("deltaHouseSize = %f, %f\n", deltaHouseSize[0],
				deltaHouseSize[1]);
		prt.printf("deltaWindowSize = %f, %f\n", deltaWindowSize[0],
				deltaWindowSize[1]);
		prt.printf("deltaInfiltration = %f, %f\n", deltaInfiltration[0],
				deltaInfiltration[1]);
		prt.printf("deltaUnitSize = %f, %f\n", deltaUnitSize[0],
				deltaUnitSize[1]);
		prt.printf("deltaInsulationQuality = %f, %f\n",
				deltaInsulationQuality[0], deltaInsulationQuality[1]);
		prt.printf("deltaSeer = %f, %f\n", deltaSeer[0], deltaSeer[1]);
		prt.printf("slabSize = %f\n", slabSize);
		prt.printf("slab_prob = %f\n", slab_prob);

		prt.close();
		return strWriter.toString();
	}

	/**
	 * Test function
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// create a random object with a seed
		BoundedRand rn = new BoundedRand(500);
		// construct the randomized thermal parameters
		ThermalParamsRand p = new ThermalParamsRand(rn, "./ThermalParam.in");

		System.out.println(ThermalParams.getStringHeader());
		System.out.println(p.toString());
		System.out.println(p.toStringOnRange());

	}
}

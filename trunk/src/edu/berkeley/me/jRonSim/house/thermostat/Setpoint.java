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

import java.util.Calendar;

/**
 * The definition of a setpoint
 * 
 * @author William Burke <billstron@gmail.com>
 */
public class Setpoint {

	private double Tsp;
	private int[] tm = new int[3];
	private Label label;

	// enumeration for the time
	public static final int HOUR = 0;
	public static final int MINUTE = 1;
	public static final int SECOND = 2;

	/**
	 * Labels for the Setpoint table entries
	 * 
	 * @author WJBurke
	 */
	enum Label {

		MORNING("Morning"), DAY("Day"), EVENING("Evening"), NIGHT("Night");
		private String name;

		/**
		 * Constructor fo the Setpoint Lable enum
		 * 
		 * @param name
		 *            -- human readable name of the entry
		 */
		Label(String name) {
			this.name = name;
		}

		/**
		 * Gets the name of the setpoint table entry
		 * 
		 * @return name of the entry
		 */
		public String getName() {
			return this.name;
		}
	}

	/**
	 * Constructor for a setpoint
	 * 
	 * @param Tsp
	 *            -- Setpoint temperature (^oF)
	 * @param hour
	 *            -- Start hour (24 hour format: 0 - 24)
	 * @param min
	 *            -- Start minute
	 * @param label
	 *            -- Label for this entry
	 */
	Setpoint(double Tsp, int hour, int min, Label label) {
		this.tm[HOUR] = hour;
		this.tm[MINUTE] = min;
		this.tm[SECOND] = 0;

		this.Tsp = Tsp;
		this.label = label;
	}

	// Construct a setpoint from a double time
	
	Setpoint(double Tsp, double time, Label label) throws Exception {
		if(time > 24) throw new Exception("time is wrong for the setpoint"); 
		this.tm[HOUR] = (int) Math.floor(time);
		this.tm[MINUTE] = (int) Math.floor(60 * (time - Math.floor(time)));
		this.tm[SECOND] = 0;

		this.Tsp = Tsp;
		this.label = label;
	}

	/**
	 * Gets the setpoint temp.
	 * 
	 * @return
	 */
	double getTsp() {
		return this.Tsp;
	}

	/**
	 * Gets the label.
	 * 
	 * @return
	 */
	Label getLabel() {
		return label;
	}

	int[] getTime() {
		return tm;
	}

	/**
	 * Tells wether or not the calender time is before this setpoint entry.
	 * 
	 * @param cal
	 * @return
	 */
	boolean isBefore(Calendar cal) {
		boolean isBefore = false;
		if (tm[HOUR] < cal.get(Calendar.HOUR_OF_DAY)) {
			isBefore = true;
		} else if (tm[HOUR] == cal.get(Calendar.HOUR_OF_DAY)) {
			if (tm[MINUTE] <= cal.get(Calendar.MINUTE)) {
				isBefore = true;
			} else if (tm[MINUTE] == cal.get(Calendar.MINUTE)) {
				if (tm[SECOND] <= cal.get(Calendar.SECOND)) {
					isBefore = true;
				} else {
					isBefore = false;
				}
			}
		}
		return isBefore;
	}
}

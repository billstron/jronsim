/*
Copyright (c) 2009, Regents of the University of California
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
 * Neither the name of the University of California, Berkeley
nor the names of its contributors may be used to endorse or promote
products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.berkeley.me.jRonSim.house.thermostat;

import TranRunJLite.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import edu.berkeley.me.jRonSim.house.simulation.HouseIO;
import edu.berkeley.me.jRonSim.house.simulation.ThermalSys;

/** This computes Hysteresis Control for HVAC equipment.  
 *
 * @author William Burke <billstron@gmail.com>
 */
public class HvacHystControlTask extends HysteresisControl {

    private boolean heaterControl;
    private double Tin;
    private HouseIO house;
    private boolean act;

    /** Construct the control task
     * 
     * @param name
     * @param sys
     * @param HeaterControl -- Defines heater/cooler control (True = heater)
     * @param edu.berkeley.me.jRonSim.house -- The edu.berkeley.me.jRonSim.house to control
     * @param dt
     */
    public HvacHystControlTask(
            String name,
            TrjSys sys,
            boolean heaterControl,
            HouseIO house,
            double dt) {
        // use the super constructor
        super(name, sys, dt, 0.0, 1.0, 0.0, 0, true, 0.6, 0.1, false);
        // store the data
        this.heaterControl = heaterControl;
        this.house = house;
        this.Tin = 75;
        this.SetSetpoint(75);
        this.act = false;
        // if it is supposed to be cooling control, reset the minimum and
        // maximum actuation values.
        if (!heaterControl) {
            this.setMinM(-1.0);
            this.setMaxM(0.0);
        }
    }

    /** Tell wether this controller operates a heater or cooler.
     * 
     * @return
     */
    boolean getHeaterControl() {
        return this.heaterControl;
    }

    /** Gets the inside temperature.
     * 
     * @return
     */
    double getTin() {
        return Tin;
    }

    /** Sets the setpoint temperature
     * 
     * @param Tsp
     */
    void setTsp(double Tsp) {
        this.SetSetpoint(Tsp);
    }

    /** Returns the unit on-state.
     *
     * @return
     */
    boolean isUnitOn() {
        return act;
    }

    /** Gets the current process value for use in control calculation.
     * 
     * @return
     */
    @Override
    public double FindProcessValue() {
        Tin = house.getTempInside();
        return Tin;
    }

    /** Sends the actuation computed by the controller to the appropriate
     * unit.  
     * @param val -- computed actuation value.  
     */
    @Override
    public void PutActuationValue(double val) {
        if (heaterControl) {
            if (val > 0) {
                act = true;
            } else {
                act = false;
            }
            house.setHeaterOnState(act);
        } else {
            if (val < 0) {
                act = true;
            } else {
                act = false;
            }
            house.setCoolerOnState(act);
        }
    }

    /** Test function
     * 
     * @param args
     */
    public static void main(String args[]) {

        double tFinal = 60 * 60 * 5;
        double dt = 1.0;
        // Set up a file for writing results
        PrintWriter dataFile0 = null;
        try {
            FileWriter fW = new FileWriter("dataFile0.txt");
            dataFile0 = new PrintWriter(fW);
        } catch (IOException e) {
            System.out.println("IO Error " + e);
            System.exit(1);  // File error -- quit
        }


        TrjTime tm = new TrjTimeSim(0.0);
        
        TrjSys sys = new TrjSys(tm);

        ThermalSys house = new ThermalSys("thermal edu.berkeley.me.jRonSim.house", tm);

        HvacHystControlTask hyst = new HvacHystControlTask("generic", sys, true, house,
                5.0);
        hyst.SetCommand(hyst.SISO_START_CONTROL);

        TrjSys aSys[] = {sys, house};
        while (tm.getRunningTime() <= tFinal) {

            for (TrjSys s : aSys) {
                if (s.RunTasks()) {
                    break; // Run all of the tasks
                } // RunTasks() returns system stop status
            }
            // Log data to a file
            dataFile0.printf("%3.3f\t %3.3f\t %s\n", tm.getRunningTime(),
                    house.getTempInside(),
                    String.valueOf(hyst.isUnitOn()));
            sys.IncrementRunningTime(dt);
        }

    }
}

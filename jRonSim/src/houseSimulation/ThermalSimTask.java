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
package houseSimulation;

import TranRunJLite.*;

/** Task that implements the most simple thermal simulation of a house.
 * 
 * @author William Burke <billstron@gmail.com>
 */
public class ThermalSimTask extends TrjTask {

    double Tin;
    double Tout = 100;
    double a = 0.00004;
    double b = 0.0015;
    boolean heaterOn;
    boolean coolerOn;

    /** Construts the most basic house simulation
     *
     * @param name
     * @param sys -- system it belongs to
     * @param dt -- time step
     */
    public ThermalSimTask(String name, TrjSys sys, double dt) {
        super(name, sys, 0, true);
        this.dtNominal = dt;
        this.stateNames.add("only one");
        this.Tin = 75;
        this.heaterOn = false;
        this.coolerOn = false;
    }

    /** Check to see if this task is ready to run
     * @param sys The system in which this task is embedded
     * @return "true" if this task is ready to run
     */
    public boolean RunTaskNow(TrjSys sys) {
        //System.out.println("ThermalSimTask.RunTaskNow()");
        return CheckTime(sys.GetRunningTime());
    }

    /** Runs the basic house simulation
     * 
     * @param sys
     * @return
     */
    @Override
    public boolean RunTask(TrjSys sys) {

        double u = 0;
        if (heaterOn) {
            u += 1.0;
        }
        if (coolerOn) {
            u -= 1.0;
        }
        Tin += (Tout - Tin) * a + u * b;

        //System.out.println("ThermalSimTask.RunTask(); returning");
        return false;
    }

    /** Returns the inside temperature
     * 
     * @return
     */
    double getTin() {
        return Tin;
    }

    /** Sets the heater on state.
     * 
     * @param state
     */
    void setHeaterOn(boolean state) {
        heaterOn = state;
    }

    /** Gets the heater on state.
     * 
     * @return
     */
    boolean getHeaterOn() {
        return heaterOn;
    }

    /** Sets the cooler on state.
     * 
     * @param state
     */
    void setCoolerOn(boolean state) {
        coolerOn = state;
    }

    /** Gets the cooler on state
     * 
     * @return
     */
    boolean getCoolerOn() {
        return coolerOn;
    }
}

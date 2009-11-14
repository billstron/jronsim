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
package thermostat;

import TranRunJLite.TrjSys;
import TranRunJLite.TrjTask;
import userInterface.UserInterfaceIO;

/** The Task that implements the User Interface.  If there is a GUI, the data
 * from this task gets displayed.  If there isn't a GUI, the data just lives
 * here.  
 * 
 * @author William Burke <billstron@gmail.com>
 */
public class UserInterfaceTask extends TrjTask implements UserInterfaceIO {

    private String auxDisplay;
    private String labelAuxMsg;
    private String mainDisplay;
    private boolean holdLed = false;
    private double tDispTsp = 0;
    private final double dtDisp = 3000;
    private double Tin = 75;
    private double Tsp = 75;
    private boolean TspNew = false;
    private double TspMod = 0;
    private boolean holdState = false;
    private boolean holdCmd = false;
    private boolean heaterLed = false;
    private boolean coolerLed = false;
    private ThermostatMode tstatMode = ThermostatMode.COOLING;
    private TrjSys sys;

    /** Construct the User Interface Task.
     * 
     * @param name
     * @param sys
     * @param dt
     */
    public UserInterfaceTask(String name, TrjSys sys,
            double dt) {
        super(name, sys, 0, true);
        this.sys = sys;
        this.dtNominal = dt;
    }

    /** Get the Auxiliary display message.
     * Used by the GUI thread.
     * 
     * @return
     */
    public synchronized String getAuxDisplay() {
        return auxDisplay;
    }

    /** Get the label for the Auxiliary display.
     * Used by the GUI thread.
     * 
     * @return
     */
    public synchronized String getAuxLabel() {
        return labelAuxMsg;
    }

    /** Get the main display text.
     * Used by the GUI thread.
     * 
     * @return
     */
    public synchronized String getMainDisplay() {
        return mainDisplay;
    }

    /** Get the state of the hold LED.
     * Used by the GUI thread.
     * 
     * @return
     */
    public synchronized boolean getHoldLed() {
        return holdLed;
    }

    /** Get the state of the Heater on-state LED.
     * Used by the GUI thread.
     *
     * @return
     */
    public synchronized boolean getHeaterLed() {
        return heaterLed;
    }

    /** Get the state of the cooler on-state LED.
     * Used by the GUI thread.
     *
     * @return
     */
    public synchronized boolean getCoolerLed() {
        //System.out.println(coolerLed);
        return coolerLed;
    }

    /** Set the setpoint change.
     * Used by the GUI thread.  
     * 
     * @param dT
     */
    public synchronized void setSetpointChange(double dT) {
        this.TspMod += dT;
        //System.out.println("setSetpointChange: " + dT);
    }

    /** Toggle the hold button.
     * Used by the GUI thread.
     * 
     */
    public synchronized void setHoldToggle() {
        this.holdCmd = !this.holdCmd;
        //System.out.println("setHoldToggle: " + this.holdCmd);
    }

    /** Indicate that the program should be stopped.
     * Used by the GUI thread.
     * 
     */
    public synchronized void stopProgram() {
        sys.SetStop();
    }

    /** Set the current mode toggle.
     * Used by the GUI thread.  
     * 
     * @param mode
     */
    public synchronized void setModeToggle(ThermostatMode mode) {
        this.tstatMode = mode;
    }

    /** Get the current hold command.
     * 
     * @return
     */
    public boolean getHoldToggle() {
        return this.holdCmd;
    }

    /** Set the Hold state.
     * 
     * @param state
     */
    public void setHoldOn(boolean state) {
        this.holdLed = state;
        this.holdCmd = state;
    }

    /** Set the current setpoint.
     * 
     * @param Tsp
     */
    public void setTsp(double Tsp) {
        if (this.Tsp != Tsp) {
            this.TspNew = true;
        }
        this.Tsp = Tsp;
    }

    /** Set the current inside temperature.
     * 
     * @param Tin
     */
    public void setTin(double Tin) {
        this.Tin = Tin;
    }

    /** Get the current setpoint modification requested.
     * 
     * @return
     */
    public double getTspMod() {
        double mod = this.TspMod;
        this.TspMod = 0;
        return mod;
    }

    /** Set the heater on-state LED.
     * 
     * @param state
     */
    public void setHeaterLed(boolean state) {
        this.heaterLed = state;
    }

    /** Set the cooler on-state LED.
     * 
     * @param state
     */
    public void setCoolerLed(boolean state) {
        this.coolerLed = state;
    }

    /** Get the current thermostat mode.
     * 
     * @return
     */
    public ThermostatMode getThermostatMode() {
        return tstatMode;
    }

    /** Get the state of the override flag
     * 
     * @return
     */
    public boolean isDrOverriden() {
        return false;
    }

    /** Check to see if this task is ready to run
     * @param sys The system in which this task is embedded
     * @return "true" if this task is ready to run
     */
    public boolean RunTaskNow(TrjSys sys) {
        return CheckTime(sys.GetRunningTime());
    }

    /** Run the User Interface Task.
     * 
     * @param sys
     * @return
     */
    @Override
    public boolean RunTask(TrjSys sys) {

        if (TspNew) {
            tDispTsp = System.currentTimeMillis() + dtDisp;
            TspNew = false;
        }
        if (System.currentTimeMillis() <= tDispTsp) {
            labelAuxMsg = "Setpoint";
            auxDisplay = String.format("%03.1f", Tsp);
        } else {
            labelAuxMsg = "Temperature";
            auxDisplay = String.format("%03.1f", Tin);
        }
        mainDisplay = "";
        return false;
    }
}

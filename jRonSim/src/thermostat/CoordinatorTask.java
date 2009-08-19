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
package thermostat;

import TranRunJLite.*;

/** This TrjTask-like object coordinates the operation of the heater control
 * and the cooler control.
 *
 * @author William Burke <billstron@gmail.com>
 */
public class CoordinatorTask extends TrjTask {

    private ThermostatMode mode;
    private HvacHystControlTask heaterHyst = null;
    private HvacHystControlTask coolerHyst = null;
    private HvacPIDControlTask heaterPid = null;
    private HvacPIDControlTask coolerPid = null;
    private boolean heaterOn = false;
    private boolean coolerOn = false;
    private double Tin;
    private double Tsp;

    /** Constructs the Coordinator Task
     *
     * @param name -- String name of the Task
     * @param sys -- The TrjSys that this task belongs to
     * @param heaterControlTask -- The heater control task it controls
     * @param coolerControlTask -- The cooler control Task it controls
     * @param dt
     */
    public CoordinatorTask(String name, TrjSys sys,
            HvacHystControlTask heaterHyst,
            HvacHystControlTask coolerHyst,
            double dt) {
        super(name, sys, 0 /*Initial State*/, true /*Start Active*/);

        stateNames.add("Hysteresis Control");
        stateNames.add("PID Control");
        this.dtNominal = dt;

        this.mode = ThermostatMode.COOLING;
        this.heaterHyst = heaterHyst;
        this.coolerHyst = coolerHyst;
    }

    /** Construct the Coordinator using both Hysteresis and PID control
     * 
     * @param name
     * @param sys
     * @param heaterHyst
     * @param coolerHyst
     * @param heaterPid
     * @param coolerPid
     * @param dt
     */
    public CoordinatorTask(String name, TrjSys sys,
            HvacHystControlTask heaterHyst,
            HvacHystControlTask coolerHyst,
            HvacPIDControlTask heaterPid,
            HvacPIDControlTask coolerPid,
            double dt) {
        super(name, sys, 0 /*Initial State*/, true /*Start Active*/);

        stateNames.add("Hysteresis Control");
        stateNames.add("PID Control");
        this.dtNominal = dt;

        this.mode = ThermostatMode.COOLING;
        this.heaterHyst = heaterHyst;
        this.coolerHyst = coolerHyst;
        this.heaterPid = heaterPid;
        this.coolerPid = coolerPid;
    }

    /* State definitions, only has one state
     */
    private final int HYSTERESIS_CONTROL = 0;
    private final int PID_CONTROL = 1;
    public final int START_HYST_CONTROL = 0;
    public final int START_PID_CONTROL = 1;

    /** Check to see if this task is ready to run
     * @param sys The system in which this task is embedded
     * @return "true" if this task is ready to run
     */
    public boolean RunTaskNow(TrjSys sys) {
        return CheckTime(sys.GetRunningTime());
    }

    /** Runs the states associated with the Coordinator Task
     *
     * @param sys -- the TrjSys that this task is associated with
     * @return always returns false
     */
    @Override
    public boolean RunTask(TrjSys sys) {
        // This only has one state, so not much to do here.
        // Get the inside temperature from the control task.
        Tin = coolerHyst.getTin();
        // set the cooler and heater control tasks on or off depending
        switch (currentState) {

            case HYSTERESIS_CONTROL:  // hysteresis control
                // shut off the pid controllers
                if (heaterPid != null && coolerPid != null) {
                    heaterPid.SetCommand(heaterPid.SISO_STOP_CONTROL);
                    coolerPid.SetCommand(coolerPid.SISO_STOP_CONTROL);
                }
                // set the controllers based on the mode
                switch (mode) {

                    case OFF:
                        heaterHyst.SetCommand(heaterHyst.SISO_STOP_CONTROL);
                        coolerHyst.SetCommand(coolerHyst.SISO_STOP_CONTROL);
                        break;

                    case HEATING:
                        heaterHyst.SetCommand(heaterHyst.SISO_START_CONTROL);
                        coolerHyst.SetCommand(coolerHyst.SISO_STOP_CONTROL);
                        break;

                    case COOLING:
                        heaterHyst.SetCommand(heaterHyst.SISO_STOP_CONTROL);
                        coolerHyst.SetCommand(coolerHyst.SISO_START_CONTROL);
                        break;
                }
                // calculate the transition
                nextState = -1;
                if (this.GetCommand() == START_PID_CONTROL) {
                    if (coolerPid != null && heaterPid != null) {
                        nextState = PID_CONTROL;
                    } else {
                        System.err.println("<CoordinatorTask> " +
                                this.name +
                                " Unsupported Control Type Selected");
                    }
                }
                break;

            case PID_CONTROL:  // pid control
                // shut off the hysteresis controllers.
                if (heaterHyst != null && coolerHyst != null) {
                    heaterHyst.SetCommand(heaterHyst.SISO_STOP_CONTROL);
                    coolerHyst.SetCommand(coolerHyst.SISO_STOP_CONTROL);
                }
                // set the controllers based on the mode
                switch (mode) {

                    case OFF:
                        heaterPid.SetCommand(heaterPid.SISO_STOP_CONTROL);
                        coolerPid.SetCommand(coolerPid.SISO_STOP_CONTROL);
                        break;

                    case HEATING:
                        heaterPid.SetCommand(heaterPid.SISO_START_CONTROL);
                        coolerPid.SetCommand(coolerPid.SISO_STOP_CONTROL);
                        break;

                    case COOLING:
                        heaterPid.SetCommand(heaterPid.SISO_STOP_CONTROL);
                        coolerPid.SetCommand(coolerPid.SISO_START_CONTROL);
                        break;
                }
                // calculate the transition
                nextState = -1;
                if (this.GetCommand() == START_HYST_CONTROL) {
                    if (heaterHyst != null && coolerHyst != null) {
                        nextState = HYSTERESIS_CONTROL;
                    } else {
                        System.err.println("<CoordinatorTask> " +
                                this.name +
                                " Unsupported Control Type Selected");
                    }
                }
        }
        return false;
    }

    /** Get the mode of operation of the Coordinator Task.
     *
     * @return -- mode: OFF, HEATING, COOLING
     */
    public ThermostatMode getMode() {
        return this.mode;
    }

    /** Sets the current mode of the Coordinator Task.
     *
     * @param mode: OFF, HEATING, COOLING
     */
    public void setMode(ThermostatMode mode) {
        this.mode = mode;
    }

    /** Gets the inside tempetature.
     *
     * @return
     */
    public double getTin() {
        return Tin;
    }

    /** Sets the setpoint temperature.
     *
     * @param Tsp
     */
    public void setTsp(double Tsp) {
        this.Tsp = Tsp;
        this.heaterHyst.setTsp(Tsp);
        this.coolerHyst.setTsp(Tsp);
    }

    /** Returns the on-state of the heater unit.
     *
     * @return
     */
    public boolean isHeaterOn() {
        heaterOn = heaterHyst.isUnitOn();
        return heaterOn;
    }

    /** Returns the on-state of the cooler unit.
     *
     * @return
     */
    public boolean isCoolerOn() {
        coolerOn = coolerHyst.isUnitOn();
        return coolerOn;
    }
}

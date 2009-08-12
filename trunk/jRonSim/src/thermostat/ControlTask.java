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

import houseSimulation.HouseIO;
import TranRunJLite.*;

/** This TrjTask-like object controls the HVAC equipment.  It has the following
 * control types (implemented in states):
 *  0 : Off State -- Turn the unit off and leave it off
 *  1 : Hysteresis State -- Provide setpoint following using Hysteresis Control
 *  2 : Linear State -- Provide setpoint following using linear control w/ PWM
 *
 * @author WJBurke
 */
public class ControlTask extends TrjTask {

    double dt; // how often to run
    boolean heaterControl; // is it controlling the heater?
    ControlType type; // what type of control to return (hysteresis, linear)
    boolean controlOn; // used to turn control on and off
    double tNext = 0;  // next time to run the task
    ControlStateOff stateOff;
    ControlStateHysteresis stateHyst;
    ControlStateLinear stateLin;
    HouseIO house = null;
    boolean unitOn = false;
    double Tin = 0;
    double Tsp = 75;

    public ControlTask(
            String name,
            TrjSys sys,
            boolean HeaterControl,
            HouseIO house,
            double dt) {
        // use the super constructor
        super(name, sys,
                0 /* Initial State */,
                true /* Set Enabled */);

        // add the states
        stateOff = new ControlStateOff(this);
        stateNames.add("Off State");

        stateHyst = new ControlStateHysteresis(this);
        stateNames.add("Hysteresis Control State");

        stateLin = new ControlStateLinear(this);
        stateNames.add("Linear Control State");

        this.dt = dt;
        this.heaterControl = HeaterControl;
        this.house = house;

        this.controlOn = false;
        this.type = ControlType.HYSTERESIS;
    }
    /** The state definition
     */
    final int OFF = 0;
    final int HYSTERESIS = 1;
    final int LINEAR = 2;

    /** Runs the state machine
     *
     * @param sys -- the TrjSys that the task belongs to.
     * @return boolean indicating if the system should stop
     */
    public boolean RunTask(TrjSys sys) {
        // get the current running time
        double t = sys.GetRunningTime();

        // run the state machine if the time is right
        if (t >= tNext) {
            // Run the state machine
            switch (currentState) {
                case OFF:
                    nextState = stateOff.run(t);
                    break;
                case HYSTERESIS:
                    nextState = stateHyst.run(t);
                    break;
                case LINEAR:
                    nextState = stateLin.run(t);
                    break;
            }
            tNext += dt;  // update the state machine timer.
        }
        return false;
    }

    boolean getHeaterControl() {
        return this.heaterControl;
    }

    HouseIO getHouse() {
        return this.house;
    }

    double getTin() {
        Tin = house.getTempInside();
        return Tin;
    }

    double getTsp() {
        //Tsp = 75;
        return Tsp;
    }

    void setTsp(double Tsp){
        this.Tsp = Tsp;
    }

    ControlType getControlType() {
        return type;
    }

    void setControlType(ControlType mode){
        type = mode;
    }

    boolean getControlOn(){
        return controlOn;
    }

    void setControlOn(boolean state){
        controlOn = state;
    }

    boolean getUnitOn() {
        return unitOn;
    }

    void setUnitOn(boolean state) {
        if (heaterControl) {
            house.setHeaterOnState(state);
        } else {
            house.setCoolerOnState(state);
        }
        unitOn = state;
    }
}

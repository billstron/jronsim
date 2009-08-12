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
 * @author bill
 */
public class CoordinatorTask extends TrjTask {

    double dt;
    double tNext;
    CoordinatorMode mode;
    ControlTask heaterTask = null;
    ControlTask coolerTask = null;
    boolean heaterOn = false;
    boolean coolerOn = false;
    double Tin;
    double Tsp;

    /** Constructs the Coordinator Task
     *
     * @param name -- String name of the Task
     * @param sys -- The TrjSys that this task belongs to
     * @param heaterControlTask -- The heater control task it controls
     * @param coolerControlTask -- The cooler control Task it controls
     * @param dt
     */
    CoordinatorTask(String name, TrjSys sys,
            ControlTask heaterControlTask,
            ControlTask coolerControlTask,
            double dt) {
        super(name, sys, 0 /*Initial State*/, true /*Start Active*/);

        stateNames.add("Coordinate Task");
        this.dt = dt;
        this.tNext = 0;

        this.mode = CoordinatorMode.COOLING;
        this.heaterTask = heaterControlTask;
        this.coolerTask = coolerControlTask;
    }

    /* State definitions, only has one state
     */
    final int COORDINATE = 0;

    /** Runs the states associated with the Coordinator Task
     *
     * @param sys -- the TrjSys that this task is associated with
     * @return always returns false
     */
    @Override
    public boolean RunTask(TrjSys sys) {
        // check to see if it is the right time to run the state
        if (sys.GetRunningTime() >= tNext) {
            // This only has one state, so not much to do here.
            // Get the inside temperature from the control tasks.
            Tin = coolerTask.getTin();
            // set the cooler and heater control tasks on or off depending
            switch (mode) {
                case OFF:
                    heaterTask.setControlOn(false);
                    coolerTask.setControlOn(false);
                    break;
                case HEATING:
                    heaterTask.setControlOn(true);
                    coolerTask.setControlOn(false);
                    break;
                case COOLING:
                    heaterTask.setControlOn(false);
                    coolerTask.setControlOn(true);
                    break;
            }
            // set the next time to run.  
            tNext += dt;
        }
        return false;
    }

    /** Get the mode of operation of the Coordinator Task.
     *
     * @return -- mode: OFF, HEATING, COOLING
     */
    CoordinatorMode getMode() {
        return this.mode;
    }

    /** Sets the current mode of the Coordinator Task.
     *
     * @param mode: OFF, HEATING, COOLING
     */
    void setMode(CoordinatorMode mode) {
        this.mode = mode;
    }

    double getTin() {
        return Tin;
    }

    void setTsp(double Tsp){
        this.Tsp = Tsp;
        this.heaterTask.setTsp(Tsp);
        this.coolerTask.setTsp(Tsp);
    }
}

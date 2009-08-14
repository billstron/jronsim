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

/** The off state runs when no other control method is in use.  It first turns
 * off the unit and continually retreives the inside temperature.
 * 
 * @author William Burke <billstron@gmail.com>
 */
class ControlStateOff extends TrjState {

    private ControlTask ct;
    private double Tin;

    /** Creates the Off State.  Nothing magical about this one.
     *
     * @param task -- the task it belogs to.  
     */
    ControlStateOff(ControlTask task) {
        super("Off Control State", task);
        this.ct = task;
    }

    /** Turn off the unit
     *
     * @param t -- current time
     */
    @Override
    protected void entryFunction(double t) {
        // shut off the unit.
        System.out.println("<" + ct.GetName() + "> " +
                "<ControlStateOff> entryFunction");
        ct.setUnitOn(false);
    }

    /** Update the inside temperature. 
     *
     * @param t -- current time
     */
    @Override
    protected void actionFunction(double t) {
        // nothing to do
        // get the appropriate data and pass along
        Tin = ct.getTin();
    }

    /** Transition 
     *
     * @param t -- current time
     * @return an integer indicating the next state, -1 for no transition
     */
    @Override
    protected int exitFunction(double t) {
        int nextState = -1;
        if (ct.getControlOn() != false) {
            switch (ct.getControlType()) {
                case HYSTERESIS:
                    nextState = ct.HYSTERESIS;
                    break;
                case LINEAR:
                    nextState = ct.LINEAR;
                    break;
            }
        }
        return nextState;
    }
}

/** The Hysteresis Control State opperates a 'normal' type of control for a
 * thermostat.  It turns off when the temperature gets too high (for heating)
 * and turns on agian when the temperature gets too low (again, for heating).
 *
 * @author William Burke <billstron@gmail.com>
 */
class ControlStateHysteresis extends TrjState {

    private ControlTask ct;
    private double anticipator = 0.1;
    private double hysterisisValue = 0.7;
    private double e = 0.0;
    private boolean act = false;
    private double Tin;
    private double Tsp;

    /** Construct the Hysteresis control state
     *
     * @param task -- The task it belogs too
     */
    ControlStateHysteresis(ControlTask task) {
        super("Hysteresis Control State", task);
        this.ct = task;
    }

    /** Resets the variables
     * 
     * @param t -- current time
     */
    @Override
    protected void entryFunction(double t) {
        System.out.println("<" + ct.GetName() + "> " +
                "<HysteresisContState> entryFunction");

        //  reset the variables
        e = 0;
        act = false;
    }

    /** Performe the Hysteresis control calculations.  
     *
     * @param t -- current time
     */
    @Override
    protected void actionFunction(double t) {
        Tin = ct.getTin();
        Tsp = ct.getTsp();

        // Calculate error and adjust by the sign of the control
        e = Tsp - Tin;
        if (!ct.getHeaterControl()) {
            e = -e;
        }

        // Calculate everything assuming that in heating mode
        if (act) {
            // Apply anticipator when heat is on
            e -= anticipator;
            // Currently heating 
            // Wait until error passes the heating hysterisis
            // point before switching off
            if (e <= -hysterisisValue) {
                act = false;  // Switch to cooling
            }
            // Otherwise, leave actuation as-is
        } else {
            // Current actuation is 0 (ie, everything off)
            // Set actuation according to sign of error
            if (e > hysterisisValue) {
                act = true;
            } else {
                act = false;
            }
        }

        // pass the activation on
        ct.setUnitOn(act);
    }

    /** Transitions based on command
     *
     * @param t -- current time
     * @return an integer indicating the next state, -1 for no transition
     */
    @Override
    protected int exitFunction(double t) {
        int nextState = -1;
        if (ct.getControlOn() == true) {
            switch (ct.getControlType())    {
                case LINEAR:
                    nextState = ct.LINEAR;
                    break;
            }
        } else {
            nextState = ct.OFF;
        }
        return nextState;
    }
}

/** The Linear Control State controls the inside temperature by using a linear
 * control law (PI) by opperating the HVAC equipment with as a PWM device.
 *
 * @author William Burke <billstron@gmail.com>
 */
class ControlStateLinear extends TrjState {

    private ControlTask ct;

    ControlStateLinear(ControlTask task) {
        super("Linear Control State", task);
        this.ct = task;
    }

    /** Runs only the first time into a state.
     *
     * @param t -- current time
     */
    @Override
    protected void entryFunction(double t) {
        System.out.println("<" + ct.GetName() + "> " + "<LinearContState> entryFunction");
    }

    /** Performed every time the state runs.
     *
     * @param t -- current time
     */
    @Override
    protected void actionFunction(double t) {
        // TODO: compute the linear control action
    }

    /** Decides when transitions should occure
     *
     * @param t -- inside temperature
     * @return an integer indicating the next state, -1 for no transition
     */
    @Override
    protected int exitFunction(double t) {
        int nextState = -1;
        if (ct.getControlOn() == true) {
            switch (ct.getControlType()) {
                case HYSTERESIS:
                    nextState = ct.HYSTERESIS;
                    break;
            }
        } else {
            nextState = ct.OFF;
        }
        return nextState;
    }
}

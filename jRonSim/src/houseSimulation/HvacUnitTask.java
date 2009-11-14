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

import TranRunJLite.TrjSys;
import TranRunJLite.TrjTask;

/**The HvacUnitTask implements the state transition logic for turning on and off
 * the unit input and fan.  This is an abstract class with four mandatory
 * methods.
 * findDuctTemp() -- returns the current duct temperature.
 * findPowerDemand() -- returns the current power demand of the system.
 * putInputState() -- sets the heater/cooler coils to on/off.
 * putFanState() -- sets the fan to on/off.
 *
 * @author William Burke <billstron@gmail.com>
 */
public class HvacUnitTask extends TrjTask
{

    private boolean heater;
    private double fanTon, fanToff;
    private double Pdemand;  // current power demand
    private double Tduct;  // Current duct exit temperature
    private HouseThermalSimTask thermTask; // the thermal unit being controlled
    /** private state definitions
     */
    private final int STATE_OFF = 0;
    private final int STATE_PRE = 1;
    private final int STATE_ON = 2;
    private final int STATE_POST = 3;
    /** Public commands to the unit.  
     */
    public static final int TURN_OFF = 0;
    public static final int TURN_ON = 1;

    /** Construct the HVAC Unit Task.
     * 
     * @param name
     * @param sys
     * @param dt
     * @param heater -- is this a heating unit?
     * @param fanTon -- Temp to turn fan on.
     * @param fanToff -- Temp to turn fan off.
     */
    public HvacUnitTask(String name, TrjSys sys, double dt, boolean heater,
            double fanTon, double fanToff, HouseThermalSimTask thermTask)
    {
        super(name, sys, 0/*Initial State*/, true/*active*/);

        stateNames.add("Off State");
        stateNames.add("Pre Cooling/Heating State");
        stateNames.add("On State");
        stateNames.add("Off Energy Extraction State");

        this.thermTask = thermTask;
        this.dtNominal = dt;
        this.heater = heater;
        this.fanTon = fanTon;
        this.fanToff = fanToff;
        this.Pdemand = 0;
    }

    /** Returns the current on state of the unit.
     * 
     * @return
     */
    public boolean getOnState()
    {
        boolean state;
        if (currentState == STATE_PRE || currentState == STATE_ON)
        {
            state = true;
        }
        else
        {
            state = false;
        }
        return state;
    }

    /** Put the fan state into the thermal sim
     *
     * @param b
     */
    private void putFanState(boolean b)
    {
        // if it is a heater then set the heater fan
        if (heater)
        {
            thermTask.setHeaterFanState(b);
        }
        // if it is a cooler, then set the cooler fan
        else
        {
            thermTask.setCoolerFanState(b);
        }
    }

    /** Put the input state into the thermal simulation
     *
     * @param b
     */
    private void putInputState(boolean b)
    {
        // if it is a heater then set the heater input
        if (heater)
        {
            thermTask.setHeaterInputState(b);
        }
        // if it is a cooler, then set the cooler input
        else
        {
            thermTask.setCoolerInputState(b);
        }
    }

    /** Get the duct temperature
     * 
     * @return
     */
    private double getDuctTemp()
    {
        double T;  // initialize the output variable
        // if this is a heater, get the heater duct temp
        if (heater)
        {
            T = thermTask.getHeaterDuctTemp();
        }
        // otherwise get the cooler duct temp
        else
        {
            T = thermTask.getCoolerDuctTemp();
        }
        return T;
    }

    /** Tells when to run the task.
     * 
     * @param sys
     * @return
     */
    @Override
    public boolean RunTaskNow(TrjSys sys)
    {
        return CheckTime(sys.GetRunningTime());
    }

    /** Implements the state transition logic.
     * 
     * @param sys
     * @return
     */
    @Override
    public boolean RunTask(TrjSys sys)
    {
        // Get the duck temperature
        Tduct = getDuctTemp();

        // run the states
        switch (currentState)
        {
            case STATE_OFF:  // nothing going on.  
                if (runEntry)
                {
                    // turn everything off
                    putFanState(false);
                    putInputState(false);
                }

                // Compute Transition
                nextState = -1;
                if (GetCommand() == TURN_ON)
                {
                    nextState = STATE_PRE;
                }
                break;

            case STATE_PRE:  // get coils to desired temp
                if (runEntry)
                {
                    // turn on the input only
                    putFanState(false);
                    putInputState(true);
                }
                // Get the difference in the duct temp and the transition temp
                double dTon = fanTon - Tduct;
                if (heater)
                {
                    dTon = -dTon;
                }
                // Compute Transition
                nextState = -1;
                if (dTon > 0)
                {
                    // if the duct is to the proper temp, go to on state
                    nextState = STATE_ON;
                }
                if (GetCommand() == TURN_OFF)
                {
                    // if the unit gets switche off, go to post state
                    nextState = STATE_POST;
                }
                break;

            case STATE_ON:  // actively heating/cooling
                if (runEntry)
                {
                    // turn both on
                    putFanState(true);
                    putInputState(true);
                }
                // Compute Transition
                nextState = -1;
                if (GetCommand() == TURN_OFF)
                {
                    // if the unit is switched off, go to post state
                    nextState = STATE_POST;
                }
                break;

            case STATE_POST:  // extracting energy from the coils
                if (runEntry)
                {
                    // turn the unit off
                    putFanState(true);
                    putInputState(false);
                }
                // Get the difference in the duct temp and the transition temp
                double dToff = Tduct - fanToff;
                if (heater)
                {
                    dToff = -dToff;
                }
                // Compute Transition
                nextState = -1;
                if (dToff > 0)
                {
                    // if the unit is down to temp, go to off state
                    nextState = STATE_OFF;
                }
                if (GetCommand() == TURN_ON)
                {
                    // if the unit is switched on, go to pre state
                    nextState = STATE_PRE;
                }
                break;
        }
        return false;
    }
}

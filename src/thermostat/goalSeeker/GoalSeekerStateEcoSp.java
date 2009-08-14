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
package thermostat.goalSeeker;

import TranRunJLite.*;
import comMessage.*;
import thermostat.*;

/** The Economic Setpoint State for the Goal Seeker.  This state is active
 * when an Economic DR event with Setpoint control is active.  
 * 
 * @author William Burke <billstron@gmail.com>
 */
class GoalSeekerStateEcoSp extends TrjState {

    private GoalSeekerTask task;
    private SupervisorTask sup;
    private CoordinatorTask coord;
    private UserInterfaceTask ui;
    private ComTask com;
    private double tDrEnd;
    private double TspDrMod;
    private boolean drOverride;

    /** Constructs the Goal Seeker Normal State.
     * 
     * @param name
     * @param task
     * @param sup
     * @param coord
     * @param ui
     * @param com
     */
    GoalSeekerStateEcoSp(String name, GoalSeekerTask task,
            SupervisorTask sup, CoordinatorTask coord, UserInterfaceTask ui,
            ComTask com) {
        super(name, task);

        this.task = task;
        this.sup = sup;
        this.coord = coord;
        this.ui = ui;
        this.com = com;
    }

    /** Process any incoming message
     * 
     * @param msg
     */
    private void processMsg(Message msg) {
        if (msg != null) {
            switch (msg.getType()) {
                case INFO:
                case DR_SETPOINT:
                case DR_COSTRATIO:
                case DR_RELIABILITY:
                    break;
            } // switch
        } // if
    }

    /** Get the end time from the message.
     * 
     * @param msg
     * @return
     */
    private double getMsgEndTime(Message msg) {
        double tEnd = 0;
        if (msg != null) {
            switch (msg.getType()) {
                case DR_SETPOINT:
                    tEnd = 0;
                    break;
                case INFO:
                case DR_COSTRATIO:
                case DR_RELIABILITY:
                    tEnd = 0;
                    break;
            } // switch
        } // if
        return tEnd;
    }

    private double getMsgTspDrMod(Message msg) {
        double mod = 0;
        if (msg != null) {
            switch (msg.getType()) {
                case DR_SETPOINT:
                    mod = 4;
                    break;
                case INFO:
                case DR_COSTRATIO:
                case DR_RELIABILITY:
                    mod = 0;
                    break;
            } // switch
        } // if
        return mod;
    }

    /** Entry function.
     * 
     * @param t
     */
    @Override
    protected void entryFunction(double t) {
        // parse the message
        TspDrMod = getMsgTspDrMod(task.nextMsg);
        tDrEnd = getMsgEndTime(task.nextMsg);
        
        // signal the supervisor based on the message
        sup.setHoldOn(false);
        sup.clearSetpointMod();
        sup.modSetpoint(TspDrMod);

        // reset the message.  
        task.nextMsg = null;
    }

    /** Action function.
     * 
     * @param t
     */
    @Override
    protected void actionFunction(double t) {

        // get the coordinator data.
        task.Tin = coord.getTin();
        task.heaterOn = coord.isHeaterOn();
        task.coolerOn = coord.isCoolerOn();
        // get the user interface data
        drOverride = ui.isDrOverriden();

        // If there is a new setpoint from the supervisor, get it.
        if (sup.isNewSetpoint()) {
            task.Tsp = sup.getSetpoint() + TspDrMod;
            coord.setTsp(task.Tsp);
            ui.setTsp(task.Tsp);
        }

        // Adjust the thermostat mode based on the ui
        task.tstatMode = ui.getThermostatMode();

        // check to see if there is a setpoint change coming from the user
        task.TspMod = ui.getTspMod();
        // If they want a change that causes more energy consupmtion, 
        if ((task.TspMod < 0.0 &&
                task.tstatMode == ThermostatMode.COOLING) ||
                (task.TspMod > 0.0 &&
                task.tstatMode == ThermostatMode.HEATING)) {
            // check the override flag, and give it to them.
            if (drOverride) {
                sup.modSetpoint(task.TspMod);
            }
        } // if it calls for less energy, then give it to them.  
        else {
            sup.modSetpoint(task.TspMod);
        }
        // Regardless, reset the Setpoint modification.
        task.TspMod = 0;

        // if we aren't waiting on any message and the buffer isn't empty, 
        // then get the oldest message, and process it. 
        if (task.nextMsg == null && com.getRxMsgBufferSize() > 0) {
            task.nextMsg = com.getRxMsgOldest();
            processMsg(task.nextMsg);
        }

        // always set the hold to false.
        task.holdOn = false;

        // propogate the inside temp
        ui.setTin(task.Tin);
        // progogate the hold state;
        ui.setHoldOn(task.holdOn);
        sup.setHoldOn(task.holdOn);
        // propogate the unit on state
        ui.setHeaterLed(task.heaterOn);
        ui.setCoolerLed(task.coolerOn);
        // propogate the thermostat mode
        coord.setMode(task.tstatMode);
    }

    /** Exit function.
     * 
     * @param t
     * @return
     */
    @Override
    protected int exitFunction(double t) {

        int next = -1;
        if (t >= tDrEnd) {
            next = task.normalState;
        }
        return next;
    }
}

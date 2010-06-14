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
package edu.berkeley.me.jRonSim.house.thermostat.goalSeeker;

import TranRunJLite.*;
import edu.berkeley.me.jRonSim.comMessage.*;
import edu.berkeley.me.jRonSim.house.thermostat.*;

/** The Normal State for the Goal Seeker.  This state is active when there is no
 * DR or load management events currently in effect.  
 * 
 * @author William Burke <billstron@gmail.com>
 */
class GoalSeekerStateNormal extends TrjState {

    private GoalSeekerTask task;
    private SupervisorTask sup;
    private CoordinatorTask coord;
    private UserInterfaceTask ui;
    private ComTask com;

    /** Constructs the Goal Seeker Normal State.
     * 
     * @param name
     * @param task
     * @param sup
     * @param coord
     * @param ui
     * @param com
     */
    GoalSeekerStateNormal(String name, GoalSeekerTask task,
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
    private void processMsg(Message msg){
        if(msg != null){
            switch(msg.getType()){
                case INFO:
                case DR_SETPOINT:
                case DR_COSTRATIO:
                case DR_RELIABILITY:
                    break;
            } // switch
        } // if
    }

    /** Entry function.
     * 
     * @param t
     */
    @Override
    protected void entryFunction(double t) {
        // reset the setpoint modifications based on how far the mod went.

        double TspModTotal = Math.signum(task.TspDrMod) * (task.TspDrMod -
                task.TspMod);
        // If the modified setpoint was 'more comfortable than' the normal
        // setpoint, keep it at the same setpoint temp.
        if(TspModTotal < 0){
            task.TspMod = task.TspDrMod - task.TspMod;
        }
        // Otherwise, reset the setpoint to the table value.  
        else {
            task.TspMod = 0;
        }
        task.TspDrMod = 0;
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
        task.holdOn = ui.getHoldToggle();
        // Add the ui setpoint modification to the current value.  
        task.TspMod += ui.getTspMod();
        //f(task.TspMod != 0.) System.out.println("Tsp Change");
        
        // If there is a new setpoint from the supervisor, get it and reset the
        // current setpoint modification.
        if (sup.isNewSetpoint()) {
            task.TspTable = sup.getSetpoint();
            task.TspMod = 0;
            //System.out.println("Updated setpoint: " + task.Tsp);
        }
        // Create the current setpoint
        task.Tsp = task.TspTable + task.TspMod;
        
        // Adjust the thermostat mode based on the ui
        task.tstatMode = ui.getThermostatMode();
        
        // if we aren't waiting on any message and the buffer isn't empty, 
        // then get the oldest message, and process it. 
        if(task.nextMsg == null && com.getRxMsgBufferSize() > 0){
            task.nextMsg = com.getRxMsgOldest();
            this.processMsg(task.nextMsg);
        }

        // propogate the setpoint temp.
        ui.setTsp(task.Tsp);
        coord.setTsp(task.Tsp);
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

        // decide on transitions based on the next message
        return task.nextTransition(task.nextMsg);
    }
}

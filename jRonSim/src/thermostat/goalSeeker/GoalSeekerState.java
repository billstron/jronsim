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

import thermostat.*;
import TranRunJLite.*;

/** The generic Goal Seeker State that all other Goal Seeker State should be
 * built from.
 * 
 * @author William Burke <billstron@gmail.com>
 */
public class GoalSeekerState extends TrjState {

    GoalSeekerTask task;
    SupervisorTask sup;
    CoordinatorTask coord;
    UserInterfaceTask ui;

    /** Construct the generic Goal Seeker State.
     * 
     * @param name
     * @param task
     * @param sup -- supervisor task
     * @param coord -- coordinator task
     * @param ui -- user interface task.  
     */
    public GoalSeekerState(String name, GoalSeekerTask task,
            SupervisorTask sup, CoordinatorTask coord, UserInterfaceTask ui) {
        super(name, task);

        this.task = task;
        this.sup = sup;
        this.coord = coord;
        this.ui = ui;

    }

    /** Gets the data from the various tasks for processing.
     *
     */
    void getData() {
        // get the coordinator data.
        task.Tin = coord.getTin();
        task.heaterOn = coord.isHeaterOn();
        task.coolerOn = coord.isCoolerOn();
        // get the supervisor data.
        task.newSp = sup.isNewSetpoint();
        // get hte user interface data
        task.holdOn = ui.getHoldToggle();
        task.TspMod = ui.getTspMod();
        task.uiMode = ui.getThermostatMode();
        
    }

    /** Send the data back to the various tasks.
     * 
     */
    void sendData() {
        // propogate the setpoint
        coord.setTsp(task.Tsp);
        ui.setTsp(task.Tsp);
        // propogate the inside temp
        ui.setTin(task.Tin);
        // progogate the hold state;
        ui.setHoldOn(task.holdOn);
        sup.setHoldOn(task.holdOn);
        // propogate the unit on state
        ui.setHeaterLed(task.heaterOn);
        ui.setCoolerLed(task.coolerOn);
        // propogate the thermostat mode
        //System.out.println("here: " + task.tstatMode);
        coord.setMode(task.tstatMode);
    }
}

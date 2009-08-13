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

/** The Normal State for the Goal Seeker.  This state is active when there is no
 * DR or load management events currently in effect.  
 * 
 * @author William Burke <billstron@gmail.com>
 */
class GoalSeekerStateNormal extends GoalSeekerState {

    GoalSeekerStateNormal(String name, GoalSeekerTask task,
            SupervisorTask sup, CoordinatorTask coord, UserInterfaceTask ui) {
        super(name, task, sup, coord, ui);
    }

    /** Entry function.
     * 
     * @param t
     */
    @Override
    protected void entryFunction(double t) {
        // nothing
    }

    /** Action function.
     * 
     * @param t
     */
    @Override
    protected void actionFunction(double t) {
        // check if there is a new setpoint
        if (task.newSp) {
            task.Tsp = sup.getSetpoint();
            System.out.println("Updated setpoint: " + task.Tsp);
        }
        // Adjust the thermostat mode based on the ui
        task.tstatMode = task.uiMode;
        // check to see if there is a setpoint change coming from the user
        // interface.  If so, send it to the supervisor and reset
        if (task.TspMod != 0.0) {
            System.out.println("Send TspMod to the supervisor");
            sup.setSetpoint(task.Tsp + task.TspMod);
            task.TspMod = 0;
        }

    }

    /** Exit function.
     * 
     * @param t
     * @return
     */
    @Override
    protected int exitFunction(double t) {
        return -1;
    }
}

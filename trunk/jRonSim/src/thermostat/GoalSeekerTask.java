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

import TranRunJLite.*;
import java.util.ArrayList;

/**
 *
 * @author bill
 */
public class GoalSeekerTask extends TrjTask {

    private SupervisorTask supervisor = null;
    private CoordinatorTask coordinator = null;
    private ArrayList<GoalSeekerState> states = new ArrayList<GoalSeekerState>();
    private double Tin;
    private double Tsp;
    private double TspSupervisor;
    private double TspUserInterface;
    private boolean newSp;
    private boolean holdOn;
    private double tNext;
    private double dt;

    GoalSeekerTask(String name, TrjSys sys,
            SupervisorTask supervisor,
            CoordinatorTask coordinator,
            double dt) {
        super(name, sys, 0 /*initial state*/, true /*start active*/);

        // add the states
        GoalSeekerStateNormal normal = new GoalSeekerStateNormal("Normal State",
                this, supervisor, coordinator);
        states.add(normal);
        stateNames.add("Normal State");

        // initialize the variables
        this.supervisor = supervisor;
        this.coordinator = coordinator;
        this.Tin = 75;
        this.Tsp = 75;
        this.TspSupervisor = this.Tsp;
        this.TspUserInterface = this.Tsp;
        this.newSp = false;
        this.dt = dt;
        this.tNext = 0;
    }
    final int normalState  = 0;

    public double getTin(){
        return Tin;
    }

    public double getTsp(){
        return Tsp;
    }

    void setTin(double Tin){
        this.Tin = Tin;
    }

    void setTsp(double Tsp){
        this.Tsp = Tsp;
    }

    boolean isNewSp(){
        return this.newSp;
    }
    void setNewSp(boolean flag){
        this.newSp = flag;
    }

    boolean isHoldOn(){
        return holdOn;
    }

    void setHoldOn(boolean flag){
        holdOn = flag;
    }


    @Override
    public boolean RunTask(TrjSys sys) {
        // get the current runtime
        double t = sys.GetRunningTime();
        // run if the time is right
        if (t >= tNext) {
            // run the state defined by the tran run system
            states.get(this.currentState).getData();
            states.get(this.currentState).run(t);
            states.get(this.currentState).sendData();
            // update the timing variable.  
            tNext += dt;
        }
        return false;
    }
}

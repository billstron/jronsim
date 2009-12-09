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
package house.thermostat;

import TranRunJLite.PWMGenerator;
import TranRunJLite.TrjSys;
import TranRunJLite.TrjTime;
import TranRunJLite.TrjTimeSim;
import house.simulation.HouseIO;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/** This supplies a pwm signal to a single hvac unit.  
 *
 * @author William Burke <billstron@gmail.com>
 */
public class HvacPwmTask extends PWMGenerator {

    private HouseIO house;
    private boolean heaterMode;

    public HvacPwmTask(String name, TrjSys sys, double period,
            boolean heaterMode, double dt, boolean triggerMode, HouseIO house) {
        super(name, sys, period, 0.0 /*sig low*/, 1.0 /*sig high*/, dt,
                triggerMode, true);
        this.house = house;
        this.heaterMode = heaterMode;
    }

    /** Put the pwm actuation value into the appropriate HVAC unit.  This
     * ignores the direction bit.  
     * @param sig
     * @param dir
     */
    @Override
    public void PutActuationValue(double sig, int dir) {
        boolean act;
        // determine the actuation signal based on the signal variable
        if (sig > 0) {
            act = true;
        } else {
            act = false;
        }
        // determine where to send the actuation based heater mode flag.
        if (heaterMode) {
            house.setHeaterOnState(act);
        } else if (dir == BACKWARD) {
            house.setCoolerOnState(act);
        }
    }

    /** Test program
     * 
     * @param args
     */
    public static void main(String args[]) {

        // setup the timeing
        double tFinal = 3 * 60 * 60;
        double dt = 0.1;
        double tNext = 0;
        double tNextLog = 0;
        double dtLog = 5;

        // Set up a file for writing results
        PrintWriter dataFile0 = null;
        try {
            FileWriter fW = new FileWriter("dataFile0.txt");
            dataFile0 = new PrintWriter(fW);
        } catch (IOException e) {
            System.out.println("IO Error " + e);
            System.exit(1);  // File error -- quit
        }

        TrjTime tm = new TrjTimeSim(0.0);  // timer
        TrjSys sys = new TrjSys(tm);  // system
        HouseIO house = null;  // dead house.

        // PWM task variables.  
        double dtPwm = 1;
        double dtPeriod = 15 * 60;
        boolean heaterMode = true;
        boolean triggerMode = true;
        // pwm task
        HvacPwmTask pwm = new HvacPwmTask("test pwm", sys, dtPeriod, heaterMode,
                dtPwm, triggerMode, house);
        pwm.SetCommand(pwm.ON_MODE);
        //pwm.setStartTime(dtPeriod / 2.0);

        double sig = -0.6;
        // run the thing
        while (tm.getRunningTime() <= tFinal) {
            if (tm.getRunningTime() >= tNext) {
                sig += 0.2;
                pwm.setDutyRatio(sig);
                pwm.trigger();
                tNext += 15 * 60;
            }
            if (sys.RunTasks()) {
                break; // Run all of the tasks
                } // RunTasks() returns system stop status

            if (tm.getRunningTime() >= tNextLog) {
                // Log data to a file
                dataFile0.printf("%3.3f\t %3.3f\t %3.3f\t %3.3f\n", tm.getRunningTime(),
                        pwm.getSignal(), pwm.getDirectionBit(), pwm.getDutyRatio());
                tNextLog += dtLog;
            }
            sys.IncrementRunningTime(dt);
        }
    }
}

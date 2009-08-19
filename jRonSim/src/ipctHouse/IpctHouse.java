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
package ipctHouse;

import TranRunJLite.*;
import houseSimulation.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import thermostat.*;

/**  The house object contains a thermostat and a thermal simulation of a house.
 * 
 * @author William Burke <billstron@gmail.com>
 */
public class IpctHouse {

    private String name;
    private TrjTime tm;
    private ThermostatSys tstat;
    private ThermalSys therm;
    private PrintWriter dataFile0 = null;

    /** The constructor for a house object.
     *
     * @param name -- the private name of the house
     * @param tm -- the timer the house is to use.  
     */
    public IpctHouse(String name, TrjTime tm) {

        this.name = name;
        this.tm = tm;

        try {
            FileWriter fW = new FileWriter("dataFile0.txt");
            dataFile0 = new PrintWriter(fW);
        } catch (IOException e) {
            System.out.println("IO Error " + e);
            System.exit(1);  // File error -- quit
        }

        therm = new ThermalSys("Basic House", tm);
        tstat = new ThermostatSys("Basic Thermostat", tm, therm, true);
    }

    /** runs the house system states and simulation.
     * 
     * @return indicates the need to stop the program (true, false)
     */
    boolean run() {
        //System.out.println("here");
        boolean stop = false;
        TrjSys syss[] = {therm, tstat};
        for (TrjSys sys : syss) {
            if (stop = sys.RunTasks()) {
                break; // Run all of the tasks
            }
        }
        //System.out.println("here2");
        return stop;
    }

    /** exit function that cleans before final shutdown.
     * 
     */
    void exit() {
        System.out.println("exited");
        dataFile0.close();
    }

    /** Log function for the house system.
     * 
     */
    void log() {
        //System.out.println("here1");
        dataFile0.printf("%6.3f\t %3.3f\t %s\n", tm.getRunningTime(),
                therm.getTempInside(),
                Boolean.toString(therm.getCoolerOnState()));
        //System.out.println("here2");
    }

    /** Test function
     * 
     * @param args
     */
    public static void main(String[] args) {
        double dt = 5.0;  // Used for samples that need a time delta
        double tFinal = 24 * 60 * 60;  // sec
        TrjTimeAccel tm = new TrjTimeAccel(300);
        IpctHouse hs = new IpctHouse("The basic house", tm);

        IpctHouseRunnable runner = new IpctHouseRunnable(dt, tFinal, tm, hs);
        Thread t = new Thread(runner);
        t.start();
    }
}

/** This is an implementable house.
 * 
 * @author WJBurke
 */
class IpctHouseRunnable implements Runnable {

    private double dt;
    private double dtLog;
    private double tFinal;
    private double tLogNext;
    private TrjTime tm;
    private final IpctHouse hs;

    /** Constructor for the implementable house.
     * 
     * @param dt -- time step
     * @param tFinal -- final running time
     * @param tm -- timing structure
     * @param hs -- the ipctHouse that is made implementable
     */
    IpctHouseRunnable(double dt, double tFinal, TrjTime tm, IpctHouse hs) {
        this.dt = dt;
        this.dtLog = 30;
        this.tLogNext = 0;
        this.tFinal = tFinal;
        this.tm = tm;
        this.hs = hs;
    }

    /** run functions
     * 
     */
    public void run() {
        boolean stop = false;
        while (tm.getRunningTime() <= tFinal && !stop) {
            stop = hs.run();
            if (tm.getRunningTime() >= tLogNext) {
                hs.log();
                //System.out.println("here");
                tLogNext += dtLog;
            }
            tm.incrementRunningTime(dt);
        }
        hs.exit();
        System.out.println("Simulation Stopped");
        System.exit(0);
    }
}

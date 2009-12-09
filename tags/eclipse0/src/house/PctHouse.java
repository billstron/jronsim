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
package house;

import TranRunJLite.*;
import aggregator.environment.Envelope;
import house.simulation.*;
import java.io.PrintWriter;
import house.thermostat.*;
import java.util.ArrayList;

/**  The house object contains a thermostat and a thermal simulation of a house.
 * 
 * @author William Burke <billstron@gmail.com>
 */
public class PctHouse implements Envelope
{

    private String name;
    private int idNum;
    private TrjTime tm;
    private ArrayList<TrjSys> sysList = new ArrayList<TrjSys>();
    private final int TSTAT_I = 0;
    private final int THERM_I = 1;

    /** The constructor for a house object.
     *
     * @param tm
     * @param gui
     */
    public PctHouse(TrjTime tm, Boolean gui)
    {
        // Create the thermal system with the default parameters
        ThermalSys therm = new ThermalSys("Default House", tm);
        // Create the thermostat system
        ThermostatSys tstat = new ThermostatSys("Basic Thermostat", tm, therm,
                gui);

        // Initialize the house
        PctHouseInit("Basic House", tm, 0, therm, tstat);
    }

    /** Construct the Pct House
     *
     * @param name
     * @param tm
     */
    public PctHouse(String name, TrjTime tm)
    {
        // Create the thermal system with the default parameters
        ThermalSys therm = new ThermalSys("Default House", tm);
        // Create the thermostat system
        Boolean gui = false;
        ThermostatSys tstat = new ThermostatSys("Basic Thermostat", tm, therm, gui);

        // Initialize the house
        PctHouseInit(name, tm, 0, therm, tstat);
    }

    /** Construct the PCT house
     * 
     * @param name
     * @param tm
     * @param idNum
     */
    public PctHouse(String name, TrjTime tm, int idNum)
    {
        // Create the thermal system with the default parameters
        ThermalSys therm = new ThermalSys("Default House", tm);
        // Create the thermostat system
        Boolean gui = false;
        ThermostatSys tstat = new ThermostatSys("Basic Thermostat", tm, therm, gui);

        // Initialize the house
        PctHouseInit(name, tm, idNum, therm, tstat);
    }

    /** Construct a Pct House using specified thermal parameters
     * 
     * @param name
     * @param tm
     * @param idNum
     * @param params
     */
    public PctHouse(String name, TrjTime tm, int idNum, ThermalParams params)
    {
        // Create the thermal system with the default parameters
        ThermalSys therm = new ThermalSys("Specific House", tm, params);
        // Create the thermostat system
        Boolean gui = false;
        ThermostatSys tstat = new ThermostatSys("Basic Thermostat", tm, therm, gui);

        // Initialize the house
        PctHouseInit(name, tm, idNum, therm, tstat);
    }

    /** Initialize the Pch House variables
     * 
     * @param name
     * @param tm
     * @param idNum
     * @param therm
     * @param tstat
     */
    private void PctHouseInit(String name, TrjTime tm, int idNum,
            ThermalSys therm, ThermostatSys tstat)
    {
        // Initialize the simulation
        this.name = name;
        this.tm = tm;
        this.idNum = idNum;

        sysList.add(tstat);  // this one is index 0
        sysList.add(therm);  // this one is index 1
    }

    /** runs the house system states and simulation.
     * 
     * @return indicates the need to stop the program (true, false)
     */
    public boolean run()
    {
        //System.out.println("here0");
        boolean stop = false;
        // Run each system in the
        //System.out.println("sysList size: " + sysList.size());
        for (TrjSys sys : sysList)
        {
            //System.out.println("here1");
            if (stop = sys.RunTasks())
            {
                break; // Run all of the tasks
            }
        }
        return stop;
    }

    /** Log function for the house system.
     * 
     */
    public void log(PrintWriter logFile)
    {
        ThermalSys therm = (ThermalSys) sysList.get(THERM_I);
        if (logFile != null)
        {
            logFile.printf("%d, %6.2f, %6.2f, %6.2f, %6.2f",
                    idNum,
                    tm.getRunningTime(),
                    therm.getOutsideTemp(),
                    therm.getTempInside(),
                    therm.getP());
        }
    }

    /** Get the name of the house.
     *
     * @return
     */
    String getName()
    {
        return name;
    }

    /** Get the ID number of this house.
     * 
     * @return
     */
    int getIdNumber()
    {
        return idNum;
    }

    /** Set the solar radiation experienced by the house.  
     *
     * @param rad
     */
    public void setSolarRadiation(double rad)
    {
        ThermalSys therm = (ThermalSys) sysList.get(THERM_I);
        therm.setSolarRadiation(rad);
    }

    /** Set the outside temperature experienced by the house.
     * 
     * @param Tout
     */
    public void setOutsideTemp(double Tout)
    {
        ThermalSys therm = (ThermalSys) sysList.get(THERM_I);
        therm.setOutsideTemp(Tout);
    }

    /** Return the outside temperature.
     * 
     * @return
     */
    public double getP()
    {
        ThermalSys therm = (ThermalSys) sysList.get(THERM_I);
        return therm.getP();
    }

    public static void main(String[] args)
    {
        double dt = 5.0;  // Used for samples that need a time delta
        double dtLog = 60;
        double tFinal = 1 * 60 * 60;  // sec
        double tLogNext = 0;
        TrjTimeAccel tm = new TrjTimeAccel(300);

        PctHouse hs = new PctHouse(tm, false);

        boolean stop = false;
        while (tm.getRunningTime() <= tFinal && !stop)
        {
            stop = hs.run();
            if (tm.getRunningTime() >= tLogNext)
            {
                hs.log(null);
                //System.out.println("here");
                tLogNext += dtLog;
            }
            tm.incrementRunningTime(dt);
        }
        System.out.println("Simulation Stopped");
        System.exit(0);
    }
}

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
package aggregator;

import TranRunJLite.TrjSys;
import TranRunJLite.TrjTask;
import aggregator.environment.Envelope;
import house.PctHouse;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * @author William Burke <billstron@gmail.com>
 */
public class NeighborhoodTask extends TrjTask implements Envelope
{

    private ArrayList<PctHouse> houseList = null;
    private double dtLog = Double.POSITIVE_INFINITY;
    private double tLogNext = 0;
    private double Tout = 90;
    private double solRad = 0;
    private double Pagg = 0;
    private PrintWriter logFile = null;

    /** Construct the NeighborhoodTask composed of a list of houses.
     * 
     * @param name
     * @param sys
     * @param dt
     * @param dtLog
     * @param houseList
     */
    public NeighborhoodTask(String name, TrjSys sys, double dt,
            ArrayList<PctHouse> houseList)
    {
        super(name, sys, 0/*Initial State*/, true/*active*/);
        this.dtNominal = dt;
        this.stateNames.add("Run Houses");
        this.houseList = houseList;

        this.dtLog = Double.POSITIVE_INFINITY;
    }

    public NeighborhoodTask(String name, TrjSys sys, double dt,
            ArrayList<PctHouse> houseList, double dtLog, PrintWriter logFile)
    {
        super(name, sys, 0/*Initial State*/, true/*active*/);
        this.dtNominal = dt;
        this.stateNames.add("Run Houses");
        this.houseList = houseList;

        this.dtLog = dtLog;
        this.logFile = logFile;
    }

    /** Get the most recent aggregate power consumption.
     * 
     * @return
     */
    public double getAggregatePower()
    {
        return Pagg;
    }

    /** Get the outside temperature.
     * 
     * @return
     */
    public double getOutsideTemp()
    {
        return Tout;
    }

    /** Get the solar radiation
     *
     * @return
     */
    public double getSolarRadiation()
    {
        return solRad;
    }

    /** Set the solar radiation
     *
     * @param rad
     */
    public void setSolarRadiation(double rad)
    {
        solRad = rad;
    }

    /** Set the Outside Temperature
     *
     * @param Tout
     */
    public void setOutsideTemp(double Tout)
    {
        this.Tout = Tout;
    }

    private void log()
    {
        if (logFile != null)
        {
            //System.out.println("here1");
            // Print the neighborhood state
            logFile.printf("%6.2f, %6.2f, %6.2f, %6.2f", sys.GetRunningTime(), Tout,
                    solRad, Pagg);
            // Then print the state of each house.
            for (PctHouse hs : houseList)
            {
                //logFile.printf(", ");
                hs.log(logFile);
            }
            // Finally send the return.
            logFile.println();
        }
    }

    /** Runs the task.  The only thing it does is update the environmental
     * conditions and run each entry in the house list.
     *
     * @param sys
     * @return
     */
    @Override
    public boolean RunTask(TrjSys sys)
    {
        boolean stop = false;  // initialize the stop flag
        Pagg = 0;
        // run all of the houses
        for (PctHouse hs : houseList)
        {
            // while the stop flag is not on
            if (!stop)
            {
                hs.setOutsideTemp(Tout);
                hs.setSolarRadiation(solRad);
                // run the house
                stop = hs.run();
                Pagg += hs.getP();
            }
            else  // if the flag is true, stop
            {
                break;  // break from the for loop
            }
        }
        // log when it is time.
        if (sys.GetRunningTime() >= tLogNext)
        {
            log();
            tLogNext += dtLog;
        }
        return stop;
    }

    @Override
    public boolean RunTaskNow(TrjSys sys)
    {
        return CheckTime(sys.GetRunningTime());
    }
}

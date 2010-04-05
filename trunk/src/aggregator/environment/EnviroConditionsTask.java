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
package aggregator.environment;

import TranRunJLite.*;
import java.util.ArrayList;

/** This TrjTask is used for computing the environmental conditions.  It is a
 * very simple Task that just computes the conditions and sets them in the
 * specified envelopeInterfaces.  
 *
 * @author William Burke <billstron@gmail.com>
 */
public class EnviroConditionsTask extends TrjTask
{

    private ArrayList<Envelope> houses = null;
    private double Tout;
    private double Rad;
    // {lon,   lat}
    // {121.3, 37.976} Stockton CA
    // {121.5, 38.517} Sacramento, CA
    private double[] location =
    {
        121.3, 37.976
    };
    private SolarRadiationModel solarModel = null;
    private OutsideTemperatureModel ToutModel = null;

    /** Construct the most basic EnviroConditionsTask class.
     *
     * @param name
     * @param sys
     * @param dt
     * @param face 
     */
    public EnviroConditionsTask(String name, TrjSys sys, double dt,
            ArrayList<Envelope> face)
    {
        super(name, sys, 0 /*initial state*/, true /*taskActive*/);
        stateNames.add("Daily Weather");
        this.dtNominal = dt;
        this.houses = face;

        // Create the condition models.  
        this.solarModel = new SolarRadiationModel(location);
        this.ToutModel = new OutsideTemperatureModel();
        try
        {
            this.ToutModel = new OutsideTempNrelData(
                    "./test/KSCK_20070228-20071015.CSV");
        }
        catch (Exception e)
        {
            System.out.println("Exception: Problem creating the outside temperature model.");
            System.out.println("\tUsing a constant instead.");
            this.ToutModel = new OutsideTemperatureModel();
        }

        this.Tout = 100;
        this.Rad = 0;
    }

    /** Runs the task.  Basically all it does is compute the current conditions
     * and set them into the houses specified in the house list.  
     *
     * @param sys
     * @return
     */
    @Override
    public boolean RunTask(TrjSys sys)
    {
        // Get the current radiation and outside temperature.  
        Rad = solarModel.computeRadiation(sys.GetCalendar());
        Tout = ToutModel.getTemperature(sys.GetCalendar());

        // Set the information into the houses.  
        for (Envelope env : houses)
        {
            env.setOutsideTemp(Tout);
            env.setSolarRadiation(Rad);
        }
        return false;  // false means not to quit the program
    }

    /** Tells when to run the task. Returns true based on a timer.
     *
     * @param sys
     * @return
     */
    @Override
    public boolean RunTaskNow(TrjSys sys)
    {
        return CheckTime(sys.GetRunningTime());
    }
}

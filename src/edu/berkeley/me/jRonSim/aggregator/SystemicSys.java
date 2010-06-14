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
package edu.berkeley.me.jRonSim.aggregator;

import TranRunJLite.TrjSys;
import TranRunJLite.TrjTime;
import java.io.PrintWriter;
import java.util.ArrayList;

import edu.berkeley.me.jRonSim.aggregator.environment.Envelope;
import edu.berkeley.me.jRonSim.aggregator.environment.EnviroConditionsTask;
import edu.berkeley.me.jRonSim.house.House;

/**
 *
 * @author William Burke <billstron@gmail.com>
 */
public class SystemicSys extends TrjSys
{

    private NeighborhoodTask hood;
    private MeasureTask measure;
    private SystemicControlTask control;
    private EnviroConditionsTask enviro;
    private String name;

    public SystemicSys(String name, TrjTime tm, ArrayList<House> houseList)
    {
        super(tm);
        this.name = name;

        // Create the neighborhood task
        double dtHood = 10;
        hood = new NeighborhoodTask("Neighborhood Task", this, dtHood,
                houseList);

        // Create the measurement task
        double dtMeasure = 30;
        measure = new MeasureTask("Measurement Task", this, dtMeasure, hood);

        // Create the systemic control task
        double dtControl = 30;
        double dtControlLog = 60;
        control = new SingleMessageControlTask("Single Message Control Task", this,
                dtControl, dtControlLog, hood);

        // Create the environmental conditions
        ArrayList<Envelope> face = new ArrayList<Envelope>(1);
        face.add(hood);
        double dtEnviro = 30;
        enviro = new EnviroConditionsTask("Environment Task", this, dtEnviro,
                face);
    }

    public SystemicSys(String name, TrjTime tm, ArrayList<House> houseList,
            double dtLog, PrintWriter hoodLog, PrintWriter ControlLog,
            PrintWriter MeasureLog)
    {
        super(tm);
        this.name = name;

        // Create the neighborhood task
        double dtHood = 10;
        hood = new NeighborhoodTask("Neighborhood Task", this, dtHood,
                houseList, dtLog, hoodLog);

        // Create the measurement task
        double dtMeasure = 30;
        measure = new MeasureTask("Measurement Task", this, dtMeasure, hood);

        // Create the systemic control task
        double dtControl = 30;
        double dtControlLog = 60;
        control = new SingleMessageControlTask("Single Message Control Task", this,
                dtControl, dtControlLog, hood);

        // Create the environmental conditions
        ArrayList<Envelope> face = new ArrayList<Envelope>(1);
        face.add(hood);
        double dtEnviro = 30;
        enviro = new EnviroConditionsTask("Environment Task", this, dtEnviro,
                face);
    }
}

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
package houseSimulation;

/**
 *
 * @author William Burke <billstron@gmail.com>
 */
public class ThermalParams
{

    public final double initTemp; // initial temperature
    // heater parameters
    public final double heaterMass;  // mass (lb)
    public final double heaterfanMax;  // maximum fan speed
    public final double heaterHeatInputMax;  // maximum input heat
    public final double heaterFanEfficiency;  // fan electricial conversion
    public final double heaterHeatEfficiency; // efficiency
    // AC parameters
    public final double coolerMass;  // mass (lb)
    public final double coolerFanMax;  // maximum fan speed
    public final double coolerHeatInputMax; // maximum input heat (btu/s)
    public final double coolerFanEfficiency;  // fan electrical conversion
    public final double coolerHeatEfficiency;  // seer for the AC unit
    // Air parameters
    public final double airMass;  // mass (lb)
    public final double windowArea; // window area (ft^2)
    public final double internalInput; // independent internal inputs (btu/s)
    public final double infiltrationFlow; // infiltration from ouside (lb/s )
    // Internal Wall parameters
    public final double intWallMass; // mass (lb)
    public final double intWallKair;  // coefficient to inside air
    // External Wall parameters
    public final double extWallMass;  // mass (lb)
    public final double extWallKair;  // coefficient to inside air
    public final double extWallKamb;  // coefficient to outside air

    /** Construct the baseline parameters.
     *
     */
    public ThermalParams()
    {
        initTemp = 76.6;
        heaterMass = 30;
        heaterfanMax = (2400 * 0.075) / 60.0;
        heaterHeatInputMax = 70000.0 / 3600.0;
        heaterFanEfficiency = .50;
        heaterHeatEfficiency = .95 * 55;

        coolerMass = 100;
        coolerFanMax = ((2400 * 0.075) / 60.0);
        coolerHeatInputMax = -((4 * 12000) / 3600); // (btu/s) 4 ton unit
        coolerFanEfficiency = 0;  // modified from .5
        coolerHeatEfficiency = 14;  // seer for the AC unit

        airMass = 1476.67 * 9 * 0.075;  //ft^2 * ft * lb/ft^3
        windowArea = 225; // ft^2 (ext 91)
        internalInput = 2880 / 3600; // btu/hr -> btu/s
        infiltrationFlow = (8325 * 0.075) / 3600; // ft^3/hr -> lb/s (ext 3375)
        intWallMass = (740 + 2 * 13290) * .5 * 5; // lb
        intWallKair = 500;  // 100
        extWallMass = 1581 * .8 * 17;  // lb
        extWallKair = 0.6475;  //(ext 0.2625)
        extWallKamb = 0.6475;  //(ext 0.2625)
    }
}

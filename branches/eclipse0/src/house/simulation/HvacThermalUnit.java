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
package house.simulation;

/** This is the thermal storage and transfer object for the HVAC units -- heater
 * and AC.  
 *
 * @author William Burke <billstron@gmail.com>
 */
public class HvacThermalUnit extends ThermalUnit
{

    private int type;
    public static final int HEATER = 0;
    public static final int COOLER = 1;
    private double QunitToAir, QunitToAmb;
    private double Qout;
    // default calculations for the cooler efficiency
    private static double mcap = -0.0064;  // Slope of the COP curves
    private static double bcap = 1.61;  // Intercept of the COP curves
    private static double mcop0 = -0.015;
    private static double bcop0 = 2.43;
    private static double cd = 0.25;  // default cyclicing degredation.

    /** Construct the HvacThermalUnit
     * 
     * @param type
     * @param m
     * @param fanMax
     * @param fanEfficiency
     * @param heatInputMax
     * @param efficiency
     */
    public HvacThermalUnit(int type, double m, double fanMax,
            double fanEfficiency, double heatInputMax, double efficiency)
    {
        super(5, 2);
        this.type = type;
        this.m = m; // typ 30.0 lb
        this.cp = 0.2;  // aluminum, 0.2 BTU/(lb F)
        this.cpAir = 0.24;  // 0.24 BTU/(lb F)
        this.fanMax = fanMax;
        // typ (2400 * 0.075) / 60.0 (ft^3/min)(lb/ft^3)(min/sec) = lb/sec
        this.fanFlow = 0.0;  // Initial fan flow 0.0
        this.fanInletTemp = 75;  // ^oF
        this.fanOutletTemp = this.fanInletTemp;  // Initial value
        this.fanEfficiency = fanEfficiency;  // typ 0.50
        this.heatInputMax = heatInputMax;
        // typ for heater: 70000.0 / 3600.0 (BTU/hr)(hr/sec) = BTU/sec
        // typ for AC: -48000 / 3600 (BTU/hr)(hr/sec) = BTU/sec; 4 ton unit
        this.heatInput = 0.0;  // Initial Value
        this.heatEfficiency = efficiency;
        // Heater COP: typ .95 * 55;
        // Cooler SEER: typ 14

        // Set the type appropriate information
        switch (type)
        {
            case HEATER:
                this.i = HouseThermalSimTask.HEATER_I;
                this.k1 = this.heatInputMax / 10.0; // BTU /(sec F)
                this.k2 = 0.05;  // Btu/(sec F) Heat loss to local ambient
                this.k3 = 1.0;  // Defines air temperature profile
                this.tempAmbient = 90.0;  // ^oF Ambient temperature
                // around the heater (assuming basement or garage)
                break;
            case COOLER:
            default:
                this.i = HouseThermalSimTask.COOLER_I;
                this.k1 = Math.abs(this.heatInputMax / 16.0);  // BTU /(sec F)
                this.k2 = 0.01;  // Btu/(sec F) Heat loss to local ambient
                this.k3 = .6;  // Defines air temperature profile
                this.tempAmbient = 90.0;  // ^oF Ambient temperature
                // around the cooler (assuming compressor is outdoors)
                break;
        }
    }

    /** Set the fan on/off based on the command.
     *
     * @param On
     */
    public void setFanState(boolean On)
    {
        if (On)
        {
            fanFlow = fanMax;
        }
        else
        {
            fanFlow = 0;
        }
    }

    /** Set the unit on/off based on the command.
     * 
     * @param On
     */
    public void setHeaterState(boolean On)
    {
        if (On)
        {
            heatInput = heatInputMax;
        }
        else
        {
            heatInput = 0;
        }
    }

    double getFanOutletTemp()
    {
        return fanOutletTemp;
    }

    /** Get the current fan flow
     *
     * @return
     */
    public double getFanFlow()
    {
        return this.fanFlow;
    }

    /** Get the fan outlet temperature
     *
     * @return
     */
    private double computeFanOutletTemp(double Tcore, double Tinlet)
    {
        double Toutlet = 0;
        // compute the Q
        if (fanFlow > 0.0)
        {
            Toutlet = Tinlet + (Tcore - Tinlet) * (1.0 - Math.exp(-1.0 / k3));
        }
        else
        {
            Toutlet = Tcore;
        }
        return Toutlet;
    }

    /** Get the heat going into the duct air
     * 
     * @return
     */
    private double computeQunitToAir(double Tcore, double Tinlet)
    {
        double q = 0;
        Tcore = x[i];
        fanInletTemp = x[HouseThermalSimTask.AIR_I];
        // compute the Q
        if (fanFlow > 0.0)
        {
            q = k1 * (Tcore - fanInletTemp) * (1.0 + k3 *
                    (Math.exp(-1.0 / k3) - 1.0));
        }
        else
        {
            q = 0.0;
        }
        return q;
    }

    /** Compute the derivative of the HvacThermalUnit for the current states and
     * inputs
     * 
     * @return
     */
    public double getDeriv()
    {
        temperature = x[i];
        fanInletTemp = x[HouseThermalSimTask.AIR_I];
        double outsideTemp = u[HouseThermalSimTask.TOUT_I];
        double insideTemp = x[HouseThermalSimTask.AIR_I];
        // compute the Q
        QunitToAir = computeQunitToAir(temperature, fanInletTemp);
        fanOutletTemp = computeFanOutletTemp(temperature, fanInletTemp);
        QunitToAmb = (temperature - tempAmbient) * k2;

        // Compute the heat going into the air from the unit
        Qout = 0;
        switch (type)
        {
            case HEATER:
                double COP = heatEfficiency;
                // the efficiency is statically computed using cop
                Qout = heatInput * COP;
                P = heatInput;
                break;
            case COOLER:
            default:
                double Seer = heatEfficiency;
                // Compute the heat input based on the COP
                double cfo = mcap * outsideTemp + bcap;
                double cfi = mcap * insideTemp + bcap;
                Qout = heatInput * cfo * cfi;
                // Compute the power input to the house from the climate control.
                // Reduce the ac by the computed COP.
                double acCop82 = Seer / (3.413 * (1 - 0.5 * cd));
                double acCop95 = acCop82 / (mcop0 * 82 + bcop0);
                double acCop = acCop95 * cfi * cfo;
                if (acCop != 0.0)
                {
                    P = (-Qout / (acCop * 3.413)) * 3600; // watts
                }
                else
                {
                    P = Double.POSITIVE_INFINITY;
                }
                break;
        }
        // Compute the derivative and return
        double dx = (Qout - QunitToAmb - QunitToAir) / (cp * m);
        return dx;
    }
}

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
package edu.berkeley.me.jRonSim.house.simulation;

/** The thermal units store and transfer energy inside the edu.berkeley.me.jRonSim.house.  
 *
 * @author William Burke <billstron@gmail.com>
 */
public abstract class ThermalUnit
{

    protected int i = 0;
    protected double P = 0;;  // power consumption
    protected double[] x;
    protected double[] u;
    protected double m;  ///< mass
    protected double cp; ///< specific heat of material in this unit
    protected double cpAir; ///< specific heat of air flowing through around, etc. this unit
    protected double temperature;  ///< current temp.
    protected double tempAmbient;  ///< Ambient temp.
    protected double fanMax;  ///< Maximum fan flow rate
    protected double fanFlow;  ///< mass flow rate
    protected double fanInletTemp;  ///< Inlet air temperature
    protected double fanOutletTemp;  ///< Outlet air temperature
    protected double fanEfficiency;  ///< Used to scale the fan power
    protected double k1;  ///< Heat transfer coef between mass and flowing air
    protected double k2;  ///< Other relevant heat transfer coefs
    protected double k3;  ///< Other relevant heat transfer coefs
    protected double heatInput;  ///< Direct heat input (Capacity of Unit)
    protected double heatInputMax;  ///< Max heat input.
    protected double heatEfficiency;  ///< Used to scale the heater power.
    protected double tempVar;  ///< Used to store variables that need to be seen.

    /** Constructor for the ThermalUnit
     *
     * @param nStates -- number of states
     * @param nInputs -- number of inputs
     */
    public ThermalUnit(int nStates, int nInputs)
    {
        this.x = new double[nStates];
        this.u = new double[nInputs];
    }

    /** Comput the derivative of the system give the x and u specified
     *
     * @param edu.berkeley.me.jRonSim.sim
     * @param x
     * @param u
     * @return
     */
    public double getDeriv(double[] x, double[] u)
    {
        setStates(x);
        setInputs(u);
        return getDeriv();
    }

    /** Compute the derivative given the current x and u.
     * 
     * @param edu.berkeley.me.jRonSim.sim
     * @return
     */
    public abstract double getDeriv();

    /** Update the current x of the system.
     *
     * @param x
     */
    public void setStates(double[] states)
    {
        this.x = states;
    }

    /** Update the current u to the system.
     * 
     * @param u
     */
    public void setInputs(double[] inputs)
    {
        this.u = inputs;
    }

    /** Get the current power consumption.
     *
     * @return
     */
    public double getP()
    {
        return P;
    }
}

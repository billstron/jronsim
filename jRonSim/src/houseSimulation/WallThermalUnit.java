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
class WallThermalUnit extends ThermalUnit
{

    private double QToAir, QToAmb;

    /** Constructs the Thermal Unit for Internal and External Walls.
     *
     * @param m
     * @param kAir
     * @param kAmb -- Set to zero for Internal Walls
     */
    public WallThermalUnit(double m, int iState, double kAir, double kAmb)
    {
        super(5, 2);
        WallThermalUnitInit(m, iState, kAir, kAmb);
    }

    /** Constructs the Thermal Unit for Internal Walls.
     * 
     * @param m
     * @param kAir
     */
    public WallThermalUnit(double m, int iState, double kAir)
    {
        super(5, 2);
        WallThermalUnitInit(m, iState, kAir, 0.0);
    }

    /** Initializes the class
     * 
     * @param m
     * @param iState
     * @param kAir
     * @param kAmb
     */
    private void WallThermalUnitInit(double m, int iState, double kAir, double kAmb)
    {
        this.i = iState;
        this.temperature = 76.0;  // ^oF Initial temperature
        this.m = m;
        this.cp = 0.29;
        this.cpAir = 0.24;
        this.k1 = kAir;
        this.k2 = kAmb;
    }

    public double getQToAir()
    {
        return QToAir;
    }

    private double computeQToAmbient(double outsideTemp)
    {
        return (outsideTemp - temperature) * k2;
    }

    private double computeQToAir(double insideTemp)
    {
        return (insideTemp - temperature) * k1;
    }

    @Override
    public double getDeriv()
    {
        // get the most current variables
        temperature = x[i];
        double insideTemp = x[HouseThermalSimTask.AIR_I];
        double outsideTemp = u[HouseThermalSimTask.TOUT_I];

        // compute the derivative
        QToAir = computeQToAir(insideTemp);
        QToAmb = computeQToAmbient(outsideTemp);
        double dx = (QToAir + QToAmb) / (cp * m);
        return dx;
    }
}

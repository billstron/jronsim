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
class AirThermalUnit extends ThermalUnit
{

    private HvacThermalUnit heater = null;
    private HvacThermalUnit cooler = null;
    private WallThermalUnit extWall = null;
    private WallThermalUnit intWall = null;

    public AirThermalUnit(double m, double windowArea, double infiltrationFlow,
            double internalInput, HvacThermalUnit heater,
            HvacThermalUnit cooler, WallThermalUnit extWall,
            WallThermalUnit intWall)
    {
        super(5, 2);
        this.m = m;  // typ 13290 * 0.075;  ft^3 * lb/ft^3
        this.temperature = 75.0;  // Initial temperature
        this.cpAir = 0.24;  // BTU/(lb F)
        this.k1 = windowArea;  // typ 225 ft^2 (ext 91)
        this.fanFlow = infiltrationFlow;
        // typ (8325  * 0.075) / 3600 ft^3/hr -> lb/s (ext 3375) infiltration
        this.heatInputMax = internalInput;
        // typ 2880 / 3600 (btu/hr) / (hr/s) -> btu/s input from appliances, etc

        this.heater = heater;
        this.cooler = cooler;
        this.intWall = intWall;
        this.extWall = extWall;
    }

    @Override
    public double getDeriv()
    {
        temperature = x[i];
        // Infiltration Calculations.
        // Air fan brings in air from the outside at the constant rate fanFlow
        fanOutletTemp = u[HouseThermalSim.TOUT_I];  // brings in air from outside

        // Internal heat sources and heat through windows.
        // First term is radiation input through the windows.
        // Second term is constant from internal sources.  Computers etc.
        //      btu/hr / 3600 = btu/sec
        heatInput = (u[HouseThermalSim.RAD_I] * k1) / 3600 + heatInputMax;

        // Air temperature is determined by a mixing process and conduction.
        // For mixing: Rate of change of air temperature = (Qin / m)(Tin - Tout)
        // Heater air mixing with room air
        double Qh = (heater.getFanFlow() / m) * (heater.getFanOutletTemp() -
                temperature);
        // Cooler air mixing with room air
        double Qc = (cooler.getFanFlow() / m) * (cooler.getFanOutletTemp() -
                temperature);
        // Infiltration Mixing.
        double Qi = (fanFlow / m) * (fanOutletTemp - temperature);
        // Combine heat inputs.
        double dx = Qh + Qc + Qi + (heatInput - intWall.getQToAir()) -
                extWall.getQToAir() / (cpAir * m);
        return dx;
    }
}

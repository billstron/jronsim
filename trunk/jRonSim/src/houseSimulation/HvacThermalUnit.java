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
public class HvacThermalUnit extends ThermalUnit {

    private double QunitToAir, QunitToAmb;
    private double Qout;
    private double Tout;

    public void setQout(double Q){
        this.Qout = Q;
    }

    @Override
    public void setTemp(double Temp) {
        this.temperature = Temp;
    }

    @Override
    public double getDeriv(HouseThermalSim sim) {
        Tout = sim.Tout;
        fanInletTemp = sim.x[sim.AIR];
        // compute the Q
        if (fanFlow > 0.0) {
            QunitToAir = k1 * (temperature -
                    fanInletTemp) * (1.0 + k3 *
                    (Math.exp(-1.0 / k3) - 1.0));
            fanOutletTemp = fanInletTemp +
                    (temperature - fanInletTemp) *
                    (1.0 - Math.exp(-1.0 / k3));
        } else {
            fanOutletTemp = temperature;
            QunitToAir = 0.0;
        }
        QunitToAmb = (temperature - tempAmbient) * k2;

        double dx = (Qout - QunitToAmb - QunitToAir) / (cp * m);
        return dx;
    }
}

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
package houseSimulation;

import TranRunJLite.*;
import ODEsolver.*;

/** Simulates the house
 * 
 * @author William Burke <billstron@gmail.com>
 */
public class HouseThermalSim extends TrjTask {

    // house parameters
    ThermalUnit houseList[];
    private int nStates;
    double Tout;  ///< guess...
    double RadSolar;
    double[] x;
    // Ode parameters. 
    double tLast;  //  Time at which simulation was last run
    double tCur;  // Current time
    double lastStep;  // For adaptive ODE solvers
    double stepSize;
    double stepMin;
    boolean useAdaptiveSolver;
    HouseODE hs = null;   // Simulation object
    double[] x0;
    double[] abstol;  // Absolute and relative tolerances
    double reltol;

    public final int AIR = 0;
    public final int HEATER = 1;
    public final int COOLER = 2;
    public final int INTWALL = 3;
    public final int EXTWALL = 4;

    public HouseThermalSim(
            String name,
            TrjSys sys,
            ThermalUnit houseList[],
            int nStates,
            boolean useAdaptiveSolver) {
        super(name, sys, 0 /*initial state*/, true /*taskActive*/);

        this.houseList = houseList;
        this.useAdaptiveSolver = useAdaptiveSolver;
        this.nStates = nStates;
        this.x = new double[nStates];
        this.x0 = new double[nStates];
        this.abstol = new double[nStates];

        tLast = 0.0;
        // Create an ODE (simulation) object
        // State variables:
        //  velMotor, angleMotor, velLoad, angleLoad
        for (int i = 0; i < nStates; i++) {
            x0[i] = 0.0;  // State variable initial values
            abstol[i] = 1.e-4;  // Absolute tolerance for adaptive solvers
        }
        reltol = 1.e-4;

        hs = new HouseODE(
                nStates, //int nn,
                x0, //double xx0[],
                0.0, //double t0,
                abstol, //double [] abstol,
                reltol //double reltol
                );
        stepMin = 1.e-7;  // Smallest allowable adaptive step size
        stepSize = 1.e-4;  // Nominal step size
    }

    public boolean RunTaskNow(TrjSys sys) {
        // The simulation runs all the time and has no states.
        // The simulation runs all the time and has no states.
        tCur = sys.GetRunningTime();
        if (tCur <= tLast) {
            // Make sure time has moved forward
            tLast = tCur;
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean RunTask(TrjSys sys) {
        // The simulation runs all the time and has no states.
        // This method just moves the simulation forward to the
        // current time.
        // Run the simulation to the current time

        tCur = sys.GetRunningTime();
        if (useAdaptiveSolver) {
            lastStep = hs.multiStepAdaptive(tCur - tLast, stepSize, stepMin);
            stepSize = lastStep;  // For the next iteration
        } else {
            hs.multiStepFixed(tCur - tLast, stepSize);
        }
        tLast = tCur;
        return false;
    }

    // Create an inner class for the simulation
    public class HouseODE extends RKF45 {

        public HouseODE(int nn, double xx0[], double t0,
                double[] abstol, double reltol) {
            super(nn, xx0, t0, abstol, reltol);
        }

        @Override
        public void deriv() {
            // State variables:
            // use x[0] ... x[nstate-1]
            for (int k = 0; k < nStates; k++) {
                ThermalUnit u = houseList[k];
                u.setTemp(x[k]);
            }
            // get the derivitives
            for(int k = 0; k < nStates; k++){
                ThermalUnit u = houseList[k];
                //dx[k] = u.getDeriv(this);
            }
            // todo: use matrix math to get the derivative.  
        }
    }
}
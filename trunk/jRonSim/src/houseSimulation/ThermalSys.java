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

import TranRunJLite.*;

/** The thermal simulation system.
 * 
 * @author William Burke <billstron@gmail.com>
 */
public class ThermalSys extends TrjSys implements HouseIO {

    String name;
    private HouseThermalSim thermSim;
    private HvacUnitTask acTask;
    private HvacUnitTask heaterTask;

    /** construct the thermal simulation
     * 
     * @param name -- name of the simulation
     * @param tm -- time structure to be used
     */
    public ThermalSys(String name, TrjTime tm) {
        super(tm);
        this.name = name;
        ThermalUnit[] unitList = new ThermalUnit[5];
        thermSim = new HouseThermalSim("House Simulation", this, unitList, 5,
                true);

        unitList[thermSim.AIR] = new AirThermalUnit();
        unitList[thermSim.COOLER] = new HvacThermalUnit();
        unitList[thermSim.HEATER] = new HvacThermalUnit();
        unitList[thermSim.EXTWALL] = new ExtWallThermalUnit();
        unitList[thermSim.INTWALL] = new IntWallThermalUnit();

        acTask = new AirConditionerTask();
        heaterTask = new HeaterTask();
        
    }

    /** get inside temperature
     * 
     * @return
     */
    public double getTempInside() {
        return thermSim.getTin();
    }

    /** get heater state
     *
     * @return heater on state
     */
    public boolean getHeaterOnState() {
        return thermSim.getHeaterOn();
    }

    /** set the heater on state
     * 
     * @param state
     */
    public void setHeaterOnState(boolean state) {
        thermSim.setHeaterOn(state);
    }

    /** get the cooler state
     *
     * @return
     */
    public boolean getCoolerOnState() {
        return thermSim.getCoolerOn();
    }

    /** set the cooler state
     * 
     * @param state
     */
    public void setCoolerOnState(boolean state) {
        thermSim.setCoolerOn(state);
    }
}
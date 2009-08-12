/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package houseSimulation;

/** SimulationIO connects the house simulation to the thermostat. 
 *
 * @author WJBurke 7/30/2009
 */
public class SimulationIO implements HouseIO {

    double Tout = 75.0;  // outside temperature
    boolean HeaterOn = false;
    boolean CoolerOn = false;

    public SimulationIO() {
        // TODO: fill in the constructor
    }
    
    /** Returns the inside temperature
     *
     * @return inside temperature
     */
    public double getTempInside() {
        return Tout;
    }

    /** Sets the Heater Unit on or off
     *
     * @param state -- Heater on?
     */
    public void setHeaterOnState(boolean state) {
        HeaterOn = state;
    }

    /** Sets the Cooler Unit on or off
     *
     * @param state Cooler on?
     */
    public void setCoolerOnState(boolean state) {
        CoolerOn = state;
    }
}

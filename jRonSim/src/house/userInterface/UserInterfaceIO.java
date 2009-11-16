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
package house.userInterface;

import house.thermostat.ThermostatMode;

/** Defines the interface for how the UserInterface should communicate with the
 * GUI.
 * 
 * @author William Burke <billstron@gmail.com>
 */
public interface UserInterfaceIO {

    /** Get the Auxiliary display message.
     * Used by the GUI thread.
     *
     * @return
     */
    public String getAuxDisplay();

    /** Get the label for the Auxiliary display.
     * Used by the GUI thread.
     *
     * @return
     */
    public String getAuxLabel();

    /** Get the main display text.
     * Used by the GUI thread.
     *
     * @return
     */
    public String getMainDisplay();

    /** Get the state of the hold LED.
     * Used by the GUI thread.
     *
     * @return
     */
    public boolean getHoldLed();

    /** Get the state of the Heater on-state LED.
     * Used by the GUI thread.
     *
     * @return
     */
    public boolean getHeaterLed();

    /** Get the state of the cooler on-state LED.
     * Used by the GUI thread.
     *
     * @return
     */
    public boolean getCoolerLed();

    /** Set the setpoint change.
     * Used by the GUI thread.
     *
     * @param dT
     */
    public void setSetpointChange(double dT);

    /** Toggle the hold button.
     * Used by the GUI thread.
     *
     */
    public void setHoldToggle();

    /** Indicate that the program should be stopped.
     * Used by the GUI thread.
     *
     */
    public void stopProgram();

    /** Set the current mode toggle.
     * Used by the GUI thread.
     *
     * @param mode
     */
    public void setModeToggle(ThermostatMode mode);
}

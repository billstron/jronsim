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

package edu.berkeley.me.jRonSim.house.userInterface;

/** Container for all of the dynamic data that goes into the thermosat GUI.
 * 
 * @author William Burke <billstron@gmail.com>
 */
public class UserInterfaceData {
    final String msgAux, msgMain, labelAuxMsg;
    final boolean ledHold, ledHeater, ledCooler;

    /** Concstruct a UserInterfaceData object.
     * 
     * @param msgAux
     * @param msgMain
     * @param labelAuxMsg
     * @param ledHold
     * @param ledHeater
     * @param ledCooler
     */
    public UserInterfaceData(String msgAux, String msgMain, String labelAuxMsg,
            boolean ledHold, boolean ledHeater, boolean ledCooler){
        this.msgAux = msgAux;
        this.labelAuxMsg = labelAuxMsg;
        this.msgMain = msgMain;
        this.ledHold = ledHold;
        this.ledHeater = ledHeater;
        this.ledCooler = ledCooler;
    }
}

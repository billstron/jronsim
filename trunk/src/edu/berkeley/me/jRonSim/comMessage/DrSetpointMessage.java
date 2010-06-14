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
package edu.berkeley.me.jRonSim.comMessage;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**This is the basic setpoint based DR message with ramps in and out.  
 *
 * @author William Burke <billstron@gmail.com>
 */
public class DrSetpointMessage extends Message {

    private double TmodMax;
    private double rampRateIn;  // T / ms
    private double rampRateOut;
    private GregorianCalendar start;
    private GregorianCalendar end;

    /** Construct the basic setpoint message with no setpoint ramping.
     * 
     * @param from -- From address
     * @param to -- To address
     * @param start -- Start Time
     * @param end -- End Time
     * @param TmodMax -- The maximum setback.
     */
    public DrSetpointMessage(InetAddress from, InetAddress to,
            GregorianCalendar start, GregorianCalendar end, double TmodMax) {
        super(from, MessageType.DR_SETPOINT);

        this.to = to;
        this.start = start;
        this.end = end;
        this.TmodMax = TmodMax;
        this.rampRateIn = Float.POSITIVE_INFINITY;
        this.rampRateOut = Float.POSITIVE_INFINITY;
    }

    /** Construct the basic setpoint message with setpoint ramping.
     *
     * @param from -- From address
     * @param to -- To address
     * @param start -- Start Time
     * @param end -- End Time
     * @param TmodMax -- The maximum setback.  
     * @param rampRateIn -- positive units of Tsp/ms, Step = Infinity.
     * @param rampRateOut -- positive units of Tsp/ms, Step = Infinity.  
     */
    public DrSetpointMessage(InetAddress from, InetAddress to,
            GregorianCalendar start, GregorianCalendar end, double TmodMax,
            double rampRateIn, double rampRateOut) {
        super(from, MessageType.DR_SETPOINT);

        this.to = to;
        this.start = start;
        this.end = end;
        this.TmodMax = TmodMax;
        this.rampRateIn = Math.abs(rampRateIn);
        this.rampRateOut = Math.abs(rampRateOut);
    }

    /** Get the DR based setback for the specified time.
     *
     * @param now -- The time of interest.
     * @return
     */
    public double getCurrentTspMod(GregorianCalendar now) {
        long tStart = start.getTimeInMillis();
        long tEnd = end.getTimeInMillis();
        long tNow = now.getTimeInMillis();

        long compStart = tNow - tStart;
        long compEnd = tNow - tEnd;

        //System.out.println(tStart + " " + tEnd);
        double TspMod = 0;
        if (compStart > 0 && compEnd <= 0) {
            // compute the ramp in.  
            TspMod = rampRateIn * compStart;
            if (TspMod > Math.abs(TmodMax)) {
                TspMod = Math.abs(TmodMax);
            }
            //System.out.println(TspMod);
            // compute the ramp out
            double tRampOut = -TspMod / rampRateOut;
            //System.out.println(tRampOut);
            if(compEnd > tRampOut){
                TspMod += (tRampOut - compEnd) * rampRateOut;
            }
            if(TspMod < 0) TspMod = 0;

            //System.out.println(TspMod);
            // bring it back to the direction it should be going.  
            TspMod *= Math.signum(TmodMax);
            //System.out.println("compStart, compEnd: " +
            //        compStart + ", " + compEnd);
        }
        return TspMod;
    }

    /** Test function.
     * 
     * @param args
     * @throws UnknownHostException
     */
    public static void main(String args[]) throws UnknownHostException {
        InetAddress from = InetAddress.getLocalHost();
        InetAddress to = InetAddress.getLocalHost();
        GregorianCalendar start = new GregorianCalendar();
        GregorianCalendar end = new GregorianCalendar();
        end.add(Calendar.HOUR_OF_DAY, 2);
        double rampIn = 4.0 / (45 * 60 * 1000);
        double rampOut = 4.0 / (60 * 60 * 1000);

        System.out.println("Ramp Rates (Tsp/ms) = " + rampIn);
        
        DrSetpointMessage msg = new DrSetpointMessage(from, to, start, end, 4,
                rampIn, rampOut);
        
        GregorianCalendar ttest = (GregorianCalendar) start.clone();
        double TspMod = 0;
        for(double dm = 0; dm <= 120; dm += 15){
            TspMod = msg.getCurrentTspMod(ttest);
            ttest.add(Calendar.MINUTE, 15);
            System.out.println("At " + dm + "min, TspMod = " + TspMod);
        }
    }
}

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
package house;

import java.io.*;
import gatewayComm.AmiCommSetup;
import TranRunJLite.TrjTime;
import TranRunJLite.TrjTimeAccel;

/**
 *
 * @author William Burke <billstron@gmail.com>
 */
public class PctHouseRunnableAMI implements Runnable
{

    private double dt;
    private double tFinal;
    private TrjTime tm;
    private final House hs;
    
    //setup communications with Gateway
    private AmiCommSetup ami;
    private String pow;
    private String t;
    private String data;
    
    
    /** Constructor for the implementable house.
     *
     * @param dt -- time step
     * @param tFinal -- final running time
     * @param tm -- timing structure
     * @param hs -- the ipctHouse that is made implementable
     */
    public PctHouseRunnableAMI(double dt, double tFinal, TrjTime tm, House hs, AmiCommSetup ami)
    {
        this.dt = dt;
        this.tFinal = tFinal;
        this.tm = tm;
        this.hs = hs;
        this.ami = ami;
    }

    public void run()
    {
    	
        boolean stop = false;
        while (tm.getRunningTime() <= tFinal && !stop)
        {
            stop = hs.run();
            
            pow = Double.toString(hs.getP());
            t = Double.toString(tm.getRunningTime());
            data = ami.convertToXML(pow, t);
            ami.postToGateway(data);
            System.out.println(data);
            
            tm.incrementRunningTime(dt);
        }
        
        System.out.println("Simulation Stopped");
        ami.postToGateway("STOP");
        ami.closeAll();
        
        System.exit(0);
    }
 
}

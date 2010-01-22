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

import TranRunJLite.TrjTime;
import TranRunJLite.TrjTimeAccel;

/**
 *
 * @author William Burke <billstron@gmail.com>
 */
public class PctHouseRunnable implements Runnable
{

    private double dt;
    private double tFinal;
    private TrjTime tm;
    private final PctHouse hs;
 
 /*   
    File dataFile = new File("PctHouseSim_data.txt");
    //to specify a certain file location use File("C://")
    //add code to clear file
    PrintWriter out = null;
*/
    /** Constructor for the implementable house.
     *
     * @param dt -- time step
     * @param tFinal -- final running time
     * @param tm -- timing structure
     * @param hs -- the ipctHouse that is made implementable
     */
    public PctHouseRunnable(double dt, double tFinal, TrjTime tm, PctHouse hs)
    {
        this.dt = dt;
        this.tFinal = tFinal;
        this.tm = tm;
        this.hs = hs;
    }

    /** run function
     *
     */
    public void run()
    {
    	/*
    	//setup data logger
        try{
        	out = new PrintWriter(new FileWriter (dataFile));
        } catch(IOException e){
        	System.err.println("Error creating PrintWriter for data output");
        }
        */
    	
        boolean stop = false;
        while (tm.getRunningTime() <= tFinal && !stop)
        {
            stop = hs.run();
            //hs.log(out);		//do logging
            tm.incrementRunningTime(dt);
        }
        System.out.println("Simulation Stopped");
        System.exit(0);
    }

    /** Test function
     *
     * @param args
     */
    
    public static void main(String[] args)
    {
        double dt = 5.0;  // Used for samples that need a time delta
        double tFinal = 24 * 60 * 60;  // sec
        TrjTimeAccel tm = new TrjTimeAccel(300);
        PctHouse hs = new PctHouse(tm, true);

        PctHouseRunnable runner = new PctHouseRunnable(dt, tFinal, tm, hs);
        Thread t = new Thread(runner);
        t.start();
    }
 
}

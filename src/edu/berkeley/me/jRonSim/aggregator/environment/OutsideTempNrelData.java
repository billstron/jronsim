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
package edu.berkeley.me.jRonSim.aggregator.environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

/** Used for computation of the outside temperature.
 *
 * @author William Burke <billstron@gmail.com>
 */
public class OutsideTempNrelData extends OutsideTemperatureModel
{

    ArrayList<GregorianCalendar> tTable = new ArrayList<GregorianCalendar>();
    ArrayList<Double> ToutTable = new ArrayList<Double>();
    static final int LOCALTIME_I = 0;
    static final int TOUT_I = 2;
    private int i0;

    public OutsideTempNrelData(String fname) throws FileNotFoundException, IOException
    {
        i0 = 0;

        File file = new File(fname);
        BufferedReader bufRdr = new BufferedReader(new FileReader(file));
        String line = null;
        int col = 0;

        // burn the header line
        for (int i = 0; i < 3; i++)
        {
            line = bufRdr.readLine();
        }
        //read each line of text file
        while ((line = bufRdr.readLine()) != null)
        {
            StringTokenizer st = new StringTokenizer(line, ",");
            boolean lineDone = false;
            col = 0;
            while (st.hasMoreTokens() && !lineDone)
            {
                String temp = st.nextToken();
                //get next token and store it in the array
                switch (col)
                {
                    case (LOCALTIME_I):
                        tTable.add(stringToCalendar(temp));
                        break;
                    case (TOUT_I):
                        double Tc = 0;
                        if (temp.contains("*"))
                        {
                            Tc = Double.valueOf(temp.substring(0, temp.indexOf("*"))) / 10;
                        }
                        else
                        {
                            Tc = Double.valueOf(temp) / 10;
                        }
                        ToutTable.add(Tc * (9.0 / 5.0) + 32.0);
                        lineDone = true;
                        break;
                    default:
                        break;
                }
                col++;
            }
        }
        //close the file
        bufRdr.close();
    }

    private GregorianCalendar stringToCalendar(String str)
    {

        int year, month, day, hour, min, sec;

        // 02/27/2007 16:00
        StringTokenizer st = new StringTokenizer(str, "/");
        month = Integer.valueOf(st.nextToken()) - 1;  // have to subtract 1
        day = Integer.valueOf(st.nextToken());
        st = new StringTokenizer(st.nextToken(), " ");
        year = Integer.valueOf(st.nextToken());
        st = new StringTokenizer(st.nextToken(), ":");
        hour = Integer.valueOf(st.nextToken());
        min = Integer.valueOf(st.nextToken());
        sec = 0;

        return new GregorianCalendar(year, month, day, hour, min, sec);
    }

    /** Get the outside temperature for the specified time.  
     *
     * @param time
     * @return
     */
    @Override
    double getTemperature(GregorianCalendar time)
    {
        //System.out.println("here");
        double Tout = 0;
        GregorianCalendar t0 = null;
        GregorianCalendar t1 = null;
        // find the intex of the table time right before the specified time.
        for (int i = i0; i < tTable.size()-1; i++)
        {
            t0 = tTable.get(i);
            t1 = tTable.get(i + 1);
            if (t0.compareTo(time) <= 0 && t1.compareTo(time) > 0)
            {
                i0 = i;
                break;
            }
        }
        // compute the temp
        // get the differences in times
        double dt = t1.getTimeInMillis() - t0.getTimeInMillis();
        double t = time.getTimeInMillis() - t0.getTimeInMillis();
        //System.out.printf("%d, %d\n", time.getTimeInMillis(), t0.getTimeInMillis());
        //System.out.println(dt + ", " + t);
        // get the differences in temps
        double T0 = ToutTable.get(i0);
        double dT = ToutTable.get(i0 + 1) - T0;
        // linearly interpolate
        Tout = T0 + dT * (t / dt);
        return Tout;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException
    {
        String fname = "/data/Cal/weatherData/KSCK_20071022-20080101.CSV";
        OutsideTempNrelData tempV = new OutsideTempNrelData(fname);

        System.out.println("data size: " + tempV.ToutTable.size());
        for (int i = 0; i < 5; i++)
        {
            System.out.println(tempV.ToutTable.get(i) + ", " + (tempV.tTable.get(i)).toString());
        }
    }
}

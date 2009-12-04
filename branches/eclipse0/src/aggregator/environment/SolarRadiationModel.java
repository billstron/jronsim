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
package aggregator.environment;

import java.util.GregorianCalendar;

/** Computes the solar radiation as per the ASHRAE standard:
 * Table 7 Extraterrestrial Solar Irradiance and Related Data
 * 2001 ASHRAE Fundamentals Handbook, p30.13
 *
 * @author William Burke <billstron@gmail.com>
 */
public class SolarRadiationModel
{

    private static final double DEG2RAD = 0.01744;
    private static final int[] daysInMonth =
    {
        31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
    };
    /** Data is for the 21st day of each month.
     * Interpolate to find other days.
     **/
    private static final double[] therm_E0 =
    {
        448.8, 444.2, 437.7, 429.9, 423.6, 420.2,
        420.3, 424.1, 430.7, 437.3, 445.3, 449.1
    };
    private static final double[] therm_ET =
    {
        -11.2, -13.9, -7.5, 1.1, 3.3, -1.4, -6.2,
        -2.4, 7.5, 15.4, 13.8, 1.6
    };
    private static final double[] therm_del =
    {
        -20.0, -10.8, 0, 11.6, 20.0, 23.45, 20.6,
        12.3, 0.0, -10.5, -19.8, -23.45
    };
    private static final double[] therm_A =
    {
        390, 385, 376, 360, 350, 345, 344, 351, 365,
        378, 387, 391
    };
    private static final double[] therm_B =
    {
        0.142, 0.144, 0.156, 0.180, 0.196, 0.205,
        0.207, 0.201, 0.177, 0.160, 0.149, 0.142
    };
    private static final double[] therm_C =
    {
        0.058, 0.060, 0.071, 0.097, 0.121, 0.134,
        0.136, 0.122, 0.092, 0.073, 0.063, 0.057
    };
    private final double therm_LSM = 120;
    private final double therm_psi = 90;  // All windows facing West 90 // 90
    private final double therm_sigma = 0;  // Flat land
    private final double therm_IAC = 0.5;  // 0.7 shading
    private final double therm_U = 2;//0.5;//10;  // ASHRAE p30.8, Table 4, (0.5)
    // Double Glazing e = .6, .5in air space.
    public final static int LAT = 0;
    public final static int LON = 1;
    private final double[] location;

    /** Construct the solar radiation model for use computing the solar
     * radiation.
     * 
     * @param location
     */
    public SolarRadiationModel(double[] location)
    {
        // Initialize the variables.
        this.location = location;

    }

    /** Interpret the table
     *
     * @param t
     * @param table
     * @return
     */
    private double interpTable7(GregorianCalendar date, double table[])
    {
        int Month = date.get(GregorianCalendar.MONTH);//date[0] - 1;
        int prevMonth = Month - 1;
        if (prevMonth < 0)
        {
            prevMonth = 11;
        }
        int nextMonth = Month + 1;
        if (nextMonth > 11)
        {
            nextMonth = 0;
        }
        int Day = date.get(GregorianCalendar.DAY_OF_MONTH);
        double tableIndex = 0;
        if (Day <= 21)
        {
            tableIndex = Month +
                    ((Day + daysInMonth[prevMonth] - 21) / daysInMonth[prevMonth]);
        }
        else
        {
            tableIndex = nextMonth + ((double) (Day - 21) / (double) daysInMonth[Month]);
        }
        double interp = (table[(int) tableIndex + 1] - table[(int) tableIndex]) * (tableIndex - (int) tableIndex) + table[(int) tableIndex];
        return interp;
    }

    /** returns the direct irradiance constant for the given time
     *
     * @param date
     * @return
     */
    double computeRadiation(GregorianCalendar date)
    {
        double LST = date.get(GregorianCalendar.HOUR_OF_DAY) -
                date.get(GregorianCalendar.DST_OFFSET);
        //GetTimeOfDayHours(t) - GetIsDstFlag(t);

        double A = interpTable7(date, therm_A);
        double B = interpTable7(date, therm_B);
        double CN = 1;
        double del = interpTable7(date, therm_del);
        double ET = interpTable7(date, therm_ET);

        double AST = LST + ET / 60 + (therm_LSM - location[LON]) / 15;
        double H = 15 * (AST - 12);

        double beta = Math.asin(Math.cos(location[LAT] * DEG2RAD) *
                Math.cos(del * DEG2RAD) * Math.cos(H * DEG2RAD) +
                Math.sin(location[LAT] * DEG2RAD) * Math.sin(del * DEG2RAD));

        double phi = Math.acos((Math.sin(beta) * Math.sin(location[LAT] * DEG2RAD) - Math.sin(del * DEG2RAD)) / (Math.cos(beta) * Math.cos(location[LAT] * DEG2RAD)));

        double gamma = phi - therm_psi * DEG2RAD;

        double theta = Math.acos(Math.cos(beta) * Math.cos(gamma) *
                Math.sin(therm_sigma * DEG2RAD) + Math.sin(beta) * Math.cos(therm_sigma * DEG2RAD));

        double Edn = (A / (Math.exp(B / Math.sin(beta)))) * CN;
        if (beta <= 0)
        {
            Edn = 0;
        }

        double Ed = Edn * Math.cos(theta);
        if (Math.cos(theta) < 0)
        {
            Ed = 0;
        }
        return Ed;
    }
}

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
package thermostat;

import TranRunJLite.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/** The definition of a setpoint
 *
 * @author WJBurke
 */
class Setpoint {

    private double Tsp;
    private int[] tm = new int[3];
    private Label label;

    private enum Time {

        HOUR, MINUTE, SECOND
    }

    /** Labels for the Setpoint table entries
     *
     * @author WJBurke
     */
    enum Label {

        MORNING("Morning"),
        DAY("Day"),
        EVENING("Evening"),
        NIGHT("Night");
        private String name;

        /** Constructor fo the Setpoint Lable enum
         *
         * @param name -- human readable name of the entry
         */
        Label(String name) {
            this.name = name;
        }

        /** Gets the name of the setpoint table entry
         *
         * @return name of the entry
         */
        public String getName() {
            return this.name;
        }
    }

    /** Constructor for a setpoint
     *
     * @param Tsp -- Setpoint temperature (^oF)
     * @param hour -- Start hour (24 hour format: 0 - 24)
     * @param min -- Start minute
     * @param label -- Label for this entry
     */
    Setpoint(double Tsp, int hour, int min, Label label) {
        this.tm[Time.HOUR.ordinal()] = hour;
        this.tm[Time.MINUTE.ordinal()] = min;
        this.tm[Time.SECOND.ordinal()] = 0;

        this.Tsp = Tsp;
        this.label = label;
    }

    double getTsp() {
        return this.Tsp;
    }

    Label getLabel() {
        return label;
    }

    boolean isBefore(Calendar cal) {
        boolean isBefore = false;
        if (tm[Time.HOUR.ordinal()] < cal.get(Calendar.HOUR_OF_DAY)) {
            isBefore = true;
        } else if (tm[Time.HOUR.ordinal()] == cal.get(Calendar.HOUR_OF_DAY)) {
            if (tm[Time.MINUTE.ordinal()] <= cal.get(Calendar.MINUTE)) {
                isBefore = true;
            } else if (tm[Time.MINUTE.ordinal()] == cal.get(Calendar.MINUTE)) {
                if (tm[Time.SECOND.ordinal()] <= cal.get(Calendar.SECOND)) {
                    isBefore = true;
                } else {
                    isBefore = false;
                }
            }
        }
        return isBefore;
    }
}




/**
 *
 * @author WJBurke
 */
public class SetpointTable {

    private ArrayList<Setpoint>[] table;// = new ArrayList<Setpoint>[7];

    public SetpointTable() {
        double Tsp = 75.0;
        int hour = 6;
        int min = 0;
        Setpoint.Label label = Setpoint.Label.MORNING;
        table = new ArrayList[7];
        for (int day = 0; day < table.length; day++) {
            table[day] = new ArrayList<Setpoint>();
            Setpoint sp = new Setpoint(Tsp, hour, min, label);
            table[day].add(sp);
            //Tsp += 1;
        }
    }

    public double getTsp(GregorianCalendar cal) {
        int day = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int dayPrev = day - 1;
        if (dayPrev < 0) {
            dayPrev = 6;
        }
        //System.out.println("day, preDay: " + day + ", " + dayPrev);
        double Tsp = table[dayPrev].get(table[dayPrev].size() - 1).getTsp();

        for (Setpoint sp : table[day]) {
            if (sp.isBefore(cal)) {
                //System.out.println("time: " + cal.get(Calendar.HOUR_OF_DAY));
                Tsp = sp.getTsp();
            }
        }
        return Tsp;
    }

    public static void main(String[] args) {

        PrintWriter dataFile0 = null;
        try {
            FileWriter fW = new FileWriter("dataFile0.txt");
            dataFile0 = new PrintWriter(fW);
        } catch (IOException e) {
            System.out.println("IO Error " + e);
            System.exit(1);  // File error -- quit
        }
        GregorianCalendar dStart = new GregorianCalendar(1977, 10, 2, 0, 0, 0);
        TrjTimeSim tm = new TrjTimeSim(dStart, 0);

        SetpointTable table = new SetpointTable();

        double t = 0;
        double dt = 60 * 30;
        double tNext = 0;
        double tStop = 48 * 3600;
        while (tm.getRunningTime() < tStop) {
            t = tm.getRunningTime();
            if (t >= tNext) {
                double Tsp = table.getTsp(tm.getCalendar(t));
                dataFile0.println(t + ", " + Tsp);
                tNext += dt;
            }
            tm.incrementRunningTime(dt);
        }
        dataFile0.close();
    }
}

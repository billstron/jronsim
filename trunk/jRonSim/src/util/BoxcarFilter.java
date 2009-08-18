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
package util;

import TranRunJLite.TrjSys;
import TranRunJLite.TrjTask;
import java.util.ArrayList;

/**Filters the selected variable using a boxcar filter.  
 *
 * @author William Burke <billstron@gmail.com>
 */
public abstract class BoxcarFilter extends TrjTask {

    private double dt;
    private double tNext = 0;
    private double tNextFilt = 0;
    private double dtBox;
    private int boxSize;
    private ArrayList<Double> yList;
    private double y = 0;
    private double yFilt = 0;
    private boolean runningFilt;

    /** Get the working variable.  
     * @return
     */
    public abstract double getProcessValue();

    /** Construct the boxcar filter.
     * 
     * @param name
     * @param sys
     * @param startActive
     * @param dtBox
     * @param runningFilt
     * @param dt
     */
    public BoxcarFilter(String name, TrjSys sys, boolean startActive,
            boolean runningFilt, double dtBox, double dt) {
        super(name, sys, 0 /*Initial State*/, startActive);
        this.stateNames.add("Filter State (Only)");

        this.dt = dt;
        this.dtBox = dtBox;
        this.boxSize = (int) Math.floor(dtBox / dt);
        this.yList = new ArrayList<Double>(this.boxSize);
        this.runningFilt = runningFilt;
    }
    private int FILTER = 0;

    /** Get the most recent value of the filter.  
     * 
     * @return
     */
    public double getFilterResult() {
        return yFilt;
    }

    /** Apply the filter.  
     * 
     * @param sys
     * @return
     */
    @Override
    public boolean RunTask(TrjSys sys) {
        double t = sys.GetRunningTime();
        if (t >= tNext) {
            // get the most recent process value
            y = getProcessValue();
            // check the size of the list and trim.
            if (yList.size() == boxSize) {
                yList.remove(yList.size() - 1);
            }
            // add the most recent value to the list.
            yList.add(0, y);
            // calculate the new filtered result.
            if (t >= tNextFilt) {
                double ySum = 0;
                for (double yK : yList) {
                    ySum += yK;
                }
                yFilt = ySum / (double) boxSize;
            }
            // calculate the new filter time
            if (runningFilt) {
                tNextFilt += dtBox;
            } else {
                tNextFilt += dt;
            }
        }
        return false;
    }
}

/*
 * Copyright (c) 2010, Regents of the University of California
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
package edu.berkeley.me.jRonSim.house.appliances;

import edu.berkeley.me.jRonSim.house.Consumer;
import edu.berkeley.me.jRonSim.util.BoundedRand;
import TranRunJLite.TrjSys;
import TranRunJLite.TrjTime;

/**
 * This system runs any simple household appliances that should turn on
 * automatically. The idea is to allow things like washing machines and clothes
 * Driers to turn on without a (simulated) human intervening.
 * 
 * @author William Burke <billstron@gmail.com>
 * @date Mar 11, 2010
 */
public class AutoAppliancesSys extends TrjSys implements Consumer {

	String name;
	private DryTask dryer;

	/**
	 * Construct the AutoApplianceSys. This system runs simple household
	 * appliances that should turn on and off automatically.
	 * 
	 * @param name
	 * @param tm
	 * @param rand
	 */
	public AutoAppliancesSys(String name, TrjTime tm, BoundedRand rand) {
		super(tm);
		this.name = name;
		// construct the drytask
		dryer = new DryTask("Clothes Dryer 0", this, 5.0, 5000, rand);
	}

	/**
	 * Get the power demand
	 * 
	 * @return
	 */
	public double getP() {
		return dryer.getP();
	}
}

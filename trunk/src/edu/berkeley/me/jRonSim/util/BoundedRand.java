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
package edu.berkeley.me.jRonSim.util;

import java.util.Random;

/**
 * @author William Burke <billstron@gmail.com>
 * @date Feb 4, 2010
 */
public class BoundedRand extends Random {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Construct a random number generator with an unspecified seed.
	 * 
	 */
	public BoundedRand() {
		super();
	}

	/**
	 * Construct a random number generator with a specified seed.
	 * 
	 * @param seed
	 */
	public BoundedRand(int seed) {
		super(seed);
	}

	/**
	 * Get a bounded random number with the default bounds [0, 1)
	 * 
	 * @return
	 */
	public double getBoundedRand() {
		return nextDouble();
	}

	/**
	 * Get a bounded random number with specified bounds.
	 * 
	 * @param bound1
	 * @param bound2
	 * @return
	 */
	public double getBoundedRand(double bound1, double bound2) {
		double var = nextDouble();
		var = var * (bound1 - bound2) + bound2;
		return var;
	}
}

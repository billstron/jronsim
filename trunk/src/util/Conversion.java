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

package util;

import java.util.Calendar;
import java.util.GregorianCalendar;

/** Conversion class converts between different types.  
 * 
 * @author William Burke <billstron@gmail.com>
 *
 */
public class Conversion {
	
	public static double CalendarToHourOfDay(Calendar cal){
		// get the hour
		double hour = (double)cal.get(Calendar.HOUR_OF_DAY);
		double min = (double)cal.get(Calendar.MINUTE);
		double sec = (double)cal.get(Calendar.SECOND);
		double ms = (double)cal.get(Calendar.MILLISECOND);
		
		double hod = hour + min/60 + (sec + ms/1000)/3600;
		
		return hod;
	}

	public static void main(String[] args){
		
		Calendar now = new GregorianCalendar();
		
		System.out.println("Hod = " + CalendarToHourOfDay(now));
		
	}
}

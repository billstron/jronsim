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
package house.thermostat;

/**
 * @author William Burke <billstron@gmail.com>
 * 
 */
public class ThermostatParams {
	public double DRuserProfile;
	public double DRcostTolerance;
	public SetpointTable setpoints;

	/**
	 * Construct the ThermostatParams Class.
	 * 
	 */
	public ThermostatParams() {
		
		System.err.println("Unsupported Class");
		System.exit(5000);
		

		/**
	    // Create a new setpoint table based on the averages
	    int dayNum;
	    double randGain1 = 0, randGain2 = 0;
	    struct ArrayListSetpoint *day = ArrayListSetpointConstruct(5);
	    
	    // decide if the thermostat is left in hold mode.  
	    if( boundedRand(0, 1) < hold_prob )  // left in hold
	    {
	    	// single setpoint
	    	struct SetpointData Morning = {avgComfortTemp, 6.0, SPMORNING}; 
	    	ArrayListSetpointAdd(day, Morning);
	    }
	    else  // uses tables
	    {
	    	// look to see if everyone is working and on the day shift
		    if(  totalRes == numWorking && numWorking == numDayShift ) 
		    {
		    	struct SetpointData Morning = 
		    	    {avgComfortTemp, avgWakeTime, SPMORNING}; 
		    	ArrayListSetpointAdd(day, Morning);
				struct SetpointData Afternoon = 
					{avgAwayTemp, avgLeaveTime, SPAFTERNOON};         	
		    	ArrayListSetpointAdd(day, Afternoon);
		    	struct SetpointData Evening = 
		    	    {avgComfortTemp, avgArriveTime, SPEVENING};
		    	ArrayListSetpointAdd(day, Evening);
		    	struct SetpointData Night = 
		    	    {avgSleepTemp, avgSleepTime, SPNIGHT};
		    	ArrayListSetpointAdd(day, Night);
		    }
		    // somebody is home all day but everyone else works the day shift
		  	else if( numWorking == numDayShift )  
		  	{
		  		// still use the average values
		    	struct SetpointData SSMorning = 
		    	    {avgComfortTemp, avgWakeTime, SPWEEKEND}; 
		        ArrayListSetpointAdd(day, SSMorning);
		        struct SetpointData SSNight = 
		            {avgSleepTemp, avgSleepTime, SPNIGHT}; 
		        ArrayListSetpointAdd(day, SSNight);
		    }
		    // somebody is home all day
		    else if( resHomeAllDay != -1 )
		    {
		    	// use the individual's values
		    	struct SetpointData SSMorning = 
		    	    { resPrefs[resHomeAllDay]->comfortTemp[COMFORT], 
		    	     resPrefs[resHomeAllDay]->wakeTime[0], SPWEEKEND}; 
		        ArrayListSetpointAdd(day, SSMorning);
		        struct SetpointData SSNight = 
		            { resPrefs[resHomeAllDay]->comfortTemp[SLEEPING], 
		    	     resPrefs[resHomeAllDay]->sleepTime[0], SPWEEKEND}; 
		        ArrayListSetpointAdd(day, SSNight);
		    }
		    // everyone works and someone works shift work.  
		    else
		    {
		    	// use the first person's preferences
		    	struct SetpointData SSMorning = 
		    	    { resPrefs[0]->comfortTemp[COMFORT], 
		    	     resPrefs[0]->wakeTime[0], SPWEEKEND}; 
		        ArrayListSetpointAdd(day, SSMorning);
		        struct SetpointData SSNight = 
		            { resPrefs[0]->comfortTemp[SLEEPING], 
		    	     resPrefs[0]->sleepTime[0], SPWEEKEND}; 
		        ArrayListSetpointAdd(day, SSNight);
		    }
		}
	    for(dayNum = 0; dayNum < 7; dayNum++)
	    {
			 Setpoints[dayNum] = day;
	    }
	**/
	}
}

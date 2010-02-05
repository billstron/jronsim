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
package house.occupant;

/**
 * @author William Burke <billstron@gmail.com>
 * @date Feb 5, 2010
 */
public class OccupantParamsRand extends OccupantParams{

	public OccupantParamsRand(){
		
		System.err.println("Unsupported Class");
		System.exit(5000);
		/**
		// Generate some residents
	    int k;
	    int resNum = 0;
	    int numWorking = 0;
	    int numDayShift = 0;
	    int resHomeAllDay = -1;
	    int resWorks = 0;
	    double avgWakeTime = 0;
	    double avgSleepTime = 0;
	    double avgLeaveTime = 0;
	    double avgArriveTime = 0;
	    double avgComfortTemp = 0;
		double avgSleepTemp = 0;
		double avgAwayTemp = 0;
	    // first decide how many will be in the house
	     totalRes = (int)ceil( boundedRand( 0, MAX_RES ) );
	    //printf("<PctHouseParamRandFill> number of residents: %d\n",  totalRes);
	    // create the resident prefs vector.  
	     resPrefs = (struct ResidentPrefs **)GetMemory(
	        	sizeof(struct ResidentPrefs *) *  totalRes, 
	        	"<PctHouseParamRandFill> Can't allocate memory.\n");
	    // fill the vector.  /**
		// Generate some residents
	    int k;
	    int resNum = 0;
	    int numWorking = 0;
	    int numDayShift = 0;
	    int resHomeAllDay = -1;
	    int resWorks = 0;
	    double avgWakeTime = 0;
	    double avgSleepTime = 0;
	    double avgLeaveTime = 0;
	    double avgArriveTime = 0;
	    double avgComfortTemp = 0;
		double avgSleepTemp = 0;
		double avgAwayTemp = 0;
	    // first decide how many will be in the house
	     totalRes = (int)ceil( boundedRand( 0, MAX_RES ) );
	    //printf("<PctHouseParamRandFill> number of residents: %d\n",  totalRes);
	    // create the resident prefs vector.  
	     resPrefs = (struct ResidentPrefs **)GetMemory(
	        	sizeof(struct ResidentPrefs *) *  totalRes, 
	        	"<PctHouseParamRandFill> Can't allocate memory.\n");
	    // fill the vector.  
	    for( resNum; resNum <  totalRes; resNum++ )
	    {
	    	// create the resident prefs variable.  
	    	struct ResidentPrefs *prefs = (struct ResidentPrefs *)GetMemory(
	        	sizeof(struct ResidentPrefs), 
	        	"<PctHouseParamRandFill> Can't allocate memory.\n");

	        // everyone has the same patience time
	        prefs->tPatience = 10*3600;
	        // motivation probabilities are all the same
	        for( k = 0; k < 7; k++ ) prefs->motivationProb[k] = motivationProb[k];
	        // different comfort temps.  
	    	for( k = 0; k < 5; k++ ) 
	    		prefs->comfortTemp[k] = 
	    			boundedRand( comfortTemp_min[k], comfortTemp_max[k] );
	    	prefs->comfortTemp[AWAY] = 
	    		boundedRand( prefs->comfortTemp[COMFORT], comfortTemp_max[AWAY] );
	    	prefs->comfortTemp[SLEEPING] = 
	    		boundedRand( prefs->comfortTemp[COMFORT], comfortTemp_max[SLEEPING] );	
	    	
	    	// set the DR motivation and comfort temps
	    	// different probs
	        for( k = 0; k < 7; k++ ) 
	        	prefs->DRmotivationProb[k] = DRmotivationProb[k];
	        // same comfort temps for now.  
	    	for( k = 0; k < 7; k++ ) 
	    		prefs->DRcomfortTemp[k] = prefs->comfortTemp[k];
	    	
	    	// decide if the person works or not
	    	if( boundedRand( 0, 1 ) < work_prob ) // they work
	    	{
	    		numWorking += 1;  // increment the number working variable
	    		// set the work hours
	    		if( boundedRand( 0, 1 ) < dayshift_prob )  // work day shift
	    		{
	    			numDayShift += 1;
	    			// decide when they work
	    			prefs->leaveTime[0] = boundedRand( 6.5, 8 );
	    			prefs->leaveTime[1] = 0.5;
	    			prefs->arriveTime[0] = prefs->leaveTime[0] + boundedRand( 7, 11 );
	    			prefs->arriveTime[1] = 0.5;
	    			// decide about their wake and sleep time.  
	    			prefs->wakeTime[0] = prefs->leaveTime[0] - boundedRand( .25, 2 ); 
	    			prefs->wakeTime[1] = 0.5;
	    			prefs->sleepTime[0] = boundedRand( 21, 23 );
	    			prefs->sleepTime[1] = 0.5;
	    		}
	    		else // work other shift
	    		{
	    			// decide when they work
	    			/*
	    			prefs->leaveTime[0] = 23.9;
	    			prefs->leaveTime[1] = 0.5;
	    			prefs->arriveTime[0] = 8;
	    			prefs->arriveTime[1] = 0.5;
	    			// decide about their wake and sleep time.  
	    			prefs->wakeTime[0] = 20;
	    			prefs->wakeTime[1] = 0.5;
	    			prefs->sleepTime[0] = 10;
	    			prefs->sleepTime[1] = 0.5;
	    			*/
	    		/**	
	    			prefs->leaveTime[0] = boundedRand(8.5, 30 );
	    			if( prefs->leaveTime[0] >= 24 ) prefs->leaveTime[0] -= 24;
	    			prefs->leaveTime[1] = 0.5;
	    			prefs->arriveTime[0] = prefs->leaveTime[0] + boundedRand( 7, 11 );
	    			if( prefs->arriveTime[0] >= 24 ) prefs->arriveTime[0] -= 24;
	    			prefs->arriveTime[1] = 0.5;
	    			// decide about their wake and sleep time.  
	    			prefs->wakeTime[0] = prefs->leaveTime[0] - boundedRand( .25, 2 );  
	    			if( prefs->wakeTime[0] < 0 ) 
	    				prefs->wakeTime[0] = 24 - prefs->wakeTime[0];
	    			prefs->wakeTime[1] = 0.5;
	    			prefs->sleepTime[0] = prefs->wakeTime[0] + boundedRand(15, 17);
	    			if( prefs->sleepTime[0] >= 24 ) prefs->sleepTime[0] -= 24;
	    			prefs->sleepTime[1] = 0.5;
	    			
	    		}
	    	}
	    	else  // they don't work
	    	{
	    		// set the value
	    		resHomeAllDay = resNum;  
				// decide about their wake and sleep time.  
				prefs->wakeTime[0] = boundedRand( 4.5, 9 );
				prefs->wakeTime[1] = 0.5;
				prefs->sleepTime[0] = boundedRand( 21, 23 );
				prefs->sleepTime[1] = 0.5;
	    		// they are home all day
	    		prefs->leaveTime[0] = INFINITY;
	    		prefs->leaveTime[1] = INFINITY;
	    		prefs->arriveTime[0] = -1;
	    		prefs->arriveTime[1] = -1;
	    		// Burn a couple of random numbers to ensure that we get the same 
	    		// housing stock regardless of the people preferences.  
	    		// This does not help when the number of people are different though
	    		boundedRand( 0, 1 );
	    		boundedRand( 0, 1 );
	    		boundedRand( 0, 1 );
	    	}
			// add up the numbers used for the averages.  
			avgWakeTime += prefs->wakeTime[0];
	    	avgSleepTime += prefs->sleepTime[0];
	    	avgLeaveTime += prefs->leaveTime[0];
	    	avgArriveTime += prefs->arriveTime[0];
	    	avgComfortTemp += prefs->comfortTemp[COMFORT];
			avgSleepTemp += prefs->comfortTemp[SLEEPING];
			avgAwayTemp += prefs->comfortTemp[AWAY];
	    	// put the preferences into the vector
	    	 resPrefs[resNum] = prefs;
	    }
		// compute the averages
		avgWakeTime = avgWakeTime / (double)  totalRes; 
		avgSleepTime = avgSleepTime / (double)  totalRes; 
		avgLeaveTime = avgLeaveTime / (double)  totalRes; 
		avgArriveTime = avgArriveTime / (double)  totalRes; 
		avgComfortTemp = avgComfortTemp / (double)  totalRes; 
		avgSleepTemp = avgSleepTemp / (double)  totalRes; 
		avgAwayTemp = avgAwayTemp / (double)  totalRes; 
		**/
	}

	
	private void getRangeFromFile(String path) {
		
		System.err.println("Unsupported Method");
		System.exit(5000);
		
		/**

		// Try to read the file and put the stuff in it
		try {
			// Open the file that is the first
			// command line parameter
			FileInputStream fstream = new FileInputStream(path);
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			int i;
			String name, value;
			StringTokenizer st;

			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				if (strLine != "\n") {
					// get the next command identifier.
					st = new StringTokenizer(strLine, ", ");
					name = st.nextToken();
					// process the rest of the line based on the command
					// identifier
					// step through the rest of the line based on the number of
					// entries that should be on the line.
					// If the line is underfilled, then we return an error
					// message.
					if (name.equalsIgnoreCase("motiv_prob")) {
						for (i = 0; i < 7; i++) {
							value = st.nextToken();
							if (value == null)
								System.err
										.printf(
												"<OccupantParamRandFill> bad input: %s\n",
												name);
							else
								motivationProb[i] = Double.parseDouble(value);
						}
					} else if (name.equalsIgnoreCase("drmotiv_prob")) {
						for (i = 0; i < 7; i++) {
							value = st.nextToken();
							if (value == null)
								System.err
										.printf(
												"<OccupantParamRandFill> bad input: %s\n",
												name);
							else
								DRmotivationProb[i] = Double.parseDouble(value);
						}
					} else if (name.equalsIgnoreCase("work_prob")) {
						value = st.nextToken();
						if (value == null)
							System.err.printf(
									"<OccupantParamRandFill> bad input: %s\n",
									name);
						else
							work_prob = Double.parseDouble(value);
						// System.err.printf("work prob = %3.3f\n", work_prob);
					} else if (name.equalsIgnoreCase("hold_prob")) {
						value = st.nextToken();
						if (value == null)
							System.err.printf(
									"<OccupantParamRandFill> bad input: %s\n",
									name);
						else
							hold_prob = Double.parseDouble(value);
					} else if (name.equalsIgnoreCase("dayshift_prob")) {
						value = st.nextToken();
						if (value == null)
							System.err.printf(
									"<OccupantParamRandFill> bad input: %s\n",
									name);
						else
							dayshift_prob = Double.parseDouble(value);
					} else if (name[0] == '#')
						;
					else
						System.err.printf(
										"<OccupantParamRandFill> UNKNOWN: '%s'\n", line);
				}
			}
		} catch (Exception e) {// Catch exception if any
			System.err.println("<OccupantParamRandFill> Error: " + e.getMessage());
		}
		**/
	}
	
}

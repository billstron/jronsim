package util;

import java.util.Calendar;
import java.util.GregorianCalendar;

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

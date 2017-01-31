package org.pentaho.pdi.steps.googleadexchangeInput.api;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DateRangeType {

	public static final String CUSTOM_DATE = "CUSTOM_DATE";
	public static final String TODAY= "TODAY";
	public static final String YESTERDAY = "YESTERDAY";
	public static final String LAST_7Days = "LAST_7Days";
	public static final String LAST_MONTH = "LAST_MONTH";
	public static final String LAST_YEAR = "LAST_YEAR";
	
	static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
	
	public static String[] getStartEndDate(String rangeType)
	{
		String type[] = new String [2];
		
		
	    
		if(rangeType.equalsIgnoreCase(TODAY))
		{
		    type[0] = "today";
			type[1] = "today";
		}
		else if(rangeType.equalsIgnoreCase(YESTERDAY))
		{
			 type[0] = "today-1d";
			 type[1] = "today-1d";
		} 
		else if(rangeType.equalsIgnoreCase(LAST_7Days))
		{
			type[0] = "today-6d";
			type[1] = "today";
		} 
		else if(rangeType.equalsIgnoreCase(LAST_MONTH))
		{
			type[0] = "startOfMonth-1m";
			type[1] = "startOfMonth-1d";
		} 
		else if(rangeType.equalsIgnoreCase(LAST_YEAR))
		{
			type[0] = "startOfMonth-12m";
			type[1] = "startOfMonth-1d";
		}
		return type;
	}
}

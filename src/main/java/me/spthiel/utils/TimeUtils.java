package me.spthiel.utils;

import java.util.Date;

public class TimeUtils {
	
	public static String toDate(long timestamp) {
		Date date = new Date(timestamp);
		return date.toString();
	}
	
	public static String toDateTime(long seconds) {
		if(seconds == 0) {
			return "0s";
		} else if(seconds < 0) {
			return "Invalid argument";
		}
		return
			display(seconds, 60*60*24, -1, "%dd ") +
				display(seconds, 60*60, 24, "%02d:") +
				display(seconds, 60, 60, "%02d:") +
				display(seconds, 1, 60, "%02d");
	}
	
	private static String display(long seconds, long unit, long max, String format) {
		if(seconds < unit) {
			return "";
		}
		return String.format(format, max < 0 ? seconds/unit : (seconds/unit)%max);
	}
}

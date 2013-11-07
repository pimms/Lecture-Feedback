package chipmunk.unlimited.feedback;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.util.Log;

/**
 * @class LectureItem
 * Representation of a lecture retrieved from TimeEdit.
 */
public class LectureItem {
	private Date mStartTime;
	private Date mEndTime;
	private String mRoom;
	private String mLecturer;
	private String mCourseName;
	
	
	public LectureItem(String date, String time, String name, String room, String lecturer) {
		setTimes(date, time);
		
		mCourseName = name;
		mRoom = room;
		mLecturer = lecturer;
	}
	
	
	/**
	 * @return
	 * String on the form: "[HH:mm] - [HH:mm]"
	 */
	public String getTimeString() {
		return getTimeString(mStartTime) + " - " + getTimeString(mEndTime);
	}
	
	public Date getStartTime() {
		return mStartTime;
	}
	
	public Date getEndTime() {
		return mEndTime;
	}
	
	public String getRoom() {
		return mRoom;
	}
	
	public String getLecturer() {
		return mLecturer;
	}
	
	public String getCourseName() {
		return mCourseName;
	}
	
	
	/**
	 * Set mStartTime and mEndTime based on "date" and "time".
	 * 
	 * @param date
	 * The date, on the form: "Day yyyy-MM-dd"
	 * 
	 * @param time
	 * The time, on the form: "HH:mm - HH:mm"
	 */
	private void setTimes(String date, String time) {
		date = date.substring(4);
		String[] dateArr = date.split("-");
		
		String timeStart = time.substring(0, 5);
		String timeEnd = time.substring(8, 13);
		
		GregorianCalendar cal = new GregorianCalendar();
		
		// Set the date
		cal.set(parseInt(dateArr[0]), 
				parseInt(dateArr[1]), 
				parseInt(dateArr[2]));
		
		// Set the start time
		String[] hhmm = timeStart.split(":");
		cal.set(Calendar.HOUR_OF_DAY, parseInt(hhmm[0]));
		cal.set(Calendar.MINUTE, parseInt(hhmm[1]));
		mStartTime = cal.getTime();
		
		// Set the end time
		hhmm = timeEnd.split(":");
		cal.set(Calendar.HOUR_OF_DAY, parseInt(hhmm[0]));
		cal.set(Calendar.MINUTE, parseInt(hhmm[1]));
		mEndTime = cal.getTime();
	}
	
	/**
	 * Remove leading zeroes and spaces from the string 
	 * before parsing it as an int.
	 * 
	 * @param string
	 * String containing a numerical value.
	 * 
	 * @return
	 * Integer.parseInt(string)
	 */
	private int parseInt(String string) {
		string = string.trim();
		
		while (string.length() != 0 && string.charAt(0) == '0') {
			string = string.substring(1);
		}
		
		if (string.length() > 0) {
			return Integer.parseInt(string);
		} 
		
		return 0;
	}
	
	
	private String getTimeString(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		int hours = cal.get(Calendar.HOUR_OF_DAY);
		int minutes = cal.get(Calendar.MINUTE);
		
		StringBuilder builder = new StringBuilder();
		
		builder.append( ((hours < 10) ? "0" : "") + hours );
		builder.append(":");
		builder.append( ((minutes < 10) ? "0" : "") + minutes );
		
		return builder.toString();
	}
	
	@Override
	public String toString() {
		return "[" + mStartTime + "-" + mEndTime + "] " + mCourseName + ", " + mRoom + ", " + mLecturer;
	}
}




















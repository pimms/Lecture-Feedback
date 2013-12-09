package chipmunk.unlimited.feedback;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import chipmunk.unlimited.feedback.webapi.SHA1;

/**
 * @class LectureItem
 * Representation of a lecture retrieved from TimeEdit.
 */
public class LectureItem implements Comparable<LectureItem> {
    /**
     * How many days is a Lecture reviewable?
     */
    public static final int REVIEWABLE_PERIOD_DAYS = 42;

	protected Date mStartTime;
	protected Date mEndTime;
	protected String mDate;
	protected String mRoom;
	protected String mLecturer;
	protected String mCourseName;
	protected String mCourseHigCode;
	
	/**
	 * @param date
	 * The date on the form "yyyy-MM-dd".
	 * 
	 * @param time
	 * The time of the lecture, on the form "HH:mm - HH:mm".
	 * 
	 * @param name
	 * The name of the course.
	 * 
	 * @param higCode
	 * The HiG course code
	 * 
	 * @param room
	 * The room in which the lecture is held.
	 * 
	 * @param lecturer
	 * The lecturer(s) giving the lecture.
	 */
	public LectureItem(	String date, String time, String name,
						String higCode, String room, String lecturer) {
		setTimes(date, time);
		
		mDate = date;
		mCourseName = name;
		mCourseHigCode = higCode;
		mRoom = room;
		mLecturer = lecturer;
	}
	
	
	/**
	 * @return
	 * The date on the form "yyyy-MM-dd"
	 */
	public String getDateString() {
		return mDate;
	}

    /**
     * @return
     * The date on the form "Monday, 01 Jan" if the date is from
     * this year, "01 Jan, 2012" if it was from another year.
     */
    public String getPrettyDateString() {
        Calendar now = Calendar.getInstance();

        Calendar then = Calendar.getInstance();
        then.setTime(mStartTime);
        
        String format = "";

        // TODO: Localize
        // Find a way to retrieve the string format_date_pretty_old
        // and format_date_pretty. The main issue is that in this
        // scope, no Context is available.
        if (now.get(Calendar.YEAR) != then.get(Calendar.YEAR)) {
            format = "MMM dd, yyyy";
        } else {
        	format = "EEEE, MMM dd";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(mStartTime);
    }

	/**
	 * @return
	 * Date-object with 0 in all non-date related
	 * fields.
	 */
	public Date getDate() {
		Calendar original = Calendar.getInstance();
		original.setTime(mStartTime);
		
		Calendar copy = Calendar.getInstance();
		copy.clear();
		copy.set(Calendar.YEAR, original.get(Calendar.YEAR));
		copy.set(Calendar.MONTH, original.get(Calendar.MONTH));
		copy.set(Calendar.DAY_OF_MONTH, original.get(Calendar.DAY_OF_MONTH));
		
		return copy.getTime();
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
	
	public long getStartTimeUNIX() {
		return mStartTime.getTime() / 1000;
	}
	
	public Date getEndTime() {
		return mEndTime;
	}
	
	public long getEndTimeUNIX() {
		return mEndTime.getTime() / 1000;
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
	
	public String getCourseHigCode() {
		return mCourseHigCode;
	}


    /**
     * Is the lecture recent enough to be reviewable?
     *
     * @return
     * true if the lecture can be reviewed
     */
    public boolean canReviewLecture() {
        Calendar limit = Calendar.getInstance();
        limit.add(Calendar.DAY_OF_MONTH, -REVIEWABLE_PERIOD_DAYS);

        Calendar now = Calendar.getInstance();

        return mEndTime.after(limit.getTime()) &&
               mStartTime.before(now.getTime());
    }


	/**
	 * Set mStartTime and mEndTime based on "date" and "time".
	 * 
	 * @param date
	 * The date, on the form: "yyyy-MM-dd"
	 * 
	 * @param time
	 * The time, on the form: "HH:mm - HH:mm"
	 */
	private void setTimes(String date, String time) {
		String[] dateArr = date.split("-");
		
		String timeStart = time.substring(0, 5);
		String timeEnd = time.substring(8, 13);
		
		Calendar cal = Calendar.getInstance();
		
		// Set the date
		cal.set(parseInt(dateArr[0]), 		// year is 1 based
				parseInt(dateArr[1]) - 1,	// month 0 based 
				parseInt(dateArr[2]));		// day is 1 based
		
		// Set the start time
		String[] hhmm = timeStart.split(":");
		cal.set(Calendar.HOUR_OF_DAY, parseInt(hhmm[0]));
		cal.set(Calendar.MINUTE, parseInt(hhmm[1]));
		cal.set(Calendar.SECOND, 0);
		mStartTime = cal.getTime();
		
		// Set the end time
		hhmm = timeEnd.split(":");
		cal.set(Calendar.HOUR_OF_DAY, parseInt(hhmm[0]));
		cal.set(Calendar.MINUTE, parseInt(hhmm[1]));
		cal.set(Calendar.SECOND, 0);
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
	protected int parseInt(String string) {
		string = string.trim();
		
		while (string.length() != 0 && string.charAt(0) == '0') {
			string = string.substring(1);
		}
		
		if (string.length() > 0) {
			return Integer.parseInt(string);
		} 
		
		return 0;
	}
	
	
	protected String getTimeString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
		return format.format(date);
	}
	/**
	 * Calculate the hash for this lecture.
     * The hash used on both the client application
     * and the backend server is the combined hash of:
     * 1. The UNIX timestamp string of the start time of the lecture
     * 2. The UNIX timestamp string of the end time of the lecture
     * 3. The course name as it appears on TimeEdit
     * 4. The HiG code of the course
     * 5. The room as it appears on TimeEdit
     * 6. The lecturer as it appears on TimeEdit
	 * 
	 * @return
	 * SHA-1 of the LectureItem's uniquely identifying
	 * values.
	 */
	public final String getLectureHash() {
		StringBuilder sb = new StringBuilder();
		sb.append(getStartTimeUNIX());
		sb.append(getEndTimeUNIX());
		sb.append(mCourseName);
		sb.append(mCourseHigCode);
		sb.append(mRoom);
		sb.append(mLecturer);
		
		return SHA1.hash(sb.toString());
	}
	@Override
	public String toString() {
		return "[" + mStartTime + "-" + mEndTime + "] " + mCourseName + ", " + mRoom + ", " + mLecturer;
	}

    @Override
    public int compareTo(LectureItem o) {
        return o.mStartTime.compareTo(mStartTime);
    }
}




















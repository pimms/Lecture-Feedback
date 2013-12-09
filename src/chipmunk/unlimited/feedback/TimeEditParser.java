package chipmunk.unlimited.feedback;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import au.com.bytecode.opencsv.CSVReader;

/**
 * The TimeEdit HTML is full of errors, causing the w3c.dom-parser to fail.
 * As a result, scavenging the HTML for data is the only solution to
 * integrate TimeEdit communication.
 *
 * This class is forked from TobbenTM's implementation
 * of a TimeEdit parser. Most of the code is poorly documented and hard to
 * follow, and should in case of severe modification needs be replaced entirely.
 *
 * The source of the fork:
 * https://github.com/TobbenTM/HiG-TimeEdit-Reader/blob/645e939dee31c249c135ea5b216890fcd92f8537/java/com/tobbentm/higreader/TimeParser.java
 *
 */
public class TimeEditParser extends AsyncHttpResponseHandler {	
	/**
	 * Callback interface for the TimeEditParser class.
	 */
	public interface OnParseCompleteListener {
		public void onTimeTableParsingComplete(List<LectureItem> items);
		public void onTimeTableParsingFailed(String errorMessage);
	}

	/* The constants defines how the parser interprets
	 * the received HTML.  
	 */
	public static final int CONTENT_TIMETABLE = 1;

	private int mContentType = 1;
	private OnParseCompleteListener mCallback;

    /**
     * @param contentType
     * Definition of what the returned content type will be.
     * Must be CONTENT_TIMETABLE.
     */
	public TimeEditParser(int contentType) {
		mContentType = contentType;
	}
	
	public void setOnParseCompleteListener(OnParseCompleteListener callback) {
		mCallback = callback;
	}
	
	
	/**
	 * The callback for successful HTTP requests.
     *
	 * @param response
     * The contents of the webpage. Can be null.
	 */
	@Override 
	public void onSuccess(String response) {
        if (mContentType == CONTENT_TIMETABLE) {
            Log.d("CSV?", response);

            List<LectureItem> list = parseTimeEditCSV(response);

            if (mCallback != null) {
                if (list != null) {
                    mCallback.onTimeTableParsingComplete(list);
                } else {
                    mCallback.onTimeTableParsingFailed("TimeEdit not implemented");
                }
            }
        }
	}
	
	@Override
	public void onFailure(Throwable throwable, String response) {
		if (mCallback != null) {
			mCallback.onTimeTableParsingFailed(response);
		}
	}


    public static String[][] search(String html, String term){
        //Log.d("PARSING", "Starting parser");
        List<String> id = new ArrayList<String>();
        List<String> name = new ArrayList<String>();
        List<String> ids = new ArrayList<String>();
        List<String> names = new ArrayList<String>();

        //The reason for splitting at data-id="-1" is that there is no valuable data
        // after it, and it would just cause problems.
        //Still not bug-free though, and should be replaced with a better regex-based
        // solution.
        String[] split = html.split("data-id=\"-1\"");

        ids.addAll(Arrays.asList(split[0].split("data-id=\"")));
        ids.remove(0);
        names.addAll(Arrays.asList(split[0].split("data-name=\"")));
        names.remove(0);


        for (String s : ids){
            id.add(s.split("\"")[0]);
        }
        for (String s : names){
            name.add(s.split("\"")[0]);
        }

        String[][] result = new String[id.size()][2];
        Log.d("HIG.SEARCH.ARRAY", name.toString() + id.toString());

        //Join the two arrays into one 2D array
        for(int i=0; i < id.size(); i++){
            result[i][0] = id.get(i);
            result[i][1] = name.get(i);
        }
        return result;
    }

    /**
     * Parse a TimeEdit CSV file and compile a set of LectureItem objects.
     *
     * @param rawCsv
     * The FULL, NON-ALTERED TimeEdit CSV file.
     *
     * @return
     * NULL on serious errors, list of compiled items otherwise.
     */
    private List<LectureItem> parseTimeEditCSV(String rawCsv) {
        // Ignore the first 4 lines
        rawCsv = rawCsv.split("\n", 5)[4];

        CSVReader reader = new CSVReader(new StringReader(rawCsv), ',', '"');
        List<LectureItem> list = new ArrayList<LectureItem>();

        try {
            String line[] = reader.readNext();

            while (line != null) {
                LectureItem item = createLectureItemFromCSVLine(line);

                if (item != null) {
                    list.add(0, item);
                    Log.d("ITEM", item.toString());
                }

                line = reader.readNext();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        return list;
    }

    /**
     *
     * @param csvLine
     * Array of items from a TimeEdit CSV file. The order of the elements
     * must be:
     *  INDEX   DESCRIPTION             EXAMPLE
     *  0       The start date          2013-11-15
     *  1       The start time          13:15
     *  2       The end date            2013-11-15
     *  3       The end time            14:00
     *  4       The course data         IMT3672, Mobil utviklingsprosjekt
     *  5       The room                D101
     *  6       Staff member            Nowostawski
     *
     *  The array may contain extra values. If any of the values are null or
     *  contains an empty string, NULL is returned.
     *
     * @return
     * LectureItem, created from the contents of csvLine.
     * If anything went wrong, NULL is returned.
     */
    LectureItem createLectureItemFromCSVLine(String csvLine[]) {
        if (csvLine.length < 7) {
            return null;
        }

        for (int i=0; i<7; i++) {
            if (csvLine[i] == null || csvLine[i].length() <= 1) {
                return null;
            }
        }

        // Get the raw values
        String startDate    = csvLine[0].trim();
        String startTime    = csvLine[1].trim();
        String endDate      = csvLine[2].trim();
        String endTime      = csvLine[3].trim();
        String courseData   = csvLine[4].trim();
        String room         = csvLine[5].trim();
        String lecturer     = csvLine[6].trim();

        // Ensure the lecture starts and ends on the same day
        if (!startDate.equals(endDate)) {
            return null;
        }

        // Separate the course code and name
        String courseSplit[] = courseData.split(",", 2);
        if (courseSplit.length != 2) {
            return null;
        }

        String courseCode = courseSplit[0];
        String courseName = courseSplit[1];

        // Join start and end time
        String time = startTime + " - " + endTime;

        LectureItem lectureItem = new LectureItem(
                startDate, time, courseName, courseCode, room, lecturer);
        return lectureItem;
    }
}































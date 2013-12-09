package chipmunk.unlimited.feedback;

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

        Log.d("CSV?", response);

        if (mCallback != null) {
            mCallback.onTimeTableParsingFailed("TimeEdit not implemented");
        }
	}
	
	@Override
	public void onFailure(Throwable throwable, String response) {
		if (mCallback != null) {
			mCallback.onTimeTableParsingFailed(response);
		}
	}
}































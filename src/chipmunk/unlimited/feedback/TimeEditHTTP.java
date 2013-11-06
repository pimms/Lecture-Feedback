package chipmunk.unlimited.feedback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * @class TimeEditHTTP 
 * Static class providing an API for TimeEdit
 * data retrieval.
 */
public class TimeEditHTTP {
	/**
	 * Get the time-table for the passed subscriptions. The timetable covers
	 * the weeks containing YESTERDAY and TODAY. If this method is called
	 * on a Monday, last week and this entire week is returned from TimeEdit.
	 * 
	 * @param subscriptions
	 * List of subscriptions (rooms, courses, classes)
	 * 
	 * @param callback
	 * The callback for the HTTP-connection.
	 */
	public static void getTimeTable(List<SubscriptionItem> subscriptions, AsyncHttpResponseHandler callback) {
		if (subscriptions.size() == 0 || callback == null) {
			return;
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyMMdd", Locale.getDefault());
		Calendar calendar = Calendar.getInstance();
		
		/* Calculate the bounding dates */
		String startDate = dateFormat.format(calendar.getTime());
		calendar.add(Calendar.DAY_OF_YEAR, 14);
		String endDate = dateFormat.format(calendar.getTime());
		
		/* Get the TimeEdit URL */
		String webUrl = getTimeEditTimeTableURL(subscriptions, startDate, endDate);
		
		/* Retrieve the web content */
		Log.d("HTTP", "TimeEdit URL: " + webUrl);
		new AsyncHttpClient().get(webUrl, callback);
	}
	
	
	/**
	 * Get the web URL that will resolve to the time table for the
	 * passed subscriptions. The URL has the following format:
	 * 	 https://web.timeedit.se/hig_no/db1/open/r.html?sid=3&h=t&p=[yyyyMMdd (start)].x2%C[yyyyMMdd (end)]
	 *   .x&objects=[ID 1],-1,[ID 2],-1,[ID N],-1,1.182&ox=0&types=0&fe=0&l=en&g=f
	 * 
	 * @param subscriptions
	 * The subscriptions to be included in the URL.
	 * 
	 * @return
	 * The URL containing the time table.
	 */
	private static String getTimeEditTimeTableURL(List<SubscriptionItem>subscriptions, String startDate, String endDate) {
		/* Generate the TimeEdit URL */
		String webUrl = "https://web.timeedit.se/hig_no/db1/open/r.html?sid=3&h=t&p=";
		
		/* Add the start and end dates */
		webUrl += startDate + ".x%2C" + endDate;
		
		/* Add the subscription filter */
		webUrl += ".x&objects=" + getSubscriptionFilter(subscriptions);
		webUrl += "&ox=0&types=0&fe=0&l=en&g=f";
		
		return webUrl;
	}
	
	/**
	 * @param subscriptions
	 * List of SusbcriptionItem objects. SubscriptionItems are uniquely identified by
	 * TimeEdit via an ID. To get all items, a separator (id "-1") must separate the
	 * items.
	 * 			
	 * @return 	
	 * The filter content of a TimeEdit query URL.
	 */
	private static String getSubscriptionFilter(List<SubscriptionItem> subscriptions) {
		String filter = "";
		
		for (int i=0; i<subscriptions.size(); i++) {
			if (i > 0) {
				filter += ",-1,";
			}
			
			filter += subscriptions.get(i).getItemId();
		}
		
		filter += ",-1,1.182";
		return filter;
	}
}







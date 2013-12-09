package chipmunk.unlimited.feedback;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.util.Log;

import chipmunk.unlimited.feedback.subscription.SubscriptionItem;

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
     * The callback will receive the entire TimeEdit HTML on success.
	 * 
	 * @param subscriptions
	 * List of subscriptions (rooms, courses, classes)
	 * 
	 * @param callback
	 * The callback that will receive the TimeEdit HTML.
	 */
	public static void getTimeTable(List<SubscriptionItem> subscriptions, AsyncHttpResponseHandler callback) {
		if (subscriptions.size() == 0 || callback == null) {
			if (callback != null) {
				callback.onSuccess(null);
			}
			return;
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyMMdd", Locale.getDefault());
		Calendar calendar = Calendar.getInstance();
		
		// TobbenTM's parser is extremely unreliable when searching with a 
		// narrow scope. Set the search parameters to include ONE WEEK AGO
		// to ONE WEEK AHEAD.
		calendar.add(Calendar.DAY_OF_YEAR, 7);
		
		/* Calculate the bounding dates */
		String endDate = dateFormat.format(calendar.getTime());
		
		// Subtract 14 as we added 7 earlier
		calendar.add(Calendar.DAY_OF_YEAR, -14);
		String startDate = dateFormat.format(calendar.getTime());
		
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
		String webUrl = "https://web.timeedit.se/hig_no/db1/open/r.csv?sid=3&h=t&p=";
		
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
	 * The filter segment of a TimeEdit query URL.
	 */
	private static String getSubscriptionFilter(List<SubscriptionItem> subscriptions) {
		String filter = "";
		
		for (int i=0; i<subscriptions.size(); i++) {
			if (i > 0) {
				// Add an "OR" separator to get ALL desired subscriptions.
				filter += ",-1,";
			}
			
			filter += subscriptions.get(i).getTimeEditCode();
		}
		
		filter += ",-1,1.183";
		return filter;
	}
	
	/**
     * Search TimeEdit for raw text.
     *
     * The callback will receive the entire TimeEdit HTML on success.
     *
     * @param term
     * Raw text to search TimeEdit for.
     *
     * @param handler
     * The callback that will receive TimeEdit HTML.
	 */
	 public static void search(String term, AsyncHttpResponseHandler handler){
        final int iType = 183;
        final String baseURL = "https://web.timeedit.se/hig_no/db1/timeedit/p/open/objects.html?max=15&partajax=t&l=en&sid=3&types=" + iType + "&search_text=";
        new AsyncHttpClient().get(baseURL + term.replaceAll(" ", "%20"), handler);
    }
}







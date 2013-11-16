package chipmunk.unlimited.feedback.webapi;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;
import chipmunk.unlimited.feedback.LectureReviewItem;
import chipmunk.unlimited.feedback.subscription.SubscriptionItem;
import chipmunk.unlimited.feedback.webapi.WebAPI.GetFeedCallback;

import com.loopj.android.http.AsyncHttpClient;

/**
 * Class managing the web API call "getFeed".
 */
class GetFeed extends WebAPICall {
	private static final String TAG = "GetFeed";	
	
	private GetFeedCallback mCallback;
	
	
	public GetFeed(GetFeedCallback callback) {
		assert(callback != null);
		mCallback = callback;
	}
	
	/**
	 * 
	 * @param baseUrl
	 * The base url containing the root level of the webAPI.
	 * 
	 * @param subscriptions
	 * The subscriptions to be filtered in.
	 * 
	 * @param first
	 * The first item to be returned in the result set.
	 * 
	 * @param count
	 * The maximum number of items to be returned.
	 */
	public void apiCall(String baseUrl, 
						List<SubscriptionItem> subscriptions,
						int first, int count)
	{
		if (subscriptions.size() == 0) {
			mCallback.onGetFeedFailure("No subscriptions defined.");
			return;
		}
		
		baseUrl += "/getFeed.php?";
		baseUrl += "filter=" + createFilterString(subscriptions);
		baseUrl += "&first=" + first;
		baseUrl += "&count=" + count;

		Log.d(TAG, baseUrl);
		
		new AsyncHttpClient().get(baseUrl, this);
	}
	
	private String createFilterString(List<SubscriptionItem> subscriptions) {
		String result = "";
		
		if (subscriptions.size() != 0) {
			result = subscriptions.get(0).getHigCode();
			
			for (int i=1; i<subscriptions.size(); i++) {
				result += "," + subscriptions.get(i).getName();
			}
		}
		
		return result;
	}
	
	
	@Override
	public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
		String response;
		
		try {
			response = new String(responseBody, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			mCallback.onGetFeedFailure("Returned content is not UTF-8 encoded");
			return;
		}
		
		List<LectureReviewItem> list = parseResultJson(response);
		if (list != null) {
			mCallback.onGetFeedSuccess(list);
		} else {
			mCallback.onGetFeedFailure("invalid JSON received. wtf yo");
		}
	}
	
	@Override 
	public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
		String bodyString;
		String errorMessage = "HTTP Error with code " + statusCode;
		
		try {
			if (responseBody != null) {
				bodyString = new String(responseBody, "UTF-8");
				errorMessage += " (" + bodyString + ")";
			}
		} catch (UnsupportedEncodingException e) {
			mCallback.onGetFeedFailure("HTTP error with code " + statusCode);
		}
		
		mCallback.onGetFeedFailure(errorMessage);
	}
	
	
	private List<LectureReviewItem> parseResultJson(String response) {
		JSONObject json = getJsonRoot(response);
		if (json == null) {
			return null;
		}
		
		List<LectureReviewItem> list = null;
		
		try {
			if (json.getString("status").equals("ok")) {
				list = new ArrayList<LectureReviewItem>();
			
				int count = json.getInt("item_count");
				JSONArray items = json.getJSONArray("items");
				
				for (int i=0; i<count; i++) {
					JSONObject item = items.getJSONObject(i);
					list.add(parseLectureReviewItem(item));
				}
			} else {
				Log.e(TAG, "Status = bad returned for getFeed()");
			}
		} catch (Exception ex) {
			Log.e(TAG, "Failed to parse items: " + ex.getMessage());
			return null;
		}
		
		return list;
	}
	
	private LectureReviewItem parseLectureReviewItem(JSONObject jsonObject) throws Exception{
		int id = jsonObject.getInt("id");
		String courseName = jsonObject.getString("course_name");
		String courseCode = jsonObject.getString("course_code");
		String lecturer = jsonObject.getString("lecturer");
		String time = jsonObject.getString("time");
		String date = jsonObject.getString("date");
		String room = jsonObject.getString("room");
		String comment = jsonObject.getString("comment");
		String strReviewTime = jsonObject.getString("review_time");
		
		Date reviewDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(strReviewTime);
		
		boolean[] ratings = new boolean[5];
		
		JSONArray array = jsonObject.getJSONArray("ratings");
		for (int i=0; i<5; i++) {
			ratings[i] = array.getBoolean(i);
		}
		
		LectureReviewItem reviewItem = new LectureReviewItem(
				date, time, courseName, courseCode, 
				room, lecturer, ratings, 
				comment, id, reviewDate);
		return reviewItem;
	}
}





















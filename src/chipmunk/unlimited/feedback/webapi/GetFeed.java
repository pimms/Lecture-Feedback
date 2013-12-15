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

import chipmunk.unlimited.feedback.LectureItem;
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
	 * Get the feed of reviews for one or more courses.
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
     *
     * @param lastId
     * All returned reviews will have a lower ID than this parameter.
     * Set to negative value to ignore this parameter.
	 */
	public void apiCall(String baseUrl, 
						List<SubscriptionItem> subscriptions, int first, int count, int lastId)
	{
		if (subscriptions.size() == 0) {
			mCallback.onGetFeedSuccess(null);
			return;
		}
		
		baseUrl += "/getFeed.php?";
		baseUrl += "filter=" + createFilterString(subscriptions);

        apiCall(baseUrl, first, count, lastId);
	}

    /**
     * Get the feed of a single lecture.
     *
     * @param baseUrl
     * The base url containing the root level of the webAPI.
     *
     * @param lectureHash
     * The lecture to be included in the feed.
     *
     * @param first
     * The first item to be returned in the result set.
     *
     * @param count
     * The maximum number of items to be returned.
     *
     * @param lastId
     * All returned reviews will have a lower ID than this parameter.
     * Set to negative value to ignore this parameter.
     */
    public void apiCall(String baseUrl, String lectureHash, int first, int count, int lastId) {
        baseUrl += "/getFeed.php?hash=" + lectureHash;
        apiCall(baseUrl, first, count, lastId);
    }

    private void apiCall(String baseUrl, int first, int count, int lastId) {
        baseUrl += "&first=" + first;
        baseUrl += "&count=" + count;

        if (lastId > 0) {
            baseUrl += "&lastid=" + lastId;
        }

        Log.v(TAG, "GetFeed -->");
        new AsyncHttpClient().get(baseUrl, this);
    }


    /**
     * Create a filter string to be used when retrieving the
     * feed for a set of courses.
     *
     * @param subscriptions
     * The courses to be included in the feed.
     *
     * @return
     * The "filter" HTTP-GET parameter value.
     */
	private String createFilterString(List<SubscriptionItem> subscriptions) {
		String result = "";
		
		if (subscriptions.size() != 0) {
			result = subscriptions.get(0).getHigCode();
			
			for (int i=1; i<subscriptions.size(); i++) {
				result += "," + subscriptions.get(i).getHigCode();
			}
		}
		
		return result;
	}

    /**
     * Create a filter string to be used when retrieving
     * the feed for a single lecture.
     *
     * @param lecture
     * The lecture on which the feed is based.
     *
     * @return
     * To be determined.
     */
    private String createFilterString(LectureItem lecture) {
        return null;
    }

	
	@Override
	public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        Log.v(TAG, "GetFeed <-- SUCCESS");

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
        Log.v(TAG, "GetFeed <-- FAILURE");

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
        int cloneCount = jsonObject.getInt("clone_count");

        if (comment.equals("null")) {
            comment = "";
        }
		
		Date reviewDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(strReviewTime);
		
		boolean[] ratings = new boolean[5];
		JSONArray array = jsonObject.getJSONArray("ratings");
		for (int i=0; i<5; i++) {
			ratings[i] = array.getBoolean(i);
		}
		
		LectureReviewItem reviewItem = new LectureReviewItem(
				date, time, courseName, courseCode, 
				room, lecturer, ratings, 
				comment, id, reviewDate, cloneCount);
		return reviewItem;
	}
}





















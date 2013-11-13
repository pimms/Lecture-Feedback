package chipmunk.unlimited.feedback.webapi;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;

import android.util.Log;
import chipmunk.unlimited.feedback.LectureReviewItem;
import chipmunk.unlimited.feedback.subscription.SubscriptionItem;

/**
 * Class managing the web API call "getFeed".
 */
public class GetFeed extends WebAPICall {
	private static final String TAG = "GetFeed";
	
	public interface GetFeedCallback {
		public void onGetFeedSuccess(List<LectureReviewItem> reviewItems);
		public void onGetFeedFailure(String errorMessage);
	}
	
	
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
		
		new AsyncHttpClient().get(baseUrl, this);
	}
	
	private String createFilterString(List<SubscriptionItem> subscriptions) {
		String result = "";
		
		if (subscriptions.size() != 0) {
			result = subscriptions.get(0).getItemId();
			
			for (int i=1; i<subscriptions.size(); i++) {
				result += "," + subscriptions.get(i).getItemId();
			}
		}
		
		return result;
	}
	
	
	@Override
	public void onSuccess(String response) {
		List<LectureReviewItem> list = parseResultJson(response);
		if (list != null) {
			mCallback.onGetFeedSuccess(list);
		} else {
			mCallback.onGetFeedFailure("invalid JSON received. wtf yo");
		}
	}
	
	@Override 
	public void onFailure(Throwable throwable, String eMsg) {
		mCallback.onGetFeedFailure(eMsg);
	}
	
	
	private List<LectureReviewItem> parseResultJson(String response) {
		JSONObject json = getJsonRoot(response);
		if (json == null) {
			return null;
		}
		
		List<LectureReviewItem> list = new ArrayList<LectureReviewItem>();
		
		try {
			int count = json.getInt("item_count");
			JSONArray items = json.getJSONArray("items");
			
			for (int i=0; i<count; i++) {
				JSONObject item = items.getJSONObject(i);
				list.add(parseLectureReviewItem(item));
			}
		} catch (Exception ex) {
			Log.e(TAG, "Failed to parse items: " + ex.getMessage());
		}
		
		return list;
	}
	
	private LectureReviewItem parseLectureReviewItem(JSONObject jsonObject) throws Exception{
		String course = jsonObject.getString("course");
		String lecturer = jsonObject.getString("lecturer");
		String time = jsonObject.getString("time");
		String date = jsonObject.getString("date");
		String room = jsonObject.getString("room");
		String comment = jsonObject.getString("comment");
		int id = jsonObject.getInt("id");
		
		boolean[] ratings = new boolean[5];
		
		JSONArray array = jsonObject.getJSONArray("ratings");
		for (int i=0; i<5; i++) {
			ratings[i] = array.getBoolean(i);
		}
		
		LectureReviewItem reviewItem = new LectureReviewItem(
				date, time, course, 
				room, lecturer, ratings, 
				comment, id);
		return reviewItem;
	}
}





















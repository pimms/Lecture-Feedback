package chipmunk.unlimited.feedback.webapi;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

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
		mCallback = callback;
	}
	
	public void apiCall(String baseUrl, 
						List<SubscriptionItem> subscriptions,
						int first, int count)
	{
		// TODO
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





















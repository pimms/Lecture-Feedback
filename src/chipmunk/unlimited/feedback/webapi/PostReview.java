package chipmunk.unlimited.feedback.webapi;

import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.json.JSONObject;

import android.content.Context;
import android.provider.Settings.Secure;
import android.util.Log;

import chipmunk.unlimited.feedback.LectureReviewItem;
import chipmunk.unlimited.feedback.webapi.WebAPI.PostReviewCallback;

import com.loopj.android.http.AsyncHttpClient;

class PostReview extends WebAPICall {
    private static final String TAG = "PostReview";
	private static final String CLIENT_HASH_SALT = "SpiceGurls";
	
	private Context mContext;
	private PostReviewCallback mCallback;
	
	
	public PostReview(Context context, PostReviewCallback callback) {
		mContext = context;
		mCallback = callback;
	}
	
	public void apiCall(String baseUrl, LectureReviewItem review) {
        Log.v(TAG, "PostReview -->");

		baseUrl += "/postReview.php";
		baseUrl += "?client_hash=" 	+ getClientHash();
		baseUrl += "&course_name=" 	+ review.getCourseName();
		baseUrl += "&course_code=" 	+ review.getCourseHigCode();
		baseUrl += "&lecturer=" 	+ review.getLecturer();
		baseUrl += "&room=" 		+ review.getRoom();
		baseUrl += "&start_time="	+ review.getStartTimeUNIX();
		baseUrl += "&end_time=" 	+ review.getEndTimeUNIX();
		baseUrl += "&attribute_version_set=1";
		baseUrl += "&attributes=" 	+ review.getRatingString();
        baseUrl += "&hash="         + review.getLectureHash();
		
		String comment = review.getURIEncodedComment();
		if (comment.length() != 0) {
			baseUrl += "&comment=" + comment;
		}
		
		new HttpClient().get(baseUrl, this);
	}
	
	
	@Override
	public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        Log.v(TAG, "PostReview <-- SUCCESS");

		String response;
		
		try {
			response = new String(responseBody, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			mCallback.onPostReviewFailure("Returned content is not UTF-8 encoded");
			return;
		}
		
		JSONObject json = getJsonRoot(response);
		try {
			if (json != null && json.getString("status").equals("ok")) {
				mCallback.onPostReviewSuccess();
			} else {
				mCallback.onPostReviewFailure("Post failed: api returned status bad");
			}
		} catch (Exception e) {
			mCallback.onPostReviewFailure("Failed to parse json: " + e.getMessage());
		}
	}
	
	@Override 
	public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        Log.v(TAG, "PostReview <-- FAILURE");
        
		String bodyString;
		String errorMessage = "HTTP Error with code " + statusCode;
		
		try {
			if (responseBody != null) {
				bodyString = new String(responseBody, "UTF-8");
				errorMessage += " (" + bodyString + ")";
			}
		} catch (UnsupportedEncodingException e) {
			mCallback.onPostReviewFailure("HTTP error with code " + statusCode);
			return;
		}
		
		mCallback.onPostReviewFailure(errorMessage);
	}

	
	private String getClientHash() {
		String androidId = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
		return SHA1.hash(androidId + CLIENT_HASH_SALT);
	}
}

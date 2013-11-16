package chipmunk.unlimited.feedback.webapi;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.Header;

import chipmunk.unlimited.feedback.LectureReviewItem;

public class PostReview extends WebAPICall {
	public interface PostReviewCallback {
		public void onPostReviewSuccess();
		public void onPostReviewFailure(String errorMessage);
	}
	
	private PostReviewCallback mCallback;
	
	
	public PostReview(PostReviewCallback callback) {
		mCallback = callback;
	}
	
	public void apiCall(String baseUrl, LectureReviewItem review) {
		
	}
	
	
	@Override
	public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
		String response;
		
		try {
			response = new String(responseBody, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			mCallback.onPostReviewFailure("Returned content is not UTF-8 encoded");
			return;
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
			mCallback.onPostReviewFailure("HTTP error with code " + statusCode);
			return;
		}
		
		mCallback.onPostReviewFailure(errorMessage);
	}
}

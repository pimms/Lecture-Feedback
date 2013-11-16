package chipmunk.unlimited.feedback.webapi;

import chipmunk.unlimited.feedback.LectureReviewItem;

public class PostReview extends WebAPICall {
	public interface PostReviewCallback {
		public void onPostReviewSuccess();
		public void onPostReviewFailure();
	}
	
	private PostReviewCallback mCallback;
	
	
	public PostReview(PostReviewCallback callback) {
		mCallback = callback;
	}
	
	public void apiCall(String baseUrl, LectureReviewItem review) {
		
	}
}

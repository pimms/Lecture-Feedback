package chipmunk.unlimited.feedback.webapi;

import java.util.List;

import android.content.Context;
import chipmunk.unlimited.feedback.LectureReviewItem;
import chipmunk.unlimited.feedback.subscription.SubscriptionItem;
import chipmunk.unlimited.feedback.webapi.PostReview.PostReviewCallback;

/**
 * @class WebAPI
 * Java interface for communicating with the webAPI
 */
public class WebAPI {
	/**
	 * The base URL of the web API. 
	 * When debugging, change this to an address accessible to
	 * your device where the API is hosted.
	 * 
	 * DO NOT ADD "/" AT THE END!
	 */
	private static final String API_URL = "http://home.knutremi.com:8080/~lecture-feedback/lfb/htdocs/api";
	
	/**
	 * Method using the web-API call "getFeed.php". The returned set of
	 * data is a reverse-chronological list of reviews.
	 * 
	 * @param callback
	 * The object to receive the finish-notifications.
	 * 
	 * @param subscriptions 
	 * The list of subscription items to be included in the result set.
	 * 
	 * @param first
	 * The first item to be returned.
	 * 
	 * @param count
	 * The maximum number of items to be returned.
	 */
	public void getFeed(GetFeed.GetFeedCallback callback, 
						List<SubscriptionItem> subscriptions, 
						int first, int count) 
	{
		GetFeed getFeed = new GetFeed(callback);
		getFeed.apiCall(API_URL, subscriptions, first, count);
	}
	
	/**
	 * Method using the web-API call "postReview.php". 
	 * 
	 * @param context
	 * The context is used to retrieve the ANDROID_ID value when passing
	 * the clienthash parameter.
	 * 
	 * @param callback
	 * The callback to receive the finish-notifications.
	 * 
	 * @param review
	 * LectureReviewItem containing the information to be posted 
	 * to the web-API.
	 */
	public void postReview(Context context, PostReviewCallback callback, LectureReviewItem review) {
		PostReview postReview = new PostReview(context, callback);
		postReview.apiCall(API_URL, review);
	}
}

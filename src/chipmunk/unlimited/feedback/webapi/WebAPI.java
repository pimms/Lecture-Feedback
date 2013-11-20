package chipmunk.unlimited.feedback.webapi;

import java.util.List;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import chipmunk.unlimited.feedback.LectureItem;
import chipmunk.unlimited.feedback.LectureReviewItem;
import chipmunk.unlimited.feedback.subscription.SubscriptionItem;

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
	 * Callback interface for the API-call getFeed
	 */
	public interface GetFeedCallback {
		public void onGetFeedSuccess(List<LectureReviewItem> reviewItems);
		public void onGetFeedFailure(String errorMessage);
	}
	
	/**
	 * Callback interface for the API-call postReview
	 */
	public interface PostReviewCallback {
		public void onPostReviewSuccess();
		public void onPostReviewFailure(String errorMessage);
	}

    /**
     * Callback interface for the API-call getStats with
     * action = "lecture_votes_all"
     */
    public interface GetLectureVotesAllCallback {
        public void onGetLectureVotesAllSuccess(List<LectureVote> items);
        public void onGetLectureVotesAllFailure(String errorMessage);
    }

    /**
     * Callback interface for the API-call getStats
     * with action = "course_votes"
     */
    public interface GetCourseVotesCallback {
        public void onGetCourseVotesSuccess(List<CourseVote> items);
        public void onGetCourseVotesFailure(String errorMessage);
    }



    /**
     * Class used to structure the return values from
     * getStats?action=course_votes. The object holds the
     * course code and the number of positive/negative votes.
     */
    public static class CourseVote {
        private String mCourseCode;
        private int mPositive;
        private int mNegative;

        public CourseVote(JSONObject item) {
            try {
                mCourseCode = item.getString("course_code");
                mPositive = item.getInt("positive");
                mNegative = item.getInt("negative");
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

        public String getCourseCode() {
            return mCourseCode;
        }

        public int getPositiveVoteCount() {
            return mPositive;
        }

        public int getNegativeVoteCount() {
            return mNegative;
        }
    }

    /**
     * Class used to structure the return values from
     * getStats?action=lecture_votes_all. The class holds
     * the LectureItem created from the return values and the
     * vote count themselves.
     */
    public static class LectureVote {
        private LectureItem mLectureItem;
        private int mPositive;
        private int mNegative;

        public LectureVote(JSONObject item) {
            try {
                String courseName   = item.getString("course_name");
                String courseCode   = item.getString("course_code");
                String lecturer     = item.getString("lecturer");
                String time         = item.getString("time");
                String date         = item.getString("date");
                String room         = item.getString("room");
                mPositive           = item.getInt("positive");
                mNegative           = item.getInt("negative");
                mLectureItem = new LectureItem(
                        date, time, courseName,
                        courseCode, room, lecturer
                );
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

        public LectureItem getLectureItem() {
            return mLectureItem;
        }

        public int getPositiveVoteCount() {
            return mPositive;
        }

        public int getNegativeVoteCount() {
            return mNegative;
        }
    }



	/**
	 * Method using the web-API call "getFeed.php". The returned set of
	 * data is a reverse-chronological list of reviews, and is based
     * on ONE or MULTIPLE courses.
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
	public void getFeed(GetFeedCallback callback, 
						List<SubscriptionItem> subscriptions, 
						int first, int count) 
	{
		GetFeed getFeed = new GetFeed(callback);
		getFeed.apiCall(API_URL, subscriptions, first, count);
	}

    /**
     * Method using the web-API call "getFeed.php". The returned set of
     * data is a reverse-chronological list of reviews, and is based
     * on a SINGLE lecture.
     *
     * @param callback
     * The object to receive the finish-notifications.
     *
     * @param lecture
     * The single lecture from which to receive a feed of reviews.
     *
     * @param first
     * The first item to be returned.
     *
     * @param count
     * The maximum number of items to be returned.
     */
    public void getFeed(GetFeedCallback callback,
                        LectureItem lecture, int first, int count) {
        GetFeed getFeed = new GetFeed(callback);
        getFeed.apiCall(API_URL, lecture, first, count);
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
	public void postReview(	Context context, 
							PostReviewCallback callback, 
							LectureReviewItem review) {
		PostReview postReview = new PostReview(context, callback);
		postReview.apiCall(API_URL, review);
	}


    /**
     * Wrapper around the web API call "getStats" with action
     * parameter = "course_votes".
     *
     * @param callback
     * The callback object.
     *
     * @param subs
     * List of subscription items to be included in the
     * result set.
     */
    public void getCourseVotes(GetCourseVotesCallback callback,
                              List<SubscriptionItem> subs) {
        GetStats getStats = new GetStats();
        getStats.apiCallCourseVotes(callback, API_URL, subs);
    }

    /**
     * Wrapper aroud the web API call "getStats" with action
     * parameter = "lecture_votes_all"
     *
     * @param callback
     * The callback object
     *
     * @param course
     * The course to retrieve lectures for
     *
     * @param first
     * The first item in the total set to be included
     *
     * @param count
     * The maxmimum number of items to be included
     */
    public void getLectureVotesAll(GetLectureVotesAllCallback callback,
                                   String course, int first, int count) {
        GetStats getStats = new GetStats();
        getStats.apiCallLectureVotesAll(callback, API_URL, course, first, count);
    }
}

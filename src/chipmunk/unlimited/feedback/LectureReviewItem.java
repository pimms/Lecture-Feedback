package chipmunk.unlimited.feedback;

import java.util.Date;

import android.net.Uri;
import android.util.Log;

/**
 * Holds the data required to define a lecture review.
 */
public class LectureReviewItem extends LectureItem {
	private static final String TAG = "LectureReviewItem";
	private static final int ATTRIBUTE_COUNT = 5;
	
	private boolean[] mRatings;
	private String mComment;
	private int mId;
	private Date mReviewDate;
    private int mCloneCount;
	
	
	/**
	 * Convert a string object created by getRatingString()
	 * into a boolean array.
	 * 
	 * @param ratingString
	 * String on the form specified by getRatingString()
	 * 
	 * @return
	 * boolean array containing the same values as ratingString.
	 */
	public static boolean[] getRatingArrayFromString(String ratingString) {
		String[] arr = ratingString.split("\\.");
		boolean[] rating = new boolean[arr.length];
		
		for (int i=0; i<arr.length; i++) {
			if (arr[i].equals("1")) {
				rating[i] = true;
			} else if (arr[i].equals("0")) {
				rating[i] = false;
			} else {
				Log.e(TAG, "Invalid rating value: " + arr[i] + "(" + ratingString + ")");
				rating[i] = false;
			}
		}
		
		if (rating.length != ATTRIBUTE_COUNT) {
			Log.e(TAG, "Invalid attribute count in geRatingArrayFromString(): " 
						+ rating.length + " received, " + ATTRIBUTE_COUNT + " expected."
						+ " Raw string: " + ratingString);
		}
		
		return rating;
	}
	
	
	/**
	 * @param date
	 * @see @class LectureItem
	 * 
	 * @param time
	 * @see @class LectureItem
	 * 
	 * @param name
	 * @see @class LectureItem
	 * 
	 * @param higCode
	 * @see @class LectureItem
	 * 
	 * @param room
	 * @see @class LectureItem
	 * 
	 * @param lecturer
	 * @see @class LectureItem
	 * 
	 * @param ratings
	 * Array containing five values, where false is
	 * negative and true is positive.
	 * 
	 * @param comment
	 * The comment of the user. Should not be null, but an empty
	 * string on no comment.
	 * 
	 * @param id
	 * The unique ID of the review, received from the web API. 
	 * For submit-instances, -1 should be passed.
	 * 
	 * @param reviewDate
	 * The date the comment was made. Null is a suitable value
	 * for submit-instances.
	 */
	public LectureReviewItem(String date, String time, String name, String higCode, 
							String room, String lecturer, boolean[] ratings, 
							String comment, int id, Date reviewDate, int cloneCount) {
		super(date, time, name, higCode, room, lecturer);
		mRatings = ratings;
		mComment = comment;
		mId = id;
		mReviewDate = reviewDate;
        mCloneCount = cloneCount;
		
		if (mRatings.length != ATTRIBUTE_COUNT) {
			Log.e(TAG, "Invalid attribute count in LectureReviewItem(): " 
					+ mRatings.length + " received, " + ATTRIBUTE_COUNT + " expected.");
		}
	}
	
	/**
	 * Create a LectureReviewItem based on a LectureItem.
	 * 
	 * @param lecture
	 * LectureItem to be passed to the super constructor.
	 * 
	 * @param ratings
	 * Array containing five values, where false is
	 * negative and true is positive.
	 * 
	 * @param comment
	 * The comment of the user. Should not be null, but an empty
	 * string on no comment.
	 * 
	 * @param id
	 * The unique ID of the review, received from the web API. 
	 * For submit-instances, -1 should be passed.
	 * 
	 * @param reviewDate
	 * The date the comment was made. Null is a suitable value
	 * for submit-instances.
	 */
	public LectureReviewItem(LectureItem lecture, boolean[] ratings,
							String comment, int id, Date reviewDate,
                            int cloneCount) {
		super(lecture.getDateString(), lecture.getTimeString(), 
			lecture.getCourseName(), lecture.getCourseHigCode(), 
			lecture.getRoom(), lecture.getLecturer());
		
		mRatings = ratings;
		mComment = comment;
		mId = id;
		mReviewDate = reviewDate;
        mCloneCount = cloneCount;
		
		if (mRatings.length != ATTRIBUTE_COUNT) {
			Log.e(TAG, "Invalid attribute count in LectureReviewItem(): " 
					+ mRatings.length + " received, " + ATTRIBUTE_COUNT + " expected.");
		}
	}



	public boolean[] getRatings() {
		return mRatings;
	}
	/**
	 * Stringify the rating-array.
	 * 
	 * @return
	 * String on the form:  "0.0.1.1.0"
	 */
	public String getRatingString() {
		StringBuilder sb = new StringBuilder();
		
		for (int i=0; i<mRatings.length; i++) {
			if (i != 0) {
				sb.append(".");
			}
			sb.append( (mRatings[i]) ? "1" : "0" );
		}
		
		return sb.toString();
	}
	
	public String getComment() {
		return mComment;
	}
	
	public String getURIEncodedComment() {
		return Uri.encode(mComment);
	}
	
	public int getId() {
		return mId;
	}
	
	public Date getReviewDate() {
		return mReviewDate;
	}

    public int getCloneCount() {
        return mCloneCount;
    }
}

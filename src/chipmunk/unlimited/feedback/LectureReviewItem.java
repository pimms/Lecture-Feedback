package chipmunk.unlimited.feedback;

import java.util.Date;

/**
 * Holds the data required to define a lecture review.
 */
public class LectureReviewItem extends LectureItem {
	private boolean[] mRatings;
	private String mComment;
	private int mId;
	private Date mReviewDate;
	
	/**
	 * @param date
	 * See LectureItem.
	 * 
	 * @param time
	 * See LectureItem.
	 * 
	 * @param name
	 * See LectureItem.
	 * 
	 * @param higCode
	 * See LectureItem
	 * 
	 * @param room
	 * See LectureItem.
	 * 
	 * @param lecturer
	 * See LectureItem.
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
	 * 
	 * @param reviewDate
	 * The date the comment was made.
	 */
	public LectureReviewItem(String date, String time, String name, String higCode, 
							String room, String lecturer, boolean[] ratings, 
							String comment, int id, Date reviewDate) {
		super(date, time, name, higCode, room, lecturer);
		mRatings = ratings;
		mComment = comment;
		mId = id;
		mReviewDate = reviewDate;
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
	 * 
	 * @param reviewDate
	 * The date the comment was made.
	 */
	public LectureReviewItem(LectureItem lecture, boolean[] ratings,
							String comment, int id, Date reviewDate) {
		super(lecture.getDateString(), lecture.getTimeString(), 
			lecture.getCourseName(), lecture.getCourseHigCode(), 
			lecture.getRoom(), lecture.getLecturer());
		
		mRatings = ratings;
		mComment = comment;
		mId = id;
		mReviewDate = reviewDate;
	}
	
	public boolean[] getRatings() {
		return mRatings;
	}
	
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
	
	public int getId() {
		return mId;
	}
	
	public Date getReviewDate() {
		return mReviewDate;
	}
}

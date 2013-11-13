package chipmunk.unlimited.feedback;

/**
 * Holds the data required to define a lecture review
 */
public class LectureReviewItem extends LectureItem {
	private boolean[] mRatings;
	private String mComment;
	private int mId;
	
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
	 */
	public LectureReviewItem(String date, String time, String name, String room, 
							 String lecturer, boolean[] ratings, String comment, int id) {
		super(date, time, name, room, lecturer);
		mRatings = ratings;
		mComment = comment;
		mId = id;
	}
	
	public boolean[] getRatings() {
		return mRatings;
	}
	
	public String getComment() {
		return mComment;
	}
	
	public int getId() {
		return mId;
	}
}

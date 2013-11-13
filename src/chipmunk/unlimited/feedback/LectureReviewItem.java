package chipmunk.unlimited.feedback;

/**
 * Holds the data required to define a lecture review
 */
public class LectureReviewItem extends LectureItem {
	private boolean[] mRatings;
	private String mComment;
	private int mId;
	
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

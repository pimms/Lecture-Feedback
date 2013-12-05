package chipmunk.unlimited.feedback.test;

import java.util.Date;

import chipmunk.unlimited.feedback.LectureItem;
import chipmunk.unlimited.feedback.LectureReviewItem;
import android.test.AndroidTestCase;

public class LectureReviewItemTest extends AndroidTestCase {

	private LectureReviewItem lectureReviewItem1; // LectureItem lecture, boolean[] ratings, String comment, int id, Date reviewDate
	private LectureReviewItem lectureReviewItem2; // String date, String time, String name, String higCode, String room, String lecturer, boolean[] ratings, String comment, int id, Date reviewDate
	
	/**
	 * Required to run the test.
	 */
	public LectureReviewItemTest() {
		super();
	}
	
	/**
	 * Test if application was initialized properly.
	 */
	protected void testPreconditions() {
	}

	/**
	 * To control our test environment.
	 * @see android.test.AndroidTestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
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
		 * For submit-instances, -1 should be passed.
		 * 
		 * @param reviewDate
		 * The date the comment was made. Null is a suitable value
		 * for submit-instances.
		 */
		
	}

	/** To control our test environment.
	 * @see android.test.AndroidTestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}
	
	public void TestGetComment() {
		
	}
	
	public void TestGetId() {
		
	}
	
	public void TestGetRatings() {
		
	}
	
	public void TestGetRatingString() {
		
	}
	
	public void TestGetReviewDate() {
		
	}
	
	public void TestGetURIEncodedComment() {
		
	}

}

package chipmunk.unlimited.feedback.test;

import java.util.Date;

import chipmunk.unlimited.feedback.LectureItem;
import chipmunk.unlimited.feedback.LectureReviewItem;
import android.test.AndroidTestCase;

public class LectureReviewItemTest extends AndroidTestCase {

	// Our testable units. One for each constructor.
	private LectureReviewItem lectureReviewItem1;
	private LectureReviewItem lectureReviewItem2;
	
	// Test data for the first constructor.
	private LectureItem lectureItem; // The first constructor takes an object as a parameter.
	private String date1 = "2013-12-06";
	private String time1 = "14:32 - 14:33";
	private String name1 = "Mobil utviklingsprosjekt";
	private String higCode1 = "IMT3672";
	private String room1 = "D101";
	private String lecturer1 = "Nowostawski";
	private boolean[] ratings1 = {false, false, true, true, false}; // Notice the corresponding ratingString1 below.
	private String comment1 = "Test comment 1";
	private int id1 = -1; // TODO: Determine whether we should test this for an existing ID as well. Or generate one.
	private Date reviewDate1 = null; // TODO: Should we generate the date here?
	
	// The stringified array we expect back from getRatingString().
	private String ratingString1 = "0.0.1.1.0";
	
	// Test data for the second constructor.
	private String date2 = "2013-12-06";
	private String time2 = "15:03 - 15:04";
	private String name2 = "Mobil Systemutvikling";
	private String higCode2 = "IMT3662";
	private String room2 = "B211";
	private String lecturer2 = "Nowostawski";
	private boolean[] ratings2 = {true, false, false, true, true};
	private String comment2 = "Test comment 2";
	private int id2 = -1;
	private Date reviewDate2 = null;
	
	/**
	 * Required to run the test.
	 */
	public LectureReviewItemTest() {
		super();
	}
	
	/**
	 * Test if application was initialized properly.
	 */
	public void testPreconditions() {
	}

	/**
	 * To control our test environment.
	 * @see android.test.AndroidTestCase#setUp()
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		
		// Construct our first unit.
		this.lectureItem = new LectureItem(this.date1, this.time1, this.name1,
				this.higCode1, this.room1, this.lecturer1);
		
		this.lectureReviewItem1 = new LectureReviewItem(this.lectureItem, 
				this.ratings1, this.comment1, this.id1, this.reviewDate1);
		
		// Construct our second unit.
		this.lectureReviewItem2 = new LectureReviewItem(this.date2, this.time2,
				this.name2, this.higCode2, this.room2, this.lecturer2,
				this.ratings2, this.comment2, this.id2, this.reviewDate2);
	}

	/** To control our test environment.
	 * @see android.test.AndroidTestCase#tearDown()
	 */
	@Override
	public void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}
	
	/**
	 * Check if test comments and object comments are equal.
	 * Retrieves test comments and expected object comments, followed by asserting that they didn't change.
	 */
	public void testGetComment() {
		
		// Retrieve test comments and expected object comments.
		
		String expected1 = this.comment1;
		String actual1 = this.lectureReviewItem1.getComment();
		
		String expected2 = this.comment2;
		String actual2 = this.lectureReviewItem2.getComment();
		
		// Assert that they haven't changed.
		assertEquals(expected1, actual1);
		assertEquals(expected2, actual2);
	}
	
	/**
	 * Check if test id's and test comments are equal.
	 * @see chipmunk.unlimited.feedback.test.LectureReviewItemTest#testGetComment An example of how we perform tests.
	 */
	public void testGetId() {
		
		int expected1 = this.id1;
		int actual1 = this.lectureReviewItem1.getId();
		
		int expected2 = this.id2;
		int actual2 = this.lectureReviewItem2.getId();
		
		assertEquals(expected1, actual1);
		assertEquals(expected2, actual2);
	}
	
	/**
	 * Are the test ratngs the same as the object ratings?
	 * @see chipmunk.unlimited.feedback.test.LectureReviewItemTest#testGetComment An example of how we perform tests.
	 */
	public void TestGetRatings() {
		
		boolean[] expected1 = this.ratings1;
		boolean[] actual1 = this.lectureReviewItem1.getRatings();
		
		boolean[] expected2 = this.ratings2;
		boolean[] actual2 = this.lectureReviewItem2.getRatings();
		
		assertEquals(expected1, actual1);
		assertEquals(expected2, actual2);
		
	}
	
	/**
	 * Can we retrieve the correct stringified rating array from the method?
	 * @see chipmunk.unlimited.feedback.test.LectureReviewItemTest#testGetComment An example of how we perform tests.
	 */
	public void testGetRatingString() {
		
		String expected1 = this.ratingString1;
		String actual1 = this.lectureReviewItem1.getRatingString();
				
		assertEquals(expected1, actual1);
	}
	
	/**
	 * Can we retrieve the correct Date from the method?
	 * @see chipmunk.unlimited.feedback.test.LectureReviewItemTest#testGetComment An example of how we perform tests.
	 */
	public void testGetReviewDate() {
		
		Date expected1 = this.reviewDate1;
		Date actual1 = this.lectureReviewItem1.getReviewDate();
		
		Date expected2 = this.reviewDate2;
		Date actual2 = this.lectureReviewItem2.getReviewDate();
		
		assertEquals(expected1, actual1);
		assertEquals(expected2, actual2);
		
	}
	
	/**
	 * Is the method comment properly URIencoded?
	 * @see chipmunk.unlimited.feedback.test.LectureReviewItemTest#testGetComment An example of how we perform tests.
	 */
	public void testGetURIEncodedComment() {
		Date expected1 = this.reviewDate1;
		Date actual1 = this.lectureReviewItem1.getReviewDate();
		
		Date expected2 = this.reviewDate2;
		Date actual2 = this.lectureReviewItem2.getReviewDate();
		
		assertEquals(expected1, actual1);
		assertEquals(expected2, actual2);
	}

}

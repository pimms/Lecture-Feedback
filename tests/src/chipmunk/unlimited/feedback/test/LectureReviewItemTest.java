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
//		lectureReviewItem1 = new LectureReviewItem(new LectureItem(date, time, name, higCode, room, lecturer), ratings, comment, id, reviewDate);
//		lectureReviewItem2 = new LectureReviewItem(date, time, name, higCode, room, lecturer, ratings, comment, id, reviewDate);
		
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

package chipmunk.unlimited.feedback.rating;

import java.security.InvalidParameterException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import chipmunk.unlimited.feedback.LectureItem;
import chipmunk.unlimited.feedback.LectureReviewItem;
import chipmunk.unlimited.feedback.R;
import chipmunk.unlimited.feedback.database.ReviewedLectureDatabase;
import chipmunk.unlimited.feedback.webapi.WebAPI;
import chipmunk.unlimited.feedback.webapi.WebAPI.PostReviewCallback;



/**
 * @class LectureRatingActivity
 * Activity for displaying and submitting a review of a lecture.
 * 
 * The activity has two different states: read-only and write. The state
 * of the activity depends on the parameters passed via the Intent.
 * Parameters are passed via the intent using the "putExtra" interface.
 * 
 * Note that if any parameter description is ambiguous or unclear, 
 * all the required parameters map DIRECTLY to get-methods in the 
 * LectureItem class. A LectureItem instance is created from the required
 * parameters. 
 * 
 * Parameter list:
 * 	 +---------------------------+--------------------------+-----------+---------+
 *   |	Parameter name			 |	Description 			| Required  | Type	  |
 *   +---------------------------+--------------------------+-----------+---------+
 *   |	PARAM_COURSE_NAME 		 | The course name 			| YES 		| String  |
 *   |  PARAM_COURSE_HIG_CODE 	 | The HiG course code		| YES 		| String  |
 *   |	PARAM_LECTURER_NAME 	 | The name of the lecturer | YES 		| String  |
 *   |  PARAM_TIME 				 | The time of the lecture  | YES 		| String  |
 *   | 	PARAM_DATE 				 | "yyyy-MM-dd" date of lec.| YES 		| String  |
 *   | 	PARAM_ROOM 				 | The room of the lecture  | YES       | String  |
 *   | 	PARAM_READ_ONLY 		 | Toggle read only? 		| NO 		| bool 	  |
 *   | 	PARAM_RATINGS			 | The ratings of all the   |			|  		  | 
 *   |							 | lecture attribtues 		| NO* 		| bool[5] |
 *   | 	PARAM_COMMENT 			 | User comment on lecture  | NO* 		| String  |
 *   +---------------------------+--------------------------+-----------+---------+
 *   *) Required parameter if "PARAM_READ_ONLY" is true
 */
public class LectureRatingActivity extends Activity 
	        implements  PostReviewCallback,
                        LectureRatingView.RatingListener {
	/** 
	 * The keys through which values will be set through the Intent 
	 */
	public static final String PARAM_COURSE_NAME 		= "param_course_name";
	public static final String PARAM_COURSE_HIG_CODE 	= "param_course_hig_code";
	public static final String PARAM_LECTURER_NAME 		= "param_lecturer_name";
	public static final String PARAM_TIME 				= "param_time";
	public static final String PARAM_DATE 				= "param_date";
	public static final String PARAM_ROOM 				= "param_room";
	public static final String PARAM_READ_ONLY 			= "param_read_only";
	public static final String PARAM_RATINGS 			= "param_ratings";
	public static final String PARAM_COMMENT 			= "param_comment";
	
	private static final String TAG = "LectureRatingActivity";


	private LectureItem mLectureItem;
	private LectureReviewItem mLectureReviewItem;
    private LectureRatingView mLectureRatingView;
	
	/** Displayed when submitting */
	private ProgressDialog mProgressDialog;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_rating);

        mLectureRatingView = new LectureRatingView(this);
        mLectureRatingView.setRatingListener(this);
        
        ScrollView scrollView = (ScrollView)findViewById(R.id.rating_scroll_content);
        scrollView.addView(mLectureRatingView, new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        ));

	    handleIntentParameters();
	}

	
	@Override 
	public void onPostReviewSuccess() {
		ReviewedLectureDatabase db = new ReviewedLectureDatabase(this);
		db.insertLectureItem(mLectureReviewItem);
		
		hideProgressDialog();
		finish();
	}
	
	@Override
	public void onPostReviewFailure(String errorMessage) {
		hideProgressDialog();
		displayErrorDialog(errorMessage);
	}


    @Override
    public void onRatingSubmit() {
        submitLectureReview();
    }

    private void submitLectureReview() {
        WebAPI webApi = new WebAPI();
        webApi.postReview(this, this, getReviewItem());

        showProgressDialog();
    }

	
	/**
	 * Set and return mLectureReviewItem.
	 */
	private LectureReviewItem getReviewItem() {
		mLectureReviewItem = new LectureReviewItem(
				mLectureItem,
				getRatingArray(),
				getComment(),
				-1, null);
		return mLectureReviewItem;
	}
	
	private boolean[] getRatingArray() {
		if (mLectureRatingView != null) {
            return mLectureRatingView.getRatingArray();
        }

        return null;
	}
	
	private String getComment() {
		if (mLectureRatingView != null) {
            return mLectureRatingView.getComment();
        }

        return null;
	}
	
	
	private void showProgressDialog() {
		hideProgressDialog();
		
		// TODO: Localization
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("Submitting...");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.show();
	}
	
	private void hideProgressDialog() {
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}

	
	private void handleIntentParameters() {
		/* Required parameter handling */
		try {
			handleRequiredParameters();	
		} catch (InvalidParameterException ex) {
			Log.e(TAG, "Missing REQUIRED parameter definitions: " + ex.getMessage());
			displayErrorDialog(ex.getMessage());
		}
		
		/* Optional parameter handling */
		try {
			handleOptionalParameters();
		} catch (InvalidParameterException ex) {
			Log.e(TAG, "Mission OPTIONAL parameter definitions: " + ex.getMessage());
			displayErrorDialog(ex.getMessage());
		}
	}
	
	
	private void handleRequiredParameters() throws InvalidParameterException {
		handleTextViewParameters();
	}
	
	private void handleTextViewParameters() throws InvalidParameterException {
		Intent intent = getIntent();
		
		/* Get the parameters */
		String courseName = intent.getStringExtra(PARAM_COURSE_NAME);
		String courseCode = intent.getStringExtra(PARAM_COURSE_HIG_CODE);
		String lecturer = intent.getStringExtra(PARAM_LECTURER_NAME);
		String time = intent.getStringExtra(PARAM_TIME);
		String date = intent.getStringExtra(PARAM_DATE);
		String room = intent.getStringExtra(PARAM_ROOM);
		
		/* Ensure required parameters are set */
		if (courseName == null || lecturer == null || time == null || room == null ||
				courseCode == null || date == null) {
			throw new InvalidParameterException("Not all required parameters are defined");
		}
		
		/* Create a LectureItem from the parameters */
		mLectureItem = new LectureItem(date, time, courseName, courseCode, room, lecturer);
		
		/* Set the text */
		mLectureRatingView.setCourseText(courseName);
		mLectureRatingView.setLecturerText(lecturer);
		mLectureRatingView.setRoomAndTimeText(time + ", " + room);
	}
	
	
	private void handleOptionalParameters() throws InvalidParameterException {
		if (getIntent().getBooleanExtra(PARAM_READ_ONLY, false)) {
			handleRatingParameters();
			handleCommentParameter();
            handleCloneWrapperView();
		}
	}
	/**
	 * Set the state of the Attribute Rating Views. The rating
	 * views are also set to be read only.
	 */
	private void handleRatingParameters() throws InvalidParameterException {
		Intent intent = getIntent();
		
		boolean[] ratings = intent.getBooleanArrayExtra(PARAM_RATINGS);
		if (ratings == null) {
			throw new InvalidParameterException("Parameter 'PARAM_RATINGS' cannot be null!");
		} else if (ratings.length < 5) {
			throw new InvalidParameterException("5 ratings expected, " + ratings.length + " ratings received!");
		}
		
		/* Set the ratings of the views and toggle them readonly */
		for (int i=0; i<mLectureRatingView.getAttributeCount(); i++) {
			int state = AttributeView.STATE_UNDEFINED;
			if (ratings[i]) {
				state = AttributeView.STATE_POSITIVE;
			} else {
				state = AttributeView.STATE_NEGATIVE;
			}
			
            mLectureRatingView.setAttributeViewState(i, state);
		}
	}
	
	private void handleCommentParameter() throws InvalidParameterException {
		String comment = getIntent().getStringExtra(PARAM_COMMENT);
		
		if (comment == null) {
			throw new InvalidParameterException("Parameter 'PARAM_COMMENT' cannot be null!");
		}
		
		EditText editText = (EditText)findViewById(R.id.rating_edit_text_comments);
		editText.setText(comment);
		//editText.setEnabled(false);
		editText.setFocusable(false);
		
		Button submitButton = (Button)findViewById(R.id.rating_button_submit);
		submitButton.setVisibility(View.INVISIBLE);
	}

    /**
     * Display the Clone-wrapper layout if the lecture has
     * not been reviewed by the local user.
     */
    private void handleCloneWrapperView() {
        ReviewedLectureDatabase reviews = new ReviewedLectureDatabase(this);
        if (mLectureItem != null) {
            int visibility;
            if (reviews.hasUserReviewed(mLectureItem)) {
                visibility = View.GONE;
            } else {
                visibility = View.VISIBLE;
            }

            View wrapper = findViewById(R.id.rating_clone_wrapper);
            if (wrapper != null) {
                wrapper.setVisibility(visibility);
            }
        } else {
            Log.d(TAG, "Unable to determine if lecture has been reviewed.");
        }
    }


	private void displayErrorDialog(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder	.setMessage(message)
				.setTitle("Error")
				.setPositiveButton("Ok", null)
				.create().show();
	}
}
package chipmunk.unlimited.feedback.rating;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import chipmunk.unlimited.feedback.LectureItem;
import chipmunk.unlimited.feedback.LectureReviewItem;
import chipmunk.unlimited.feedback.R;
import chipmunk.unlimited.feedback.R.id;
import chipmunk.unlimited.feedback.R.layout;
import chipmunk.unlimited.feedback.database.ReviewedLectureDatabase;
import chipmunk.unlimited.feedback.rating.AttributeRatingView.OnRatingChangeListener;
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
	   implements OnRatingChangeListener,
	   			  OnClickListener,
	   			  PostReviewCallback {
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
	
	private List<AttributeRatingView> mAttributeViews;
	
	private LectureItem mLectureItem;
	private LectureReviewItem mLectureReviewItem;
	
	/** Displayed when submitting */
	private ProgressDialog mProgressDialog;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_lecture_rating);
	    
	    createAttributeRatingViews();
	    handleIntentParameters();
	    
	    
	    Button button = (Button)findViewById(R.id.rating_button_submit);
	    button.setOnClickListener(this);
	}

	@Override 
	public void onRatingStateChange(AttributeRatingView ratingView, int state) {
		/* Let the overall rating (index 0) determine all undefined attributes */
		if (ratingView == mAttributeViews.get(0)) {
			for (int i=1; i<mAttributeViews.size(); i++) {
				AttributeRatingView attributeView = mAttributeViews.get(i);
				if (attributeView.getState() == AttributeRatingView.STATE_UNDEFINED) {
					attributeView.setState(state);
				}
			}
		}
	}
	
	@Override 
	public void onClick(View view) {
		if (view.getId() == R.id.rating_button_submit) {
			// Ensure all rating views have a value
			for (int i=0; i<mAttributeViews.size(); i++) {
				if (mAttributeViews.get(i).getState() == AttributeRatingView.STATE_UNDEFINED) {
					// TODO: Localize
					displayErrorDialog("All fields must be rated"); 
					return;
				}
			}
			
			submitLectureReview();
		}
	}
	
	
	private void submitLectureReview() {
		WebAPI webApi = new WebAPI();
		webApi.postReview(this, this, getReviewItem());
		
		showProgressDialog();
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
		boolean[] ratings = new boolean[mAttributeViews.size()];
		
		for (int i=0; i<mAttributeViews.size(); i++) {
			int state = mAttributeViews.get(i).getState();
			ratings[i] = (state == AttributeRatingView.STATE_POSITIVE);
		}
		
		return ratings;
	}
	
	private String getComment() {
		EditText editText = (EditText)findViewById(R.id.rating_edit_text_comments);
		return editText.getText().toString();
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
	
	
	private void createAttributeRatingViews() {
		LinearLayout wrapper = (LinearLayout)findViewById(R.id.rating_attribute_wrapper);
		
		String[] attrs = new String[] { "Overall", "Relevance", "Progression", "something", "something" };
		mAttributeViews = new ArrayList<AttributeRatingView>();
		
		for (int i=0; i<5; i++) {
			AttributeRatingView attributeView = new AttributeRatingView(this);
			attributeView.setAttributeName(attrs[i]);
			attributeView.setOnRatingChangeListener(this);
			
			mAttributeViews.add(attributeView);
			wrapper.addView(attributeView);
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
		TextView tvCourse = (TextView)findViewById(R.id.rating_text_view_course);
		TextView tvLecturer = (TextView)findViewById(R.id.rating_text_view_lecturer);
		TextView tvTimeroom = (TextView)findViewById(R.id.rating_text_view_time_room);
		
		tvCourse.setText(courseName);
		tvLecturer.setText(lecturer);
		tvTimeroom.setText(time + ", " + room);
	}
	
	
	private void handleOptionalParameters() throws InvalidParameterException {
		if (getIntent().getBooleanExtra(PARAM_READ_ONLY, false)) {
			handleRatingParameters();
			handleCommentParameter();	
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
		for (int i=0; i<mAttributeViews.size(); i++) {
			int state = AttributeRatingView.STATE_UNDEFINED;
			if (ratings[i]) {
				state = AttributeRatingView.STATE_POSITIVE;
			} else {
				state = AttributeRatingView.STATE_NEGATIVE;
			}
			
			mAttributeViews.get(i).setState(state);
			mAttributeViews.get(i).setReadOnly(true);
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


	private void displayErrorDialog(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder	.setMessage(message)
				.setTitle("Error")
				.setPositiveButton("Ok", null)
				.create().show();
	}
}
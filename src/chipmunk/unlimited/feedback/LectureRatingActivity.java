package chipmunk.unlimited.feedback;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import chipmunk.unlimited.feedback.AttributeRatingView.OnRatingChangeListener;

public class LectureRatingActivity extends Activity implements OnRatingChangeListener {
	/** 
	 * The keys through which values will be set through the Intent 
	 */
	public static final String PARAM_COURSE_NAME = "param_course_name";
	public static final String PARAM_LECTURER_NAME = "param_lecturer_name";
	public static final String PARAM_TIME = "param_time";
	public static final String PARAM_ROOM = "param_room";
	
	private List<AttributeRatingView> mAttributeViews;
	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_lecture_rating);
	    
	    createAttributeRatingViews();
	    setIntentParameters();
	    
	    
	    Button button = (Button)findViewById(R.id.rating_button_submit);
	    button.setOnClickListener(new OnClickListener() {
	    	@Override
	    	public void onClick(View v) {
	    		/* Submit yo */
	    	}
	    });
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
	
	/**
	 * Set attributes based on values from the creating Intent.
	 */
	private void setIntentParameters() {
		Intent intent = getIntent();
		
		String courseName = intent.getStringExtra(PARAM_COURSE_NAME);
		String lecturer = intent.getStringExtra(PARAM_LECTURER_NAME);
		String time = intent.getStringExtra(PARAM_TIME);
		String room = intent.getStringExtra(PARAM_ROOM);
		
		setLabelTexts(courseName, lecturer, time + ", " + room);
	}
	
	private void setLabelTexts(String courseName, String lecturerName, String timeAndPlace) {	
		TextView tvCourse = (TextView)findViewById(R.id.rating_text_view_course);
		TextView tvLecturer = (TextView)findViewById(R.id.rating_text_view_lecturer);
		TextView tvTimeroom = (TextView)findViewById(R.id.rating_text_view_time_room);
		
		tvCourse.setText(courseName);
		tvLecturer.setText(lecturerName);
		tvTimeroom.setText(timeAndPlace);
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

}

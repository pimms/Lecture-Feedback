package chipmunk.unlimited.feedback;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import chipmunk.unlimited.feedback.AttributeRatingView.OnRatingChangeListener;

public class LectureRatingActivity extends Activity implements OnRatingChangeListener {
	private List<AttributeRatingView> mAttributeViews;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_lecture_rating);
	    
	    createAttributeRatingViews();
	    
	    Button button = (Button)findViewById(R.id.rating_button_submit);
	    button.setOnClickListener(new OnClickListener() {
	    	@Override
	    	public void onClick(View v) {
	    		TimeEditParser parser = new TimeEditParser(TimeEditParser.CONTENT_TIMETABLE);
	    		
	    		List<SubscriptionItem> items = new ArrayList<SubscriptionItem>();
	    		items.add(new SubscriptionItem("161571.182", "12HBSPA"));
	    		
	    		TimeEditHTTP.getTimeTable(items, parser);
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

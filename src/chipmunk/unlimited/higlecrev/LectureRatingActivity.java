package chipmunk.unlimited.higlecrev;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class LectureRatingActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_lecture_rating);
	    
	    createAttributeRatingViews();
	}
	
	private void createAttributeRatingViews() {
		LinearLayout wrapper = (LinearLayout)findViewById(R.id.rating_attribute_wrapper);
		
		String[] attrs = new String[] { "Overall", "Relevance", "Progression", "something", "something" };
		
		for (int i=0; i<5; i++) {
			AttributeRatingView attribute = new AttributeRatingView(this);
			attribute.setAttribute(attrs[i]);
			
			wrapper.addView(attribute);
		}
	}

}

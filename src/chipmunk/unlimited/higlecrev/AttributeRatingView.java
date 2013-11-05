package chipmunk.unlimited.higlecrev;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @class AttributeRatingView
 * Allows the user to give a certain attribute of a
 * lecuture a positive or negative review.
 */
public class AttributeRatingView extends LinearLayout implements OnClickListener {
	private String mAttribute = "_default_ (none)";
	
	private TextView mAttributeName;
	private Button mButtonNegative;
	private Button mButtonPositive;
	
	
	public AttributeRatingView(Context context) {
		super(context);
		initialize();
	}
	
	public AttributeRatingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}
	
	public AttributeRatingView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initialize();
	}
	
	private void initialize() {
		LayoutInflater.from(getContext()).inflate(R.layout.lecture_rating_element, this);
		
		mAttributeName = (TextView)findViewById(R.id.rating_text_view_attribute);
		
		mButtonNegative = (Button)findViewById(R.id.rating_button_negative);
		mButtonPositive = (Button)findViewById(R.id.rating_button_positive);
		
		mButtonNegative.setOnClickListener(this);
		mButtonPositive.setOnClickListener(this);
	}
	
	
	public void setAttribute(String attribute) {
		mAttribute = attribute;
	}
	
	
	
	/**
	 * Register and respond to clicks on the positive / negative buttons
	 */
	@Override
	public void onClick(View view) {
		
	}
}

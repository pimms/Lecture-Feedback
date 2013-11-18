package chipmunk.unlimited.feedback.rating;

import chipmunk.unlimited.feedback.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @class AttributeView
 * Allows the user to give a certain attribute of a
 * lecuture a positive or negative review.
 */
public class AttributeView extends LinearLayout implements OnClickListener {
	/**
	 * @interface OnRatingChangeListener
	 * Callback interface for listening to changes in the Rating View.
	 */
	public interface OnRatingChangeListener {
		/**
		 * Called whenever the state of the Rating View changes.
		 * @param ratingView
		 * The view whose state changed.
		 * 
		 * @param state
		 * The new state of the Rating View. Can have two values:
		 * 	1.	AttributeView.STATE_POSITIVE
		 * 	2.	AttributeView.STATE_NEGATIVE.
		 */
		public void onRatingStateChange(AttributeView ratingView, int state);
	}
	
	public static final int STATE_UNDEFINED = 0;
	public static final int STATE_POSITIVE = 1;
	public static final int STATE_NEGATIVE = 2;
	
	private String mAttributeName = "_default_ (none)";
	private int mState = STATE_UNDEFINED;
	private boolean mReadOnly = false;
	
	private OnRatingChangeListener mCallback;
	
	private TextView mTextViewAttributeName;
	private AttributeButton mButtonNegative;
	private AttributeButton mButtonPositive;
	
	
	public AttributeView(Context context) {
		super(context);
		initialize();
	}
	
	public AttributeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}
	
	public AttributeView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initialize();
	}
	
	private void initialize() {
		LayoutInflater.from(getContext()).inflate(R.layout.attribute_rating_view, this);
		
		mTextViewAttributeName = (TextView)findViewById(R.id.rating_text_view_attribute);
		mButtonNegative = (AttributeButton)findViewById(R.id.rating_button_negative);
		mButtonPositive = (AttributeButton)findViewById(R.id.rating_button_positive);
		
		mButtonNegative.setOnClickListener(this);
		mButtonNegative.setType(AttributeButton.NEGATIVE);
		mButtonNegative.setState(AttributeButton.INACTIVE);
		
		mButtonPositive.setOnClickListener(this);
		mButtonPositive.setType(AttributeButton.POSITIVE);
		mButtonPositive.setState(AttributeButton.INACTIVE);
	}
	
	
	public void setOnRatingChangeListener(OnRatingChangeListener callback) {
		mCallback = callback;
	}
	
	public void setAttributeName(String attributeName) {
		mAttributeName = attributeName;
		mTextViewAttributeName.setText(mAttributeName);
	}
	
	public int getState() {
		return mState;
	}
	
	public void setState(int state) {
		if (state == mState) {
			return;
		}
		
		AttributeButton buttonActive;
		AttributeButton buttonInactive;
		
		if (state == STATE_POSITIVE) {
			buttonActive = mButtonPositive;
			buttonInactive = mButtonNegative;
		}  else if (state == STATE_NEGATIVE) {
			buttonActive = mButtonNegative;
			buttonInactive = mButtonPositive;
		} else {
			return;
		}
		
		// Update the buttons
		buttonActive.setState(AttributeButton.ACTIVE);
		buttonInactive.setState(AttributeButton.INACTIVE);
		
		// Notify the callback
		mState = state;
		if (mCallback != null) {
			mCallback.onRatingStateChange(this, mState);
		}
	}
	
	public void setReadOnly(boolean readOnly) {
		mReadOnly = readOnly;
	}
	
	
	/**
	 * Register and respond to clicks on the positive / negative buttons
	 */
	@Override
	public void onClick(View view) {
		if (mReadOnly == true) {
			return;
		}
		
		int state = STATE_UNDEFINED;
		
		if (view == mButtonNegative) {
			state = STATE_NEGATIVE;
		} else if (view == mButtonPositive) {
			state = STATE_POSITIVE;
		} else {
			return;
		}
											
		setState(state);
	}
}

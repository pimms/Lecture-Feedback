package chipmunk.unlimited.feedback.rating;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageButton;
import chipmunk.unlimited.feedback.R;

public class AttributeButton extends ImageButton {
	private static final String TAG = "AttributeButton";
	
	/* Does the button affect positively or negatively? */
	public static final int NEGATIVE = 1;
	public static final int POSITIVE = 2;
	
	/* Is the button active or not? */
	public static final int ACTIVE   = 1;
	public static final int INACTIVE = 2;
	
	
	private int mType = NEGATIVE;
	private int mState = INACTIVE;
	
	
	public AttributeButton(Context context) {
		super(context);
	}
	
	public AttributeButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public AttributeButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	
	public void setType(int type) {
		mType = type;
		updateBackgroundDrawable();
	}
	
	public void setState(int state) {
		mState = state;
		updateBackgroundDrawable();
	}
	
	
	public int getType() {
		return mType;
	}
	
	public int getState() {
		return mState;
	}
	
	
	/**
	 * Set the background of the button based on the TYPE and STATE 
	 * of the button.
	 */
	private void updateBackgroundDrawable() {
		int drawableId = 0;
		
		if (mType == NEGATIVE) {
			if (mState == ACTIVE) {
				drawableId = R.drawable.attribute_rating_button_negative_active;
			} else if (mState == INACTIVE) {
				drawableId = R.drawable.attribute_rating_button_negative_inactive;
			}
		} else if (mType == POSITIVE) {
			if (mState == ACTIVE) {
				drawableId = R.drawable.attribute_rating_button_positive_active;
			} else if (mState == INACTIVE) {
				drawableId = R.drawable.attribute_rating_button_positive_inactive;
			}
		}
		
		if (drawableId != 0) {
			Drawable drawable = getContext().getResources().getDrawable(drawableId);
			setBackgroundDrawable(drawable);
		} else {
			Log.e(TAG, "Invalid combination of STATE("+mState+") and TYPE("+mType+")");
		}
	}
}























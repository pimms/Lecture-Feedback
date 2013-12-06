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
	public static final int EFFECT_NEGATIVE = 1;
	public static final int EFFECT_POSITIVE = 2;
	
	/* Is the button active or not? */
	public static final int STATE_ACTIVE = 1;
	public static final int STATE_INACTIVE = 2;
	
	
	private int mType = EFFECT_NEGATIVE;
	private int mState = STATE_INACTIVE;
	
	
	public AttributeButton(Context context) {
		super(context);
	}
	
	public AttributeButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public AttributeButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

    /**
     * Change the effect the button has on the attribute.
     *
     * @param effectType
     * The new effect type. Must be either EFFECT_POSITIVE
     * or EFFECT_NEGATIVE.
     */
	public void setEffectType(int effectType) {
		mType = effectType;
		updateBackgroundDrawable();
	}
    /**
     * Change the state of the button.
     *
     * @param state
     * The new state. Must be either STATE_ACTIVE or
     * STATE_INACTIVE.
     */
	public void setState(int state) {
		mState = state;
		updateBackgroundDrawable();
	}
	
	
	public int getEffectType() {
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
		
		if (mType == EFFECT_NEGATIVE) {
			if (mState == STATE_ACTIVE) {
				drawableId = R.drawable.attribute_rating_button_negative_active;
			} else if (mState == STATE_INACTIVE) {
				drawableId = R.drawable.attribute_rating_button_negative_inactive;
			}
		} else if (mType == EFFECT_POSITIVE) {
			if (mState == STATE_ACTIVE) {
				drawableId = R.drawable.attribute_rating_button_positive_active;
			} else if (mState == STATE_INACTIVE) {
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























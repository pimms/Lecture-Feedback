package chipmunk.unlimited.feedback.rating;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import chipmunk.unlimited.feedback.R;

/**
 *
 */
public class LectureRatingView extends LinearLayout
                        implements  AttributeView.OnRatingChangeListener,
                                    View.OnClickListener {
    private static final String[] LECTURE_ATTRIBUTES = new String[] {
        "Overall", "Clarity", "Progression", "Engagement", "Material coverage"
    };
    private static final String[] LECTURE_ATTRIBUTE_DESC = new String[] {
        "How would you rate the lecture in total?",
        "Was the material presented clearly and unambiguously?",
        "Was the lecture properly paced?",
        "Did the lecturers engagement spread?",
        "Was the presented material covered thoroughly enough?"
    };


    public interface RatingListener {
        /**
         * Called when the Submit-button is pressed
         * and all the values are valid.
         */
        public void onRatingSubmit();
    }


    private ArrayList<AttributeView> mAttributeViews;
    private RatingListener mCallback;


    public LectureRatingView(Context context) {
        super(context);
        initialize();
    }

    public LectureRatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public LectureRatingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }


    private void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.lecture_rating_view, this);
        createAttributeRatingViews();

        Button submit = (Button)findViewById(R.id.rating_button_submit);
        submit.setOnClickListener(this);
    }

    private void createAttributeRatingViews() {
        LinearLayout wrapper = (LinearLayout)findViewById(R.id.rating_attribute_wrapper);
        mAttributeViews = new ArrayList<AttributeView>();

        for (int i=0; i<5; i++) {
            AttributeView attributeView = new AttributeView(getContext());
            attributeView.setAttributeName(LECTURE_ATTRIBUTES[i]);
            attributeView.setOnRatingChangeListener(this);

            mAttributeViews.add(attributeView);
            wrapper.addView(attributeView);
        }
    }


    public void setRatingListener(RatingListener callback) {
        mCallback = callback;
    }

    public void setAttributeViewState(int index, int state) {
        mAttributeViews.get(index).setState(state);
        mAttributeViews.get(index).setReadOnly(true);
    }

    public void setCourseText(String courseText) {
        TextView tv = (TextView)findViewById(R.id.rating_text_view_course);
        tv.setText(courseText);
    }

    public void setLecturerText(String lecturerText) {
        TextView tv = (TextView)findViewById(R.id.rating_text_view_lecturer);
        tv.setText(lecturerText);
    }

    public void setRoomAndTimeText(String rntText) {
        TextView tv = (TextView)findViewById(R.id.rating_text_view_time_room);
        tv.setText(rntText);
    }


    public boolean[] getRatingArray() {
        boolean[] ratings = new boolean[mAttributeViews.size()];

        for (int i=0; i<mAttributeViews.size(); i++) {
            ratings[i] = mAttributeViews.get(i).getState() == AttributeView.STATE_POSITIVE;
        }

        return ratings;
    }

    public String getComment() {
        EditText editText = (EditText)findViewById(R.id.rating_edit_text_comments);
        return editText.getText().toString();
    }

    public int getAttributeCount() {
        return mAttributeViews.size();
    }


    @Override
    public void onRatingStateChange(AttributeView ratingView, int state) {
		/* Let the overall rating (index 0) determine all undefined attributes */
        if (ratingView == mAttributeViews.get(0)) {
            for (int i=1; i<mAttributeViews.size(); i++) {
                AttributeView attributeView = mAttributeViews.get(i);
                if (attributeView.getState() == AttributeView.STATE_UNDEFINED) {
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
                if (mAttributeViews.get(i).getState() == AttributeView.STATE_UNDEFINED) {
                    return;
                }
            }

            mCallback.onRatingSubmit();
        }
    }
}

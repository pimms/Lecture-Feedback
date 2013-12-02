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
    public interface RatingListener {
        /**
         * Called when the Submit-button is pressed
         * and all the values are valid.
         */
        public void onRatingSubmit();
    }


    private ArrayList<AttributeView> mAttributeViews;
    private RatingListener mCallback;
    private Context mContext;


    public LectureRatingView(Context context) {
        super(context);

        mContext = context;
        initialize();
    }

    public LectureRatingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        initialize();
    }

    public LectureRatingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mContext = context;
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

        for (int i=0; i<getAttributeCount(); i++) {
            AttributeView attributeView = new AttributeView(getContext());
            attributeView.setAttributeName(getAttributeName(i));
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

    public void setDateAndTimeText(String datetimeText) {
        TextView tv = (TextView)findViewById(R.id.rating_text_view_datetime);
        tv.setText(datetimeText);
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
        return 5;
    }

    public String getAttributeName(int i) {
        int id = -1;

        switch (i) {
            case 0: id = R.string.rating_attr_overall;      break;
            case 1: id = R.string.rating_attr_clarity;      break;
            case 2: id = R.string.rating_attr_progression;  break;
            case 3: id = R.string.rating_attr_engagement;   break;
            case 4: id = R.string.rating_attr_coverage;     break;
            default:return "UNKOWN";
        }

        return mContext.getResources().getString(id);
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

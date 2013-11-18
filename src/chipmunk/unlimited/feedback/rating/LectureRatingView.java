package chipmunk.unlimited.feedback.rating;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import chipmunk.unlimited.feedback.R;

/**
 *
 */
public class LectureRatingView extends LinearLayout {
    public LectureRatingView(Context context) {
        super(context);
    }

    public LectureRatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LectureRatingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void initialize() {
        LayoutInflater.from(getContext()).inflate(R.layout.lecture_rating_view, this);
    }
}

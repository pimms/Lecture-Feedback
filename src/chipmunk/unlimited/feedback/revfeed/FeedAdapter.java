package chipmunk.unlimited.feedback.revfeed;

import java.util.List;

import chipmunk.unlimited.feedback.LectureReviewItem;
import chipmunk.unlimited.feedback.R;
import chipmunk.unlimited.feedback.R.id;
import chipmunk.unlimited.feedback.R.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @class FeedAdapter
 * Adapter class preparing LectureReviewItem objects
 * for display in a ListView.
 */
public class FeedAdapter extends BaseAdapter {
	private static LayoutInflater sInflater;
	
	private List<LectureReviewItem> mReviewItems;
	private int mFeedState = Feed.STATE_DEFAULT;

	
	public FeedAdapter(Context context) {
		sInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}


    public void setFeedState(int state) {
        mFeedState = state;
    }

	
	public void setReviewItems(List<LectureReviewItem> reviewItems) {
		mReviewItems = reviewItems;
        notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		if (mReviewItems != null) {
			return mReviewItems.size();
		}
		
		return 0;
	}
	
	@Override
	public Object getItem(int position) {
		if (mReviewItems != null && position < mReviewItems.size()) {
			return mReviewItems.get(position);
		}
		
		return null;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		
		if (vi == null) {
			vi = sInflater.inflate(R.layout.feed_review_item, null);
		}
		
		TextView tvCourse = (TextView)vi.findViewById(R.id.feed_item_text_view_course);
		TextView tvLecturer = (TextView)vi.findViewById(R.id.feed_item_text_view_lecturer);
		TextView tvPositive = (TextView)vi.findViewById(R.id.feed_item_text_view_positive);
		TextView tvNegative = (TextView)vi.findViewById(R.id.feed_item_text_view_negative);
		TextView tvComment = (TextView)vi.findViewById(R.id.feed_item_text_view_comment);
		
		LectureReviewItem item = (LectureReviewItem)getItem(position);
		tvCourse.setText(item.getCourseName());
		tvLecturer.setText(item.getLecturer() + ", " + item.getRoom());
		tvComment.setText(item.getComment());
		
		boolean[] ratings = item.getRatings();
		int negative = 0;
		int positive = 0;
		for (int i=0; i<5; i++) {
			if (ratings[i]) {
				positive++;
			} else {
				negative++;
			}
		}
		tvPositive.setText("" + positive);
		tvNegative.setText("" + negative);

        // Handle feed_item_text_view_date and feed_item_text_view_time
        // based on the current state.
        handleDateTimeTextViews(vi, item);
		
		return vi;
	}

    /**
     * Handle the content of the TextViews containing the date
     * and time based on the current FeedState.
     */
    private void handleDateTimeTextViews(View listItem, LectureReviewItem lecture) {
        TextView tvDate = (TextView)listItem.findViewById(R.id.feed_item_text_view_date);
        TextView tvTime = (TextView)listItem.findViewById(R.id.feed_item_text_view_time);

        if (mFeedState == Feed.STATE_DEFAULT) {
            // Set the text usual
            tvDate.setText(lecture.getPrettyDateString());
            tvTime.setText(lecture.getTimeString());
        } else if (mFeedState == Feed.STATE_COURSE) {
            tvDate.setText(null);
            tvTime.setText(null);
        }
    }

}

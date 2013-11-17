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
	
	
	public FeedAdapter(Context context) {
		sInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	
	public void setReviewItems(List<LectureReviewItem> reviewItems) {
		mReviewItems = reviewItems;
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
		TextView tvTime = (TextView)vi.findViewById(R.id.feed_item_text_view_time);
		TextView tvLecturer = (TextView)vi.findViewById(R.id.feed_item_text_view_lecturer);
		TextView tvAttributes = (TextView)vi.findViewById(R.id.feed_item_text_view_attributes);
		TextView tvComment = (TextView)vi.findViewById(R.id.feed_item_text_view_comment);
		
		LectureReviewItem item = (LectureReviewItem)getItem(position);
		tvCourse.setText(item.getCourseName());
		tvTime.setText(item.getTimeString());
		tvLecturer.setText(item.getLecturer());
		tvComment.setText(item.getComment());
		
		boolean[] ratings = item.getRatings();
		String ratingText = "[";
		for (int i=0; i<5; i++) {
			ratingText += "   ";
			ratingText += (ratings[i]) ? "+" : "-";
		}
		ratingText += "    ]";
		tvAttributes.setText(ratingText);
		
		
		return vi;
	}
}

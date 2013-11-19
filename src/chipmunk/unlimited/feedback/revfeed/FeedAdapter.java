package chipmunk.unlimited.feedback.revfeed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import chipmunk.unlimited.feedback.LectureReviewItem;
import chipmunk.unlimited.feedback.R;

import android.content.Context;
import android.util.Log;
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
    private static final int LIST_ITEM_TYPE_LECTURE_SEPARATOR = 1;
    private static final int LIST_ITEM_TYPE_REVIEW = 2;
	
	private List<LectureReviewItem> mReviewItems;
	private int mFeedState = Feed.STATE_DEFAULT;

    /**
     * To differentiate between Lecture-list items and
     * separator-items, this list holds LIST_ITEM_TYPE_XX
     * in the order in which they appear.
     */
    private List<Integer> mListItemTypes;

	
	public FeedAdapter(Context context) {
		sInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListItemTypes = new ArrayList<Integer>();
	}


    public void setFeedState(int state) {
        mFeedState = state;
    }

	public void setReviewItems(List<LectureReviewItem> reviewItems) {
		mReviewItems = reviewItems;

        defineItemOrder();
        notifyDataSetChanged();
	}


    /**
     * Define in which order Review-items and separator-items
     * will appear in the final list.
     */
    private void defineItemOrder() {
        // Clear the previous list
        mListItemTypes.clear();

        if (mFeedState == Feed.STATE_DEFAULT) {
            defineItemOrderDefault();
        } else if (mFeedState == Feed.STATE_COURSE) {
            defineItemOrderLectureSeparator();
        }
    }

    private void defineItemOrderDefault() {
        // Don't add any separators
        for (int i=0; i<mReviewItems.size(); i++) {
            mListItemTypes.add(LIST_ITEM_TYPE_REVIEW);
        }
    }

    private void defineItemOrderLectureSeparator() {
        if (mReviewItems.size() == 0) {
            return;
        }

        // Sort the items
        Collections.sort(mReviewItems);

        // Add separators between each lecture
        mListItemTypes.add(LIST_ITEM_TYPE_LECTURE_SEPARATOR);
        mListItemTypes.add(LIST_ITEM_TYPE_REVIEW);
        LectureReviewItem prev = mReviewItems.get(0);
        LectureReviewItem cur = prev;

        int numSep = 1;

        for (int i=1; i<mReviewItems.size(); i++) {
            prev = cur;
            cur = mReviewItems.get(i);

            if (!cur.getLectureHash().equals(prev.getLectureHash())) {
                mListItemTypes.add(LIST_ITEM_TYPE_LECTURE_SEPARATOR);
                numSep++;
            }

            mListItemTypes.add(LIST_ITEM_TYPE_REVIEW);
        }
    }


    private int getItemType(int position) {
        return mListItemTypes.get(position);
    }

    private LectureReviewItem getReviewItem(int position) {
        int reviews = 0;

        if (mListItemTypes.get(position) == LIST_ITEM_TYPE_REVIEW) {
            for (int i=0; i<position; i++) {
                if (mListItemTypes.get(i) == LIST_ITEM_TYPE_REVIEW) {
                    reviews++;
                }
            }
            try {
                return mReviewItems.get(reviews);
            } catch (Exception ex) {
                Log.d("EXCP", ex.getMessage());
                ex.printStackTrace();
                Log.d("LOLWAT", "lolwat");
                return null;
            }
        } else {
            return null;
        }
    }
    /**
     * Lecture-review separators are created based on the
     * FOLLOWING lecture. This method returns the review item
     * displayed in list item (position+1).
     *
     * @return
     * The following item if (position == LECTURE_SEPARATOR) and
     * (position+1 == REVIEW). null otherwise.
     */
    private LectureReviewItem getLectureSeparatorItem(int position) {
        if (getItemType(position) == LIST_ITEM_TYPE_LECTURE_SEPARATOR &&
                getItemType(position+1) == LIST_ITEM_TYPE_REVIEW) {
            return getReviewItem(position+1);
        }

        return null;
    }


	@Override
	public int getCount() {
		return mListItemTypes.size();
	}

	@Override
	public Object getItem(int position) {
		if (mListItemTypes.get(position) == LIST_ITEM_TYPE_REVIEW) {
            return getReviewItem(position);
        }

        return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemType(position);

        if (type == LIST_ITEM_TYPE_REVIEW) {
            return getReviewView(position, convertView);
        } else if (type == LIST_ITEM_TYPE_LECTURE_SEPARATOR) {
            return getLectureSeparatorView(position, convertView);
        }

        /* Exceptions, ho! */
        return null;
	}

    public View getLectureSeparatorView(int position, View convertView) {
        View vi = convertView;

        if (vi == null || (vi != null && vi.getId() != R.layout.list_item_lecture_separator)) {
            vi = sInflater.inflate(R.layout.list_item_lecture_separator, null);
        }

        TextView tvDate = (TextView)vi.findViewById(R.id.lecture_separator_text_view_date);
        TextView tvTime = (TextView)vi.findViewById(R.id.lecture_separator_text_view_time);
        TextView tvLecturer = (TextView)vi.findViewById(R.id.lecture_separator_text_view_lecturer);

        LectureReviewItem reviewItem = getLectureSeparatorItem(position);
        tvDate.setText(reviewItem.getPrettyDateString());
        tvTime.setText(reviewItem.getTimeString());
        tvLecturer.setText(reviewItem.getLecturer());

        return vi;
    }

    public View getReviewView(int position, View convertView) {
        View vi = convertView;

        if (vi == null || (vi != null && vi.getId() != R.layout.feed_review_item)) {
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

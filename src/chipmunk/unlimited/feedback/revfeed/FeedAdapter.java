package chipmunk.unlimited.feedback.revfeed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import chipmunk.unlimited.feedback.database.SubscriptionDatabase;
import chipmunk.unlimited.feedback.webapi.WebAPI.*;
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
 *
 * The feed adapter is tightly knit with the Feed-class, and
 * alters it's behaviour based on the Feed's state.
 *
 * Feed.STATE_DEFAULT:
 * Feed.STATE_SINGLE_LECTURE:
 *      Only reviews will be displayed.
 *
 * Feed.STATE_SINGLE_COURSE:
 *      ( CURRENTLY NOT USED )
 *      Separators will be placed between the separate
 *      lectures.
 */
public class FeedAdapter extends BaseAdapter  {
    private static final String TAG = "FeedAdapter";

	private static LayoutInflater sInflater;
    private static final int LIST_ITEM_TYPE_UNDEFINED = 0;
    private static final int LIST_ITEM_TYPE_LECTURE_SEPARATOR = 1;
    private static final int LIST_ITEM_TYPE_REVIEW = 2;
	
	private List<LectureReviewItem> mReviewItems;
	private int mFeedState = Feed.STATE_DEFAULT;
    private Context mContext;

    private boolean mTutorial;


    /**
     * To differentiate between Lecture-list items and
     * separator-items, this list holds LIST_ITEM_TYPE_XX
     * in the order in which they appear.
     */
    private List<Integer> mListItemTypes;

	
	public FeedAdapter(Context context) {
        mContext = context;

		sInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListItemTypes = new ArrayList<Integer>();
	}


    public void setFeedState(int state) {
        mFeedState = state;
    }

	public void setReviewItems(List<LectureReviewItem> reviewItems) {
		mReviewItems = reviewItems;

        if (mReviewItems != null && mReviewItems.size() != 0) {
            mTutorial = false;
            defineItemOrder();
            notifyDataSetChanged();
        } else {
            mTutorial = true;
        }

        notifyDataSetChanged();
	}

    public void appendReviewItems(List<LectureReviewItem> appendItems) {
        if (mReviewItems == null) {
            setReviewItems(appendItems);
        } else if (appendItems != null) {
            mReviewItems.addAll(appendItems);
            defineItemOrder();
            notifyDataSetChanged();
        }
    }


    /**
     * Define in which order Review-items and separator-items
     * will appear in the final list.
     */
    private void defineItemOrder() {
        // Clear the previous list
        mListItemTypes.clear();

        if (mFeedState == Feed.STATE_COURSE ||
            mFeedState == Feed.STATE_LECTURE) {
            defineItemOrderWithoutSeparators();
        } else if (mFeedState == Feed.STATE_DEFAULT) {
            defineItemOrderWithSeparator();
        }
    }
    /**
     * Only the reviews themselves will be displayed. The order
     * is the exact same as they arrived in from the web API.
     */
    private void defineItemOrderWithoutSeparators() {
        // Don't add any separators
        for (int i=0; i<mReviewItems.size(); i++) {
            mListItemTypes.add(LIST_ITEM_TYPE_REVIEW);
        }
    }
    /**
     * Reviews not reviewed on the same day will be separated
     * by a separator.
     */
    private void defineItemOrderWithSeparator() {
        if (mReviewItems.size() == 0) {
            return;
        }

        // Add separators between each lecture
        mListItemTypes.add(LIST_ITEM_TYPE_LECTURE_SEPARATOR);
        mListItemTypes.add(LIST_ITEM_TYPE_REVIEW);
        LectureReviewItem prev = mReviewItems.get(0);
        LectureReviewItem cur = prev;

        int numSep = 1;

        for (int i=1; i<mReviewItems.size(); i++) {
            prev = cur;
            cur = mReviewItems.get(i);

            if (!cur.reviewedSameDate(prev)) {
                mListItemTypes.add(LIST_ITEM_TYPE_LECTURE_SEPARATOR);
                numSep++;
            }

            mListItemTypes.add(LIST_ITEM_TYPE_REVIEW);
        }
    }


    public int getFeedState() {
        return mFeedState;
    }

    private int getItemType(int position) {
        if (!mTutorial) {
            return mListItemTypes.get(position);
        }

        return LIST_ITEM_TYPE_UNDEFINED;
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
        if (!mTutorial) {
		    return mListItemTypes.size();
        }

        return 1;
	}
	@Override
	public Object getItem(int position) {
		if (!mTutorial && mListItemTypes.get(position) == LIST_ITEM_TYPE_REVIEW) {
            return getReviewItem(position);
        }

        return null;
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
    /**
     * @return
     * The ID of the last LectureReviewItem in the list.
     * A negative value is returned if the list is empty.
     */
    public int getLastReviewID() {
        if (mReviewItems != null && mReviewItems.size() != 0) {
            return mReviewItems.get(mReviewItems.size()-1).getId();
        }

        return -1;
    }


    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }
    @Override
    public boolean isEnabled(int position) {
        if (!mTutorial) {
            return mListItemTypes.get(position) != LIST_ITEM_TYPE_LECTURE_SEPARATOR;
        }

        return false;
    }


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        if (mTutorial) {
            return getTutorialView(convertView);
        }

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
        if (vi == null || vi.getId() != R.layout.list_item_separator) {
            vi = LayoutInflater.from(mContext).inflate(R.layout.list_item_separator, null);
        }

        TextView tv = (TextView)vi.findViewById(R.id.separator_text_view_title);

        LectureReviewItem review = getLectureSeparatorItem(position);
        if (review != null) {
            String prefix = mContext.getResources().getString(R.string.frag_feed_separator_prefix);
            String relative = review.getRelativeReviewDateString(mContext);

            tv.setText(prefix + ' ' + relative);
        }

        return vi;
    }

    public View getReviewView(int position, View convertView) {
        View vi = convertView;

        if (vi == null || vi.getId() != R.layout.list_item_review) {
            vi = sInflater.inflate(R.layout.list_item_review, null);
        }

        TextView tvCourse   = (TextView)vi.findViewById(R.id.feed_item_text_view_course);
        TextView tvLecturer = (TextView)vi.findViewById(R.id.feed_item_text_view_lecturer);
        TextView tvPositive = (TextView)vi.findViewById(R.id.simple_thumb_text_view_positive);
        TextView tvNegative = (TextView)vi.findViewById(R.id.simple_thumb_text_view_negative);
        TextView tvComment  = (TextView)vi.findViewById(R.id.feed_item_text_view_comment);
        TextView tvClones   = (TextView)vi.findViewById(R.id.feed_item_text_view_clones);

        LectureReviewItem item = (LectureReviewItem)getItem(position);
        tvCourse.setText(item.getCourseName());
        tvLecturer.setText(item.getLecturer() + ", " + item.getRoom());
        tvComment.setText(item.getComment());

        if (item.getCloneCount() != 0) {
            String cloneText;
            if (item.getCloneCount() == 1) {
                cloneText = mContext.getResources().getString(R.string.clone_count_singular);
            } else {
                cloneText = mContext.getResources().getString(R.string.clone_count_plural);
            }
            tvClones.setText("" + item.getCloneCount() + " " + cloneText);
        } else {
            tvClones.setVisibility(View.GONE);
        }

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
            tvDate.setText(lecture.getPrettyDateString(mContext));
            tvTime.setText(lecture.getTimeString());
        } else {
            tvDate.setText(null);
            tvTime.setText(null);
        }
    }

    private View getTutorialView(View convertView) {
        if (convertView == null || convertView.getId() != R.layout.tutorial) {
            convertView = sInflater.inflate(R.layout.tutorial, null);
        }

        TextView tvTitle = (TextView)convertView.findViewById(R.id.tutorial_text_view_title);
        TextView tvDesc  = (TextView)convertView.findViewById(R.id.tutorial_text_view_desc);
        SubscriptionDatabase db = new SubscriptionDatabase(mContext);

        if (db.getSubscriptionList().size() != 0) {
            tvTitle.setText(mContext.getResources().getString(
                    R.string.frag_feed_tutorial_title_no_items));
            tvDesc.setText(mContext.getResources().getString(
                    R.string.frag_feed_tutorial_desc_no_items));
        } else {
            tvTitle.setText(mContext.getResources().getString(
                    R.string.frag_feed_tutorial_title_no_subs));
            tvDesc.setText(mContext.getResources().getString(
                    R.string.frag_feed_tutorial_desc_no_subs));
        }

        return convertView;
    }
}

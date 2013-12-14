package chipmunk.unlimited.feedback.today;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import chipmunk.unlimited.feedback.LectureItem;
import chipmunk.unlimited.feedback.R;
import chipmunk.unlimited.feedback.database.ReviewedLectureDatabase;
import chipmunk.unlimited.feedback.database.SubscriptionDatabase;
import chipmunk.unlimited.feedback.webapi.HttpClient;

/**
 * @class DailyLectureAdapter
 * Displays LectureItem instances. While the word "Daily" 
 * and "Today" is thrown a lot when it comes to this 
 * aspect of the application, the displayed lectures will
 * have occurred in the past 24 hours. 
 */
public class DailyLectureAdapter extends BaseAdapter {
	private static final String TAG = "DailyLectureAdapter";

	private static final int LIST_ITEM_TYPE_LECTURE = 1;
	private static final int LIST_ITEM_TYPE_SEPARATOR = 2;

    private boolean mTutorial;

	
	/**
	 * To differentiate between Lecture-list items and 
	 * separator-items, this list holds LIST_ITEM_TYPE_XX
	 * in the order in which they appear.
	 */
	private List<Integer> mListItemTypes;

	/**
	 * The lectures to be displayed in the view.
	 */
	private List<LectureItem> mLectureItems;
	
	/**
	 * The strings to be included in the separating list views. 
	 */
	private List<String> mSeparatorStrings;
	
	private LayoutInflater mInflater;
	private Context mContext;
	
	
	public DailyLectureAdapter(Context context) {
		mContext = context;
		mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		mListItemTypes = new ArrayList<Integer>();
		mSeparatorStrings = new ArrayList<String>();
	}
	
	public void setLectureItems(List<LectureItem> items) {
		mLectureItems = items;

        if (mLectureItems == null || mLectureItems.size() == 0) {
            mTutorial = true;
        } else {
            mTutorial = false;
            stripIrrelevantLectures();
            defineItemOrder();
        }
	}
	
	
	@Override
	public int getCount() {
        if (!mTutorial) {
		    return mListItemTypes.size();
        } else {
            return 1;
        }
	}
	@Override
	public Object getItem(int position) {
        if (!mTutorial) {
		    return getLectureForListItem(position);
        }

        return null;
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}
	@Override
	public boolean isEnabled(int position) {
        if (!mTutorial) {
		    return mListItemTypes.get(position) == LIST_ITEM_TYPE_LECTURE
                    && HttpClient.isInternetAvailable(mContext);
        }

        return false;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        if (mTutorial) {
            return getTutorialView(convertView);
        }

		LectureItem item = getLectureForListItem(position);
		
		if (item != null) {
			convertView = getLectureListItem(convertView, item);
		} else {
			convertView = getSeparatorListItem(convertView, position);
		}
		
		return convertView;
	}
	
	private View getLectureListItem(View convertView, LectureItem item) {
		View vi = convertView;
		if (vi == null || 
			(vi != null && vi.getId() != R.layout.today_list_item_lecture))
		{
			vi = mInflater.inflate(R.layout.today_list_item_lecture, null);
		}
		
		TextView tvCourse = (TextView)vi.findViewById(R.id.today_text_view_course_name);
		TextView tvLecturer = (TextView)vi.findViewById(R.id.today_text_view_lecturer);
		TextView tvTime = (TextView)vi.findViewById(R.id.today_text_view_time);
		
		tvCourse.setText(item.getCourseName());
		tvLecturer.setText(item.getLecturer());
		tvTime.setText(item.getTimeString());
		
		/* Change the background slightly if the lecture has been reviewed */
		ReviewedLectureDatabase db = new ReviewedLectureDatabase(mContext);
		if (db.hasUserReviewed(item)) {
			vi.setBackgroundResource(R.drawable.today_item_reviewed);
		} else {
			vi.setBackgroundResource(0);
		}
		
		return vi;
	}
	
	private View getSeparatorListItem(View convertView, int position) {
		View vi = convertView;
		if (vi == null || (vi != null && vi.getId() != R.layout.list_item_separator)) {
			vi = mInflater.inflate(R.layout.list_item_separator, null);
		}
		
		TextView title = (TextView)vi.findViewById(R.id.separator_text_view_title);
		title.setText(getTitleForListItemSeparator(position));
		
		return vi;
	}

    private View getTutorialView(View convertView) {
        if (convertView == null || convertView.getId() != R.layout.tutorial) {
            convertView = mInflater.inflate(R.layout.tutorial, null);
        }

        TextView tvTitle = (TextView)convertView.findViewById(R.id.tutorial_text_view_title);
        TextView tvDesc  = (TextView)convertView.findViewById(R.id.tutorial_text_view_desc);
        SubscriptionDatabase db = new SubscriptionDatabase(mContext);

        if (!HttpClient.isInternetAvailable(mContext)) {
            tvTitle.setText(mContext.getString(R.string.no_internet_tutorial_title));
            tvDesc.setText(mContext.getString(R.string.no_internet_tutorial_desc));
        } else if (db.getSubscriptionList().size() != 0) {
            tvTitle.setText(mContext.getString(R.string.frag_today_tutorial_title_no_lectures));
            tvDesc.setText(mContext.getString(R.string.frag_today_tutorial_desc_no_lectures));
        } else {
            tvTitle.setText(mContext.getString(R.string.frag_today_tutorial_title_no_subs));
            tvDesc.setText(mContext.getString(R.string.frag_today_tutorial_desc_no_subs));
        }

        return convertView;
    }

	
	/**
	 * Remove all irrelevant lectures from the data set.
	 * If a lecture cannot be voted for, it is counted as
     * irrelevant.
	 */
	private void stripIrrelevantLectures() {
		for (int i=0; i<mLectureItems.size(); i++) {
			if (!mLectureItems.get(i).canReviewLecture()) {
				mLectureItems.remove(i--);
			}
		}
	}
	/**
	 * Insert separators into mItemTypes when appropriate.
	 */
	private void defineItemOrder() {
		mListItemTypes.clear();
		mSeparatorStrings.clear();
		
		if (mLectureItems == null || mLectureItems.size() == 0) {
			return;
		}
		
		Date firstDate = mLectureItems.get(0).getDate();
		mListItemTypes.add(LIST_ITEM_TYPE_LECTURE);
		
		Date prevDate = firstDate;
		Date curDate = firstDate;
		
		for (int i=1; i<mLectureItems.size(); i++) {
			curDate = mLectureItems.get(i).getDate();
			if (!curDate.equals(prevDate)) {
				mListItemTypes.add(LIST_ITEM_TYPE_SEPARATOR);
				mSeparatorStrings.add(getDateString(curDate));
			}
			
			prevDate = curDate;
			mListItemTypes.add(LIST_ITEM_TYPE_LECTURE);
		}
		
		if (!firstDate.equals(curDate)) {
			mListItemTypes.add(0, LIST_ITEM_TYPE_SEPARATOR);
			mSeparatorStrings.add(0, getDateString(firstDate));
		}
	}
	
	
	/**
	 * Get a suitable title for the date
	 *
	 * @return
	 * "Mon, 01 Jan" if date is more than 2 days old, otherwise
	 * "today" or "yesterday".
	 */
	private String getDateString(Date someDate) {
		Calendar compare = Calendar.getInstance();
		
		Calendar date = Calendar.getInstance();
		date.setTime(someDate);
		
		// Compare against today
		if (equalDates(compare, date)) {
			return mContext.getResources().getString(R.string.today);
		}
		
		// Compare against yesterday
		compare.add(Calendar.DAY_OF_MONTH, -1);
		if (equalDates(compare, date)) {
			return mContext.getResources().getString(R.string.yesterday);
		}
		
		// Return the date on proper format
        String strFormat = mContext.getResources().getString(R.string.format_date_today_sep);
		SimpleDateFormat format = new SimpleDateFormat(strFormat, Locale.getDefault());
		return format.format(date.getTime());
	}
	/**
	 * @return
	 * Whether or not the DATES of the two Date-objects are equal.
	 */
	private boolean equalDates(Calendar d1, Calendar d2) {
		return (
			d1.get(Calendar.YEAR) == d2.get(Calendar.YEAR) &&
			d1.get(Calendar.MONTH)== d2.get(Calendar.MONTH) &&
			d1.get(Calendar.DAY_OF_MONTH) == d2.get(Calendar.DAY_OF_MONTH)
		);
	}
	
	
	/**
	 * Get the listItemIndex-th LectureItem when also counting separator
	 * list items. 
	 * 
	 * @param listItemIndex
	 * The index of the list view, starting from the top.
	 * 
	 * @return
	 * NULL if the index-th list item is a separator, or
	 * mLectureItems[index-x] otherwise.
	 */
	private LectureItem getLectureForListItem(int listItemIndex) {
		int lectures = 0;
		
		if (mListItemTypes.get(listItemIndex) == LIST_ITEM_TYPE_LECTURE) {
			for (int i=0; i<listItemIndex; i++) {
				if (mListItemTypes.get(i) == LIST_ITEM_TYPE_LECTURE) {
					lectures++;
				}
			}
			return mLectureItems.get(lectures);
		} else {
			return null;
		}
	}
	/**
	 * Get the listItemIndex-th separator title.
	 * 
	 * @param listItemIndex
	 * The index of the list view, starting from the top.
	 * 
	 * @return
	 * NULL if the index-th list item is a lecture, or 
	 * the correct string otherwise.
	 */
	private String getTitleForListItemSeparator(int listItemIndex) {
		int separators = 0;
		
		if (mListItemTypes.get(listItemIndex) == LIST_ITEM_TYPE_SEPARATOR) {
			for (int i=0; i<listItemIndex; i++) {
				if (mListItemTypes.get(i) == LIST_ITEM_TYPE_SEPARATOR) {
					separators++;
				}
			}
			return mSeparatorStrings.get(separators);
		} else {
			return null;
		}
	}
}




























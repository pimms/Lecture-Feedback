package chipmunk.unlimited.feedback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import chipmunk.unlimited.feedback.database.ReviewedLectureDatabase;

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
		stripIrrelevantLectures();
		defineItemOrder();
	}
	
	
	@Override
	public int getCount() {
		return mListItemTypes.size();
	}
	
	@Override
	public Object getItem(int position) {
		return getLectureForListItem(position);
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
		return mListItemTypes.get(position) == LIST_ITEM_TYPE_LECTURE;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
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
			vi.setBackgroundColor(Color.RED);
		} else {
			vi.setBackgroundColor(Color.TRANSPARENT);
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

	
	/**
	 * Remove all irrelevant lectures from the data set.
	 * The relevant time-period is NOW and 24 hours back.
	 * Only the start time OR the end time of the lecture
	 * has to fit within this 24 hour period for the lecture
	 * to be considered relevant.
	 * 
	 * TODO:
	 * For development purposes, the cut-off limit has been
	 * increased to 10 days. Lower this at an appropriate time.
	 */
	private void stripIrrelevantLectures() {
		Calendar cal = Calendar.getInstance();
		
		Date upperBounds = cal.getTime();
		cal.add(Calendar.DAY_OF_YEAR, -10);
		Date lowerBounds = cal.getTime();
		
		Log.d(TAG, "BOUNDS: " + lowerBounds + ", " + upperBounds);
		
		for (int i=0; i<mLectureItems.size(); i++) {
			LectureItem lecture = mLectureItems.get(i);
			
			Date startTime = lecture.getStartTime();
			Date endTime = lecture.getEndTime();
			
			//Log.d(TAG, "Lecture: " + startTime + ", " + endTime);
			
			if (!endTime.after(lowerBounds) || !startTime.before(upperBounds)) {
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
		Date prevDate = firstDate;
		Date curDate = firstDate;
		
		for (int i=0; i<mLectureItems.size(); i++) {
			curDate = mLectureItems.get(i).getDate();
			if (!curDate.equals(prevDate)) {
				prevDate = curDate;
				mListItemTypes.add(LIST_ITEM_TYPE_SEPARATOR);
				mSeparatorStrings.add(getDateString(curDate));
			}
			
			
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
	 * TODO: Localize
	 * 
	 * @return
	 * "yyyy-MM-dd" if date is more than 2 days old, otherwise
	 * "today" or "yesterday".
	 */
	private String getDateString(Date someDate) {
		Calendar compare = Calendar.getInstance();
		
		Calendar date = Calendar.getInstance();
		date.setTime(someDate);
		
		// Compare against today
		if (equalDates(compare, date)) {
			return "Today";
		}
		
		// Compare against yesterday
		compare.add(Calendar.DAY_OF_MONTH, -1);
		if (equalDates(compare, date)) {
			return "Yesterday";
		}
		
		// Return the date on proper format
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
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




























package chipmunk.unlimited.feedback;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import chipmunk.unlimited.feedback.database.ReviewedLectureDatabase;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @class DailyLectureAdapter
 */
public class DailyLectureAdapter extends BaseAdapter {
	private static final String TAG = "DailyLectureAdapter";
	
	private LayoutInflater mInflater;
	private List<LectureItem> mLectureItems;
	private Context mContext;
	
	public DailyLectureAdapter(Context context) {
		mContext = context;
		mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void setLectureItems(List<LectureItem> items) {
		mLectureItems = items;
		stripIrrelevantLectures();
	}
	
	@Override
	public int getCount() {
		if (mLectureItems != null) {
			return mLectureItems.size();
		}
		
		return 0;
	}
	
	@Override
	public Object getItem(int position) {
		return mLectureItems.get(position);
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (vi == null) {
			vi = mInflater.inflate(R.layout.today_list_item_lecture, null);
		}
		
		TextView tvCourse = (TextView)vi.findViewById(R.id.today_text_view_course_name);
		TextView tvLecturer = (TextView)vi.findViewById(R.id.today_text_view_lecturer);
		TextView tvTime = (TextView)vi.findViewById(R.id.today_text_view_time);
		
		LectureItem item = (LectureItem)getItem(position);
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
	
	
	/**
	 * Remove all irrelevant lectures from the data set.
	 * The relevant time-period is NOW and 24 hours back.
	 * Only the start time OR the end time of the lecture
	 * has to fit within this 24 hour period for the lecture
	 * to be considered relevant.
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
}




























package chipmunk.unlimited.feedback.revfeed;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import chipmunk.unlimited.feedback.LectureItem;
import chipmunk.unlimited.feedback.LectureReviewItem;
import chipmunk.unlimited.feedback.database.SubscriptionDatabase;
import chipmunk.unlimited.feedback.subscription.SubscriptionItem;
import chipmunk.unlimited.feedback.webapi.WebAPI;

/**
 *
 */
public class Feed implements WebAPI.GetFeedCallback {
    /* The Fragment is in it's default state, and will use  *
     * ALL SubscriptionItems from the local Database.       */
    private static final int STATE_DEFAULT      = 0;

    /* The fragment is in SINGLE_COURSE state, and will     *
     * only display a single course.                        */
    private static final int STATE_COURSE       = 1;

    /* The fragment is in SINGLE_LECTURE state and will     *
     * only display reviews made of a single lecture        */
    private static final int STATE_LECTURE      = 2;

    private static final String TAG = "Feed";


    public interface FeedListener {
        public void onFeedUpdate(List<LectureReviewItem> feed);
    }


    private int mState = STATE_DEFAULT;
    private SubscriptionItem mSubItem;
    private LectureItem mLecture;

    private Context mContext;
    private FeedListener mCallback;


    public Feed(Context context, FeedListener callback) {
        mContext = context;
        mCallback = callback;
    }


    public void setStateDefault() {
        mState = STATE_DEFAULT;
    }

    public void setStateCourse(SubscriptionItem subItem) {
        mState = STATE_COURSE;
        mSubItem = subItem;
    }

    public void setStateLecture(LectureItem lecture) {
        mState = STATE_LECTURE;
        mLecture = lecture;
    }


    public void update(int first, int count) {
        if (mState == STATE_DEFAULT) {
            updateDefault(first, count);
        } else if (mState == STATE_COURSE) {
            updateWithSingleCourse(first, count);
        } else if (mState == STATE_LECTURE) {
            updateWithSingleLecture(first, count);
        } else {
            Log.e(TAG, "Undefined state: " + mState);
        }
    }

    private void updateDefault(int first, int count) {
        SubscriptionDatabase subDb = new SubscriptionDatabase(mContext);

        WebAPI webApi = new WebAPI();
        webApi.getFeed(this, subDb.getSubscriptionList(), first, count);
    }

    private void updateWithSingleCourse(int first, int count) {

    }

    private void updateWithSingleLecture(int first, int count) {

    }


    @Override
    public void onGetFeedSuccess(List<LectureReviewItem> items) {
        mCallback.onFeedUpdate(items);
    }

    @Override
    public void onGetFeedFailure(String errorMessage) {
        Log.e(TAG, "GetFeed failed: " + errorMessage);
        mCallback.onFeedUpdate(new ArrayList<LectureReviewItem>());
    }
}

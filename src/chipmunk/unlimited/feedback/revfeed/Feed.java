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
    public static final int STATE_DEFAULT      = 0;

    /* The fragment is in SINGLE_COURSE state, and will     *
     * only display a single course.                        */
    public static final int STATE_COURSE       = 1;

    /* The fragment is in SINGLE_LECTURE state and will     *
     * only display reviews made of a single lecture        */
    public static final int STATE_LECTURE      = 2;

    private static final String TAG = "Feed";


    public interface FeedListener {
        public void onFeedUpdate(List<LectureReviewItem> feed);
    }


    private int mState = STATE_DEFAULT;
    private SubscriptionItem mSubItem;
    private String mLectureHash;

    private Context mContext;
    private FeedListener mCallback;


    public Feed(Context context, FeedListener callback) {
        setFeedListener(callback);
        mContext = context;
    }


    public void setFeedListener(FeedListener callback) {
        mCallback = callback;
    }


    public int getState() {
        return mState;
    }

    public void setStateDefault() {
        mState = STATE_DEFAULT;
    }

    public void setStateCourse(SubscriptionItem subItem) {
        mState = STATE_COURSE;
        mSubItem = subItem;
    }

    public void setStateLecture(String hash) {
        mState = STATE_LECTURE;
        mLectureHash = hash;
    }


    public void update(int first, int count) {
        if (mState == STATE_DEFAULT) {
            updateDefault(first, count);
        } else if (mState == STATE_COURSE) {
            updateWithSingleCourse(first, count);
        } else if (mState == STATE_LECTURE) {
            updateWithSingleLecture(first, count);
        } else {
            onGetFeedFailure("State not supported: " + mState);
        }
    }

    private void updateDefault(int first, int count) {
        SubscriptionDatabase subDb = new SubscriptionDatabase(mContext);

        WebAPI webApi = new WebAPI();
        webApi.getFeed(this, subDb.getSubscriptionList(), first, count);
    }

    private void updateWithSingleCourse(int first, int count) {
        ArrayList<SubscriptionItem> subs = new ArrayList<SubscriptionItem>();
        subs.add(mSubItem);

        WebAPI webApi = new WebAPI();
        webApi.getFeed(this, subs, first, count);
    }

    @Override
    public void onGetFeedFailure(String errorMessage) {
        Log.e(TAG, "GetFeed failed: " + errorMessage);

        if (mCallback != null) {
            mCallback.onFeedUpdate(new ArrayList<LectureReviewItem>());
        }
    }


    private void updateWithSingleLecture(int first, int count) {
        WebAPI webApi = new WebAPI();
        webApi.getFeed(this, mLectureHash, first, count);
    }

    @Override
    public void onGetFeedSuccess(List<LectureReviewItem> items) {
        if (mCallback != null) {
            mCallback.onFeedUpdate(items);
        }
    }
}

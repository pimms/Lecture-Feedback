package chipmunk.unlimited.feedback;

import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import uk.co.senab.actionbarpulltorefresh.library.*;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Common superclass shared by FeedFragment,
 * StatisticsFragment and TodayFragment. Provides
 * a framework for safely updating the fragments.
 *
 * Subclasses must override "doRefresh()", and must
 * call "onUpdateCompleted()" once the update has
 * finalized.
 *
 * Updates are initiated using pull to refresh functionality.
 *
 * If a subclass wishes to have their list view pulled,
 * it MUST have the id "android.R.id.list".
 *
 *
 * Under unknown but common conditions, the AsyncHttp library hangs,
 * and does call onSuccess() or onFailure() on the callback. The cause
 * of the issue is unknown, but the current workaround is
 * to start a timer when the Fragment begins updating. If the timer
 * runs out before a response is received, the Fragment updates
 * itself again.
 */
public abstract class UpdateableFragment extends ListFragment
        implements OnRefreshListener {
    public static final int REFRESH_TIMEOUT_SECONDS = 15;
    private static final String TAG = "UpdateableFragment";

    private boolean mInitialized;
    private boolean mScheduledUpdate;

    private PullToRefreshLayout mPtrLayout;

    private Handler mHandler;


    @Override
    public void onStart() {
        super.onStart();

        ViewGroup viewGroup = (ViewGroup)getView();
        mPtrLayout = new PullToRefreshLayout(getActivity());

        ActionBarPullToRefresh.from(getActivity())
                .insertLayoutInto(viewGroup)
                .theseChildrenArePullable(android.R.id.list)
                .listener(this)
                .setup(mPtrLayout);

        onFragmentInitialized();
    }
    @Override
    public void onRefreshStarted(View view) {
        refreshContents();
    }


    public PullToRefreshLayout getPullToRefreshLayout() {
        return mPtrLayout;
    }

    /**
     * Method called when the fragment is initialized.
     * Will call refreshContents() if a call was made to that
     * method before the Fragment was initialized.
     */
    private void onFragmentInitialized() {
        mInitialized = true;

        if (mScheduledUpdate) {
            mScheduledUpdate = false;
            refreshContents();
        }
    }
    /**
     * Called when the list view is pulled, and can also be
     * called externally to kick start a refresh.
     *
     * If the Fragment is not initialized at time of calling
     * this method, it will be called as soon as the Fragment
     * is initialized.
     */
    public final void refreshContents() {
        if (mInitialized) {
            Log.v(TAG, "Updating class: " + getClass().getCanonicalName());
            doRefresh();
            mPtrLayout.setRefreshing(true);

            startTimer();
        } else {
            mScheduledUpdate = true;
        }
    }
    /**
     * Invalidate any pre-initialization requests to update the Fragment.
     */
    public final void cancelScheduledRefresh() {
        mScheduledUpdate = false;
    }
    /**
     * Called to indicate that the update finalized.
     * Will stop the "updating" animation in the top bar.
     */
    public final void onUpdateCompleted() {
        Log.v(TAG, "Class updated: " + getClass().getCanonicalName());
        mPtrLayout.setRefreshing(false);
        mPtrLayout.setRefreshComplete();

        cancelTimer();
    }


    private void startTimer() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onTimerCompleted(handler);
            }
        }, REFRESH_TIMEOUT_SECONDS * 1000);

        mHandler = handler;
    }

    private void cancelTimer() {
        mHandler = null;
    }

    private void onTimerCompleted(Handler handler) {
        if (mHandler != null && mHandler == handler) {
            Log.d(TAG, "Recovering from AsyncHttp hangup");
            doRefresh();
        }
    }


    /**
     * Abstract method that must be overidden by the subclass.
     */
    protected abstract void doRefresh();
}


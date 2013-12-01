package chipmunk.unlimited.feedback;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;

/**
 * Common superclass shared by FeedFragment,
 * StatisticsFragment and TodayFragment. Provides
 * a framework for safely updating the fragments.
 *
 * Subclasses must override "doRefresh()", and must
 * call "onFragmentInitialized()" after all init has
 * been performed.
 */

public abstract class UpdateableFragment extends ListFragment {
    private boolean mInitialized;
    private boolean mScheduledUpdate;


    protected void onFragmentInitialized() {
        mInitialized = true;

        if (mScheduledUpdate) {
            doRefresh();
            mScheduledUpdate = false;
        }
    }

    @Override
    public void onActivityCreated(Bundle bundle) {
        onFragmentInitialized();
        super.onActivityCreated(bundle);
    }

    public final void refreshContents() {
        if (mInitialized) {
            doRefresh();
        } else {
            mScheduledUpdate = true;
        }
    }

    protected abstract void doRefresh();
}


package chipmunk.unlimited.feedback;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

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
 */
public abstract class UpdateableFragment extends ListFragment
        implements OnRefreshListener {
    private boolean mInitialized;
    private boolean mScheduledUpdate;

    private PullToRefreshLayout mPtrLayout;


    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onActivityCreated(bundle);
        onFragmentInitialized();

        ViewGroup viewGroup = (ViewGroup)view;
        mPtrLayout = new PullToRefreshLayout(getActivity());

        ActionBarPullToRefresh.from(getActivity())
                .insertLayoutInto(viewGroup)
                .theseChildrenArePullable(android.R.id.list)
                .listener(this)
                .setup(mPtrLayout);
    }
    @Override
    public void onRefreshStarted(View view) {
        refreshContents();
    }


    public PullToRefreshLayout getPullToRefreshLayout() {
        return mPtrLayout;
    }


    protected void onFragmentInitialized() {
        mInitialized = true;

        if (mScheduledUpdate) {
            doRefresh();
            mScheduledUpdate = false;
        }
    }

    public final void refreshContents() {
        if (mInitialized) {
            doRefresh();
            mPtrLayout.setRefreshing(true);
        } else {
            mScheduledUpdate = true;
        }
    }

    public final void onUpdateCompleted() {
        mPtrLayout.setRefreshing(false);
        mPtrLayout.setRefreshComplete();
    }

    protected abstract void doRefresh();
}


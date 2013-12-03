package chipmunk.unlimited.feedback;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;


public class ScrollToRefreshListView extends ListView implements AbsListView.OnScrollListener {
    public interface OnScrollToRefreshListener {
        public void onScrollRefreshBegin(ScrollToRefreshListView view);
    }

    private int mScrollState;
    private OnScrollToRefreshListener mListener;
    private boolean mIsRefreshing;
    private boolean mAtBottom;


    public ScrollToRefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ScrollToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrollToRefreshListView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setOnScrollListener(this);


        ViewGroup vg = (ViewGroup)getParent();

    }

    public void setOnScrollToRefreshListener(OnScrollToRefreshListener listener) {
        mListener = listener;
    }


    private void onRefreshBegin() {
        mIsRefreshing = true;

        // Display shit

        if (mListener != null) {
            mListener.onScrollRefreshBegin(this);
        }
    }

    public void onRefreshComplete() {
        mIsRefreshing = false;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int state) {
        if (absListView == this) {
            mScrollState = state;
        }
    }
    @Override
    public void onScroll (AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (view == this) {
            boolean wasAtBottom = mAtBottom;
            mAtBottom = (firstVisibleItem + visibleItemCount == totalItemCount);

            if (!mIsRefreshing && !wasAtBottom && mAtBottom) {
                if (mScrollState != SCROLL_STATE_IDLE) {
                    if (mListener != null) {
                        onRefreshBegin();
                    }
                }
            }
        }
    }
}

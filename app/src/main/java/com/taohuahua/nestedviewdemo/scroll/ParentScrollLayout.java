package com.taohuahua.nestedviewdemo.scroll;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent2;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.taohuahua.nestedviewdemo.R;

/**
 * @author heJianfeng
 * @date 2019-10-14
 */
public class ParentScrollLayout extends LinearLayout implements NestedScrollingParent2 {

    private NestedScrollingParentHelper parentHelper;
    private RecyclerView mRecyclerView;

    // Is Refreshing
    volatile private boolean mRefreshing = false;

    private int mViewHeight;
    private Scroller mScroller;
    private DIRECTION mDirection;

    enum DIRECTION {
        UP, DOWN
    }

    public ParentScrollLayout(Context context) {
        this(context, null);
    }

    public ParentScrollLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParentScrollLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parentHelper = new NestedScrollingParentHelper(this);
        mScroller = new Scroller(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mRecyclerView = findViewById(R.id.recyler_view);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mViewHeight = getHeight();
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        return isEnabled() && (!canChildScrollUp()) && !mRefreshing;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        parentHelper.onNestedScrollAccepted(child, target, axes, type);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        parentHelper.onStopNestedScroll(target, type);
        mRefreshing = false;
        if (getScrollY() != 0) {
            if (mDirection == DIRECTION.DOWN) {
                //isFinish = true;
                scrollBottom();
            } else if (mDirection == DIRECTION.UP) {
                //isFinish = false;
                scrollOrigin();
            }
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @Nullable int[] consumed, int type) {
        if ((!canChildScrollUp() && dy < 0) || (dy > 0 && getScrollY() < 0)) {
            consumed[1] += dy;
            if (dy > 0 && (getScrollY() + dy) > 0) {
                dy = Math.abs(getScrollY());
            }
            scrollBy(0, dy);
            mRefreshing = true;
        }
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        mDirection = velocityY < 0 ? DIRECTION.DOWN : DIRECTION.UP;
        if (mDirection == DIRECTION.UP) {
            scrollOrigin();
        }
        return !canChildScrollUp();
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    /**
     * 滚动出屏幕
     */
    private void scrollBottom() {
        int delta = mViewHeight + getScrollY();
        mScroller.startScroll(0, getScrollY(), 0, -delta, 200);
        invalidate();
    }

    private void scrollOrigin() {
        mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), 200);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            postInvalidate();
            /*percentageNotify();
            if (mScroller.isFinished() && isFinish) {
                if (onFinishListener != null) {
                    onFinishListener.onFinish();
                } else {
                    scrollOrigin();
                    isFinish = false;
                }
            }*/
        }
    }


    /**
     * Whether child view can scroll up
     *
     * @return
     */
    public boolean canChildScrollUp() {
        if (mRecyclerView == null) {
            return false;
        }
        return ViewCompat.canScrollVertically(mRecyclerView, -1);
    }

    /**
     * Whether child view can scroll down
     *
     * @return
     */
    public boolean canChildScrollDown() {
        if (mRecyclerView == null) {
            return false;
        }
        return ViewCompat.canScrollVertically(mRecyclerView, 1);
    }

}

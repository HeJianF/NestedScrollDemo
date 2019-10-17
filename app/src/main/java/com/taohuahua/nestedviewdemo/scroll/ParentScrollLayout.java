package com.taohuahua.nestedviewdemo.scroll;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent2;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * @author heJianfeng
 * @date 2019-10-14
 */
public class ParentScrollLayout extends LinearLayout implements NestedScrollingParent2 {

    private NestedScrollingParentHelper parentHelper;

    private boolean isTouch;
    private int mViewHeight;
    private VelocityTracker mVelocityTracker;
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
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mViewHeight = getHeight();
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        parentHelper.onNestedScrollAccepted(child, target, axes, type);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        parentHelper.onStopNestedScroll(target, type);
        if (getScrollY() != 0) {
            if (mDirection == DIRECTION.DOWN) {
                scrollBottom();
            } else if (mDirection == DIRECTION.UP) {
                scrollOrigin();
            }
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @Nullable int[] consumed, int type) {
        if (!canChildScrollUp(target) || (dy > 0 && getScrollY() < 0)) {
            if (dy > 0 && (getScrollY() + dy) > 0) {
                dy = Math.abs(getScrollY());
            }
            consumed[1] += dy;
            if (isTouch) {
                scrollBy(0, dy);
            }
        }
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        return getScrollY() <= 0 && !canChildScrollUp(target);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        acquireVelocityTracker(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouch = true;
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.computeCurrentVelocity(1000);
                mDirection = mVelocityTracker.getYVelocity() < 0 ? DIRECTION.UP : DIRECTION.DOWN;
                break;
            case MotionEvent.ACTION_UP:
                recycleVelocityTracker();
                isTouch = false;
                break;
        }
        return super.dispatchTouchEvent(ev);
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

    private void acquireVelocityTracker(final MotionEvent event) {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    /**
     * Whether child view can scroll up
     *
     * @return
     */
    public boolean canChildScrollUp(View view) {
        return ViewCompat.canScrollVertically(view, -1);
    }

    /**
     * Whether child view can scroll down
     *
     * @return
     */
   /* public boolean canChildScrollDown() {
        if (mScrollView == null) {
            return false;
        }
        return ViewCompat.canScrollVertically(mScrollView, 1);
    }*/

}

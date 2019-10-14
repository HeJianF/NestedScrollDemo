package com.taohuahua.nestedviewdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * 从上向下滚动的控件
 *
 * @author heJianfeng
 * @date 2019-10-12
 */
public class TopToBottomFinishLayout extends RelativeLayout {

    private int mViewHeight;
    private float mLastY;

    private boolean isSliding;
    private boolean isFinish;
    private OnFinishListener onFinishListener;
    private SlidingPercentageListener slidingPercentageListener;

    private int mTouchSlop;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private DIRECTION mDirection;

    enum DIRECTION {
        UP, DOWN
    }

    public TopToBottomFinishLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopToBottomFinishLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScroller = new Scroller(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mViewHeight = this.getHeight();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        acquireVelocityTracker(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.computeCurrentVelocity(1000);
                mDirection = mVelocityTracker.getYVelocity() < 0 ? DIRECTION.UP : DIRECTION.DOWN;
                break;
            case MotionEvent.ACTION_UP:
                recycleVelocityTracker();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (slideValidity(mLastY - y)) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dy = mLastY - y;
                mLastY = y;
                if (slideValidity(dy)) {
                    isSliding = true;
                }
                if (isSliding && (getScrollY() < 0 || mDirection == DIRECTION.DOWN)) {
                    if (Math.abs(getScrollY()) >= mViewHeight) {
                        if (onFinishListener != null) {
                            onFinishListener.onFinish();
                            return true;
                        }
                    }
                    if (getScrollY() + dy > 0) {
                        dy = Math.abs(getScrollY());
                    }
                    scrollBy(0, (int) dy);
                    percentageNotify();
                }
                break;
            case MotionEvent.ACTION_UP:
                isSliding = false;
                if (mDirection == DIRECTION.DOWN) {
                    isFinish = true;
                    scrollBottom();
                } else if (mDirection == DIRECTION.UP) {
                    isFinish = false;
                    scrollOrigin();
                }
                break;
        }
        return true;
    }

    /**
     * 滚动出屏幕
     */
    private void scrollBottom() {
        int delta = mViewHeight + getScrollY();
        mScroller.startScroll(0, getScrollY(), 0, -delta, 200);
        invalidate();
    }

    /**
     * 滚动到起始位置
     */
    private void scrollOrigin() {
        mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), 200);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            postInvalidate();
            percentageNotify();
            if (mScroller.isFinished() && isFinish) {
                if (onFinishListener != null) {
                    onFinishListener.onFinish();
                } else {
                    scrollOrigin();
                    isFinish = false;
                }
            }
        }
    }

    private boolean slideValidity(float dy) {
        return Math.abs(dy) > mTouchSlop;
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
     * 滚动结束需要调用的接口
     */
    public interface OnFinishListener {
        void onFinish();
    }

    /**
     * 计算控件向下滑动的比例
     */
    public interface SlidingPercentageListener {
        void onPercentage(float percentage);
    }

    public void setOnFinishListener(OnFinishListener finishListener) {
        this.onFinishListener = finishListener;
    }

    public void setSlidingPercentageListener(SlidingPercentageListener listener) {
        this.slidingPercentageListener = listener;
    }

    private void percentageNotify() {
        if (slidingPercentageListener != null) {
            float percentage = Math.abs((float) getScrollY()) / mViewHeight;
            slidingPercentageListener.onPercentage(percentage);
        }
    }

}

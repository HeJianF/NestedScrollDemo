package com.taohuahua.nestedviewdemo.scroll;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingParent2;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Scroller;

/**
 * 从上向下滑动关闭的控件
 * <p>
 * 1.需要手动实现OnFinishListener{@link OnFinishListener}接口来dismiss
 * 2.可以选择实现SlidingPercentageListener{@link SlidingPercentageListener}接口来监听View滑动的比例（0.0-1.0）,可以用来设置背景透明度等
 * 3.可以在子控件中包含可滑动的View(RecycleVie、NestedScrollView)
 *
 * @author heJianfeng
 * @date 2019-10-12
 */
public class TopToBottomFinishLayout extends RelativeLayout implements NestedScrollingParent2 {

    private int mViewHeight;
    private float mLastY;
    private boolean isScrolling;
    private boolean isFinish;
    private boolean hasNested;

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private DIRECTION mDirection;//手指滑动方向

    enum DIRECTION {
        UP, DOWN
    }

    private OnFinishListener onFinishListener;
    private SlidingPercentageListener slidingPercentageListener;

    public TopToBottomFinishLayout(Context context) {
        this(context, null);
    }

    public TopToBottomFinishLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopToBottomFinishLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScroller = new Scroller(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        mViewHeight = getHeight();
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        hasNested = true;
        //不处理传上来的fling事件
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0 && type != ViewCompat.TYPE_NON_TOUCH;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        scrollStop();
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @Nullable int[] consumed, int type) {
        // 向下滑 || 向上滑
        if (notChildScrollUp(target) || (dy > 0 && getScrollY() < 0)) {
            //避免向上滑动的时候getScrollY大于0
            if (getScrollY() + dy > 0) {
                dy = Math.abs(getScrollY());
            }
            if (consumed != null) {
                //父控件消耗全部dy
                consumed[1] += dy;
                startScroll(dy);
            }
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        return notChildScrollUp(target);
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
                if (!hasNested && slideValidity(mLastY - y)) {
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
            case MotionEvent.ACTION_DOWN:
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = mLastY - y;
                mLastY = y;
                if (slideValidity(dy)) {
                    isScrolling = true;
                }
                if (isScrolling && (mDirection == DIRECTION.DOWN || getScrollY() < 0)) {
                    //向下滚动的距离超过View的高度时，直接调用onFinish
                    if (Math.abs(getScrollY()) >= mViewHeight) {
                        if (onFinishListener != null) {
                            onFinishListener.onFinish();
                            return true;
                        }
                    }
                    //避免向上滑动的时候getScrollY大于0
                    if (getScrollY() + dy > 0) {
                        dy = Math.abs(getScrollY());
                    }
                    startScroll((int) dy);
                }
                break;
            case MotionEvent.ACTION_UP:
                scrollStop();
                break;
        }
        return true;
    }

    private void startScroll(int dy) {
        isScrolling = true;
        scrollBy(0, dy);
        percentageNotify();
    }

    private void scrollStop() {
        if (isScrolling) {
            if (mDirection == DIRECTION.DOWN) {
                isFinish = true;
                scrollBottom();
            } else if (mDirection == DIRECTION.UP) {
                isFinish = false;
                scrollOrigin();
            }
            isScrolling = false;
        }
    }

    /**
     * 向下滚动出屏幕
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
                //在向下滑动结束后，若用户没有实现onFinish方法，则View恢复原位
                if (onFinishListener != null) {
                    onFinishListener.onFinish();
                } else {
                    isFinish = false;
                    scrollOrigin();
                }
            }
        }
    }

    private boolean slideValidity(float dy) {
        return Math.abs(dy) > 3;
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
     * 判断View能否向上滑动
     *
     * @param view
     * @return true 滑动到顶了，View不能继续向上滑动了
     */
    public boolean notChildScrollUp(View view) {
        return !ViewCompat.canScrollVertically(view, -1);
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

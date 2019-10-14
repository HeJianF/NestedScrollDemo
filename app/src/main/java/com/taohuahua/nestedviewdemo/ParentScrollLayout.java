package com.taohuahua.nestedviewdemo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent2;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * @author heJianfeng
 * @date 2019-10-14
 */
public class ParentScrollLayout extends LinearLayout implements NestedScrollingParent2 {

    private NestedScrollingParentHelper parentHelper;
    private RecyclerView mRecyclerView;
    // Is Refreshing
    volatile private boolean mRefreshing = false;

    public ParentScrollLayout(Context context) {
        this(context, null);
    }

    public ParentScrollLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParentScrollLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parentHelper = new NestedScrollingParentHelper(this);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnabled() || canChildScrollUp()) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mRecyclerView = findViewById(R.id.recyler_view);
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
        Log.d("ParentScrollLayout", "onStopNestedScroll: ");
        if (isRefreshing()) {
            return;
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        Log.d("ParentScrollLayout", "onNestedScroll: " + dyConsumed + "   " + dyUnconsumed);
        scrollBy(0, -dyConsumed);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @Nullable int[] consumed, int type) {
        //consumed[1] += dy;
        Log.d("ParentScrollLayout", "onNestedPreScroll: " + dy);
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

    public boolean isRefreshing() {
        return mRefreshing;
    }

}

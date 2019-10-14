package com.taohuahua.nestedviewdemo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChild2;
import android.support.v4.view.NestedScrollingChildHelper;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * @author heJianfeng
 * @date 2019-10-14
 */
public class ChildScrollLayout extends LinearLayout implements NestedScrollingChild2 {

    NestedScrollingChildHelper childHelper;

    public ChildScrollLayout(Context context) {
        this(context, null);
    }

    public ChildScrollLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChildScrollLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        childHelper = new NestedScrollingChildHelper(this);
    }

    @Override
    public boolean startNestedScroll(int axes, int type) {
        return childHelper.startNestedScroll(axes, type);
    }

    @Override
    public void stopNestedScroll(int type) {
        childHelper.stopNestedScroll(type);
    }

    @Override
    public boolean hasNestedScrollingParent(int type) {
        return childHelper.hasNestedScrollingParent(type);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow, int type) {
        return childHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow, int type) {
        return childHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
    }

}

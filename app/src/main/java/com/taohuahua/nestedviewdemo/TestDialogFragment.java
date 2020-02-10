package com.taohuahua.nestedviewdemo;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.taohuahua.nestedviewdemo.scroll.TopToBottomFinishLayout;

/**
 * @author heJianfeng
 * @date 2019-10-14
 */
public class TestDialogFragment extends DialogFragment {

    public void show(FragmentManager fragmentManager) {
        fragmentManager.beginTransaction()
                .add(this, TestDialogFragment.class.getName())
                .commitAllowingStateLoss();
    }

    public static TestDialogFragment instance() {
        return new TestDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setStyle(STYLE_NO_TITLE, R.style.BottomDialog);
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item, container);
        RecyclerView mRecyclerView = view.findViewById(R.id.can_scroll_view);
        String[] items = getResources().getStringArray(R.array.tab_B);

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(items);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);

        TopToBottomFinishLayout finishLayout = view.findViewById(R.id.top_to_bottom);
        finishLayout.setOnFinishListener(new TopToBottomFinishLayout.OnFinishListener() {
            @Override
            public void onFinish() {
                dismiss();
            }
        });
        finishLayout.setSlidingPercentageListener(new TopToBottomFinishLayout.SlidingPercentageListener() {
            @Override
            public void onPercentage(float percentage) {
                if (getDialog() != null && getDialog().getWindow() != null) {
                    Window window = getDialog().getWindow();
                    WindowManager.LayoutParams params = window.getAttributes();
                    params.dimAmount = (1 - percentage) / 2;
                    window.setAttributes(params);
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.BOTTOM;
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
        }
    }
}

package com.taohuahua.nestedviewdemo;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author heJianfeng
 * @date 2019-10-14
 */
public class MyDialogFragment extends DialogFragment {

    public void show(FragmentManager fragmentManager) {
        fragmentManager.beginTransaction()
                .add(this, MyDialogFragment.class.getName())
                .commitAllowingStateLoss();
    }

    public static MyDialogFragment instance() {
        return new MyDialogFragment();
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
        RecyclerView mRecyclerView = view.findViewById(R.id.recyler_view);
        String[] items = getResources().getStringArray(R.array.tab_B);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(items);
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
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

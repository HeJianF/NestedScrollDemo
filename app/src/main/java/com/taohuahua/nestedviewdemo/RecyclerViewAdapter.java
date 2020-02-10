package com.taohuahua.nestedviewdemo;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by anupamchugh on 05/10/16.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.TextItemViewHolder> {

    private String[] items;

    public RecyclerViewAdapter(String[] items) {
        this.items = items;
    }

    @Override
    public TextItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_list_item, parent, false);
        return new TextItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TextItemViewHolder holder, final int position) {
        holder.bind(items[position]);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return items.length;
    }

    class TextItemViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        public TextItemViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.list_item);
        }

        public void bind(final String text) {
            textView.setText(text);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("TextItemViewHolder", "onClick: " + text);
                }
            });
        }

    }
}

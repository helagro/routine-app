package com.hlag.routine;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<Step> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private final int [] visible = {View.VISIBLE, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT};
    private final int [] invisible = {View.GONE, 0, 0};

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, List<Step> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.step_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Step step = mData.get(position);

        int []visibility = step.getChecked() ? invisible : visible;
        holder.itemView.setVisibility(visibility[0]);
        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        params.width = visibility[1];
        params.height = visibility[2];
        holder.itemView.setLayoutParams(params);

        holder.myTextView.setText(step.getText());
        holder.myCheckbox.setChecked(step.getChecked());
        holder.myCheckbox.setOnCheckedChangeListener((compoundButton, b) -> {
            step.setChecked(compoundButton.isChecked());

            holder.itemView.setVisibility(visible[0]);
            ViewGroup.LayoutParams params2 = holder.itemView.getLayoutParams();
            params2.height = 0;
            params2.width = 0;
            holder.itemView.setLayoutParams(params2);
        });



    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        CheckBox myCheckbox;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.tvAnimalName);
            myCheckbox = itemView.findViewById(R.id.step_finished);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Step getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}

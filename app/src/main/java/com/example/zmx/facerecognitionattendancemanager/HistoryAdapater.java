package com.example.zmx.facerecognitionattendancemanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class HistoryAdapater extends RecyclerView.Adapter<HistoryAdapater.ViewHolder> {

    private Context mContext;

    private List<History> mHistoryList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView historyName;
        TextView historyTime;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            historyName = (TextView) view.findViewById(R.id.history_name);
            historyTime = (TextView) view.findViewById(R.id.history_time);
        }
    }

    public HistoryAdapater(List<History> historyList) {
        mHistoryList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.history_item,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        History history = mHistoryList.get(position);
        holder.historyName.setText(history.getStuName());
        holder.historyTime.setText(history.getRegister_time());
    }

    @Override
    public int getItemCount() {
        return mHistoryList.size();
    }
}

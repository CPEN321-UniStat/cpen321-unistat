package com.example.unistat.StatsCardView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unistat.R;

import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.StatsHolder>{

    // CardAdapter Class
    private Context mContext;
    private ArrayList<StatsCards> stats;

    public CardAdapter(Context mContext, ArrayList<StatsCards> stats) {
        this.mContext = mContext;
        this.stats = stats;
    }

    @NonNull
    @Override
    public StatsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_card_template, parent, false);
        return new StatsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatsHolder holder, int position) {
        StatsCards curStat = stats.get(position);
        holder.mentorNameCard.setText(curStat.getMentorName());
        holder.univNameCard.setText(curStat.getUnivName());
        holder.univMajorCard.setText(curStat.getUnivMajor());
        holder.univGpaCard.setText(curStat.getUnivGpa());
        holder.univEntranceScoreCard.setText(curStat.getUnivEntranceScore());
        holder.univBioCard.setText(curStat.getUnivBio());
    }

    @Override
    public int getItemCount() {
        return stats.size();
    }

    //View Holder Class: StatsHolder
    class StatsHolder extends RecyclerView.ViewHolder{
        private TextView mentorNameCard;
        private TextView univNameCard;
        private TextView univMajorCard;
        private TextView univGpaCard;
        private TextView univEntranceScoreCard;
        private TextView univBioCard;

        public StatsHolder(View itemView) {
            super(itemView);
            mentorNameCard = itemView.findViewById(R.id.mentorName);
            univNameCard = itemView.findViewById(R.id.univName);
            univMajorCard = itemView.findViewById(R.id.univMajor);
            univGpaCard = itemView.findViewById(R.id.univGpa);
            univEntranceScoreCard = itemView.findViewById(R.id.univEntranceScore);
            univBioCard = itemView.findViewById(R.id.univBio);
        }

        void setDetails(StatsCards stats){
            mentorNameCard.setText(stats.getMentorName());
            univNameCard.setText(stats.getUnivName());
            univMajorCard.setText(stats.getUnivMajor());
            univGpaCard.setText(stats.getUnivGpa());
            univEntranceScoreCard.setText(stats.getUnivEntranceScore());
            univBioCard.setText(stats.getUnivBio());
        }

    }

}
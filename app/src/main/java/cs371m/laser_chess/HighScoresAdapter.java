package cs371m.laser_chess;


import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Abhi on 11/30/2016.
 */

public class HighScoresAdapter extends RecyclerView.Adapter<HighScoresAdapter.ViewHolder> {
    private static final String TAG = "CustomAdapter";
    private List<Score> scores;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView wins;
        public TextView loses;

        public ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            wins = (TextView) view.findViewById(R.id.wins);
            loses = (TextView) view.findViewById(R.id.loses);
        }
    }


    public HighScoresAdapter(ArrayList<Score> scores) {
        this.scores = scores;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Score score = scores.get(position);
        holder.name.setText(score.getName());
        holder.wins.setText(String.format("%d", score.getWins()));
        holder.loses.setText(String.format("%d", score.getLoses()));
    }



    @Override
    public int getItemCount() {
        return scores.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.scores_list_item, parent, false);
        return new ViewHolder(v);
    }


}


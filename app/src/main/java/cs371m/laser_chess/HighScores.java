package cs371m.laser_chess;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Abhi on 11/30/2016.
 */

public class HighScores extends FragmentActivity {

    private RecyclerView scoresView;
    private ArrayList<Score> highScores;
    private HighScoresAdapter adapter;
    private SharedPreferences prefs;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        username = getIntent().getStringExtra("username");
        scoresView = (RecyclerView) findViewById(R.id.high_scores_list_view);
        highScores = new ArrayList<Score>();

        prefs = this.getSharedPreferences("scores", MODE_PRIVATE);
        Map<String,?> scoresMap = prefs.getAll();
        for (Map.Entry<String, ?> entry : scoresMap.entrySet()) {
            System.out.println("Key = " + entry.getKey());
            System.out.println("Value = " + entry.getValue());
            Score score = new Score(entry.getKey(), (String)entry.getValue());
            highScores.add(score);
        }
        adapter = new HighScoresAdapter(highScores);
        scoresView.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        scoresView.setLayoutManager(llm);

        final HighScores act = this;
        ((Button) findViewById(R.id.mainMenu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(act, MainActivity.class);
                //startActivity(intent);
                // changed to just call finish
                finish();
            }
        });


        ((Button) findViewById(R.id.resetScores)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                highScores.clear();
                SharedPreferences.Editor edit = prefs.edit();
                edit.clear();
                edit.putString(username, "0,0");
                edit.apply();

                highScores.add(new Score(username, "0,0"));
                adapter.notifyDataSetChanged();
            }
        });
    }
}

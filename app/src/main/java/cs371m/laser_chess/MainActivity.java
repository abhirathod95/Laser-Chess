package cs371m.laser_chess;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    Boolean loggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loggedIn = false;

        TextView title = (TextView) findViewById(R.id.titleText);
        // http://www.fonts101.com/fonts/view/Techno/66599/LASER_GUN; free for personal use
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/laser.ttf");
        title.setTypeface(custom_font);
        title.setText("Laser Chess");


        Button findMatch = (Button) findViewById(R.id.findmatch_but);
        findMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!loggedIn){
                    Toast.makeText(getApplicationContext(), "Log in to find a match!",Toast.LENGTH_SHORT).show();
                } else {
                    // Start matchmaking
                }
            }
        });
    }


}

package cs371m.laser_chess;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Path;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by daniel on 11/9/16. Draws from online tutorial at:
 * http://www.londatiga.net/it/programming/android/how-to-programmatically-scan-or-discover-android-bluetooth-device/
 */

public class OpponentList extends Activity {

    private ListView mListView;
    private DeviceAdapter mAdapter;
    private ArrayList<BluetoothDevice> mDeviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_opponents);
        mDeviceList	= getIntent().getExtras().getParcelableArrayList("devicelist");

        mListView = (ListView) findViewById(R.id.op_list);

        mAdapter = new DeviceAdapter(this, mDeviceList);
        mListView.setAdapter(mAdapter);
    }

    public void startTheGame(String mac){
        Toast.makeText(getApplicationContext(), "Kill the ListView; Start the game Activity.",Toast.LENGTH_SHORT).show();


        Intent newGame = new Intent(OpponentList.this, GameLogic.class);
        newGame.putExtra("mac", mac);
        finish();
        startActivity(newGame);
    }
}

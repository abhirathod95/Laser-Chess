package cs371m.laser_chess;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by daniel on 11/9/16.
 * ListView activity that launches after devices are found.
 * Processes the devices to only show laser-chess players and then returns
 * the selected device (the one the user hit play on) to the mainactivity result.
 */

public class OpponentList extends Activity {

    private ListView mListView;
    private DeviceAdapter mAdapter;
    private ArrayList<BluetoothDevice> mDeviceList;
    private ArrayList<BluetoothDevice> mDataPlayers; // laser-chess facebook results

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_opponents);
        mDeviceList	= getIntent().getExtras().getParcelableArrayList("devicelist");

        mDataPlayers = new ArrayList<BluetoothDevice>();
        for (BluetoothDevice device: mDeviceList){
            if (device != null && device.getName() != null) {
                if (device.getName().startsWith("Laser-Chess: ")) {
                    mDataPlayers.add(device);
                }
            }
        }
        mListView = (ListView) findViewById(R.id.op_list);
        mAdapter = new DeviceAdapter(this, mDataPlayers);
        mListView.setAdapter(mAdapter);
    }

    public void finishOppList(BluetoothDevice dev){

        Intent returnIntent = new Intent();
        Bundle b = new Bundle();
        b.putParcelable("targetDevice", dev);
        returnIntent.putExtras(b);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

    }
}

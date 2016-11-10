package cs371m.laser_chess;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.NULL;

/**
 * Created by daniel on 11/9/16. Adapter for our ListView of opponents.
 * Borrows from Witchel's DemoListView project.
 */

public class DeviceAdapter extends BaseAdapter{

    private LayoutInflater mInflater;
    private ArrayList<BluetoothDevice> mData;
    private Context con;

    public DeviceAdapter(Context context, ArrayList<BluetoothDevice> devices) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        con = context;
        mData = devices;
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mData.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.op_list_item, parent, false);
        }
        return bindView(convertView, position, parent);
    }


    protected View bindView(View theView, final int position, ViewGroup parent) {
        TextView name = (TextView) theView.findViewById(R.id.op_name);
        TextView mac = (TextView) theView.findViewById(R.id.mac_Addr);
        Button play = (Button) theView.findViewById(R.id.play_but);

        String deviceName;
        if (mData.get(position).getName() == null){
            deviceName = "No Name Retrieved";
        } else {
            deviceName = mData.get(position).getName();
        }
        name.setText(deviceName);
        mac.setText(mData.get(position).getAddress());


        // Calls the startTheGame function in OpponentList Activity
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(con instanceof OpponentList){
                    ((OpponentList)con).startTheGame(mData.get(position).getAddress());
                }
            }
        });

        return theView;
    }

    @Override
    public int getCount() {
        if (mData == null){
            return 0;
        } else
        return mData.size();
    }
}

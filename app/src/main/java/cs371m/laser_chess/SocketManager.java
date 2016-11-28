package cs371m.laser_chess;

import android.bluetooth.BluetoothSocket;

import java.io.Serializable;

/**
 * Created by daniel on 11/27/16.
 */

public class SocketManager implements Serializable{

    BluetoothSocket sock;

    public SocketManager(){
    }

    public void setSocket(BluetoothSocket s){
        sock = s;
    }

    public BluetoothSocket getSocket(){
        return sock;
    }
}

package cs371m.laser_chess;

import android.app.Application;
import android.bluetooth.BluetoothSocket;


/**
 * Created by daniel on 11/27/16.
 * They told me to use a service. I said nei, munum vio nota pennan static class.
 */

public final class SocketManager extends Application {

    static BluetoothSocket sock;

    private SocketManager(){
    }

    public static void setSocket(BluetoothSocket s){
        sock = s;
    }

    public static BluetoothSocket getSocket(){
        return sock;
    }
}

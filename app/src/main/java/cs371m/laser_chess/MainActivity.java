/**
 * Created by daniel on 11/6/16. Much Bluetooth code here adapted from:
 * https://developer.android.com/guide/topics/connectivity/bluetooth.html.
 */


package cs371m.laser_chess;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


public class MainActivity extends FragmentActivity {

    Boolean loggedIn; // Facebook
    Boolean cancel;
    String username;
    String passingName;
    UUID uid = UUID.fromString("80d2bf7f-a00e-4951-9a33-6434ca4e27d8"); // unique to this app

    AcceptThread hostThread;

    private CallbackManager callbackManager; // for Facebook login
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mainSocket;

    private final static int FACEBOOK_LOGIN_CODE = 1;
    private final static int REQUEST_ENABLE_BT = 2;

    private final static int LOCATION_PERMISSION= 11;
    private final static int OPPONENT_LIST_ACTIVITY=12;

    private ProgressDialog findingDialogue;
    private ProgressDialog hostingDialogue;

    ArrayList<BluetoothDevice> mDeviceList;

    private Button highScores;

    @Override
    /**
     * big bad oncreate. avert your eyes if you are not used to graphic displays of gore
     * NOT boilerplate
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        cancel = false;
        // set it to null initially
        username = null;

        findingDialogue = new ProgressDialog(this);
        findingDialogue.setCancelable(false);
        findingDialogue.setMessage("Looking for opponents...");
        findingDialogue.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                cancel();
                mBluetoothAdapter.cancelDiscovery();
            }
        });

        hostingDialogue = new ProgressDialog(this);
        hostingDialogue.setCancelable(false);
        hostingDialogue.setMessage("Waiting for opponents...");
        hostingDialogue.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // Stop being discoverable without having to prompt the user.
                mBluetoothAdapter.getDefaultAdapter().disable();
                mBluetoothAdapter.getDefaultAdapter().enable();
                cancel();
            }
        });

        FacebookSdk.sdkInitialize(getApplicationContext(), FACEBOOK_LOGIN_CODE);
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_main);


        highScores = (Button) findViewById(R.id.highscore_but);
        final Intent intent = new Intent(this, HighScores.class);
        highScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        AppEventsLogger.activateApp(this);
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_but);

        // Requests Bluetooth on app launch.
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(getApplicationContext(), "Your device does not support Bluetooth.\nThis game will not work.",Toast.LENGTH_SHORT).show();
        } else {
            // Device supports Bluetooth.
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        // Initial login check.
        if (AccessToken.getCurrentAccessToken() == null){
            //User logged out
            loggedIn = false;
        } else {
            loggedIn = true;
        }

        // Listener to track login/logout.
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                if (currentAccessToken == null){
                    //User logged out
                    loggedIn = false;
                } else {
                    loggedIn = true;
                }
            }
        };

        // Facebook button callback.
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException e) {
            }
        });

        // Title textview stuff for fancy font.
        TextView title = (TextView) findViewById(R.id.titleText);
        // http://www.fonts101.com/fonts/view/Techno/66599/LASER_GUN; free for personal use
        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/laser.ttf");
        title.setTypeface(custom_font);
        title.setText("Laser Chess");
    }

    /**
     *  Handles results for facebook, listview, and permissions as of right now
     *  NOT boilerplate
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT){
            if (resultCode != RESULT_OK){
                Toast.makeText(getApplicationContext(), "Bluetooth must be active to play.",Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == FACEBOOK_LOGIN_CODE){
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == OPPONENT_LIST_ACTIVITY) {
            mDeviceList.clear();
            if(resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                BluetoothDevice target = bundle.getParcelable("targetDevice");

                ConnectThread connectThread = new ConnectThread(target);
                connectThread.startConnect();

            }
        }
    }

    /** boilerplate with minor alterations
     * https://developer.android.com/guide/topics/connectivity/bluetooth.html
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array.
                mDeviceList.add(device);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (!cancel){
                    Toast.makeText(getApplicationContext(), "Opponent search finished.", Toast.LENGTH_SHORT).show();
                    findingDialogue.dismiss();
                    // Start the OpponentList Activity
                    Intent newIntent = new Intent(MainActivity.this, OpponentList.class);
                    newIntent.putParcelableArrayListExtra("devicelist", mDeviceList);
                    startActivityForResult(newIntent, OPPONENT_LIST_ACTIVITY);
                }
                cancel = false;
            }
        }
    };

    /** OnClick for Host a Match. Makes the device discoverable. Opens a BluetoothServerSocket in
     * the AcceptThread.
     *  NOT boilerplate.
     */
    public void hostClick(View v){
        if (!loggedIn){
            Toast.makeText(getApplicationContext(), "Log in to host a match!",Toast.LENGTH_SHORT).show();
        } else if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(getApplicationContext(), "No Bluetooth On Device. No Play.",Toast.LENGTH_SHORT).show();
            // Device supports Bluetooth.
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                Profile profile = Profile.getCurrentProfile();
                username = "Laser-Chess: " +profile.getFirstName() + " " + profile.getLastName();
                mBluetoothAdapter.setName(username);
                passingName = profile.getFirstName() + " " + profile.getLastName();


                // LOL this is absolutely retarded but it is the only way.
                Method method;
                try {
                    method = mBluetoothAdapter.getClass().getMethod("setScanMode", int.class, int.class);
                    method.invoke(mBluetoothAdapter,BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, 120);
                    hostingDialogue.show();
                    hostThread = new AcceptThread();
                    hostThread.startAccept();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    /** OnClick for Find a Match. Uses startDiscovery() to look for devices.
     * Opens a BluetoothSocket to connect to hosts in the ConnectThread.
     * NOT boilerplate
     **/
    public void findClick(View v){
        if (!loggedIn){
            Toast.makeText(getApplicationContext(), "Log in to find a match!",Toast.LENGTH_SHORT).show();
        }

        else if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(getApplicationContext(), "No Bluetooth On Device. No Play.",Toast.LENGTH_SHORT).show();
        } else {
            // Device supports Bluetooth.
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                mDeviceList = new ArrayList<BluetoothDevice>();
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION);
            }
        }
    }

    /** boilerplate code
     *  https://developer.android.com/guide/topics/connectivity/bluetooth.html
     **/
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // task you need to do.
                    mBluetoothAdapter.startDiscovery();

                    IntentFilter filter = new IntentFilter();
                    filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
                    filter.addAction(BluetoothDevice.ACTION_FOUND);
                    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                    // Don't forget to unregister during onDestroy
                    registerReceiver(mReceiver, filter);

                    // Dialogue closes if user clicks cancel, or the scan finishes by itself with
                    // DISCOVERY_FINISHED in broadcast receiver
                    findingDialogue.show();
                } else {
                    // permission denied, boo!
                    Toast.makeText(getApplicationContext(), "Enable location permissions to find opponents.",Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }


    /** Handles the unregistering of the broadcast receiver.
     * NOT boilerplate
     */
    public void onDestroy() {
        try{
            if(mReceiver!=null)
                unregisterReceiver(mReceiver);
        }catch(Exception e)
        {

        }
        super.onDestroy();

    }

    /** Delay after cancelling a Host. This gives the Bluetooth Adapter time to turn back on after
     *  cancelling discoverability.
     *  NOT boilerplate
     */
    public void cancel(){
        cancel=true;

        if (hostThread!=null) {
            hostThread.cancel();
        }

        final Button find = (Button) findViewById(R.id.findmatch_but);
        final Button host = (Button) findViewById(R.id.hostmatch_but);

        find.setEnabled(false);
        host.setEnabled(false);

        Timer buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        find.setEnabled(true);
                        host.setEnabled(true);
                    }
                });
            }
        }, 1500);
        mBluetoothAdapter.getDefaultAdapter().enable();
    }


    /**
     * NOT boilerplate
     *  Called eventually by the host AND the player to connect to the host.
     *  I distinguish the host for the sole reason of knowing who goes first.
     */
    public void startGame(BluetoothSocket sock, Boolean host){

        hostingDialogue.dismiss();

        Intent newGame = new Intent(this, GameLogic.class);
        newGame.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        newGame.putExtra("host", host);
        if(passingName == null)
            passingName = "Anon";
        newGame.putExtra("username", passingName);

        SocketManager.setSocket(mainSocket);
        startActivity(newGame);
    }

    /**
     * boilerplate code with severe threading alterations
     * https://developer.android.com/guide/topics/connectivity/bluetooth.html#ConnectingDevices
     */
    public class AcceptThread{
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("Laser-Chess", uid);
            } catch (IOException e) { }
            mmServerSocket = tmp;
        }

        // LOL.
        public void startAccept() {
            new Thread(
                new Runnable() {
                    public void run() {
                        BluetoothSocket socket;
                        // Keep listening until exception occurs or a socket is returned
                        while (true) {
                            try {
                                socket = mmServerSocket.accept();
                            } catch (IOException e) {
                                // goes here when user cancels hosting
                                break;
                            }
                            // If a connection was accepted
                            if (socket != null) {
                                // Do work to manage the connection (in a separate thread)
                                try {
                                    // got the BluetoothSocket
                                    mmServerSocket.close();
                                    mainSocket=socket;
                                    startGame(socket, true);
                                } catch (Exception e) {
                                }
                                break;
                            }
                        }
                    }
                }
            ).start();
        }

        /** Will cancel the listening socket, and cause the thread to finish */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) { }
        }
    }


    /**
     * Another thread class, courtesy of android Bluetooth documentation.
     * boilerplate code with severe threading alterations
     */
    private class ConnectThread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(uid);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void startConnect() {
            new Thread(
                    new Runnable() {
                        public void run() {
                            // Cancel discovery because it will slow down the connection
                            mBluetoothAdapter.cancelDiscovery();

                            try {
                                // Connect the device through the socket. This will block
                                // until it succeeds or throws an exception
                                mmSocket.connect();
                            } catch (IOException connectException) {
                                // Unable to connect; close the socket and get out
                                try {
                                    mmSocket.close();
                                } catch (IOException closeException) {
                                    Toast.makeText(getApplicationContext(), "Unable to connect; socket closed;",Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }
                            mainSocket=mmSocket;
                            // Do work to manage the connection (in a separate thread)
                            startGame(mmSocket, false);
                        }
                    }
            ).start();
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}

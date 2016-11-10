/**
 * Created by daniel on 11/6/16. Much Bluetooth code here adapted from:
 * https://developer.android.com/guide/topics/connectivity/bluetooth.html.
 */


package cs371m.laser_chess;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
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
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends FragmentActivity {

    Boolean loggedIn; // Facebook
    Boolean cancel;
    String username;

    private CallbackManager callbackManager; // for Facebook login
    BluetoothAdapter mBluetoothAdapter;
    private final static int FACEBOOK_LOGIN_CODE = 1;
    private final static int REQUEST_ENABLE_BT = 2;

    private final static int LOCATION_PERMISSION= 11;

    private ProgressDialog findingDialogue;
    private ProgressDialog hostingDialogue;

    ArrayList<BluetoothDevice> mDeviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        cancel = false;

        // Dialogue code taken from Bluetooth tutorial at // http://www.londatiga.net
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

        // Dialogue code adapted from Bluetooth tutorial at // http://www.londatiga.net
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


        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "cs371m.laser_chess",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }






        FacebookSdk.sdkInitialize(getApplicationContext(), FACEBOOK_LOGIN_CODE);
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_main);

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

        // Facebook button callback.//////////////////////////////////////////////////////////////////////////////
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            // http://stackoverflow.com/questions/33139932/how-to-get-email-id-from-android-facebook-sdk-4-6-0
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

    // Facebook login activity result.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT){
            if (resultCode != RESULT_OK){
                Toast.makeText(getApplicationContext(), "Bluetooth must be active to play.",Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == FACEBOOK_LOGIN_CODE){
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    // https://developer.android.com/guide/topics/connectivity/bluetooth.html
    // Create a BroadcastReceiver for ACTION_FOUND
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
                    startActivity(newIntent);
                }
                cancel = false;
            }
        }
    };

    // OnClick for Host a Match. Makes the device discoverable.
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
                username = profile.getFirstName() + " " + profile.getLastName();
                mBluetoothAdapter.setName(username);
                // Gets already paired devices.
                //mDeviceList = new ArrayList<BluetoothDevice>(mBluetoothAdapter.getBondedDevices());

                // LOL this is absolutely retarded but my goodness it is the only way, I promise.
                Method method;
                try {
                    method = mBluetoothAdapter.getClass().getMethod("setScanMode", int.class, int.class);
                    method.invoke(mBluetoothAdapter,BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, 120);
                    hostingDialogue.show();
                    Log.e("invoke","method invoke successfully");
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    }

    // OnClick for Find a Match. Uses startDiscovery() to look for devices.
    public void findClick(View v){
        if (!loggedIn){
            Toast.makeText(getApplicationContext(), "Log in to find a match!",Toast.LENGTH_SHORT).show();
        } else if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(getApplicationContext(), "No Bluetooth On Device. No Play.",Toast.LENGTH_SHORT).show();
        } else {
            // Device supports Bluetooth.
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                // Gets already paired devices.
                //mDeviceList = new ArrayList<BluetoothDevice>(mBluetoothAdapter.getBondedDevices());

                mDeviceList = new ArrayList<BluetoothDevice>();
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION);
            }
        }
    }


    // https://developer.android.com/training/permissions/requesting.html?hl=es#perm-check
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
     *
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
     */
    public void cancel(){
        cancel=true;

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
}

package cs371m.laser_chess;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MainActivity extends FragmentActivity {

    Boolean loggedIn; // yeah

    private CallbackManager callbackManager; // for Facebook login
    BluetoothAdapter mBluetoothAdapter;
    private final static int FACEBOOK_LOGIN_CODE = 1;
    private final static int REQUEST_ENABLE_BT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);






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

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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
                //System.out.println("boo");
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

        // Listener for Find A Match button.
        Button findMatch = (Button) findViewById(R.id.findmatch_but);
        findMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!loggedIn){
                    Toast.makeText(getApplicationContext(), "Log in to find a match!",Toast.LENGTH_SHORT).show();
                } else if (mBluetoothAdapter == null) {
                    // Device does not support Bluetooth
                    Toast.makeText(getApplicationContext(), "No Bluetooth On Device. No Play.",Toast.LENGTH_SHORT).show();
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    } else {
                        // Start matchmaking
                    }
                }

            }
        });



    }

    // Facebook login activity result.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT){
            if (resultCode != RESULT_OK){
                Toast.makeText(getApplicationContext(), "Bluetooth was not activated.",Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == FACEBOOK_LOGIN_CODE){
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

}

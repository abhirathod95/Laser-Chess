package cs371m.laser_chess;

import android.bluetooth.BluetoothSocket;
import android.content.res.TypedArray;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Abhi on 11/8/2016.
 */

public class GameLogic extends FragmentActivity {

    private ImageButton rotate_left, rotate_right;
    private GameBoard gameBoard;
    private Cell selectedCell;

    private BluetoothSocket btSocket;
    final int MESSAGE_READ = 8888;
    private boolean host;
    ConnectedThread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        selectedCell = null;
        gameBoard = (GameBoard) findViewById(R.id.board);
        rotate_left = (ImageButton) findViewById(R.id.rotate_left);
        rotate_right = (ImageButton) findViewById(R.id.rotate_right);
        rotate_left.setEnabled(false);
        rotate_right.setEnabled(false);
        setOnClickListeners();
        gameBoard.invalidate();


        // I identify host for who goes first. take care of that later abhi
        host = getIntent().getBooleanExtra("host", false);

        btSocket = SocketManager.getSocket();
        thread = new ConnectedThread(btSocket);
        thread.runThread();

    }

    public void turnOver() {
        // add sending the data here
        gameBoard.invalidate();
        Toast.makeText(getApplicationContext(), "Turn over!", Toast.LENGTH_LONG).show();
    }

    public void setOnClickListeners() {
        rotate_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Piece selected = selectedCell.getPiece();
                if(selected == null || !selected.isRotatable()) {
                    Toast.makeText(getApplicationContext(), "Invalid Move! Piece not rotatable!", Toast.LENGTH_LONG).show();
                    return;
                }
                selected.rotate(-90);
                turnOver();
            }
        });
        rotate_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Piece selected = selectedCell.getPiece();
                if(selected == null || !selected.isRotatable()) {
                    Toast.makeText(getApplicationContext(), "Invalid Move! Piece not rotatable!", Toast.LENGTH_LONG).show();
                    return;
                }
                selected.rotate(90);
                turnOver();

            }
        });

        gameBoard.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    int startX = (int) event.getX();
                    int startY = (int) event.getY();
                    Cell newSelectedCell = gameBoard.getSelectedCell(startX, startY);
                    if(selectedCell == null) {
                        if(newSelectedCell.getPiece() != null && newSelectedCell.getPiece().isFriendly()) {
                            selectedCell = newSelectedCell;
                            rotate_left.setEnabled(true);
                            rotate_right.setEnabled(true);
                        } else
                            return false;
                    } else {
                        Piece selected =  selectedCell.getPiece();
                        if(selected.isMovable())
                            if(selected.move(selectedCell, newSelectedCell))
                                turnOver();
                        selectedCell = null;
                        rotate_left.setEnabled(false);
                        rotate_right.setEnabled(false);
                    }
                }
                return false;
            }
        });
    }

    // MESSAGES GO HERE, THIS IS WHERE WE PROB WILL HANDLE ENDTURN STUFF
    public Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            Toast.makeText(getApplicationContext(), "message received",Toast.LENGTH_SHORT).show();
        }
    };


    public void testSocket(View view){
        thread.write(("lul").getBytes());
    }


    private class ConnectedThread{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void runThread() {
            new Thread(
                    new Runnable() {
                        public void run() {
                            byte[] buffer = new byte[1024];  // buffer store for the stream
                            int bytes; // bytes returned from read()

                            // Keep listening to the InputStream until an exception occurs
                            while (true) {
                                try {
                                    // Read from the InputStream
                                    bytes = mmInStream.read(buffer);
                                    // Send the obtained bytes to the UI activity
                                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                            .sendToTarget();
                                } catch (IOException e) {
                                    break;
                                }
                            }
                        }
                    }
            ).start();
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}
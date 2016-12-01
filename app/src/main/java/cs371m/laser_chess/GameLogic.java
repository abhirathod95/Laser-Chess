package cs371m.laser_chess;

import android.app.AlertDialog;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Abhi on 11/8/2016.
 */

public class GameLogic extends FragmentActivity {


    public enum Color {
        BLACK, RED
    }

    private String username;
    private String enemyUsername;
    private ImageButton rotate_left, rotate_right;
    private TextView turnText;
    private GameBoard gameBoard;
    private Cell selectedCell;
    private Color color;
    private SharedPreferences prefs;
    private BluetoothSocket btSocket;
    final int MESSAGE_READ = 8888;
    private boolean userTurn;
    ConnectedThread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        username = getIntent().getStringExtra("username");
        prefs =  this.getSharedPreferences("scores", MODE_PRIVATE);
        if(!prefs.contains(username)) {
            System.out.println("ADDING IN BECAUSE PREFS HAS NO ME FOR " + username);
            updateScores(username, "0,0");
        }

        selectedCell = null;
        gameBoard = (GameBoard) findViewById(R.id.board);
        rotate_left = (ImageButton) findViewById(R.id.rotate_left);
        rotate_right = (ImageButton) findViewById(R.id.rotate_right);
        rotate_left.setEnabled(false);
        rotate_right.setEnabled(false);
        setOnClickListeners();

        TextView colorText = (TextView) findViewById(R.id.colorText);
        turnText = (TextView) findViewById(R.id.turnText);

        // Set the colors and make sure that host is always black
        userTurn = getIntent().getBooleanExtra("host", false);
        if(userTurn) {
            color = Color.BLACK;
            colorText.setText(getText(R.string.black_color));
            turnText.setText(getText(R.string.your_turn));
            gameBoard.setOnTouchListener(playing);
        } else {
            color = Color.RED;
            colorText.setText(getText(R.string.red_color));
            turnText.setText(getText(R.string.player_two_turn));
            gameBoard.setOnTouchListener(notPlaying);
        }
        gameBoard.setColor(color);
        gameBoard.invalidate();
        btSocket = SocketManager.getSocket();
        thread = new ConnectedThread(btSocket);
        thread.runThread();

        // Send score on initialization
        String scoreSend = "score" + "," + username + "," + prefs.getString(username, "ERROR") + ",";
        thread.write(scoreSend.getBytes());
    }

    public void updateScores(String key, String val) {
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(key, val);
        prefsEditor.apply();
    }

    public void addWinLoss(String name, boolean win) {
        String scores = prefs.getString(name, "0,0");
        String[] splits = scores.split(",");
        int wins = Integer.parseInt(splits[0]);
        int loses = Integer.parseInt(splits[1]);
        System.out.println("ADDING IN FOR " + name + " with win " + win + " and loss " + loses);
        if(win)
            wins++;
        else
            loses++;
        System.out.println("ADDING IN FOR " + name + " with win " + win + " and loss " + loses);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(name, wins + "," + loses);
        prefsEditor.apply();
    }

    // 0 to shoot black's laser, 1 to shoot red's laser
    public void shootLaser(Color type) {
        System.out.println("IN SHOOT LASER");
        turnText.setText(getText(R.string.firing_laser));
        Cell currCell;
        int in, out, rowInd, columnInd;
        List<Float> laser = new ArrayList<Float>();

        // Get starting cell
        if(type == Color.BLACK) {
            Piece sphinx = gameBoard.getCell(0, 0).getPiece();
            if(sphinx.getOrient() == 0) {
                rowInd = 1;
                columnInd = 0;
                in = 0;
                out = 2;
            }else {
                rowInd = 0;
                columnInd = 1;
                in = 3;
                out = 1;
            }
        } else {
            Piece sphinx = gameBoard.getCell(gameBoard.getNumRows() - 1, gameBoard.getNumColumns() - 1).getPiece();
            if(sphinx.getOrient() == 1) {
                rowInd = gameBoard.getNumRows() - 1;
                columnInd = gameBoard.getNumColumns() - 2;
                in = 1;
                out = 3;
            } else {
                rowInd = gameBoard.getNumRows() - 2;
                columnInd = gameBoard.getNumColumns() - 1;
                in = 2;
                out = 0;
            }
        }

        boolean hasPiece;
        float startX, startY, endX, endY;;
        while(true) {
            currCell = gameBoard.getCell(rowInd, columnInd);
            Piece currPiece = currCell.getPiece();
            hasPiece = false;
            if(currPiece != null) {
                out = currPiece.reflectedSide(in);
                hasPiece = true;
            }

            System.out.println(in + " " + out);

            if(out == -2){
                break;
            }
            if(out == -1) {
                if(currPiece.getType() == Piece.Type.PHARAOH) {
                    gameOver(currPiece.getBitmapColor());
                    break;

                }
                currCell.setPiece(null);
                currCell.setCurrentlySelected(false);
                break;
            }

            if(hasPiece) {
                System.out.println("HAS PIECE");
                startX = getX(in, currCell);
                startY = getY(in, currCell);
                endX = currCell.getX() + (currCell.getWidth() / 2);
                endY = currCell.getY() + (currCell.getWidth() / 2);
                System.out.println(startX + " " + startY + " " + endX + " " + endY);
                gameBoard.addLaserPoint(startX, startY);
                gameBoard.addLaserPoint(endX, endY);
                startX = endX;
                startY = endY;
                endX = getX(out, currCell);
                endY = getY(out, currCell);
                gameBoard.addLaserPoint(startX, startY);
                gameBoard.addLaserPoint(endX, endY);
                gameBoard.invalidate();
            } else {
                System.out.println("HAS NO PIECE");
                startX = getX(in, currCell);
                startY = getY(in, currCell);
                endX = getX(out, currCell);
                endY = getY(out, currCell);
                System.out.println(startX + " " + startY + " " + startX + " " + endY);
                gameBoard.addLaserPoint(startX, startY);
                gameBoard.addLaserPoint(endX, endY);
            }

            rowInd = getNextRowNum(out, rowInd);
            columnInd = getNextColumnNum(out, columnInd);
            in = oppositeSide(out);
            out = oppositeSide(in);

            if(rowInd < 0 || columnInd < 0 | rowInd > 7 || columnInd > 9) {
                break;
            }
        }
        gameBoard.invalidate();
    }

    public void gameOver(GameLogic.Color color) {
        // close connection
        // moved to onDestroy
        // thread.cancel();

        AlertDialog alertDialog = new AlertDialog.Builder(this).create(); //Read Update
        alertDialog.setTitle("GAME OVER!");
        if(color == this.color) {
            alertDialog.setMessage("You Lost!");
            System.out.println("ADDING IN LOSS FOR us " + username);
            addWinLoss(username, false);
            System.out.println("ADDING IN WIN FOR " + enemyUsername);
            addWinLoss(enemyUsername, true);
        } else {
            alertDialog.setMessage("You Win!");
            System.out.println("ADDING IN WIN FOR US " + username);
            addWinLoss(username, true);
            System.out.println("ADDING IN LOSS FOR " + enemyUsername);
            addWinLoss(enemyUsername, false);
        }

        final GameLogic act = this;
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "High Scores", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(act, HighScores.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            }
        });

        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Main Menu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Intent intent = new Intent(act, MainActivity.class);
                //startActivity(intent);
                //absolutely retarded
                finish();
            }
        });


        alertDialog.show();
    }


    public int oppositeSide(int side) {
        switch (side) {
            case 0:
                return 2;
            case 1:
                return 3;
            case 2:
                return 0;
            case 3:
                return 1;
        }
        return -1;
    }

    public int getNextRowNum(int out, int row){
        switch (out) {
            case 0:
                return row - 1;
            case 2:
                return row + 1;
            case 1:
            case 3:
                return row;
        }
        return -1;
    }

    public int getNextColumnNum(int out, int column){
        switch (out) {
            case 0:
            case 2:
                return column;
            case 1:
                return column + 1;
            case 3:
                return column - 1;
        }
        return -1;
    }

    public float getX(int side, Cell cell) {
        switch(side) {
            case 0:
            case 2:
                return cell.getX() + (cell.getWidth() / 2);
            case 1:
                return cell.getX() + cell.getWidth();
            case 3:
                return cell.getX();
        }
        return 0;
    }

    public float getY(int side, Cell cell) {
        switch(side) {
            case 0:
                return cell.getY();
            case 1:
            case 3:
                return cell.getY() + (cell.getHeight() / 2);
            case 2:
                return cell.getY() + cell.getHeight();
        }
        return 0;
    }

    public void turnOver(String change) {
        change = selectedCell.getI() + "," + selectedCell.getJ() + "," + change;

        // Make everything disabled so the player cant hit anything
        rotate_left.setEnabled(false);
        rotate_right.setEnabled(false);
        selectedCell.setCurrentlySelected(false);
        selectedCell = null;
        gameBoard.invalidate();
        gameBoard.setOnTouchListener(notPlaying);

        // Send off the data so they don't have to wait till after we shoot our laser
        thread.write((change).getBytes());

        // shoot our laser
        shootLaser(color);
        turnText.setText(getText(R.string.player_two_turn));
    }

    public void setOnClickListeners() {
        rotate_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Piece selected = selectedCell.getPiece();
                if(selected == null || !selected.isRotatable() || !selected.rotate(-90))
                    Toast.makeText(getApplicationContext(), getText(R.string.invalid_rotate), Toast.LENGTH_LONG).show();
                else
                    turnOver("rotate,left,");
            }
        });
        rotate_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Piece selected = selectedCell.getPiece();
                if(selected == null || !selected.isRotatable() || !selected.rotate(90))
                    Toast.makeText(getApplicationContext(), getText(R.string.invalid_rotate), Toast.LENGTH_LONG).show();
                else
                    turnOver("rotate,right,");
            }
        });
    }

    public Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            byte[] writeBuf = (byte[]) msg.obj;
            String writeMessage = new String(writeBuf);
            //Toast.makeText(getApplicationContext(), "message received: " + writeMessage,Toast.LENGTH_SHORT).show();

            String[] instructions = writeMessage.split(",");

            // Special case where we are first connecting and getting the score
            if(instructions[0].equals("score")) {
                String name = instructions[1];
                enemyUsername = name;
                String score = instructions[2] + "," + instructions[3];
                System.out.println("ADDING IN THE FIRST REQUEST SCORE FOR " + name);
                updateScores(name, score);
                return;
            }

            Cell changingCell = gameBoard.getCell(Integer.parseInt(instructions[0]), Integer.parseInt(instructions[1]));

            if(instructions[2].equals("rotate")) {
                if(instructions[3].equals("right"))
                    changingCell.getPiece().rotate(90);
                else
                    changingCell.getPiece().rotate(-90);
            } else if(instructions[2].equals("move")){
                Cell newCell = gameBoard.getCell(Integer.parseInt(instructions[3]), Integer.parseInt(instructions[4]));
                changingCell.getPiece().move(changingCell, newCell);
            }

            gameBoard.invalidate();

            if(color == Color.BLACK)
                shootLaser(Color.RED);
            else
                shootLaser(Color.BLACK);

            turnText.setText(getText(R.string.your_turn));
            gameBoard.setOnTouchListener(playing);
        }
    };

    protected View.OnTouchListener playing = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int startX = (int) event.getX();
                int startY = (int) event.getY();
                Cell newSelectedCell = gameBoard.getSelectedCell(startX, startY);
                if(selectedCell == null) {
                    if(newSelectedCell.getPiece() != null && newSelectedCell.getPiece().isFriendly()) {
                        selectedCell = newSelectedCell;
                        selectedCell.setCurrentlySelected(true);
                        gameBoard.invalidate();
                        rotate_left.setEnabled(true);
                        rotate_right.setEnabled(true);
                    } else
                        return false;
                } else {
                    Piece selected =  selectedCell.getPiece();
                    selectedCell.setCurrentlySelected(false);
                    gameBoard.invalidate();
                    if(selected.isMovable())
                        if(selected.move(selectedCell, newSelectedCell))
                            turnOver("move," + newSelectedCell.getI() + "," + newSelectedCell.getJ() + ",");
                    selectedCell = null;
                    rotate_left.setEnabled(false);
                    rotate_right.setEnabled(false);
                }
            }
            return false;
        }
    };

    protected View.OnTouchListener notPlaying = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Toast.makeText(getApplicationContext(), getText(R.string.not_your_turn), Toast.LENGTH_LONG).show();
            return false;
        }
    };

    // code adapted from android bluetooth documentation.
    // changed the threading to run in background thread so
    // that the ui thread doesn't cock itself

    private class ConnectedThread{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        // Dont ever call this constructor, i do it for us
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

        // I CALL THIS FOR US DONT EVER USE THIS ABHI
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

        /* Call this to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        thread.cancel();

    }
}
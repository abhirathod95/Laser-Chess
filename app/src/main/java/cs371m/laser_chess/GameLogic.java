package cs371m.laser_chess;

import android.content.res.TypedArray;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * Created by Abhi on 11/8/2016.
 */

public class GameLogic extends FragmentActivity {

    private ImageButton rotate_left, rotate_right;
    private GameBoard gameBoard;
    private Cell selectedCell;

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
}
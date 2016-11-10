package cs371m.laser_chess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Abhi on 11/8/2016.
 * This class does all the work required to draw things
 */

public class GameBoard extends View {

    private Paint paint;
    private Cell[][] cells = null;
    private int cellHeight;
    private int cellWidth;
    private int numRows;
    private int numColumns;

    public GameBoard(Context context, AttributeSet aSet) {
        super(context, aSet);
        paint = new Paint();
        numColumns = 10;
        numRows = 8;
    }

    public Cell getSelectedCell(int x, int y) {
        System.out.println(x + " " + y);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 10; j++) {
                if(cells[i][j].getBackground().contains(x,y)) {
                    System.out.println("CELL at " + i + " " + j + " selected!");
                    return cells[i][j];
                }
            }
        }
        return new Cell(0, 0, cellWidth, cellHeight, Cell.Type.EMPTY);
    }

    synchronized private void initializePieces() {
        // Create the board
        cells = new Cell[8][10];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 10; j++) {
                Cell currCell = new Cell(j * cellWidth, i * cellHeight, cellWidth, cellHeight, Cell.Type.EMPTY);
                cells[i][j] = (currCell);
            }
        }

        // Create the pieces
        Piece sphinx = new Sphinx(getContext(), true);
        Piece red_sphinx = new Sphinx(getContext(), false);
        Piece scarab = new Scarab(getContext(), true);
        Piece red_scarab = new Scarab(getContext(), false);
        Piece pyramid = new Pyramid(getContext(), true);
        Piece red_pyramid = new Pyramid(getContext(), false);
        Piece anubis = new Anubis(getContext(), true);
        Piece red_anubis = new Anubis(getContext(), false);
        Piece pharaoh = new Pharaoh(getContext(), true);
        Piece red_pharaoh = new Pharaoh(getContext(), false);

        // Initial configuration of pieces

        // Pharaohs
        cells[0][5].setPiece(pharaoh);
        cells[7][4].setPiece(red_pharaoh);

        // Sphinxs
        cells[0][0].setPiece(sphinx);
        cells[7][9].setPiece(red_sphinx);

        // Scarabs
        cells[3][4].setPiece(red_scarab);
        cells[3][5].setPiece(red_scarab.copy());
        cells[3][5].piece.rotate(90);
        cells[4][4].setPiece(scarab);
        cells[4][5].setPiece(scarab.copy());
        cells[4][4].piece.rotate(90);

        // Anubis
        cells[0][4].setPiece(anubis);
        cells[0][6].setPiece(anubis.copy());
        cells[7][3].setPiece(red_anubis);
        cells[7][5].setPiece(red_anubis.copy());


        // Black Pyramids
        cells[3][2].setPiece(pyramid);
        cells[4][9].setPiece(pyramid.copy());
        cells[2][3].setPiece(pyramid.copy());
        cells[2][3].piece.rotate(90);
        cells[4][2].setPiece(cells[2][3].piece.copy());
        cells[3][9].setPiece(cells[2][3].piece.copy());
        cells[7][2].setPiece(cells[2][3].piece.copy());
        cells[6][7].setPiece(pyramid.copy());
        cells[6][7].piece.rotate(180);

        // Red Pyramids
        cells[1][2].setPiece(red_pyramid);
        cells[0][7].setPiece(red_pyramid.copy());
        cells[0][7].piece.rotate(-90);
        cells[4][0].setPiece(cells[0][7].piece.copy());
        cells[3][7].setPiece(cells[0][7].piece.copy());
        cells[5][6].setPiece(cells[0][7].piece.copy());
        cells[3][0].setPiece(red_pyramid.copy());
        cells[3][0].piece.rotate(180);
        cells[4][7].setPiece(cells[3][0].piece.copy());


    }

    @Override
    synchronized public void onDraw(Canvas canvas) {

        //create a black canvas
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        cellHeight = getHeight() / 8;
        cellWidth = getWidth() / 10;

        if(cells == null) {
            initializePieces();
        }

        for(int i = 0; i < numRows; i++) {
            for(int j = 0; j < numColumns; j++) {
                Cell cell = cells[i][j];
                canvas.drawRect(cell.getBackground(), paint);
                if (cell.getPiece() != null) {
                    canvas.drawBitmap(cell.getPiece().getBitmap(), null, cell.getBackground(), paint);
                }
            }
        }
    }
}

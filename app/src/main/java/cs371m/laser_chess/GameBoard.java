package cs371m.laser_chess;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Abhi on 11/8/2016.
 * This class does all the work required to draw things
 */

public class GameBoard extends View {

    private Paint paint;
    private float startX, startY, endX, endY;
    private Cell[][] cells = null;
    private List<Float> laser = new ArrayList<Float>();
    private int cellHeight;
    private int cellWidth;
    private int numRows;
    private int numColumns;
    private GameLogic.Color playerColor;

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
        cells = new Cell[numRows][numColumns];
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numColumns; j++) {
                Cell currCell = new Cell(j * cellWidth, i * cellHeight, cellWidth, cellHeight, Cell.Type.EMPTY);
                cells[i][j] = (currCell);
            }
        }

        Boolean isBlack = false;
        if(playerColor == GameLogic.Color.BLACK)
            isBlack = true;

        // Create the pieces
        Piece sphinx = new Sphinx(getContext(), isBlack, GameLogic.Color.BLACK);
        Piece red_sphinx = new Sphinx(getContext(), !isBlack,GameLogic.Color.RED);
        Piece scarab = new Scarab(getContext(), isBlack, GameLogic.Color.BLACK);
        Piece red_scarab = new Scarab(getContext(), !isBlack, GameLogic.Color.RED);
        Piece pyramid = new Pyramid(getContext(), isBlack, GameLogic.Color.BLACK);
        Piece red_pyramid = new Pyramid(getContext(), !isBlack, GameLogic.Color.RED);
        Piece anubis = new Anubis(getContext(), isBlack, GameLogic.Color.BLACK);
        Piece red_anubis = new Anubis(getContext(), !isBlack, GameLogic.Color.RED);
        Piece pharaoh = new Pharaoh(getContext(), isBlack, GameLogic.Color.BLACK);
        Piece red_pharaoh = new Pharaoh(getContext(), !isBlack, GameLogic.Color.RED);

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
        Cell selectedCell = null;

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
                if(cell.isCurrentlySelected()) {
                    selectedCell = cell;
                    continue;
                }
                if (cell.getPiece() != null) {
                    canvas.drawBitmap(cell.getPiece().getBitmap(), null, cell.getBackground(), paint);
                }
                canvas.drawRect(cell.getBackground(), paint);
            }
        }


        // Draw the selected cell last on top with a different stroke
        //paint.setColor(Color.WHITE);
        paint.setStrokeWidth(15);
        if(selectedCell != null){
            if (selectedCell.getPiece() != null) {
                canvas.drawBitmap(selectedCell.getPiece().getBitmap(), null, selectedCell.getBackground(), paint);
            }
            canvas.drawRect(selectedCell.getBackground(), paint);
        }

        // Draw the laser
        float[] laser = getFloatArr();
        if(laser.length > 0) {
            System.out.println(startX + " " + startY + " " + endX + " " + endY);
            paint.setColor(Color.RED);
            canvas.drawLines(laser, paint);
        }
    }

    public void setColor(GameLogic.Color color) {
        this.playerColor = color;
    }

    public Cell getCell(int i, int j) {
        return cells[i][j];
    }

    public int getNumColumns() {
        return numColumns;
    }

    public int getNumRows() {
        return numRows;
    }

    public void addLaserPoint(float x, float y){
        laser.add(x);
        laser.add(y);
    }

    public void clearLaser() {
        laser.clear();
    }

    public float[] getFloatArr() {
        int i = 0;
        float[] points = new float[laser.size()];
        for (Float f : laser) {
            points[i++] = (f != null ? f : Float.NaN); // Or whatever default you want.
        }
        laser.clear();
        return points;
    }
}

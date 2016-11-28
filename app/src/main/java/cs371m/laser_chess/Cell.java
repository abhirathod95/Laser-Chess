package cs371m.laser_chess;

import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * Created by Abhi on 11/8/2016.
 */

public class Cell {


    public enum Type {
        EMPTY
    }

    protected Piece piece;
    protected Rect background;
    protected Type type;
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    public Cell(int x, int y, int width, int height, Type type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.background = new Rect(x, y, x+width, y+height);
        this.type = type;
        this.piece = null;
    }

    public Rect getBackground() {
        return background;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public Piece getPiece() {
        return piece;
    }
}

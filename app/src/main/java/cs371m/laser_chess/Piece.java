package cs371m.laser_chess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.widget.Toast;

/**
 * Created by Abhi on 11/9/2016.
 */

public class Piece {

    public enum Type {
        PYRAMID, PHARAOH, ANUBIS, SCARAB, SPHINX
    }

    protected Context mContext;
    protected Bitmap bitmap;
    protected Type type;
    protected boolean friendly;
    protected boolean rotatable;
    protected boolean movable;

    public Piece(Context context, boolean friendly) {
        this.mContext = context;
        this.friendly = friendly;
        bitmap = null;
        rotatable = true;
        movable = true;
    }

    public Piece copy() {
        Piece piece = new Piece(mContext, friendly);
        piece.bitmap = this.bitmap;
        piece.type = this.type;
        piece.rotatable = this.rotatable;

        return piece;
    }

    public boolean isMovable() {
        return movable;
    }

    public boolean isRotatable() {
        return rotatable;
    }

    public Type getType() {
        return type;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public boolean isFriendly() {
        return friendly;
    }

    public void rotate(float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public boolean move(Cell from, Cell to) {
        if(Math.abs(to.getX() - from.getX()) > (to.getWidth() + 1) || Math.abs(to.getY() - from.getY()) > (to.getHeight() + 1)) {
            Toast.makeText(mContext, "Invalid Move! You can only move one space in a turn!", Toast.LENGTH_LONG).show();
            return false;
        }
        if(from == to) {
            return false;
        }

        if(to.getPiece() == null) {
            to.setPiece(from.getPiece());
            from.setPiece(null);
        }
        return true;
    }
}

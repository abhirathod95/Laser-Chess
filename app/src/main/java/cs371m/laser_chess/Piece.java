package cs371m.laser_chess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.widget.Toast;

import java.util.EnumSet;


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
    // 0 for default png, 1 for rotated 90 degrees, 2 for rotated 180, etc...
    protected int orient;

    public Piece(Context context, boolean friendly) {
        this.mContext = context;
        this.friendly = friendly;
        bitmap = null;
        rotatable = true;
        movable = true;
        orient = 0;
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

    // ANGLE SHOULD ALWAYS BE 90, 180 or 270 IN OUR CASE
    public void rotate(float angle) {
        if (angle == 0 || angle == 360 || angle == -360)
            return;

        int newAng;
        if(angle > 0) {
            newAng = ((int) angle) / 90;
            this.orient = (newAng + this.orient) % 4;
            System.out.println(orient);
        } else {
            newAng = ((int) angle) / 90;
            this.orient = (4 - newAng + this.orient) % 4;
            System.out.println(orient);
        }
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


    // -2 means piece was hit but don't remove, stop the laser
    // -1 means the piece was hit and needs to be removed, stop the laser
    // else, laser came out from the returned int side
    public int reflectedSide(int laserIn) {
        return -1;
    }
}

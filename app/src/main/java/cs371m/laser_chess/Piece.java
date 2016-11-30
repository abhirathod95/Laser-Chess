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
    protected GameLogic.Color bitmapColor;

    public Piece(Context context, boolean friendly, GameLogic.Color bitmapColor) {
        this.mContext = context;
        this.friendly = friendly;
        this.bitmapColor = bitmapColor;

        bitmap = null;
        rotatable = true;
        movable = true;
        orient = 0;
    }

    public Piece copy() {
        Piece piece;
        if (type == Type.PYRAMID)
            piece = new Pyramid(mContext, friendly, bitmapColor);
        else if (type == Type.SPHINX)
            piece = new Sphinx(mContext, friendly, bitmapColor);
        else if (type == Type.ANUBIS)
            piece = new Anubis(mContext, friendly, bitmapColor);
        else if (type == Type.PHARAOH)
            piece = new Pharaoh(mContext, friendly, bitmapColor);
        else if (type == Type.SCARAB)
            piece = new Scarab(mContext, friendly, bitmapColor);
        else{
            System.out.println("ERROR COPYING TYPE NOT SET");
            piece = new Piece(mContext, friendly, bitmapColor);
        }
        piece.bitmap = this.bitmap;
        piece.type = this.type;
        piece.movable = this.movable;
        piece.rotatable = this.rotatable;
        piece.orient = this.orient;
        return piece;
    }

    public int getOrient() {
        return orient;
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

    public GameLogic.Color getBitmapColor() {
        return bitmapColor;
    }

    protected int getNewOrient(float angle) {
        int newOrient, newAng;

        if(angle > 0) {
            newAng = ((int) angle) / 90;
            newOrient = (newAng + this.orient) % 4;
            System.out.println("NEW ORIENTATION: " + newOrient);
        } else {
            newAng = ((int) angle * -1) / 90;
            newOrient = (4 - newAng + this.orient) % 4;
            System.out.println("NEW ORIENTATION: " + newOrient);
        }
        return newOrient;
    }

    // ANGLE SHOULD ALWAYS BE 90, 180 or 270 IN OUR CASE
    public boolean rotate(float angle) {
        //System.out.println("COMING FROM PIECE");
        if (angle == 0 || angle == 360 || angle == -360)
            return false;
        this.orient = getNewOrient(angle);
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return true;
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
        System.out.println("INSIDE FROM PIECE");
        return -1;
    }
}

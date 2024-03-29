package cs371m.laser_chess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

/**
 * Created by Abhi on 11/9/2016.
 */

public class Scarab extends Piece{

    public Scarab(Context context, boolean friendly, GameLogic.Color bitmapColor){
        super(context, friendly, bitmapColor);
        this.type = Type.SCARAB;
        if(getBitmapColor() == GameLogic.Color.RED) {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.red_scarab);
        } else {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.scarab);
        }
    }

    @Override
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
        } else if(to.getPiece().getType() == Type.PYRAMID || to.getPiece().getType() == Type.ANUBIS){
            Piece temp = to.getPiece();
            to.setPiece(from.getPiece());
            from.setPiece(temp);
        }else
            return false;
        return true;
    }

    @Override
    public int reflectedSide(int side) {
        switch(this.orient) {
            case 0:
            case 2:
                switch(side) {
                    case 0:
                        return 1;
                    case 1:
                        return 0;
                    case 2:
                        return 3;
                    case 3:
                        return 2;
                    default:
                        System.out.println("IN SIDE IS OUT OF BOUNDS");
                        return -1;
                }
            case 1:
            case 3:
                switch(side) {
                    case 0:
                        return 3;
                    case 1:
                        return 2;
                    case 2:
                        return 1;
                    case 3:
                        return 0;
                    default:
                        System.out.println("IN SIDE IS OUT OF BOUNDS");
                        return -1;
                }
        }
        System.out.println("ORIENTATION IS OUT OF BOUNDS");
        return -1;
    }

}

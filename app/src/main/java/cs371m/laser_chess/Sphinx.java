package cs371m.laser_chess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

/**
 * Created by Abhi on 11/9/2016.
 */

public class Sphinx extends Piece {

    public Sphinx(Context context, boolean friendly, GameLogic.Color bitmapColor){
        super(context, friendly, bitmapColor);
        this.type = Type.SPHINX;
        if(getBitmapColor() == GameLogic.Color.RED) {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.red_sphinx);
            rotate(180);
        } else {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.sphinx);
        }
        movable = false;
    }

    @Override
    public int reflectedSide(int laserIn) {
        //System.out.println("COMING FROM SPHINX");
        return -2;
    }

    @Override
    public boolean rotate(float angle) {
        //System.out.println("COMING FROM SPHINX");
        if (angle == 0 || angle == 360 || angle == -360)
            return false;

        int newOrient = getNewOrient(angle);
        if(bitmapColor == GameLogic.Color.BLACK) {
            if(newOrient == 1 || newOrient == 2)
                return false;
        } else {
            if(newOrient == 0 || newOrient == 3)
                return false;
        }

        this.orient = getNewOrient(angle);
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return true;
    }
}

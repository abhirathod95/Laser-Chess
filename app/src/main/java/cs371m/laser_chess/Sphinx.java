package cs371m.laser_chess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Abhi on 11/9/2016.
 */

public class Sphinx extends Piece {

    public Sphinx(Context context, boolean friendly){
        super(context, friendly);
        this.type = Type.SPHINX;
        if(!isFriendly()) {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.red_sphinx);
            rotate(180);
        } else {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.sphinx);
        }
        movable = false;
    }
}

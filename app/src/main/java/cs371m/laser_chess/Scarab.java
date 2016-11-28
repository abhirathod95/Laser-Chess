package cs371m.laser_chess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Abhi on 11/9/2016.
 */

public class Scarab extends Piece{

    public Scarab(Context context, boolean friendly){
        super(context, friendly);
        this.type = Type.SCARAB;
        if(!isFriendly()) {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.red_scarab);
        } else {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.scarab);
        }
    }
}

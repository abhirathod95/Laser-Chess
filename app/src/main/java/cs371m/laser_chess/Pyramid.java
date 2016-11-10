package cs371m.laser_chess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Abhi on 11/9/2016.
 */

public class Pyramid extends Piece{

    public Pyramid(Context context, boolean friendly){
        super(context, friendly);
        this.type = Type.PYRAMID;
        if(!isFriendly()) {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.red_pyramid);
        } else {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.pyramid);
        }
    }
}

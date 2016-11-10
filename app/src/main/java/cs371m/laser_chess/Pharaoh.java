package cs371m.laser_chess;

import android.content.Context;
import android.graphics.BitmapFactory;

/**
 * Created by Abhi on 11/10/2016.
 */

public class Pharaoh extends Piece {

    public Pharaoh(Context context, boolean friendly){
        super(context, friendly);
        this.type = Type.PHARAOH;
        if(!isFriendly()) {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.red_pharaoh);
        } else {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.pharaoh);
        }
        rotatable = false;
    }
}

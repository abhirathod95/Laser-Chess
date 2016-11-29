package cs371m.laser_chess;

import android.content.Context;
import android.graphics.BitmapFactory;

/**
 * Created by Abhi on 11/10/2016.
 */

public class Anubis extends Piece {

    public Anubis(Context context, boolean friendly){
        super(context, friendly);
        this.type = Type.ANUBIS;
        if(!isFriendly()) {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.red_anubis);
        } else {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.anubis);
            rotate(180);
        }
    }

    @Override
    public int reflectedSide(int side) {
        if(side == this.orient)
            return -2;
        else
            return -1;
    }
}

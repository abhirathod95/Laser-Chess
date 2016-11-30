package cs371m.laser_chess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Abhi on 11/9/2016.
 */

public class Pyramid extends Piece{

    public Pyramid(Context context, boolean friendly, GameLogic.Color bitmapColor){
        super(context, friendly, bitmapColor);
        this.type = Type.PYRAMID;
        if(getBitmapColor() == GameLogic.Color.RED) {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.red_pyramid);
        } else {
            bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.pyramid);
        }
    }

    @Override
    public int reflectedSide(int side) {
        System.out.println("THE IN: " + side + " AND THE ORIENT: " + this.orient);
        switch(this.orient) {
            case 0:
                if(side == 2)
                    return 3;
                else if(side == 3)
                    return 2;
                else
                    return -1;
            case 1:
                if(side == 0)
                    return 3;
                else if(side == 3)
                    return 0;
                else
                    return -1;
            case 2:
                if(side == 0)
                    return 1;
                else if(side == 1)
                    return 0;
                else
                    return -1;
            case 3:
                if(side == 1)
                    return 2;
                else if(side == 2)
                    return 1;
                else
                    return -1;
        }
        return -1;
    }

}

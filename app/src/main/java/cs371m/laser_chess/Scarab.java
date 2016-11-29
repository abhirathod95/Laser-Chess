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

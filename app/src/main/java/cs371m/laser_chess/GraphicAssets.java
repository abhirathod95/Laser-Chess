package cs371m.laser_chess;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.util.Hashtable;

/**
 * Created by Abhi on 11/9/2016.
 */

public class GraphicAssets {
    private Context mContext;
    private Hashtable<Integer, Drawable> assets;

    public GraphicAssets(Context c) {
        mContext = c;
        assets = new Hashtable<>();
    }


    public Drawable getStone(int id){
        if (assets.containsKey(id)) return assets.get(id);

        // Create stone if not already load - lazy loading, you may load everything in constructor
        Drawable d = new BitmapDrawable(BitmapFactory.decodeResource(mContext.getResources(), id));
        assets.put(id, d);
        return d;
    }

}

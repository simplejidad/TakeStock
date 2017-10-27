package com.santiagogil.takestock.util;

import android.support.v7.widget.CardView;
import android.view.View;

/**
 * Created by digitalhouse on 26/10/17.
 */

public class LayoutHelper {

    public static void setBackgroundColor(View view, Integer independence){


        Integer itemIndependence = 120 - independence/255*30;

        if(independence == 0){
            view.setBackgroundColor(LayoutHelper.getIntFromColor(200, 100, 100));
        } else {
            view.setBackgroundColor(LayoutHelper.getIntFromColor(itemIndependence, 150, itemIndependence));
        }

    };

    public static int getIntFromColor(int Red, int Green, int Blue){
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }

    public static void setCardBackgroundColor(CardView cardView, Integer independence){


        Integer itemIndependence = 120 - independence/255*30;

        if(independence == 0){
            cardView.setCardBackgroundColor(LayoutHelper.getIntFromColor(200, 100, 100));
        } else {
            cardView.setCardBackgroundColor(LayoutHelper.getIntFromColor(itemIndependence, 150, itemIndependence));
        }

    };
}

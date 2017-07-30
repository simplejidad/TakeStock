package com.santiagogil.takestock.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;

/**
 * Created by digitalhouse on 29/07/17.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class SharedElementTransition extends TransitionSet {
    public SharedElementTransition() {
        setOrdering(ORDERING_TOGETHER);
        addTransition(new ChangeBounds()).
                addTransition(new ChangeTransform());
    }
}

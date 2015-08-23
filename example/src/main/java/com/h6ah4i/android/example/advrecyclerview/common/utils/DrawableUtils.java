package com.h6ah4i.android.example.advrecyclerview.common.utils;

import android.graphics.drawable.Drawable;

/**
 * Created by hasegawa on 6/17/15.
 */
public class DrawableUtils {
    private static final int[] EMPTY_STATE = new int[] {};

    public static void clearState(Drawable drawable) {
        if (drawable != null) {
            drawable.setState(EMPTY_STATE);
        }
    }
}

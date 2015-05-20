package com.h6ah4i.android.example.advrecyclerview.common.compat;

import android.os.Build;
import android.view.View;

import com.wnafee.vector.MorphButton;

/**
 * Created by hasegawa on 5/13/15.
 */
public class MorphButtonCompat {
    static abstract class Impl {
        public abstract MorphButton.MorphState getState(View v);
        public abstract void setState(View v, MorphButton.MorphState state);
        public abstract void setState(View v, MorphButton.MorphState state, boolean animate);
    }

    private static final Impl IMPL;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            IMPL = new MorphButtonCompatImplICS();
        } else {
            IMPL = new MorphButtonCompatImplPreICS();
        }
    }

    View mView;

    public MorphButtonCompat(View v) {
        mView = v;
    }

    public MorphButton.MorphState getState() {
        if (mView != null) {
            return IMPL.getState(mView);
        } else {
            return MorphButton.MorphState.START;
        }
    }

    public void setState(MorphButton.MorphState state) {
        if (mView != null) {
            IMPL.setState(mView, state);
        }
    }

    public void setState(MorphButton.MorphState state, boolean animate) {
        if (mView != null) {
            IMPL.setState(mView, state, animate);
        }
    }
}

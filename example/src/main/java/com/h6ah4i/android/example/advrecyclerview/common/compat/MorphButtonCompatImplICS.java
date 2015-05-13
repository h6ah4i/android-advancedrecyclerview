package com.h6ah4i.android.example.advrecyclerview.common.compat;

import android.view.View;

import com.wnafee.vector.MorphButton;

/**
 * Created by hasegawa on 5/13/15.
 */
class MorphButtonCompatImplICS extends MorphButtonCompat.Impl {

    @Override
    public MorphButton.MorphState getState(View v) {
        return ((MorphButton) v).getState();
    }

    @Override
    public void setState(View v, MorphButton.MorphState state) {
        ((MorphButton) v).setState(state);
    }

    @Override
    public void setState(View v, MorphButton.MorphState state, boolean animate) {
        ((MorphButton) v).setState(state, animate);
    }
}

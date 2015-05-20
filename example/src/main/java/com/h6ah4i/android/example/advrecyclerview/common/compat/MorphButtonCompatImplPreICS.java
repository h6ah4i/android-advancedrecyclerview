package com.h6ah4i.android.example.advrecyclerview.common.compat;

import android.view.View;

import com.wnafee.vector.MorphButton;

/**
 * Created by hasegawa on 5/13/15.
 */
class MorphButtonCompatImplPreICS extends MorphButtonCompat.Impl {
    @Override
    public MorphButton.MorphState getState(View v) {
        return MorphButton.MorphState.START;
    }

    @Override
    public void setState(View v, MorphButton.MorphState state) {
    }

    @Override
    public void setState(View v, MorphButton.MorphState state, boolean animate) {
    }
}

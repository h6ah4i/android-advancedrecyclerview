package com.h6ah4i.android.widget.advrecyclerview.draggable;

import android.support.v7.widget.RecyclerView;

/**
 * Created by hasegawa on 8/22/15.
 */
class LeftRightEdgeEffectDecorator extends BaseEdgeEffectDecorator {
    public LeftRightEdgeEffectDecorator(RecyclerView recyclerView) {
        super(recyclerView);
    }

    @Override
    protected int getEdgeDirection(int no) {
        switch (no) {
            case 0:
                return EDGE_LEFT;
            case 1:
                return EDGE_RIGHT;
            default:
                throw new IllegalArgumentException();
        }
    }
}

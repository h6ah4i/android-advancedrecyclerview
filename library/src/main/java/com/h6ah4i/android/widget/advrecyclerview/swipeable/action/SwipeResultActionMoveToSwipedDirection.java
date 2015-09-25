package com.h6ah4i.android.widget.advrecyclerview.swipeable.action;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;

/**
 * Created by hasegawa on 9/16/15.
 */
public abstract class SwipeResultActionMoveToSwipedDirection extends SwipeResultAction {
    public SwipeResultActionMoveToSwipedDirection() {
        super(RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_MOVE_TO_SWIPED_DIRECTION);
    }
}

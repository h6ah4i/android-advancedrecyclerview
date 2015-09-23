package com.h6ah4i.android.widget.advrecyclerview.swipeable;

import android.support.v7.widget.RecyclerView;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.LegacySwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;

/**
 * Created by hasegawa on 9/20/15.
 */
public class SwipeableItemInternalUtils {
    private SwipeableItemInternalUtils() {
    }

    public static SwipeResultAction invokeOnSwipeItem(
            BaseSwipeableItemAdapter<?> adapter, RecyclerView.ViewHolder holder, int position, int result) {

        if (adapter instanceof LegacySwipeableItemAdapter) {

            int reaction = ((LegacySwipeableItemAdapter) adapter).onSwipeItem(
                    holder, position, result);

            switch (reaction) {
                case RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_DEFAULT:
                case RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_MOVE_TO_SWIPED_DIRECTION:
                case RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM:
                    //noinspection deprecation
                    return new LegacySwipeResultAction<>(
                            (LegacySwipeableItemAdapter) adapter,
                            holder, position, result, reaction);
                default:
                    throw new IllegalStateException("Unexpected reaction type: " + reaction);
            }
        } else {
            return ((SwipeableItemAdapter) adapter).onSwipeItem(holder, position, result);
        }
    }
}

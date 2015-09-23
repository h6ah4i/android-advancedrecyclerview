package com.h6ah4i.android.widget.advrecyclerview.expandable;

import android.support.v7.widget.RecyclerView;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;

/**
 * Created by hasegawa on 9/20/15.
 */
class ExpandableSwipeableItemInternalUtils {
    private ExpandableSwipeableItemInternalUtils() {
    }

    public static SwipeResultAction invokeOnSwipeItem(
            BaseExpandableSwipeableItemAdapter<?, ?> adapter, RecyclerView.ViewHolder holder,
            int groupPosition, int childPosition, int result) {

        if (adapter instanceof LegacyExpandableSwipeableItemAdapter) {
            int reaction;

            if (childPosition == RecyclerView.NO_POSITION) {
                reaction = ((LegacyExpandableSwipeableItemAdapter) adapter).onSwipeGroupItem(
                        holder, groupPosition, result);
            } else {
                reaction = ((LegacyExpandableSwipeableItemAdapter) adapter).onSwipeChildItem(
                        holder, groupPosition, childPosition, result);
            }

            switch (reaction) {
                case RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_DEFAULT:
                case RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_MOVE_TO_SWIPED_DIRECTION:
                case RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_REMOVE_ITEM:
                    //noinspection deprecation
                    return new LegacyExpandableSwipeResultAction<>(
                            (LegacyExpandableSwipeableItemAdapter) adapter,
                            holder, groupPosition, childPosition, result, reaction);
                default:
                    throw new IllegalStateException("Unexpected reaction type: " + reaction);
            }
        } else {
            if (childPosition == RecyclerView.NO_POSITION) {
                return ((ExpandableSwipeableItemAdapter) adapter).onSwipeGroupItem(holder, groupPosition, result);
            } else {
                return ((ExpandableSwipeableItemAdapter) adapter).onSwipeChildItem(holder, groupPosition, childPosition, result);
            }
        }
    }
}

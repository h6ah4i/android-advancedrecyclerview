package com.h6ah4i.android.widget.advrecyclerview.swipeable;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.annotation.SwipeableItemStateFlags;

/**
 * Helper class for decoding {@link SwipeableItemViewHolder#getSwipeStateFlags()} flag values.
 */
public class SwipeableItemState {
    private int mFlags;

    @SwipeableItemStateFlags
    public int getFlags() {
        return mFlags;
    }

    public void setFlags(@SwipeableItemStateFlags int flags) {
        mFlags = flags;
    }

    /**
     * Checks whether the swiping is currently performed.
     *
     * @return True if the user is swiping an item, otherwise else.
     */
    public boolean isSwiping() {
        return (mFlags & SwipeableItemConstants.STATE_FLAG_SWIPING) != 0;
    }

    /**
     * Checks whether the item is being dragged.
     *
     * @return True if the associated item is being swiped, otherwise false.
     */
    public boolean isActive() {
        return (mFlags & SwipeableItemConstants.STATE_FLAG_IS_ACTIVE) != 0;
    }

    /**
     * Checks whether state flags are changed or not.
     *
     * @return True if flags are updated, otherwise false.
     */
    public boolean isUpdated() {
        return (mFlags & SwipeableItemConstants.STATE_FLAG_IS_UPDATED) != 0;
    }
}

package com.h6ah4i.android.widget.advrecyclerview.draggable;

import com.h6ah4i.android.widget.advrecyclerview.draggable.annotation.DraggableItemStateFlags;

/**
 * Helper class for decoding {@link DraggableItemViewHolder#getDragStateFlags()} flag values.
 */
public class DraggableItemState {
    private int mFlags;

    @DraggableItemStateFlags
    public int getFlags() {
        return mFlags;
    }

    public void setFlags(@DraggableItemStateFlags int flags) {
        mFlags = flags;
    }

    /**
     * Checks whether the dragging is currently performed.
     *
     * @return True if the user is dragging an item, otherwise else.
     */
    public boolean isDragging() {
        return (mFlags & DraggableItemConstants.STATE_FLAG_DRAGGING) != 0;
    }

    /**
     * Checks whether the item is being dragged.
     *
     * @return True if the associated item is being dragged, otherwise false.
     */
    public boolean isActive() {
        return (mFlags & DraggableItemConstants.STATE_FLAG_IS_ACTIVE) != 0;
    }

    /**
     * Checks whether the item is in range of drag-sortable items.
     *
     * @return True if the associated item is in range, otherwise false.
     */
    public boolean isInRange() {
        return (mFlags & DraggableItemConstants.STATE_FLAG_IS_IN_RANGE) != 0;
    }

    /**
     * Checks whether state flags are changed or not.
     *
     * @return True if flags are updated, otherwise false.
     */
    public boolean isUpdated() {
        return (mFlags & DraggableItemConstants.STATE_FLAG_IS_UPDATED) != 0;
    }
}

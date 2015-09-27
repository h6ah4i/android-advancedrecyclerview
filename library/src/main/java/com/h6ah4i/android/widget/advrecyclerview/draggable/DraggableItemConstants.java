package com.h6ah4i.android.widget.advrecyclerview.draggable;

/**
 * Created by hasegawa on 9/27/15.
 */
public interface DraggableItemConstants {
    /**
     * State flag for the {@link DraggableItemViewHolder#setDragStateFlags(int)} and {@link DraggableItemViewHolder#getDragStateFlags()} methods.
     * Indicates that currently performing dragging.
     */
    int STATE_FLAG_DRAGGING = (1 << 0);

    /**
     * State flag for the {@link DraggableItemViewHolder#setDragStateFlags(int)} and {@link DraggableItemViewHolder#getDragStateFlags()} methods.
     * Indicates that this item is being dragged.
     */
    int STATE_FLAG_IS_ACTIVE = (1 << 1);

    /**
     * State flag for the {@link DraggableItemViewHolder#setDragStateFlags(int)} and {@link DraggableItemViewHolder#getDragStateFlags()} methods.
     * Indicates that this item is in the range of drag-sortable items
     */
    int STATE_FLAG_IS_IN_RANGE = (1 << 2);

    /**
     * State flag for the {@link DraggableItemViewHolder#setDragStateFlags(int)} and {@link DraggableItemViewHolder#getDragStateFlags()} methods.
     * If this flag is set, some other flags are changed and require to apply.
     */
    int STATE_FLAG_IS_UPDATED = (1 << 31);

    // ---
}

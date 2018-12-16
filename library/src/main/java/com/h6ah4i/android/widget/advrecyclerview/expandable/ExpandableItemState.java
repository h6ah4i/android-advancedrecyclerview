package com.h6ah4i.android.widget.advrecyclerview.expandable;

import com.h6ah4i.android.widget.advrecyclerview.expandable.annotation.ExpandableItemStateFlags;

/**
 * Helper class for decoding {@link ExpandableItemViewHolder#getExpandStateFlags()} flag values.
 */
public class ExpandableItemState {
    private int mFlags;

    @ExpandableItemStateFlags
    public int getFlags() {
        return mFlags;
    }

    public void setFlags(@ExpandableItemStateFlags int flags) {
        mFlags = flags;
    }

    /**
     * Checks whether the swiping is currently performed.
     *
     * @return True if the user is swiping an item, otherwise else.
     */
    public boolean isSwiping() {
        return (mFlags & ExpandableItemConstants.STATE_FLAG_IS_EXPANDED) != 0;
    }

    /**
     * Checks whether the item is a child item.
     *
     * @return True if the associated item is a child item, otherwise false.
     */
    public boolean isChild() {
        return (mFlags & ExpandableItemConstants.STATE_FLAG_IS_CHILD) != 0;
    }

    /**
     * Checks whether the item is a group item.
     *
     * @return True if the associated item is a child item, otherwise false.
     */
    public boolean isGroup() {
        return (mFlags & ExpandableItemConstants.STATE_FLAG_IS_GROUP) != 0;
    }

    /**
     * Checks whether the group is expanded.
     *
     * @return True if the expandable group is expanded, otherwise false.
     */
    public boolean isExpanded() {
        return (mFlags & ExpandableItemConstants.STATE_FLAG_IS_EXPANDED) != 0;
    }

    /**
     * Checks whether the expanded state is changed or not. You can use this method to determine the group item indicator should animate.
     *
     * @return True if the group's expanded state has changed, otherwise false.
     */
    public boolean hasExpandedStateChanged() {
        return (mFlags & ExpandableItemConstants.STATE_FLAG_HAS_EXPANDED_STATE_CHANGED) != 0;
    }

    /**
     * Checks whether state flags are changed or not.
     *
     * @return True if flags are updated, otherwise false.
     */
    public boolean isUpdated() {
        return (mFlags & ExpandableItemConstants.STATE_FLAG_IS_UPDATED) != 0;
    }
}

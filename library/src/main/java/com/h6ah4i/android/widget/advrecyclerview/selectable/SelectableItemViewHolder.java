package com.h6ah4i.android.widget.advrecyclerview.selectable;

/**
 * <p>Public interface used by selectable items connected to {@link com.h6ah4i.android.widget.advrecyclerview.selectable.SelectableItemWrapperAdapter}.</p>
 */
public interface SelectableItemViewHolder {
    /**
     * <p>Turn selection mode on for this holder.</p>
     *
     * @param selectable True if selection mode is on.
     */
    void setSelectable(boolean selectable);

    /**
     * <p>Current selection mode state.</p>
     *
     * @return True if selection mode is on.
     */
    boolean isSelectable();

    /**
     * <p>Set this item to be selected (the activated state, for Views and Drawables)</p>
     *
     * @param activated True if selected/activated.
     */
    void setActivated(boolean activated);

    /**
     * <p>Return true if the item is selected/activated.</p>
     *
     * @return True if selected/activated.
     */
    boolean isActivated();

    /**
     * <p>Returns the adapter position this item is currently bound to.
     * This can (and often will) change; if attached to a {@link com.h6ah4i.android.widget.advrecyclerview.selectable.SelectableItemWrapperAdapter},
     * {@link com.h6ah4i.android.widget.advrecyclerview.selectable.SelectableItemWrapperAdapter#bindHolder(android.support.v7.widget.RecyclerView.ViewHolder, int, long)}
     * should be called whenever this value changes.</p>
     *
     * @return Position this holder is currently bound to.
     */
    int getPosition();

    /**
     * <p>Return the item id this item is currently bound to.
     * This can (and often will) change; if attached to a {@link com.h6ah4i.android.widget.advrecyclerview.selectable.SelectableItemWrapperAdapter},
     * {@link com.h6ah4i.android.widget.advrecyclerview.selectable.SelectableItemWrapperAdapter#bindHolder(android.support.v7.widget.RecyclerView.ViewHolder, int, long)}
     * should be called whenever this value changes.</p>
     *
     * @return Item id this holder is currently bound to.
     */
    long getItemId();
}

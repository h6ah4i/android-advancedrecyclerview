/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.h6ah4i.android.widget.advrecyclerview.expandable;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.h6ah4i.android.widget.advrecyclerview.utils.CustomRecyclerViewUtils;

/**
 * Provides item expansion operation for {@link android.support.v7.widget.RecyclerView}
 */
public class RecyclerViewExpandableItemManager implements ExpandableItemConstants {
    private static final String TAG = "ARVExpandableItemMgr";

    /**
     * Packed position version of {@link android.support.v7.widget.RecyclerView#NO_POSITION}
     */
    public static final long NO_EXPANDABLE_POSITION = ExpandableAdapterHelper.NO_EXPANDABLE_POSITION;


    /**
     * Used for being notified when a group is expanded
     */
    public interface OnGroupExpandListener {
        /**
         * Callback method to be invoked when a group in this expandable list has been expanded.
         *
         * @param groupPosition The group position that was expanded
         * @param fromUser      Whether the expand request is issued by a user operation
         */
        void onGroupExpand(int groupPosition, boolean fromUser);
    }

    /**
     * Used for being notified when a group is collapsed
     */
    public interface OnGroupCollapseListener {
        /**
         * Callback method to be invoked when a group in this expandable list has been collapsed.
         *
         * @param groupPosition The group position that was collapsed
         * @param fromUser      Whether the collapse request is issued by a user operation
         */
        void onGroupCollapse(int groupPosition, boolean fromUser);
    }

    // ---

    private SavedState mSavedState;

    private RecyclerView mRecyclerView;
    private ExpandableRecyclerViewWrapperAdapter mAdapter;
    private RecyclerView.OnItemTouchListener mInternalUseOnItemTouchListener;
    private OnGroupExpandListener mOnGroupExpandListener;
    private OnGroupCollapseListener mOnGroupCollapseListener;

    private long mTouchedItemId = RecyclerView.NO_ID;
    private int mTouchSlop;
    private int mInitialTouchX;
    private int mInitialTouchY;

    /**
     * Constructor.
     *
     * @param savedState The saved state object which is obtained from the {@link #getSavedState()} method.
     */
    public RecyclerViewExpandableItemManager(@Nullable Parcelable savedState) {
        mInternalUseOnItemTouchListener = new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return RecyclerViewExpandableItemManager.this.onInterceptTouchEvent(rv, e);
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        };

        if (savedState instanceof SavedState) {
            mSavedState = (SavedState) savedState;
        }
    }

    /**
     * Indicates this manager instance has released or not.
     *
     * @return True if this manager instance has released
     */
    public boolean isReleased() {
        return (mInternalUseOnItemTouchListener == null);
    }

    /**
     * <p>Attaches {@link android.support.v7.widget.RecyclerView} instance.</p>
     * <p>Before calling this method, the target {@link android.support.v7.widget.RecyclerView} must set
     * the wrapped adapter instance which is returned by the
     * {@link #createWrappedAdapter(android.support.v7.widget.RecyclerView.Adapter)} method.</p>
     *
     * @param rv The {@link android.support.v7.widget.RecyclerView} instance
     */
    public void attachRecyclerView(@NonNull RecyclerView rv) {
        if (isReleased()) {
            throw new IllegalStateException("Accessing released object");
        }

        if (mRecyclerView != null) {
            throw new IllegalStateException("RecyclerView instance has already been set");
        }

        mRecyclerView = rv;
        mRecyclerView.addOnItemTouchListener(mInternalUseOnItemTouchListener);
        mTouchSlop = ViewConfiguration.get(mRecyclerView.getContext()).getScaledTouchSlop();
    }

    /**
     * <p>Detach the {@link android.support.v7.widget.RecyclerView} instance and release internal field references.</p>
     * <p>This method should be called in order to avoid memory leaks.</p>
     */
    public void release() {
        if (mRecyclerView != null && mInternalUseOnItemTouchListener != null) {
            mRecyclerView.removeOnItemTouchListener(mInternalUseOnItemTouchListener);
        }
        mInternalUseOnItemTouchListener = null;
        mOnGroupExpandListener = null;
        mOnGroupCollapseListener = null;
        mRecyclerView = null;
        mSavedState = null;
    }

    /**
     * Create wrapped adapter.
     *
     * @param adapter The target adapter.
     * @return Wrapped adapter which is associated to this {@link RecyclerViewExpandableItemManager} instance.
     */
    @SuppressWarnings("unchecked")
    public RecyclerView.Adapter createWrappedAdapter(@NonNull RecyclerView.Adapter adapter) {
        if (!adapter.hasStableIds()) {
            throw new IllegalArgumentException("The passed adapter does not support stable IDs");
        }

        if (mAdapter != null) {
            throw new IllegalStateException("already have a wrapped adapter");
        }

        int[] adapterSavedState = (mSavedState != null) ? mSavedState.adapterSavedState : null;
        mSavedState = null;

        mAdapter = new ExpandableRecyclerViewWrapperAdapter(this, adapter, adapterSavedState);

        // move listeners to wrapper adapter
        mAdapter.setOnGroupExpandListener(mOnGroupExpandListener);
        mOnGroupExpandListener = null;

        mAdapter.setOnGroupCollapseListener(mOnGroupCollapseListener);
        mOnGroupCollapseListener = null;

        return mAdapter;
    }

    /**
     * <p>Gets saved state object in order to restore the internal state.</p>
     * <p>Call this method in Activity/Fragment's onSavedInstance() and save to the bundle.</p>
     *
     * @return The Parcelable object which stores information need to restore the internal states.
     */
    public Parcelable getSavedState() {
        int[] adapterSavedState = null;

        if (mAdapter != null) {
            adapterSavedState = mAdapter.getExpandedItemsSavedStateArray();
        }

        return new SavedState(adapterSavedState);
    }

    /*package*/ boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        if (mAdapter == null) {
            return false;
        }

        final int action = MotionEventCompat.getActionMasked(e);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                handleActionDown(rv, e);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (handleActionUpOrCancel(rv, e)) {
                    // NOTE: it requires to return false to work click effect properly (issue #44)
                    return false;
                }
                break;
        }

        return false;
    }

    private void handleActionDown(RecyclerView rv, MotionEvent e) {
        final RecyclerView.ViewHolder holder = CustomRecyclerViewUtils.findChildViewHolderUnderWithTranslation(rv, e.getX(), e.getY());

        mInitialTouchX = (int) (e.getX() + 0.5f);
        mInitialTouchY = (int) (e.getY() + 0.5f);

        if (holder instanceof ExpandableItemViewHolder) {
            mTouchedItemId = holder.getItemId();
        } else {
            mTouchedItemId = RecyclerView.NO_ID;
        }
    }

    private boolean handleActionUpOrCancel(RecyclerView rv, MotionEvent e) {
        final long touchedItemId = mTouchedItemId;
        final int initialTouchX = mInitialTouchX;
        final int initialTouchY = mInitialTouchY;

        mTouchedItemId = RecyclerView.NO_ID;
        mInitialTouchX = 0;
        mInitialTouchY = 0;

        if (!((touchedItemId != RecyclerView.NO_ID) && (MotionEventCompat.getActionMasked(e) == MotionEvent.ACTION_UP))) {
            return false;
        }

        final int touchX = (int) (e.getX() + 0.5f);
        final int touchY = (int) (e.getY() + 0.5f);

        final int diffX = touchX - initialTouchX;
        final int diffY = touchY - initialTouchY;

        if (!((Math.abs(diffX) < mTouchSlop) && (Math.abs(diffY) < mTouchSlop))) {
            return false;
        }

        final RecyclerView.ViewHolder holder = CustomRecyclerViewUtils.findChildViewHolderUnderWithTranslation(rv, e.getX(), e.getY());

        if (!((holder != null) && (holder.getItemId() == touchedItemId))) {
            return false;
        }

        final int position = CustomRecyclerViewUtils.getSynchronizedPosition(holder);

        if (position == RecyclerView.NO_POSITION) {
            return false;
        }

        final View view = holder.itemView;
        final int translateX = (int) (ViewCompat.getTranslationX(view) + 0.5f);
        final int translateY = (int) (ViewCompat.getTranslationY(view) + 0.5f);
        final int viewX = touchX - (view.getLeft() + translateX);
        final int viewY = touchY - (view.getTop() + translateY);

        return mAdapter.onTapItem(holder, position, viewX, viewY);
    }

    /**
     * <p>Expand all groups.</p>
     * <p>Note that this method does not invoke the {@link OnGroupExpandListener#onGroupExpand(int, boolean)} callback.</p>
     */
    public void expandAll() {
        if (mAdapter != null) {
            mAdapter.expandAll();
        }
    }

    /**
     * <p>Collapse all groups.</p>
     * <p>Note that this method does not invoke the {@link OnGroupCollapseListener#onGroupCollapse(int, boolean)} callback.</p>
     */
    public void collapseAll() {
        if (mAdapter != null) {
            mAdapter.collapseAll();
        }
    }

    /**
     * Expand a group.
     *
     * @param groupPosition The group position to be expanded
     * @return True if the group was expanded, false otherwise  (If the group was already expanded, this will return false)
     */
    public boolean expandGroup(int groupPosition) {
        return (mAdapter != null) && mAdapter.expandGroup(groupPosition, false);
    }

    /**
     * Collapse a group.
     *
     * @param groupPosition The group position to be collapsed
     * @return True if the group was collapsed, false otherwise  (If the group was already collapsed, this will return false)
     */
    public boolean collapseGroup(int groupPosition) {
        return (mAdapter != null) && mAdapter.collapseGroup(groupPosition, false);
    }

    /**
     * Converts a flat position (the raw position of an item in the list) to a group and/or child position
     * (represented in a packed position). Use {@link #getPackedPositionChild(long)}, {@link #getPackedPositionGroup(long)} to unpack.
     *
     * @param flatPosition The flat position to be converted
     * @return The group and/or child position for the given flat position in packed position representation.
     */
    public long getExpandablePosition(int flatPosition) {
        if (mAdapter == null) {
            return ExpandableAdapterHelper.NO_EXPANDABLE_POSITION;
        }
        return mAdapter.getExpandablePosition(flatPosition);
    }

    /**
     * Converts a group and/or child position to a flat position.
     *
     * @param packedPosition The group and/or child position to be converted in packed position representation.
     * @return The group and/or child position for the given flat position in packed position representation.
     */
    public int getFlatPosition(long packedPosition) {
        if (mAdapter == null) {
            return RecyclerView.NO_POSITION;
        }
        return mAdapter.getFlatPosition(packedPosition);
    }

    /**
     * Gets the child position from a packed position.
     * To get the group that this child belongs to, use {@link #getPackedPositionGroup(long)}.
     * See {@link #getPackedPositionForChild(int, int)}.
     *
     * @param packedPosition The packed position from which the child position will be returned.
     * @return The child position portion of the packed position. If this does not contain a child, returns {@link android.support.v7.widget.RecyclerView#NO_POSITION}.
     */
    public static int getPackedPositionChild(long packedPosition) {
        return ExpandableAdapterHelper.getPackedPositionChild(packedPosition);
    }

    /**
     * <p>Returns the packed position representation of a child position.</p>
     * <p>In general, a packed position should be used in situations where the position given to/returned from
     * {@link RecyclerViewExpandableItemManager} method can either be a child or group.
     * The two positions are packed into a single long which can be unpacked using {@link #getPackedPositionChild(long)} and
     * {@link #getPackedPositionGroup(long)}.</p>
     *
     * @param groupPosition The child's parent group's position
     * @param childPosition The child position within the group
     * @return The packed position representation of the child (and parent group).
     */
    public static long getPackedPositionForChild(int groupPosition, int childPosition) {
        return ExpandableAdapterHelper.getPackedPositionForChild(groupPosition, childPosition);
    }

    /**
     * Returns the packed position representation of a group's position. See {@link #getPackedPositionForChild(int, int)}.
     *
     * @param groupPosition The child's parent group's position.
     * @return The packed position representation of the group.
     */
    public static long getPackedPositionForGroup(int groupPosition) {
        return ExpandableAdapterHelper.getPackedPositionForGroup(groupPosition);
    }

    /**
     * Gets the group position from a packed position. See {@link #getPackedPositionForChild(int, int)}.
     *
     * @param packedPosition The packed position from which the group position will be returned.
     * @return THe group position of the packed position. If this does not contain a group, returns {@link android.support.v7.widget.RecyclerView#NO_POSITION}.
     */
    public static int getPackedPositionGroup(long packedPosition) {
        return ExpandableAdapterHelper.getPackedPositionGroup(packedPosition);
    }

    /**
     * Whether the given group is currently expanded.
     *
     * @param groupPosition The group to check
     * @return Whether the group is currently expanded
     */
    public boolean isGroupExpanded(int groupPosition) {
        return (mAdapter != null) && mAdapter.isGroupExpanded(groupPosition);
    }

    /**
     * <p>Gets combined ID for child item.</p>
     * <p>bit 0-31: Lower 32 bits of the childId
     * bit 32-62: Lower 31 bits of the groupId
     * bit 63: reserved</p>
     *
     * @param groupId The ID of the group that contains the child.
     * @param childId The ID of the child.
     * @return The unique ID of the child across all groups and children in the list
     */
    public static long getCombinedChildId(long groupId, long childId) {
        return ExpandableAdapterHelper.getCombinedChildId(groupId, childId);
    }

    /**
     * <p>Gets combined ID for child item.</p>
     * <p>bit 0-31: all bits are set to 1
     * bit 32-62: Lower 31 bits of the groupId
     * bit 63: reserved</p>
     *
     * @param groupId The ID of the group that contains the child.
     * @return The unique ID of the child across all groups and children in the list
     */
    public static long getCombinedGroupId(long groupId) {
        return ExpandableAdapterHelper.getCombinedGroupId(groupId);
    }

    /**
     * Checks whether the passed view type is a group's one.
     *
     * @param rawViewType raw view type value (return value of {@link android.support.v7.widget.RecyclerView.ViewHolder#getItemViewType()})
     * @return True for the a group view type, otherwise false
     */
    public static boolean isGroupViewType(int rawViewType) {
        return ExpandableAdapterHelper.isGroupViewType(rawViewType);
    }

    /**
     * Gets group view type from a raw view type.
     *
     * @param rawViewType raw view type value (return value of {@link android.support.v7.widget.RecyclerView.ViewHolder#getItemViewType()})
     * @return Group view type for the given raw view type.
     */
    public static int getGroupViewType(int rawViewType) {
        return ExpandableAdapterHelper.getGroupViewType(rawViewType);
    }

    /**
     * Gets child view type from a raw view type.
     *
     * @param rawViewType raw view type value (return value of {@link android.support.v7.widget.RecyclerView.ViewHolder#getItemViewType()})
     * @return Child view type for the given raw view type.
     */
    public static int getChildViewType(int rawViewType) {
        return ExpandableAdapterHelper.getChildViewType(rawViewType);
    }

    /**
     * Register a callback to be invoked when an group item has been expanded.
     *
     * @param listener The callback that will be invoked.
     */
    public void setOnGroupExpandListener(@Nullable OnGroupExpandListener listener) {
        if (mAdapter != null) {
            mAdapter.setOnGroupExpandListener(listener);
        } else {
            // pending
            mOnGroupExpandListener = listener;
        }
    }

    /**
     * Register a callback to be invoked when an group item has been collapsed.
     *
     * @param listener The callback that will be invoked.
     */
    public void setOnGroupCollapseListener(@Nullable OnGroupCollapseListener listener) {
        if (mAdapter != null) {
            mAdapter.setOnGroupCollapseListener(listener);
        } else {
            // pending
            mOnGroupCollapseListener = listener;
        }
    }

    /**
     * Restore saves state. See {@link #restoreState(android.os.Parcelable, boolean, boolean)}.
     * (This method does not invoke any hook methods and listener events)
     *
     * @param savedState The saved state object
     */
    public void restoreState(@Nullable Parcelable savedState) {
        restoreState(savedState, false, false);
    }

    /**
     * <p>Restore saves state.</p>
     * <p>This method is useful when the adapter can not be prepared (because data loading may takes time and processed asynchronously)
     * before creating this manager instance.</p>
     *
     * @param savedState    The saved state object
     * @param callHooks     Whether to call hook routines
     *                      ({@link ExpandableItemAdapter#onHookGroupExpand(int, boolean)},
     *                      {@link ExpandableItemAdapter#onHookGroupCollapse(int, boolean)})
     * @param callListeners Whether to invoke {@link OnGroupExpandListener} and/or {@link OnGroupCollapseListener} listener events
     */
    public void restoreState(@Nullable Parcelable savedState, boolean callHooks, boolean callListeners) {
        if (savedState == null) {
            return; // do nothing
        }

        if (!(savedState instanceof SavedState)) {
            throw new IllegalArgumentException("Illegal saved state object passed");
        }

        if (!((mAdapter != null) && (mRecyclerView != null))) {
            throw new IllegalStateException("RecyclerView has not been attached");
        }

        mAdapter.restoreState(((SavedState) savedState).adapterSavedState, callHooks, callListeners);
    }

    /**
     * <p>Notify any registered observers that the group item at <code>groupPosition</code> has changed.</p>
     * <p>This is an group item change event, not a structural change event. It indicates that any
     * reflection of the data at <code>groupPosition</code> is out of date and should be updated.
     * The item at <code>groupPosition</code> retains the same identity.</p>
     * <p>This method does not notify for children that are contained in the specified group.
     * If children have also changed, use {@link #notifyGroupAndChildrenItemsChanged(int)} instead.</p>
     *
     * @param groupPosition Position of the group item that has changed
     * @see #notifyGroupAndChildrenItemsChanged(int)
     */
    public void notifyGroupItemChanged(int groupPosition) {
        mAdapter.notifyGroupItemChanged(groupPosition);
    }

    /**
     * <p>Notify any registered observers that the group and children items at <code>groupPosition</code> have changed.</p>
     * <p>This is an group item change event, not a structural change event. It indicates that any
     * reflection of the data at <code>groupPosition</code> is out of date and should be updated.
     * The item at <code>groupPosition</code> retains the same identity.</p>
     *
     * @param groupPosition Position of the group item which contains changed children
     * @see #notifyGroupItemChanged(int)
     * @see #notifyChildrenOfGroupItemChanged(int)
     */
    public void notifyGroupAndChildrenItemsChanged(int groupPosition) {
        mAdapter.notifyGroupAndChildrenItemsChanged(groupPosition, null);
    }

    /**
     * <p>Notify any registered observers that the group and children items at <code>groupPosition</code> have changed.</p>
     * <p>This is an group item change event, not a structural change event. It indicates that any
     * reflection of the data at <code>groupPosition</code> is out of date and should be updated.
     * The item at <code>groupPosition</code> retains the same identity.</p>
     *
     * @param groupPosition Position of the group item which contains changed children
     * @param payload  A non-null list of merged payloads. Can be empty list if requires full update.
     * @see #notifyGroupItemChanged(int)
     * @see #notifyChildrenOfGroupItemChanged(int)
     * @see #notifyGroupAndChildrenItemsChanged(int)
     */
    public void notifyGroupAndChildrenItemsChanged(int groupPosition, Object payload) {
        mAdapter.notifyGroupAndChildrenItemsChanged(groupPosition, payload);
    }

    /**
     * <p>Notify any registered observers that the children items contained in the group item at <code>groupPosition</code> have changed.</p>
     * <p>This is an group item change event, not a structural change event. It indicates that any
     * reflection of the data at <code>groupPosition</code> is out of date and should be updated.
     * The item at <code>groupPosition</code> retains the same identity.</p>
     * <p>This method does not notify for the group item.
     * If the group has also changed, use {@link #notifyGroupAndChildrenItemsChanged(int)} instead.</p>
     *
     * @param groupPosition Position of the group item which contains changed children
     * @see #notifyGroupAndChildrenItemsChanged(int)
     */
    public void notifyChildrenOfGroupItemChanged(int groupPosition) {
        mAdapter.notifyChildrenOfGroupItemChanged(groupPosition, null);
    }

    /**
     * <p>Notify any registered observers that the children items contained in the group item at <code>groupPosition</code> have changed.</p>
     * <p>This is an group item change event, not a structural change event. It indicates that any
     * reflection of the data at <code>groupPosition</code> is out of date and should be updated.
     * The item at <code>groupPosition</code> retains the same identity.</p>
     * <p>This method does not notify for the group item.
     * If the group has also changed, use {@link #notifyGroupAndChildrenItemsChanged(int)} instead.</p>
     *
     * @param groupPosition Position of the group item which contains changed children
     * @param payload  A non-null list of merged payloads. Can be empty list if requires full update.
     * @see #notifyGroupAndChildrenItemsChanged(int)
     */
    public void notifyChildrenOfGroupItemChanged(int groupPosition, Object payload) {
        mAdapter.notifyChildrenOfGroupItemChanged(groupPosition, payload);
    }

    /**
     * <p>Notify any registered observers that the child item at <code>{groupPosition, childPosition}</code> has changed.</p>
     * <p>This is an item change event, not a structural change event. It indicates that any
     * reflection of the data at <code>{groupPosition, childPosition}</code> is out of date and should be updated.
     * The item at <code>{groupPosition, childPosition}</code> retains the same identity.</p>
     *
     * @param groupPosition Position of the group item which contains the changed child
     * @param childPosition Position of the child item in the group that has changed
     * @see #notifyChildItemRangeChanged(int, int, int)
     */
    public void notifyChildItemChanged(int groupPosition, int childPosition) {
        mAdapter.notifyChildItemChanged(groupPosition, childPosition, null);
    }

    /**
     * <p>Notify any registered observers that the child item at <code>{groupPosition, childPosition}</code> has changed.</p>
     * <p>This is an item change event, not a structural change event. It indicates that any
     * reflection of the data at <code>{groupPosition, childPosition}</code> is out of date and should be updated.
     * The item at <code>{groupPosition, childPosition}</code> retains the same identity.</p>
     *
     * @param groupPosition Position of the group item which contains the changed child
     * @param childPosition Position of the child item in the group that has changed
     * @param payload  A non-null list of merged payloads. Can be empty list if requires full update.
     * @see #notifyChildItemRangeChanged(int, int, int)
     */
    public void notifyChildItemChanged(int groupPosition, int childPosition, Object payload) {
        mAdapter.notifyChildItemChanged(groupPosition, childPosition, payload);
    }

    /**
     * <p>Notify any registered observers that the <code>itemCount</code> child items starting at
     * position <code>{groupPosition, childPosition}</code> have changed.</p>
     * <p>This is an item change event, not a structural change event. It indicates that
     * any reflection of the data in the given position range is out of date and should
     * be updated. The items in the given range retain the same identity.</p>
     *
     * @param groupPosition      Position of the group item which contains the changed child
     * @param childPositionStart Position of the first child item in the group that has changed
     * @param itemCount          Number of items that have changed
     * @see #notifyChildItemChanged(int, int)
     */
    public void notifyChildItemRangeChanged(int groupPosition, int childPositionStart, int itemCount) {
        mAdapter.notifyChildItemRangeChanged(groupPosition, childPositionStart, itemCount, null);
    }

    /**
     * <p>Notify any registered observers that the <code>itemCount</code> child items starting at
     * position <code>{groupPosition, childPosition}</code> have changed.</p>
     * <p>This is an item change event, not a structural change event. It indicates that
     * any reflection of the data in the given position range is out of date and should
     * be updated. The items in the given range retain the same identity.</p>
     *
     * @param groupPosition      Position of the group item which contains the changed child
     * @param childPositionStart Position of the first child item in the group that has changed
     * @param itemCount          Number of items that have changed
     * @param payload  A non-null list of merged payloads. Can be empty list if requires full update.
     * @see #notifyChildItemChanged(int, int)
     */
    public void notifyChildItemRangeChanged(int groupPosition, int childPositionStart, int itemCount, Object payload) {
        mAdapter.notifyChildItemRangeChanged(groupPosition, childPositionStart, itemCount, payload);
    }

    /**
     * <p>Notify any registered observers that the group item reflected at <code>groupPosition</code>
     * has been newly inserted. The group item previously at <code>groupPosition</code> is now at
     * position <code>groupPosition + 1</code>.</p>
     * <p>This is a structural change event. Representations of other existing items in the
     * data set are still considered up to date and will not be rebound, though their
     * positions may be altered.</p>
     *
     * @param groupPosition Position of the newly inserted group item in the data set
     * @see #notifyGroupItemInserted(int, boolean)
     * @see #notifyGroupItemRangeInserted(int, int)
     * @see #notifyGroupItemRangeInserted(int, int, boolean)
     */
    public void notifyGroupItemInserted(int groupPosition) {
        notifyGroupItemInserted(groupPosition, false);
    }

    /**
     * <p>Notify any registered observers that the group item reflected at <code>groupPosition</code>
     * has been newly inserted. The group item previously at <code>groupPosition</code> is now at
     * position <code>groupPosition + 1</code>.</p>
     * <p>This is a structural change event. Representations of other existing items in the
     * data set are still considered up to date and will not be rebound, though their
     * positions may be altered.</p>
     *
     * @param groupPosition Position of the newly inserted group item in the data set
     * @param expanded      Whether the groups will be inserted already expanded
     * @see #notifyGroupItemInserted(int)
     * @see #notifyGroupItemRangeInserted(int, int)
     * @see #notifyGroupItemRangeInserted(int, int, boolean)
     */
    public void notifyGroupItemInserted(int groupPosition, boolean expanded) {
        mAdapter.notifyGroupItemInserted(groupPosition, expanded);
    }

    /**
     * <p>Notify any registered observers that the currently reflected <code>itemCount</code>
     * group items starting at <code>groupPositionStart</code> have been newly inserted. The group items
     * previously located at <code>groupPositionStart</code> and beyond can now be found starting
     * at position <code>groupPositionStart + itemCount</code>.</p>
     * <p>This is a structural change event. Representations of other existing items in the
     * data set are still considered up to date and will not be rebound, though their positions
     * may be altered.</p>
     *
     * @param groupPositionStart Position of the first group item that was inserted
     * @param itemCount          Number of group items inserted
     * @see #notifyGroupItemInserted(int)
     * @see #notifyGroupItemInserted(int, boolean)
     * @see #notifyGroupItemRangeInserted(int, int, boolean)
     */
    public void notifyGroupItemRangeInserted(int groupPositionStart, int itemCount) {
        notifyGroupItemRangeInserted(groupPositionStart, itemCount, false);
    }

    /**
     * <p>Notify any registered observers that the currently reflected <code>itemCount</code>
     * group items starting at <code>groupPositionStart</code> have been newly inserted and may be <code>expanded</code>.
     * The group items previously located at <code>groupPositionStart</code> and beyond can now be found starting
     * at position <code>groupPositionStart + itemCount</code>.</p>
     * <p>This is a structural change event. Representations of other existing items in the
     * data set are still considered up to date and will not be rebound, though their positions
     * may be altered.</p>
     *
     * @param groupPositionStart Position of the first group item that was inserted
     * @param itemCount          Number of group items inserted
     * @param expanded           Whether the groups will be inserted already expanded
     * @see #notifyGroupItemInserted(int)
     * @see #notifyGroupItemInserted(int, boolean)
     * @see #notifyGroupItemRangeInserted(int, int)
     */
    public void notifyGroupItemRangeInserted(int groupPositionStart, int itemCount, boolean expanded) {
        mAdapter.notifyGroupItemRangeInserted(groupPositionStart, itemCount, expanded);
    }

    /**
     * <p>Notify any registered observers that the group item reflected at <code>groupPosition</code>
     * has been newly inserted. The group item previously at <code>groupPosition</code> is now at
     * position <code>groupPosition + 1</code>.</p>
     * <p>This is a structural change event. Representations of other existing items in the
     * data set are still considered up to date and will not be rebound, though their
     * positions may be altered.</p>
     *
     * @param groupPosition Position of the group item which contains the inserted child
     * @param childPosition Position of the newly inserted child item in the data set
     * @see #notifyChildItemRangeInserted(int, int, int)
     */
    public void notifyChildItemInserted(int groupPosition, int childPosition) {
        mAdapter.notifyChildItemInserted(groupPosition, childPosition);
    }

    /**
     * <p>Notify any registered observers that the currently reflected <code>itemCount</code>
     * child items starting at <code>childPositionStart</code> have been newly inserted. The child items
     * previously located at <code>childPositionStart</code> and beyond can now be found starting
     * at position <code>childPositionStart + itemCount</code>.</p>
     * <p>This is a structural change event. Representations of other existing items in the
     * data set are still considered up to date and will not be rebound, though their positions
     * may be altered.</p>
     *
     * @param groupPosition      Position of the group item which contains the inserted child
     * @param childPositionStart Position of the first child item that was inserted
     * @param itemCount          Number of child items inserted
     * @see #notifyChildItemInserted(int, int)
     */
    public void notifyChildItemRangeInserted(int groupPosition, int childPositionStart, int itemCount) {
        mAdapter.notifyChildItemRangeInserted(groupPosition, childPositionStart, itemCount);
    }

    /**
     * <p>Notify any registered observers that the group item previously located at <code>groupPosition</code>
     * has been removed from the data set. The group items previously located at and after
     * <code>groupPosition</code> may now be found at <code>oldGroupPosition - 1</code>.</p>
     * <p>This is a structural change event. Representations of other existing items in the
     * data set are still considered up to date and will not be rebound, though their positions
     * may be altered.</p>
     *
     * @param groupPosition Position of the group item that has now been removed
     * @see #notifyGroupItemRangeRemoved(int, int)
     */
    public void notifyGroupItemRemoved(int groupPosition) {
        mAdapter.notifyGroupItemRemoved(groupPosition);
    }

    /**
     * <p>Notify any registered observers that the <code>itemCount</code> group items previously
     * located at <code>groupPositionStart</code> have been removed from the data set. The group items
     * previously located at and after <code>groupPositionStart + itemCount</code> may now be found
     * at <code>oldPosition - itemCount</code>.</p>
     * <p>This is a structural change event. Representations of other existing items in the data
     * set are still considered up to date and will not be rebound, though their positions
     * may be altered.</p>
     *
     * @param groupPositionStart Previous position of the first group item that was removed
     * @param itemCount          Number of group items removed from the data set
     */
    public void notifyGroupItemRangeRemoved(int groupPositionStart, int itemCount) {
        mAdapter.notifyGroupItemRangeRemoved(groupPositionStart, itemCount);
    }

    /**
     * <p>Notify any registered observers that the child item previously located at <code>childPosition</code>
     * has been removed from the data set. The child items previously located at and after
     * <code>childPosition</code> may now be found at <code>oldGroupPosition - 1</code>.</p>
     * <p>This is a structural change event. Representations of other existing items in the
     * data set are still considered up to date and will not be rebound, though their positions
     * may be altered.</p>
     *
     * @param groupPosition Position of the group item which was the parent of the child item that was removed
     * @param childPosition Position of the child item that has now been removed
     * @see #notifyGroupItemRangeRemoved(int, int)
     */
    public void notifyChildItemRemoved(int groupPosition, int childPosition) {
        mAdapter.notifyChildItemRemoved(groupPosition, childPosition);
    }

    /**
     * <p>Notify any registered observers that the <code>itemCount</code> child items previously
     * located at <code>childPositionStart</code> have been removed from the data set. The child items
     * previously located at and after <code>childPositionStart + itemCount</code> may now be found
     * at <code>oldPosition - itemCount</code>.</p>
     * <p>This is a structural change event. Representations of other existing items in the data
     * set are still considered up to date and will not be rebound, though their positions
     * may be altered.</p>
     *
     * @param groupPosition      Position of the group item which was the parent of the child item that was removed
     * @param childPositionStart Previous position of the first child item that was removed
     * @param itemCount          Number of child items removed from the data set
     */
    public void notifyChildItemRangeRemoved(int groupPosition, int childPositionStart, int itemCount) {
        mAdapter.notifyChildItemRangeRemoved(groupPosition, childPositionStart, itemCount);
    }

    /**
     * <p>Notify any registered observers that the group item reflected at
     * <code>fromGroupPosition</code> has been moved to <code>toGroupPosition</code>.</p>
     * <p>This is a structural change event. Representations of other existing items in the data set are
     * still considered up to date and will not be rebound, though their positions may be altered.</p>
     *
     * @param fromGroupPosition Previous position of the group item.
     * @param toGroupPosition New position of the group item.
     */
    public void notifyGroupItemMoved(int fromGroupPosition, int toGroupPosition) {
        mAdapter.notifyGroupItemMoved(fromGroupPosition, toGroupPosition);
    }

    /**
     * <p>Notify any registered observers that the child item reflected at
     * <code>groupPosition, fromChildPosition</code> has been moved to <code>groupPosition, toChildPosition</code>.</p>
     * <p>This is a structural change event. Representations of other existing items in the data set are
     * still considered up to date and will not be rebound, though their positions may be altered.</p>
     *
     * @param groupPosition Group position of the child item.
     * @param fromChildPosition Previous child position of the child item.
     * @param toChildPosition New child position of the child item.
     */
    public void notifyChildItemMoved(int groupPosition, int fromChildPosition, int toChildPosition) {
        mAdapter.notifyChildItemMoved(groupPosition, fromChildPosition, toChildPosition);
    }

    /**
     * <p>Notify any registered observers that the child item reflected at
     * <code>fromGroupPosition, fromChildPosition</code> has been moved to <code>toGroupPosition, toChildPosition</code>.</p>
     * <p>This is a structural change event. Representations of other existing items in the data set are
     * still considered up to date and will not be rebound, though their positions may be altered.</p>
     * 
     * @param fromGroupPosition Previous group position of the child item.
     * @param fromChildPosition Previous child position of the child item.
     * @param toGroupPosition New group position of the child item.
     * @param toChildPosition New child position of the child item.
     */
    public void notifyChildItemMoved(int fromGroupPosition, int fromChildPosition, int toGroupPosition, int toChildPosition) {
        mAdapter.notifyChildItemMoved(fromGroupPosition, fromChildPosition, toGroupPosition, toChildPosition);
    }

    /**
     * Gets the number of groups.
     *
     * @return the number of groups
     */
    public int getGroupCount() {
        return mAdapter.getGroupCount();
    }

    /**
     * Gets the number of children in a specified group.
     *
     * @param groupPosition the position of the group for which the children count should be returned
     * @return the number of children
     */
    public int getChildCount(int groupPosition) {
        return mAdapter.getChildCount(groupPosition);
    }

    /**
     * Scroll to a group.
     *
     * @param groupPosition   Position of the group item
     * @param childItemHeight Height of each child item height
     */
    public void scrollToGroup(int groupPosition, int childItemHeight) {
        scrollToGroup(groupPosition, childItemHeight, 0, 0);
    }

    /**
     * Scroll to a group.
     *
     * @param groupPosition   Position of the group item
     * @param childItemHeight Height of each child item height
     * @param topMargin       Top margin
     * @param bottomMargin    Bottom margin
     */
    public void scrollToGroup(int groupPosition, int childItemHeight, int topMargin, int bottomMargin) {
        int totalChildrenHeight = getChildCount(groupPosition) * childItemHeight;
        scrollToGroupWithTotalChildrenHeight(groupPosition, totalChildrenHeight, topMargin, bottomMargin);
    }

    /**
     * Scroll to a group with specifying total children height.
     *
     * @param groupPosition       Position of the group item
     * @param totalChildrenHeight Total height of children items
     * @param topMargin           Top margin
     * @param bottomMargin        Bottom margin
     */
    @SuppressWarnings("StatementWithEmptyBody")
    public void scrollToGroupWithTotalChildrenHeight(int groupPosition, int totalChildrenHeight, int topMargin, int bottomMargin) {
        long packedPosition = RecyclerViewExpandableItemManager.getPackedPositionForGroup(groupPosition);
        int flatPosition = getFlatPosition(packedPosition);

        RecyclerView.ViewHolder vh = mRecyclerView.findViewHolderForLayoutPosition(flatPosition);

        if (vh == null) {
            return;
        }

        if (!isGroupExpanded(groupPosition)) {
            totalChildrenHeight = 0;
        }

        int groupItemTop = vh.itemView.getTop();
        int groupItemBottom = vh.itemView.getBottom();

        int parentHeight = mRecyclerView.getHeight();

        //noinspection UnnecessaryLocalVariable
        final int topRoom = groupItemTop;
        final int bottomRoom = parentHeight - groupItemBottom;

        if (topRoom <= topMargin) {
            // scroll down
            // WTF! smoothScrollBy() does not work properly!
            // smoothScrollToPosition() does not scroll smoothly BUT scrollToPosition(flatPosition) does!

            int parentTopPadding = mRecyclerView.getPaddingTop();
            int itemTopMargin = ((RecyclerView.LayoutParams) vh.itemView.getLayoutParams()).topMargin;
            int offset = topMargin - parentTopPadding - itemTopMargin;

            ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(flatPosition, offset);
        } else if (bottomRoom >= (totalChildrenHeight + bottomMargin)) {
            // no need to scroll
        } else {
            // scroll up
            int scrollAmount = Math.max(0, totalChildrenHeight + bottomMargin - bottomRoom);
            scrollAmount = Math.min(topRoom - topMargin, scrollAmount);
            mRecyclerView.smoothScrollBy(0, scrollAmount);
        }
    }

    /**
     * Gets the number of expanded groups.
     *
     * @return the number of expanded groups
     */
    public int getExpandedGroupsCount() {
        return mAdapter.getExpandedGroupsCount();
    }

    /**
     * Gets the number of collapsed groups.
     *
     * @return the number of collapsed groups
     */
    public int getCollapsedGroupsCount() {
        return mAdapter.getCollapsedGroupsCount();
    }

    /**
     * Whether the all groups are expanded.
     *
     * @return True if there is at least 1 group exists and every groups are expanded, otherwise false.
     */
    public boolean isAllGroupsExpanded() {
        return mAdapter.isAllGroupsExpanded();
    }

    /**
     * Whether the all groups are expanded.
     *
     * @return True if no group exists or every groups are collapsed, otherwise false.
     */
    public boolean isAllGroupsCollapsed() {
        return mAdapter.isAllGroupsCollapsed();
    }

    public static class SavedState implements Parcelable {
        final int[] adapterSavedState;

        public SavedState(int[] adapterSavedState) {
            this.adapterSavedState = adapterSavedState;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeIntArray(this.adapterSavedState);
        }

        SavedState(Parcel in) {
            this.adapterSavedState = in.createIntArray();
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}

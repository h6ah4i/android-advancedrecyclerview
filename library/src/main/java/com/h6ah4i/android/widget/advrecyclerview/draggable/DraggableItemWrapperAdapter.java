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

package com.h6ah4i.android.widget.advrecyclerview.draggable;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.utils.BaseWrapperAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

class DraggableItemWrapperAdapter<VH extends RecyclerView.ViewHolder> extends BaseWrapperAdapter<VH> {
    private static final String TAG = "ARVDraggableWrapper";

    private static final int STATE_FLAG_INITIAL_VALUE = -1;

    private static final boolean LOCAL_LOGV = false;
    private static final boolean LOCAL_LOGD = false;
    private static final boolean DEBUG_BYPASS_MOVE_OPERATION_MODE = false;

    private RecyclerViewDragDropManager mDragDropManager;
    private DraggableItemAdapter mDraggableItemAdapter;
    private RecyclerView.ViewHolder mDraggingItem;
    private ItemDraggableRange mDraggableRange;
    private long mDraggingItemId = RecyclerView.NO_ID;
    private int mDraggingItemInitialPosition = RecyclerView.NO_POSITION;
    private int mDraggingItemCurrentPosition = RecyclerView.NO_POSITION;

    public DraggableItemWrapperAdapter(RecyclerViewDragDropManager manager, RecyclerView.Adapter<VH> adapter) {
        super(adapter);

        mDraggableItemAdapter = getDraggableItemAdapter(adapter);
        if (getDraggableItemAdapter(adapter) == null) {
            throw new IllegalArgumentException("adapter does not implement DraggableItemAdapter");
        }

        if (manager == null) {
            throw new IllegalArgumentException("manager cannot be null");
        }

        mDragDropManager = manager;
    }

    @Override
    protected void onRelease() {
        super.onRelease();
        mDraggingItem = null;
        mDraggableItemAdapter = null;
        mDragDropManager = null;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        final VH holder = super.onCreateViewHolder(parent, viewType);

        if (holder instanceof DraggableItemViewHolder) {
            ((DraggableItemViewHolder) holder).setDragStateFlags(STATE_FLAG_INITIAL_VALUE);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        if (isDragging()) {
            final long itemId = holder.getItemId();

            final int origPosition = convertToOriginalPosition(
                    position, mDraggingItemInitialPosition, mDraggingItemCurrentPosition);

            if (itemId == mDraggingItemId && holder != mDraggingItem) {
                Log.w(TAG, "an another view holder object for the currently dragging item is assigned");
            }

            int flags = RecyclerViewDragDropManager.STATE_FLAG_DRAGGING;

            if (itemId == mDraggingItemId) {
                flags |= RecyclerViewDragDropManager.STATE_FLAG_IS_ACTIVE;
            }
            if (mDraggableRange.checkInRange(position)) {
                flags |= RecyclerViewDragDropManager.STATE_FLAG_IS_IN_RANGE;
            }

            safeUpdateFlags(holder, flags);
            super.onBindViewHolder(holder, origPosition);
        } else {
            safeUpdateFlags(holder, 0);
            super.onBindViewHolder(holder, position);
        }
    }

    @Override
    public long getItemId(int position) {
        if (isDragging()) {
            final int origPosition = convertToOriginalPosition(
                    position, mDraggingItemInitialPosition, mDraggingItemCurrentPosition);
            return super.getItemId(origPosition);
        } else {
            return super.getItemId(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isDragging()) {
            final int origPosition = convertToOriginalPosition(
                    position, mDraggingItemInitialPosition, mDraggingItemCurrentPosition);
            return super.getItemViewType(origPosition);
        } else {
            return super.getItemViewType(position);
        }
    }

    protected static int convertToOriginalPosition(int position, int dragInitial, int dragCurrent) {
        if (dragInitial < 0 || dragCurrent < 0) {
            // not dragging
            return position;
        } else {
            if ((dragInitial == dragCurrent) ||
                    ((position < dragInitial) && (position < dragCurrent)) ||
                    (position > dragInitial) && (position > dragCurrent)) {
                return position;
            } else if (dragCurrent < dragInitial) {
                if (position == dragCurrent) {
                    return dragInitial;
                } else {
                    return position - 1;
                }
            } else { // if (dragCurrent > dragInitial)
                if (position == dragCurrent) {
                    return dragInitial;
                } else {
                    return position + 1;
                }
            }
        }
    }

    @Override
    protected void onHandleWrappedAdapterChanged() {
        if (shouldCancelDragOnDataUpdated()) {
            cancelDrag();
        } else {
            super.onHandleWrappedAdapterChanged();
        }
    }

    @Override
    protected void onHandleWrappedAdapterItemRangeChanged(int positionStart, int itemCount) {
        if (shouldCancelDragOnDataUpdated()) {
            cancelDrag();
        } else {
            super.onHandleWrappedAdapterItemRangeChanged(positionStart, itemCount);
        }
    }

    @Override
    protected void onHandleWrappedAdapterItemRangeInserted(int positionStart, int itemCount) {
        if (shouldCancelDragOnDataUpdated()) {
            cancelDrag();
        } else {
            super.onHandleWrappedAdapterItemRangeInserted(positionStart, itemCount);
        }
    }

    @Override
    protected void onHandleWrappedAdapterItemRangeRemoved(int positionStart, int itemCount) {
        if (shouldCancelDragOnDataUpdated()) {
            cancelDrag();
        } else {
            super.onHandleWrappedAdapterItemRangeRemoved(positionStart, itemCount);
        }
    }

    @Override
    protected void onHandleWrappedAdapterRangeMoved(int fromPosition, int toPosition, int itemCount) {
        if (shouldCancelDragOnDataUpdated()) {
            cancelDrag();
        } else {
            super.onHandleWrappedAdapterRangeMoved(fromPosition, toPosition, itemCount);
        }
    }

    private boolean shouldCancelDragOnDataUpdated() {
        //noinspection SimplifiableIfStatement
        if (DEBUG_BYPASS_MOVE_OPERATION_MODE) {
            return false;
        }
        return isDragging();
    }

    private void cancelDrag() {
        if (mDragDropManager != null) {
            mDragDropManager.cancelDrag();
        }
    }

    // NOTE: This method is called from RecyclerViewDragDropManager
    /*package*/ void onDragItemStarted(RecyclerView.ViewHolder holder, ItemDraggableRange range) {
        if (LOCAL_LOGD) {
            Log.d(TAG, "onDragItemStarted(holder = " + holder + ")");
        }

        if (DEBUG_BYPASS_MOVE_OPERATION_MODE) {
            return;
        }

        if (holder.getItemId() == RecyclerView.NO_ID) {
            throw new IllegalStateException("dragging target must provides valid ID");
        }

        mDraggingItemInitialPosition = holder.getPosition();
        mDraggingItemCurrentPosition = mDraggingItemInitialPosition;
        mDraggingItem = holder;
        mDraggingItemId = holder.getItemId();
        mDraggableRange = range;

        notifyDataSetChanged();
    }

    // NOTE: This method is called from RecyclerViewDragDropManager
    /*package*/ void onDragItemFinished(RecyclerView.ViewHolder holder, boolean result) {
        if (LOCAL_LOGD) {
            Log.d(TAG, "onDragItemFinished(holder = " + holder + ", result = " + result + ")");
        }

        if (holder != mDraggingItem) {
            throw new IllegalStateException("onDragItemFinished() - may be a bug (mDraggingItem != holder)");
        }

        if (DEBUG_BYPASS_MOVE_OPERATION_MODE) {
            return;
        }

        if (result && (mDraggingItemCurrentPosition != mDraggingItemInitialPosition)) {
            // apply to wrapped adapter
            RecyclerView.Adapter adapter = getWrappedAdapter();
            while(adapter instanceof BaseWrapperAdapter) {
                adapter = ((BaseWrapperAdapter)adapter).getWrappedAdapter();
            }
            ((DraggableItemAdapter)adapter).onMoveItem(
                    mDraggingItemInitialPosition, mDraggingItemCurrentPosition);
        }

        mDraggingItemInitialPosition = RecyclerView.NO_POSITION;
        mDraggingItemCurrentPosition = RecyclerView.NO_POSITION;
        mDraggableRange = null;
        mDraggingItemId = RecyclerView.NO_ID;
        mDraggingItem = null;

        notifyDataSetChanged();
    }

    @Override
    public void onViewRecycled(VH holder) {
        if (isDragging()) {
            if (holder == mDraggingItem) {
                try {
                    throw new IllegalStateException("onViewRecycled() - may be a bug. dragging item should be recycled.");
                } catch (IllegalStateException e) {
                    // TODO  is there any way to prevent the dragging item from be recycled...?

                    mDragDropManager.cancelDrag();
                }
            }
        }

        super.onViewRecycled(holder);
    }

    // NOTE: This method is called from RecyclerViewDragDropManager
    /*package*/
    @SuppressWarnings("unchecked")
    boolean canStartDrag(RecyclerView.ViewHolder holder, int x, int y) {
        if (LOCAL_LOGV) {
            Log.v(TAG, "canStartDrag(holder = " + holder + ", x = " + x + ", y = " + y + ")");
        }
        return mDraggableItemAdapter.onCheckCanStartDrag(holder, x, y);
    }

    // NOTE: This method is called from RecyclerViewDragDropManager
    /*package*/
    @SuppressWarnings("unchecked")
    ItemDraggableRange getItemDraggableRange(RecyclerView.ViewHolder holder) {
        if (LOCAL_LOGV) {
            Log.v(TAG, "getItemDraggableRange(holder = " + holder + ")");
        }
        return mDraggableItemAdapter.onGetItemDraggableRange(holder);
    }

    // NOTE: This method is called from RecyclerViewDragDropManager
    /*package*/ void moveItem(int fromPosition, int toPosition) {
        if (LOCAL_LOGD) {
            Log.d(TAG, "onMoveItem(fromPosition = " + fromPosition + ", toPosition = " + toPosition + ")");
        }

        if (DEBUG_BYPASS_MOVE_OPERATION_MODE) {
            mDraggableItemAdapter.onMoveItem(fromPosition, toPosition);
            return;
        }

        final int origFromPosition = convertToOriginalPosition(
                fromPosition, mDraggingItemInitialPosition, mDraggingItemCurrentPosition);

        if (origFromPosition != mDraggingItemInitialPosition) {
            throw new IllegalStateException(
                    "onMoveItem() - may be a bug or has duplicate IDs  --- " +
                            "mDraggingItemInitialPosition = " + mDraggingItemInitialPosition + ", " +
                            "mDraggingItemCurrentPosition = " + mDraggingItemCurrentPosition + ", " +
                            "origFromPosition = " + origFromPosition + ", " +
                            "fromPosition = " + fromPosition + ", " +
                            "toPosition = " + toPosition);
        }

        mDraggingItemCurrentPosition = toPosition;

        // NOTE:
        // Don't move items in wrapped adapter here.

        // notify to observers
        notifyItemMoved(fromPosition, toPosition);
    }

    protected boolean isDragging() {
        return mDragDropManager.isDragging();
    }

    private static void safeUpdateFlags(RecyclerView.ViewHolder holder, int flags) {
        if (!(holder instanceof DraggableItemViewHolder)) {
            return;
        }

        final DraggableItemViewHolder holder2 = (DraggableItemViewHolder) holder;

        final int curFlags = holder2.getDragStateFlags();
        final int mask = ~RecyclerViewDragDropManager.STATE_FLAG_IS_UPDATED;

        // append UPDATED flag
        if ((curFlags == STATE_FLAG_INITIAL_VALUE) || (((curFlags ^ flags) & mask) != 0)) {
            flags |= RecyclerViewDragDropManager.STATE_FLAG_IS_UPDATED;
        }

        ((DraggableItemViewHolder) holder).setDragStateFlags(flags);
    }

    private static DraggableItemAdapter getDraggableItemAdapter(RecyclerView.Adapter adapter) {
        return WrapperAdapterUtils.findWrappedAdapter(adapter, DraggableItemAdapter.class);
    }
}

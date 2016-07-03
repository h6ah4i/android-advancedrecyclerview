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

import com.h6ah4i.android.widget.advrecyclerview.swipeable.BaseSwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemInternalUtils;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault;
import com.h6ah4i.android.widget.advrecyclerview.utils.BaseWrapperAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.CustomRecyclerViewUtils;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import java.util.List;

class DraggableItemWrapperAdapter<VH extends RecyclerView.ViewHolder> extends BaseWrapperAdapter<VH> implements SwipeableItemAdapter<VH> {
    private static final String TAG = "ARVDraggableWrapper";

    private static final int STATE_FLAG_INITIAL_VALUE = -1;

    private interface Constants extends DraggableItemConstants {
    }

    private static final boolean LOCAL_LOGV = false;
    private static final boolean LOCAL_LOGD = false;
    private static final boolean LOCAL_LOGI = true;
    private static final boolean DEBUG_BYPASS_MOVE_OPERATION_MODE = false;

    private RecyclerViewDragDropManager mDragDropManager;
    private DraggableItemAdapter mDraggableItemAdapter;
    private RecyclerView.ViewHolder mDraggingItemViewHolder;
    private DraggingItemInfo mDraggingItemInfo;
    private ItemDraggableRange mDraggableRange;
    private int mDraggingItemInitialPosition = RecyclerView.NO_POSITION;
    private int mDraggingItemCurrentPosition = RecyclerView.NO_POSITION;
    private int mItemMoveMode;

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
        mDraggingItemViewHolder = null;
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
    public void onBindViewHolder(VH holder, int position, List<Object> payloads) {
        if (isDragging()) {
            final long draggingItemId = mDraggingItemInfo.id;
            final long itemId = holder.getItemId();

            final int origPosition = convertToOriginalPosition(
                    position, mDraggingItemInitialPosition, mDraggingItemCurrentPosition, mItemMoveMode);

            if (itemId == draggingItemId && holder != mDraggingItemViewHolder) {
                if (mDraggingItemViewHolder != null) {
                    onDraggingItemRecycled();
                }

                if (LOCAL_LOGI) {
                    Log.i(TAG, "a new view holder object for the currently dragging item is assigned");
                }

                mDraggingItemViewHolder = holder;
                mDragDropManager.onNewDraggingItemViewBound(holder);
            }

            int flags = Constants.STATE_FLAG_DRAGGING;

            if (itemId == draggingItemId) {
                flags |= Constants.STATE_FLAG_IS_ACTIVE;
            }
            if (mDraggableRange.checkInRange(position)) {
                flags |= Constants.STATE_FLAG_IS_IN_RANGE;
            }

            safeUpdateFlags(holder, flags);
            super.onBindViewHolder(holder, origPosition, payloads);
        } else {
            safeUpdateFlags(holder, 0);
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public long getItemId(int position) {
        if (isDragging()) {
            final int origPosition = convertToOriginalPosition(
                    position, mDraggingItemInitialPosition, mDraggingItemCurrentPosition, mItemMoveMode);
            return super.getItemId(origPosition);
        } else {
            return super.getItemId(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isDragging()) {
            final int origPosition = convertToOriginalPosition(
                    position, mDraggingItemInitialPosition, mDraggingItemCurrentPosition, mItemMoveMode);
            return super.getItemViewType(origPosition);
        } else {
            return super.getItemViewType(position);
        }
    }

    protected static int convertToOriginalPosition(int position, int dragInitial, int dragCurrent, int itemMoveMode) {
        if (dragInitial < 0 || dragCurrent < 0) {
            // not dragging
            return position;
        } else if (itemMoveMode == RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT) {
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
        } else if (itemMoveMode == RecyclerViewDragDropManager.ITEM_MOVE_MODE_SWAP) {
            if (position == dragCurrent) {
                return dragInitial;
            } else if (position == dragInitial) {
                return dragCurrent;
            } else {
                return position;
            }
        } else {
            throw new IllegalStateException("unexpected state");
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
    /*package*/ void onDragItemStarted(DraggingItemInfo draggingItemInfo, RecyclerView.ViewHolder holder, ItemDraggableRange range, int itemMoveMode) {
        if (LOCAL_LOGD) {
            Log.d(TAG, "onDragItemStarted(holder = " + holder + ")");
        }

        if (DEBUG_BYPASS_MOVE_OPERATION_MODE) {
            return;
        }

        if (holder.getItemId() == RecyclerView.NO_ID) {
            throw new IllegalStateException("dragging target must provides valid ID");
        }

        mDraggingItemInitialPosition = mDraggingItemCurrentPosition = holder.getAdapterPosition();
        mDraggingItemInfo = draggingItemInfo;
        mDraggingItemViewHolder = holder;
        mDraggableRange = range;
        mItemMoveMode = itemMoveMode;

        notifyDataSetChanged();
    }

    // NOTE: This method is called from RecyclerViewDragDropManager
    /*package*/ void onDragItemFinished(boolean result) {
        if (LOCAL_LOGD) {
            Log.d(TAG, "onDragItemFinished(result = " + result + ")");
        }

        if (DEBUG_BYPASS_MOVE_OPERATION_MODE) {
            return;
        }

        if (result && (mDraggingItemCurrentPosition != mDraggingItemInitialPosition)) {
            // apply to wrapped adapter
            DraggableItemAdapter adapter = WrapperAdapterUtils.findWrappedAdapter(
                    getWrappedAdapter(), DraggableItemAdapter.class);
            adapter.onMoveItem(mDraggingItemInitialPosition, mDraggingItemCurrentPosition);
        }

        mDraggingItemInitialPosition = RecyclerView.NO_POSITION;
        mDraggingItemCurrentPosition = RecyclerView.NO_POSITION;
        mDraggableRange = null;
        mDraggingItemInfo = null;
        mDraggingItemViewHolder = null;

        notifyDataSetChanged();
    }

    @Override
    public void onViewRecycled(VH holder) {
        if (isDragging()) {
            if (holder == mDraggingItemViewHolder) {
                onDraggingItemRecycled();
            }
        }

        super.onViewRecycled(holder);
    }

    private void onDraggingItemRecycled() {
        if (LOCAL_LOGI) {
            Log.i(TAG, "a view holder object which is bound to currently dragging item is recycled");
        }
        mDraggingItemViewHolder = null;
        mDragDropManager.onDraggingItemViewRecycled();
    }

    // NOTE: This method is called from RecyclerViewDragDropManager
    /*package*/
    @SuppressWarnings("unchecked")
    boolean canStartDrag(RecyclerView.ViewHolder holder, int position, int x, int y) {
        if (LOCAL_LOGV) {
            Log.v(TAG, "canStartDrag(holder = " + holder + ", position = " + position + ", x = " + x + ", y = " + y + ")");
        }
        return mDraggableItemAdapter.onCheckCanStartDrag(holder, position, x, y);
    }

    // NOTE: This method is called from RecyclerViewDragDropManager
    /*package*/
    @SuppressWarnings("unchecked")
    boolean canDropItems(int draggingPosition, int dropPosition) {
        if (LOCAL_LOGV) {
            Log.v(TAG, "canDropItems(draggingPosition = " + draggingPosition + ", dropPosition = " + dropPosition + ")");
        }
        return mDraggableItemAdapter.onCheckCanDrop(draggingPosition, dropPosition);
    }

    // NOTE: This method is called from RecyclerViewDragDropManager
    /*package*/
    @SuppressWarnings("unchecked")
    ItemDraggableRange getItemDraggableRange(RecyclerView.ViewHolder holder, int position) {
        if (LOCAL_LOGV) {
            Log.v(TAG, "getItemDraggableRange(holder = " + holder + ", position = " + position + ")");
        }
        return mDraggableItemAdapter.onGetItemDraggableRange(holder, position);
    }

    // NOTE: This method is called from RecyclerViewDragDropManager
    /*package*/ void moveItem(int fromPosition, int toPosition, int layoutType) {
        if (LOCAL_LOGD) {
            Log.d(TAG, "onMoveItem(fromPosition = " + fromPosition + ", toPosition = " + toPosition + ")");
        }

        if (DEBUG_BYPASS_MOVE_OPERATION_MODE) {
            mDraggableItemAdapter.onMoveItem(fromPosition, toPosition);
            return;
        }

        final int origFromPosition = convertToOriginalPosition(
                fromPosition, mDraggingItemInitialPosition, mDraggingItemCurrentPosition, mItemMoveMode);

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
        if (mItemMoveMode == RecyclerViewDragDropManager.ITEM_MOVE_MODE_DEFAULT && CustomRecyclerViewUtils.isLinearLayout(layoutType)) {
            notifyItemMoved(fromPosition, toPosition);
        } else {
            notifyDataSetChanged();
        }
    }

    protected boolean isDragging() {
        return (mDraggingItemInfo != null);
    }

    /*package*/ int getDraggingItemInitialPosition() {
        return mDraggingItemInitialPosition;
    }

    /*package*/ int getDraggingItemCurrentPosition() {
        return mDraggingItemCurrentPosition;
    }

    private static void safeUpdateFlags(RecyclerView.ViewHolder holder, int flags) {
        if (!(holder instanceof DraggableItemViewHolder)) {
            return;
        }

        final DraggableItemViewHolder holder2 = (DraggableItemViewHolder) holder;

        final int curFlags = holder2.getDragStateFlags();
        final int mask = ~Constants.STATE_FLAG_IS_UPDATED;

        // append UPDATED flag
        if ((curFlags == STATE_FLAG_INITIAL_VALUE) || (((curFlags ^ flags) & mask) != 0)) {
            flags |= Constants.STATE_FLAG_IS_UPDATED;
        }

        ((DraggableItemViewHolder) holder).setDragStateFlags(flags);
    }

    private static DraggableItemAdapter getDraggableItemAdapter(RecyclerView.Adapter adapter) {
        return WrapperAdapterUtils.findWrappedAdapter(adapter, DraggableItemAdapter.class);
    }

    private int getOriginalPosition(int position) {
        int correctedPosition;

        if (isDragging()) {
            correctedPosition = convertToOriginalPosition(
                    position, mDraggingItemInitialPosition, mDraggingItemCurrentPosition, mItemMoveMode);
        } else {
            correctedPosition = position;
        }
        return correctedPosition;
    }

    //
    // SwipeableItemAdapter implementations
    //
    @SuppressWarnings("unchecked")
    @Override
    public int onGetSwipeReactionType(VH holder, int position, int x, int y) {
        RecyclerView.Adapter adapter = getWrappedAdapter();
        if (!(adapter instanceof BaseSwipeableItemAdapter)) {
            return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_DEFAULT;
        }

        int correctedPosition = getOriginalPosition(position);

        BaseSwipeableItemAdapter<VH> swipeableItemAdapter = (BaseSwipeableItemAdapter<VH>) adapter;
        return swipeableItemAdapter.onGetSwipeReactionType(holder, correctedPosition, x, y);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onSetSwipeBackground(VH holder, int position, int type) {
        RecyclerView.Adapter adapter = getWrappedAdapter();
        if (!(adapter instanceof BaseSwipeableItemAdapter)) {
            return;
        }

        int correctedPosition = getOriginalPosition(position);

        BaseSwipeableItemAdapter<VH> swipeableItemAdapter = (BaseSwipeableItemAdapter<VH>) adapter;
        swipeableItemAdapter.onSetSwipeBackground(holder, correctedPosition, type);
    }

    @SuppressWarnings("unchecked")
    @Override
    public SwipeResultAction onSwipeItem(VH holder, int position, int result) {
        RecyclerView.Adapter adapter = getWrappedAdapter();
        if (!(adapter instanceof BaseSwipeableItemAdapter)) {
            return new SwipeResultActionDefault();
        }

        int correctedPosition = getOriginalPosition(position);

        return SwipeableItemInternalUtils.invokeOnSwipeItem(
                (BaseSwipeableItemAdapter) adapter, holder, correctedPosition, result);
    }
}

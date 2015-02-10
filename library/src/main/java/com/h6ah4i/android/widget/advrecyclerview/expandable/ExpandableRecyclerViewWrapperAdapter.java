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

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.BaseWrapperAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

public class ExpandableRecyclerViewWrapperAdapter
        extends BaseWrapperAdapter<RecyclerView.ViewHolder>
        implements DraggableItemAdapter<RecyclerView.ViewHolder>,
        SwipeableItemAdapter<RecyclerView.ViewHolder> {
    private static final String TAG = "ARVExpandableWrapper";

    private static final int VIEW_TYPE_FLAG_IS_GROUP = ExpandableAdapterHelper.VIEW_TYPE_FLAG_IS_GROUP;

    private static final int STATE_FLAG_INITIAL_VALUE = -1;

    private ExpandableItemAdapter mExpandableItemAdapter;
    private RecyclerViewExpandableItemManager mExpandableListManager;
    private ExpandablePositionTranslator mPositionTranslator;

    public ExpandableRecyclerViewWrapperAdapter(RecyclerViewExpandableItemManager manager, RecyclerView.Adapter<RecyclerView.ViewHolder> adapter, int[] expandedItemsSavedState) {
        super(adapter);

        mExpandableItemAdapter = getExpandableItemAdapter(adapter);
        if (mExpandableItemAdapter == null) {
            throw new IllegalArgumentException("adapter does not implement RecyclerViewExpandableListManager");
        }

        if (manager == null) {
            throw new IllegalArgumentException("manager cannot be null");
        }

        mExpandableListManager = manager;

        mPositionTranslator = new ExpandablePositionTranslator();
        mPositionTranslator.build(mExpandableItemAdapter);

        if (expandedItemsSavedState != null) {
            mPositionTranslator.restoreExpandedGroupItems(expandedItemsSavedState);
        }
    }

    @Override
    protected void onRelease() {
        super.onRelease();

        mExpandableItemAdapter = null;
        mExpandableListManager = null;
    }

    @Override
    public int getItemCount() {
        return mPositionTranslator.getItemCount();
    }

    @Override
    public long getItemId(int position) {
        if (mExpandableItemAdapter == null) {
            return RecyclerView.NO_ID;
        }

        final long expandablePosition = mPositionTranslator.getExpandablePosition(position);
        final int groupPosition = ExpandableAdapterHelper.getPackedPositionGroup(expandablePosition);
        final int childPosition = ExpandableAdapterHelper.getPackedPositionChild(expandablePosition);

        if (childPosition == RecyclerView.NO_POSITION) {
            final long groupId = mExpandableItemAdapter.getGroupId(groupPosition);
            return ExpandableAdapterHelper.getCombinedGroupId(groupId);
        } else {
            final long groupId = mExpandableItemAdapter.getGroupId(groupPosition);
            final long childId = mExpandableItemAdapter.getChildId(groupPosition, childPosition);
            return ExpandableAdapterHelper.getCombinedChildId(groupId, childId);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mExpandableItemAdapter == null) {
            return 0;
        }

        final long expandablePosition = mPositionTranslator.getExpandablePosition(position);
        final int groupPosition = ExpandableAdapterHelper.getPackedPositionGroup(expandablePosition);
        final int childPosition = ExpandableAdapterHelper.getPackedPositionChild(expandablePosition);

        final int type;

        if (childPosition == RecyclerView.NO_POSITION) {
            type = mExpandableItemAdapter.getGroupItemViewType(groupPosition);
        } else {
            type = mExpandableItemAdapter.getChildItemViewType(groupPosition, childPosition);
        }

        if ((type & VIEW_TYPE_FLAG_IS_GROUP) != 0) {
            throw new IllegalStateException("Illegal view type (type = " + Integer.toHexString(type) + ")");
        }

        return (childPosition == RecyclerView.NO_POSITION) ? (type | VIEW_TYPE_FLAG_IS_GROUP) : (type);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mExpandableItemAdapter == null) {
            return null;
        }

        final int maskedViewType = (viewType & (~VIEW_TYPE_FLAG_IS_GROUP));

        final RecyclerView.ViewHolder holder;

        if ((viewType & VIEW_TYPE_FLAG_IS_GROUP) != 0) {
            holder = mExpandableItemAdapter.onCreateGroupViewHolder(parent, maskedViewType);
        } else {
            holder = mExpandableItemAdapter.onCreateChildViewHolder(parent, maskedViewType);
        }

        if (holder instanceof ExpandableItemViewHolder) {
            ((ExpandableItemViewHolder) holder).setExpandStateFlags(STATE_FLAG_INITIAL_VALUE);
        }

        return holder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mExpandableItemAdapter == null) {
            return;
        }

        final long expandablePosition = mPositionTranslator.getExpandablePosition(position);
        final int groupPosition = ExpandableAdapterHelper.getPackedPositionGroup(expandablePosition);
        final int childPosition = ExpandableAdapterHelper.getPackedPositionChild(expandablePosition);
        final int viewType = (holder.getItemViewType() & (~VIEW_TYPE_FLAG_IS_GROUP));

        // update flags
        int flags = 0;

        if (childPosition == RecyclerView.NO_POSITION) {
            flags |= RecyclerViewExpandableItemManager.STATE_FLAG_IS_GROUP;
        } else {
            flags |= RecyclerViewExpandableItemManager.STATE_FLAG_IS_CHILD;
        }

        if (mPositionTranslator.isGroupExpanded(groupPosition)) {
            flags |= RecyclerViewExpandableItemManager.STATE_FLAG_IS_EXPANDED;
        }

        safeUpdateFlags(holder, flags);

        if (childPosition == RecyclerView.NO_POSITION) {
            mExpandableItemAdapter.onBindGroupViewHolder(holder, groupPosition, viewType);
        } else {
            mExpandableItemAdapter.onBindChildViewHolder(holder, groupPosition, childPosition, viewType);
        }
    }

    private void rebuildPositionTranslator() {
        if (mPositionTranslator != null) {
            int [] savedState = mPositionTranslator.getSavedStateArray();
            mPositionTranslator.build(mExpandableItemAdapter);
            mPositionTranslator.restoreExpandedGroupItems(savedState);
        }
    }

    @Override
    protected void onHandleWrappedAdapterChanged() {
        rebuildPositionTranslator();
        super.onHandleWrappedAdapterChanged();
    }

    @Override
    protected void onHandleWrappedAdapterItemRangeChanged(int positionStart, int itemCount) {
        super.onHandleWrappedAdapterItemRangeChanged(positionStart, itemCount);
    }

    @Override
    protected void onHandleWrappedAdapterItemRangeInserted(int positionStart, int itemCount) {
        rebuildPositionTranslator();
        super.onHandleWrappedAdapterItemRangeInserted(positionStart, itemCount);
    }

    @Override
    protected void onHandleWrappedAdapterItemRangeRemoved(int positionStart, int itemCount) {
        if (itemCount == 1) {
            final long expandablePosition = mPositionTranslator.getExpandablePosition(positionStart);
            final int groupPosition = ExpandableAdapterHelper.getPackedPositionGroup(expandablePosition);
            final int childPosition = ExpandableAdapterHelper.getPackedPositionChild(expandablePosition);

            if (childPosition == RecyclerView.NO_POSITION) {
                mPositionTranslator.removeGroupItem(groupPosition);
            } else {
                mPositionTranslator.removeChildItem(groupPosition, childPosition);
            }
        } else {
            rebuildPositionTranslator();
        }

        super.onHandleWrappedAdapterItemRangeRemoved(positionStart, itemCount);
    }

    @Override
    protected void onHandleWrappedAdapterRangeMoved(int fromPosition, int toPosition, int itemCount) {
        rebuildPositionTranslator();
        super.onHandleWrappedAdapterRangeMoved(fromPosition, toPosition, itemCount);
    }

    @Override
    public boolean onCheckCanStartDrag(RecyclerView.ViewHolder holder, int x, int y) {
        if (!(mExpandableItemAdapter instanceof ExpandableDraggableItemAdapter)) {
            return false;
        }

        final ExpandableDraggableItemAdapter adapter = (ExpandableDraggableItemAdapter) mExpandableItemAdapter;

        final int flatPosition = holder.getPosition();
        final long expandablePosition = mPositionTranslator.getExpandablePosition(flatPosition);
        final int groupPosition = ExpandableAdapterHelper.getPackedPositionGroup(expandablePosition);
        final int childPosition = ExpandableAdapterHelper.getPackedPositionChild(expandablePosition);

        if (childPosition == RecyclerView.NO_POSITION) {
            return adapter.onCheckGroupCanStartDrag(holder, groupPosition, x, y);
        } else {
            return adapter.onCheckChildCanStartDrag(holder, groupPosition, childPosition, x, y);
        }
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        if (!(mExpandableItemAdapter instanceof ExpandableDraggableItemAdapter)) {
            return;
        }

        if (fromPosition == toPosition) {
            return;
        }

        final ExpandableDraggableItemAdapter adapter = (ExpandableDraggableItemAdapter) mExpandableItemAdapter;

        final long expandableFromPosition = mPositionTranslator.getExpandablePosition(fromPosition);
        final int fromGroupPosition = ExpandableAdapterHelper.getPackedPositionGroup(expandableFromPosition);
        final int fromChildPosition = ExpandableAdapterHelper.getPackedPositionChild(expandableFromPosition);

        final long expandableToPosition = mPositionTranslator.getExpandablePosition(toPosition);
        final int toGroupPosition = ExpandableAdapterHelper.getPackedPositionGroup(expandableToPosition);
        final int toChildPosition = ExpandableAdapterHelper.getPackedPositionChild(expandableToPosition);

        final boolean fromIsGroup = (fromChildPosition == RecyclerView.NO_POSITION);
        final boolean toIsGroup = (toChildPosition == RecyclerView.NO_POSITION);

        int actualToFlatPosition = fromPosition;

        if (fromIsGroup && toIsGroup) {
            adapter.onMoveGroupItem(fromGroupPosition, toGroupPosition);
            mPositionTranslator.moveGroupItem(fromGroupPosition, toGroupPosition);
            actualToFlatPosition = toPosition;
        } else if (!fromIsGroup && !toIsGroup) {
            int modToChildPosition;

            // correct child position
            if (fromGroupPosition == toGroupPosition) {
                modToChildPosition = toChildPosition;
            } else {
                if (fromPosition < toPosition) {
                    modToChildPosition = toChildPosition + 1;
                } else {
                    modToChildPosition = toChildPosition;
                }
            }

            actualToFlatPosition = mPositionTranslator.getFlatPosition(
                    ExpandableAdapterHelper.getPackedPositionForChild(fromGroupPosition, modToChildPosition));

            adapter.onMoveChildItem(fromGroupPosition, fromChildPosition, toGroupPosition, modToChildPosition);
            mPositionTranslator.moveChildItem(fromGroupPosition, fromChildPosition, toGroupPosition, modToChildPosition);
        } else if (!fromIsGroup && toIsGroup) {
            int modToGroupPosition;
            int modToChildPosition;

            if (toPosition < fromPosition) {
                if (toGroupPosition == 0) {
                    // insert at the top
                    modToGroupPosition = toGroupPosition;
                    modToChildPosition = 0;
                } else {
                    // insert at the end
                    modToGroupPosition = toGroupPosition - 1;
                    modToChildPosition = mPositionTranslator.getChildCount(modToGroupPosition);
                }
            } else {
                if (mPositionTranslator.isGroupExpanded(toGroupPosition)) {
                    // insert at the top
                    modToGroupPosition = toGroupPosition;
                    modToChildPosition = 0;
                } else {
                    // insert at the end
                    modToGroupPosition = toGroupPosition;
                    modToChildPosition = mPositionTranslator.getChildCount(modToGroupPosition);
                }
            }

            if (fromGroupPosition == modToGroupPosition) {
                final int lastIndex = Math.max(0, mPositionTranslator.getChildCount(modToGroupPosition) - 1);
                modToChildPosition = Math.min(modToChildPosition, lastIndex);
            }

            if (!((fromGroupPosition == modToGroupPosition) && (fromChildPosition == modToChildPosition))) {
                if (mPositionTranslator.isGroupExpanded(toGroupPosition)) {
                    actualToFlatPosition = toPosition;
                } else {
                    actualToFlatPosition = RecyclerView.NO_POSITION;
                }

                adapter.onMoveChildItem(fromGroupPosition, fromChildPosition, modToGroupPosition, modToChildPosition);
                mPositionTranslator.moveChildItem(fromGroupPosition, fromChildPosition, modToGroupPosition, modToChildPosition);
            }
        } else { // if (fromIsGroup && !toIsGroup)
            if (fromGroupPosition != toGroupPosition) {
                actualToFlatPosition = mPositionTranslator.getFlatPosition(ExpandableAdapterHelper.getPackedPositionForGroup(toGroupPosition));

                adapter.onMoveGroupItem(fromGroupPosition, toGroupPosition);
                mPositionTranslator.moveGroupItem(fromGroupPosition, toGroupPosition);
            }
        }

        if (actualToFlatPosition != fromPosition) {
            if (actualToFlatPosition != RecyclerView.NO_POSITION) {
                notifyItemMoved(fromPosition, actualToFlatPosition);
            } else {
                notifyItemRemoved(fromPosition);
            }
        }
    }

    @Override
    public int onGetSwipeReactionType(RecyclerView.ViewHolder holder, int x, int y) {
        if (!(mExpandableItemAdapter instanceof ExpandableSwipeableItemAdapter)) {
            return RecyclerViewSwipeManager.REACTION_CAN_NOT_SWIPE_BOTH;
        }

        final ExpandableSwipeableItemAdapter adapter = (ExpandableSwipeableItemAdapter) mExpandableItemAdapter;

        final long expandablePosition = mPositionTranslator.getExpandablePosition(holder.getPosition());
        final int groupPosition = ExpandableAdapterHelper.getPackedPositionGroup(expandablePosition);
        final int childPosition = ExpandableAdapterHelper.getPackedPositionChild(expandablePosition);

        if (childPosition == RecyclerView.NO_POSITION) {
            return adapter.onGetGroupItemSwipeReactionType(holder, groupPosition, x, y);
        } else {
            return adapter.onGetChildItemSwipeReactionType(holder, groupPosition, childPosition, x, y);
        }
    }

    @Override
    public void onSetSwipeBackground(RecyclerView.ViewHolder holder, int type) {
        if (!(mExpandableItemAdapter instanceof ExpandableSwipeableItemAdapter)) {
            return;
        }

        final ExpandableSwipeableItemAdapter adapter = (ExpandableSwipeableItemAdapter) mExpandableItemAdapter;

        final long expandablePosition = mPositionTranslator.getExpandablePosition(holder.getPosition());
        final int groupPosition = ExpandableAdapterHelper.getPackedPositionGroup(expandablePosition);
        final int childPosition = ExpandableAdapterHelper.getPackedPositionChild(expandablePosition);

        if (childPosition == RecyclerView.NO_POSITION) {
            adapter.onSetGroupItemSwipeBackground(holder, groupPosition, type);
        } else {
            adapter.onSetChildItemSwipeBackground(holder, groupPosition, childPosition, type);
        }
    }

    @Override
    public int onSwipeItem(RecyclerView.ViewHolder holder, int result) {
        if (!(mExpandableItemAdapter instanceof ExpandableSwipeableItemAdapter)) {
            return RecyclerViewSwipeManager.AFTER_SWIPE_REACTION_DEFAULT;
        }

        final ExpandableSwipeableItemAdapter adapter = (ExpandableSwipeableItemAdapter) mExpandableItemAdapter;

        final long expandablePosition = mPositionTranslator.getExpandablePosition(holder.getPosition());
        final int groupPosition = ExpandableAdapterHelper.getPackedPositionGroup(expandablePosition);
        final int childPosition = ExpandableAdapterHelper.getPackedPositionChild(expandablePosition);

        if (childPosition == RecyclerView.NO_POSITION) {
            return adapter.onSwipeGroupItem(holder, groupPosition, result);
        } else {
            return adapter.onSwipeChildItem(holder, groupPosition, childPosition, result);
        }
    }

    @Override
    public void onPerformAfterSwipeReaction(RecyclerView.ViewHolder holder, int result, int reaction) {
        if (!(mExpandableItemAdapter instanceof ExpandableSwipeableItemAdapter)) {
            return;
        }

        final ExpandableSwipeableItemAdapter adapter = (ExpandableSwipeableItemAdapter) mExpandableItemAdapter;

        final long expandablePosition = mPositionTranslator.getExpandablePosition(holder.getPosition());
        final int groupPosition = ExpandableAdapterHelper.getPackedPositionGroup(expandablePosition);
        final int childPosition = ExpandableAdapterHelper.getPackedPositionChild(expandablePosition);

        if (childPosition == RecyclerView.NO_POSITION) {
            adapter.onPerformAfterSwipeGroupReaction(holder, groupPosition, result, reaction);
        } else {
            adapter.onPerformAfterSwipeChildReaction(holder, groupPosition, childPosition, result, reaction);
        }
    }
    // NOTE: This method is called from RecyclerViewExpandableItemManager
    /*package*/ boolean onTapItem(RecyclerView.ViewHolder holder, int x, int y) {
        if (mExpandableItemAdapter == null) {
            return false;
        }

        final int position = holder.getPosition();
        final long expandablePosition = mPositionTranslator.getExpandablePosition(position);
        final int groupPosition = ExpandableAdapterHelper.getPackedPositionGroup(expandablePosition);
        final int childPosition = ExpandableAdapterHelper.getPackedPositionChild(expandablePosition);

        if (childPosition != RecyclerView.NO_POSITION) {
            return false;
        }

        final boolean expand = !(mPositionTranslator.isGroupExpanded(groupPosition));

        boolean result = mExpandableItemAdapter.onCheckCanExpandOrCollapseGroup(holder, groupPosition, x, y, expand);

        if (!result) {
            return false;
        }

        if (expand) {
            expandGroup(groupPosition);
        } else {
            collapseGroup(groupPosition);
        }

        return true;
    }

    /*package*/ boolean collapseGroup(int groupPosition) {
        if (!mPositionTranslator.isGroupExpanded(groupPosition)) {
            return false;
        }

        if (mPositionTranslator.collapseGroup(groupPosition)) {
            final long packedPosition = ExpandableAdapterHelper.getPackedPositionForGroup(groupPosition);
            final int flatPosition = mPositionTranslator.getFlatPosition(packedPosition);
            final int childCount = mPositionTranslator.getChildCount(groupPosition);

            notifyItemRangeRemoved(flatPosition + 1, childCount);
        }


        {
            final long packedPosition = ExpandableAdapterHelper.getPackedPositionForGroup(groupPosition);
            final int flatPosition = mPositionTranslator.getFlatPosition(packedPosition);

            notifyItemChanged(flatPosition);
        }

        return true;
    }

    /*package*/ boolean expandGroup(int groupPosition) {
        if (mPositionTranslator.isGroupExpanded(groupPosition)) {
            return false;
        }


        if (mPositionTranslator.expandGroup(groupPosition)) {
            final long packedPosition = ExpandableAdapterHelper.getPackedPositionForGroup(groupPosition);
            final int flatPosition = mPositionTranslator.getFlatPosition(packedPosition);
            final int childCount = mPositionTranslator.getChildCount(groupPosition);

            notifyItemRangeInserted(flatPosition + 1, childCount);
        }

        {
            final long packedPosition = ExpandableAdapterHelper.getPackedPositionForGroup(groupPosition);
            final int flatPosition = mPositionTranslator.getFlatPosition(packedPosition);

            notifyItemChanged(flatPosition);
        }

        return true;
    }

    /*package*/ boolean isGroupExpanded(int groupPosition) {
        return mPositionTranslator.isGroupExpanded(groupPosition);
    }

    /*package*/ long getExpandablePosition(int flatPosition) {
        return mPositionTranslator.getExpandablePosition(flatPosition);
    }

    /*package*/ int getFlatPosition(long packedPosition) {
        return mPositionTranslator.getFlatPosition(packedPosition);
    }

    /*package*/ int[] getExpandedItemsSavedStateArray() {
        if (mPositionTranslator != null) {
            return mPositionTranslator.getSavedStateArray();
        } else {
            return null;
        }
    }

    private static ExpandableItemAdapter getExpandableItemAdapter(RecyclerView.Adapter adapter) {
        return WrapperAdapterUtils.findWrappedAdapter(adapter, ExpandableItemAdapter.class);
    }

    private static void safeUpdateFlags(RecyclerView.ViewHolder holder, int flags) {
        if (!(holder instanceof ExpandableItemViewHolder)) {
            return;
        }

        final ExpandableItemViewHolder holder2 = (ExpandableItemViewHolder) holder;

        final int curFlags = holder2.getExpandStateFlags();
        final int mask = ~RecyclerViewExpandableItemManager.STATE_FLAG_IS_UPDATED;

        // append UPDATED flag
        if ((curFlags == STATE_FLAG_INITIAL_VALUE) || (((curFlags ^ flags) & mask) != 0)) {
            flags |= RecyclerViewExpandableItemManager.STATE_FLAG_IS_UPDATED;
        }

        ((ExpandableItemViewHolder) holder).setExpandStateFlags(flags);
    }
}

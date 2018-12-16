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

package com.h6ah4i.android.example.advrecyclerview.demo_ed_with_section;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.h6ah4i.android.example.advrecyclerview.R;
import com.h6ah4i.android.example.advrecyclerview.common.data.AbstractExpandableDataProvider;
import com.h6ah4i.android.example.advrecyclerview.common.utils.DrawableUtils;
import com.h6ah4i.android.example.advrecyclerview.common.utils.ViewUtils;
import com.h6ah4i.android.example.advrecyclerview.common.widget.ExpandableItemIndicator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemState;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableDraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemState;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.expandable.GroupPositionItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;

import androidx.annotation.NonNull;

class ExpandableDraggableWithSectionExampleAdapter
        extends AbstractExpandableItemAdapter<ExpandableDraggableWithSectionExampleAdapter.MyGroupViewHolder, ExpandableDraggableWithSectionExampleAdapter.MyChildViewHolder>
        implements ExpandableDraggableItemAdapter<ExpandableDraggableWithSectionExampleAdapter.MyGroupViewHolder, ExpandableDraggableWithSectionExampleAdapter.MyChildViewHolder> {
    private static final String TAG = "MyEDWithSectionAdapter";

    private static final int GROUP_ITEM_VIEW_TYPE_SECTION_HEADER = 0;
    private static final int GROUP_ITEM_VIEW_TYPE_SECTION_ITEM = 1;

    private boolean mAllowItemsMoveAcrossSections;

    private AbstractExpandableDataProvider mProvider;

    public static abstract class MyBaseViewHolder extends AbstractDraggableItemViewHolder implements ExpandableItemViewHolder {
        public FrameLayout mContainer;
        public View mDragHandle;
        public TextView mTextView;
        private final ExpandableItemState mExpandState = new ExpandableItemState();

        public MyBaseViewHolder(View v) {
            super(v);
            mContainer = v.findViewById(R.id.container);
            mDragHandle = v.findViewById(R.id.drag_handle);
            mTextView = v.findViewById(android.R.id.text1);
        }

        @Override
        public int getExpandStateFlags() {
            return mExpandState.getFlags();
        }

        @Override
        public void setExpandStateFlags(int flags) {
            mExpandState.setFlags(flags);
        }

        @NonNull
        @Override
        public ExpandableItemState getExpandState() {
            return mExpandState;
        }
    }

    public static class MyGroupViewHolder extends MyBaseViewHolder {
        public ExpandableItemIndicator mIndicator;

        public MyGroupViewHolder(View v) {
            super(v);
            mIndicator = v.findViewById(R.id.indicator);
        }
    }

    public static class MyChildViewHolder extends MyBaseViewHolder {
        public MyChildViewHolder(View v) {
            super(v);
        }
    }

    public ExpandableDraggableWithSectionExampleAdapter(AbstractExpandableDataProvider dataProvider) {
        mProvider = dataProvider;

        // ExpandableItemAdapter, ExpandableDraggableItemAdapter require stable ID,
        // and also have to implement the getGroupItemId()/getChildItemId() methods appropriately.
        setHasStableIds(true);
    }

    @Override
    public int getGroupCount() {
        return mProvider.getGroupCount();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return mProvider.getChildCount(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return mProvider.getGroupItem(groupPosition).getGroupId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return mProvider.getChildItem(groupPosition, childPosition).getChildId();
    }

    @Override
    public int getGroupItemViewType(int groupPosition) {
        final AbstractExpandableDataProvider.GroupData item = mProvider.getGroupItem(groupPosition);

        if (item.isSectionHeader()) {
            return GROUP_ITEM_VIEW_TYPE_SECTION_HEADER;
        } else {
            return GROUP_ITEM_VIEW_TYPE_SECTION_ITEM;
        }
    }

    @Override
    public int getChildItemViewType(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    @NonNull
    public MyGroupViewHolder onCreateGroupViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        final View v;
        switch (viewType) {
            case GROUP_ITEM_VIEW_TYPE_SECTION_HEADER:
                v = inflater.inflate(R.layout.list_section_header, parent, false);
                break;
            case GROUP_ITEM_VIEW_TYPE_SECTION_ITEM:
                v = inflater.inflate(R.layout.list_group_item_draggable, parent, false);
                break;
            default:
                throw new IllegalStateException("Unexpected viewType (= " + viewType + ")");
        }

        return new MyGroupViewHolder(v);
    }

    @Override
    @NonNull
    public MyChildViewHolder onCreateChildViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.list_item_draggable, parent, false);
        return new MyChildViewHolder(v);
    }

    @Override
    public void onBindGroupViewHolder(@NonNull MyGroupViewHolder holder, int groupPosition, int viewType) {
        switch (viewType) {
            case GROUP_ITEM_VIEW_TYPE_SECTION_HEADER:
                onbBindSectionHeaderGroupViewHolder(holder, groupPosition);
                break;
            case GROUP_ITEM_VIEW_TYPE_SECTION_ITEM:
                onBindItemGroupViewHolder(holder, groupPosition);
                break;
        }
    }

    private void onbBindSectionHeaderGroupViewHolder(@NonNull MyGroupViewHolder holder, int groupPosition) {
        // group item
        final AbstractExpandableDataProvider.GroupData item = mProvider.getGroupItem(groupPosition);

        // set text
        holder.mTextView.setText(item.getText());
    }

    private void onBindItemGroupViewHolder(@NonNull MyGroupViewHolder holder, int groupPosition) {
        // group item
        final AbstractExpandableDataProvider.GroupData item = mProvider.getGroupItem(groupPosition);

        // set text
        holder.mTextView.setText(item.getText());

        // set background resource (target view ID: container)
        final DraggableItemState dragState = holder.getDragState();
        final ExpandableItemState expandState = holder.getExpandState();

        if (dragState.isUpdated() || expandState.isUpdated()) {
            int bgResId;
            boolean animateIndicator = expandState.hasExpandedStateChanged();

            if (dragState.isActive()) {
                bgResId = R.drawable.bg_group_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                DrawableUtils.clearState(holder.mContainer.getForeground());
            } else if (dragState.isDragging() && dragState.isInRange()) {
                bgResId = R.drawable.bg_group_item_dragging_state;
            } else if (expandState.isExpanded()) {
                bgResId = R.drawable.bg_group_item_expanded_state;
            } else {
                bgResId = R.drawable.bg_group_item_normal_state;
            }

            holder.mContainer.setBackgroundResource(bgResId);
            holder.mIndicator.setExpandedState(expandState.isExpanded(), animateIndicator);
        }
    }

    @Override
    public void onBindChildViewHolder(@NonNull MyChildViewHolder holder, int groupPosition, int childPosition, int viewType) {
        // child item
        final AbstractExpandableDataProvider.ChildData item = mProvider.getChildItem(groupPosition, childPosition);

        // set text
        holder.mTextView.setText(item.getText());

        final DraggableItemState dragState = holder.getDragState();

        if (dragState.isUpdated()) {
            int bgResId;

            if (dragState.isActive()) {
                bgResId = R.drawable.bg_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                DrawableUtils.clearState(holder.mContainer.getForeground());
            } else if (dragState.isDragging() && dragState.isInRange()) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            holder.mContainer.setBackgroundResource(bgResId);
        }
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(@NonNull MyGroupViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        // check is normal item
        if (!isSectionHeader(holder)) {
            return false;
        }

        // check is enabled
        if (!(holder.itemView.isEnabled())) {
            return false;
        }

        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;

        final int offsetX = containerView.getLeft() + (int) (containerView.getTranslationX() + 0.5f);
        final int offsetY = containerView.getTop() + (int) (containerView.getTranslationY() + 0.5f);

        return !ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public boolean onCheckGroupCanStartDrag(@NonNull MyGroupViewHolder holder, int groupPosition, int x, int y) {
        // check is normal item
        if (!isSectionHeader(holder)) {
            return false;
        }

        // x, y --- relative from the itemView's top-left
        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;

        final int offsetX = containerView.getLeft() + (int) (containerView.getTranslationX() + 0.5f);
        final int offsetY = containerView.getTop() + (int) (containerView.getTranslationY() + 0.5f);

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public boolean onCheckChildCanStartDrag(@NonNull MyChildViewHolder holder, int groupPosition, int childPosition, int x, int y) {
        // x, y --- relative from the itemView's top-left
        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;

        final int offsetX = containerView.getLeft() + (int) (containerView.getTranslationX() + 0.5f);
        final int offsetY = containerView.getTop() + (int) (containerView.getTranslationY() + 0.5f);

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public ItemDraggableRange onGetGroupItemDraggableRange(@NonNull MyGroupViewHolder holder, int groupPosition) {
        if (mAllowItemsMoveAcrossSections) {
            return null;
        } else {
            // sort within the same section
            final int start = findFirstSectionItem(groupPosition);
            final int end = findLastSectionItem(groupPosition);

            return new GroupPositionItemDraggableRange(start, end);
        }
    }

    @Override
    public ItemDraggableRange onGetChildItemDraggableRange(@NonNull MyChildViewHolder holder, int groupPosition, int childPosition) {
        if (mAllowItemsMoveAcrossSections) {
            return null;
        } else {
            // sort within the same group
            return new GroupPositionItemDraggableRange(groupPosition, groupPosition);

//            // sort within the same section
//            final int start = findFirstSectionItem(groupPosition);
//            final int end = findLastSectionItem(groupPosition);
//
//            return new GroupPositionItemDraggableRange(start, end);
//
//            // sort within the specified child range
//            final int start = 0;
//            final int end = 2;
//
//            return new GroupPositionItemDraggableRange(start, end);
        }
    }

    @Override
    public boolean onCheckGroupCanDrop(int draggingGroupPosition, int dropGroupPosition) {
        return true;
    }

    @Override
    public boolean onCheckChildCanDrop(int draggingGroupPosition, int draggingChildPosition, int dropGroupPosition, int dropChildPosition) {
        final AbstractExpandableDataProvider.GroupData item = mProvider.getGroupItem(dropGroupPosition);

        if (item.isSectionHeader()) {
            // If the group item is a section header, skip it.
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onMoveGroupItem(int fromGroupPosition, int toGroupPosition) {
        mProvider.moveGroupItem(fromGroupPosition, toGroupPosition);
    }

    @Override
    public void onMoveChildItem(int fromGroupPosition, int fromChildPosition, int toGroupPosition, int toChildPosition) {
        mProvider.moveChildItem(fromGroupPosition, fromChildPosition, toGroupPosition, toChildPosition);
    }

    @Override
    public void onGroupDragStarted(int groupPosition) {
        notifyDataSetChanged();
    }

    @Override
    public void onChildDragStarted(int groupPosition, int childPosition) {
        notifyDataSetChanged();
    }

    @Override
    public void onGroupDragFinished(int fromGroupPosition, int toGroupPosition, boolean result) {
        notifyDataSetChanged();
    }

    @Override
    public void onChildDragFinished(int fromGroupPosition, int fromChildPosition, int toGroupPosition, int toChildPosition, boolean result) {
        notifyDataSetChanged();
    }

    private int findFirstSectionItem(int position) {
        AbstractExpandableDataProvider.GroupData item = mProvider.getGroupItem(position);

        if (item.isSectionHeader()) {
            throw new IllegalStateException("section item is expected");
        }

        while (position > 0) {
            AbstractExpandableDataProvider.GroupData prevItem = mProvider.getGroupItem(position - 1);

            if (prevItem.isSectionHeader()) {
                break;
            }

            position -= 1;
        }

        return position;
    }

    private int findLastSectionItem(int position) {
        AbstractExpandableDataProvider.GroupData item = mProvider.getGroupItem(position);

        if (item.isSectionHeader()) {
            throw new IllegalStateException("section item is expected");
        }

        final int lastIndex = getGroupCount() - 1;

        while (position < lastIndex) {
            AbstractExpandableDataProvider.GroupData nextItem = mProvider.getGroupItem(position + 1);

            if (nextItem.isSectionHeader()) {
                break;
            }

            position += 1;
        }

        return position;
    }

    public void setAllowItemsMoveAcrossSections(boolean allowed) {
        mAllowItemsMoveAcrossSections = allowed;
    }

    private static boolean isSectionHeader(MyGroupViewHolder holder) {
        final int groupViewType = RecyclerViewExpandableItemManager.getGroupViewType(holder.getItemViewType());
        return (groupViewType != GROUP_ITEM_VIEW_TYPE_SECTION_HEADER);
    }
}

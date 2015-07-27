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

import android.support.v4.view.ViewCompat;
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
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableDraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.expandable.GroupPositionItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;

public class MyExpandableDraggableWithSectionItemAdapter
        extends AbstractExpandableItemAdapter<MyExpandableDraggableWithSectionItemAdapter.MyGroupViewHolder, MyExpandableDraggableWithSectionItemAdapter.MyChildViewHolder>
        implements ExpandableDraggableItemAdapter<MyExpandableDraggableWithSectionItemAdapter.MyGroupViewHolder, MyExpandableDraggableWithSectionItemAdapter.MyChildViewHolder> {
    private static final String TAG = "MyEDWithSectionAdapter";

    private static final int GROUP_ITEM_VIEW_TYPE_SECTION_HEADER = 0;
    private static final int GROUP_ITEM_VIEW_TYPE_SECTION_ITEM = 1;

    private AbstractExpandableDataProvider mProvider;

    public static abstract class MyBaseViewHolder extends AbstractDraggableItemViewHolder implements ExpandableItemViewHolder {
        public FrameLayout mContainer;
        public View mDragHandle;
        public TextView mTextView;
        private int mExpandStateFlags;

        public MyBaseViewHolder(View v) {
            super(v);
            mContainer = (FrameLayout) v.findViewById(R.id.container);
            mDragHandle = v.findViewById(R.id.drag_handle);
            mTextView = (TextView) v.findViewById(android.R.id.text1);
        }

        @Override
        public int getExpandStateFlags() {
            return mExpandStateFlags;
        }

        @Override
        public void setExpandStateFlags(int flag) {
            mExpandStateFlags = flag;
        }
    }

    public static class MyGroupViewHolder extends MyBaseViewHolder {
        public ExpandableItemIndicator mIndicator;

        public MyGroupViewHolder(View v) {
            super(v);
            mIndicator = (ExpandableItemIndicator) v.findViewById(R.id.indicator);
        }
    }

    public static class MyChildViewHolder extends MyBaseViewHolder {
        public MyChildViewHolder(View v) {
            super(v);
        }
    }

    public MyExpandableDraggableWithSectionItemAdapter(AbstractExpandableDataProvider dataProvider) {
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
    public MyGroupViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
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
    public MyChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.list_item_draggable, parent, false);
        return new MyChildViewHolder(v);
    }

    @Override
    public void onBindGroupViewHolder(MyGroupViewHolder holder, int groupPosition, int viewType) {
        switch (viewType) {
            case GROUP_ITEM_VIEW_TYPE_SECTION_HEADER:
                onbBindSectionHeaderGroupViewHolder(holder, groupPosition);
                break;
            case GROUP_ITEM_VIEW_TYPE_SECTION_ITEM:
                onBindItemGroupViewHolder(holder, groupPosition);
                break;
        }
    }

    private void onbBindSectionHeaderGroupViewHolder(MyGroupViewHolder holder, int groupPosition) {
        // group item
        final AbstractExpandableDataProvider.GroupData item = mProvider.getGroupItem(groupPosition);

        // set text
        holder.mTextView.setText(item.getText());
    }

    private void onBindItemGroupViewHolder(MyGroupViewHolder holder, int groupPosition) {
        // group item
        final AbstractExpandableDataProvider.GroupData item = mProvider.getGroupItem(groupPosition);

        // set text
        holder.mTextView.setText(item.getText());

        // set background resource (target view ID: container)
        final int dragState = holder.getDragStateFlags();
        final int expandState = holder.getExpandStateFlags();

        if (((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_UPDATED) != 0) ||
                ((expandState & RecyclerViewExpandableItemManager.STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId;
            boolean isExpanded;

            if ((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_group_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                DrawableUtils.clearState(holder.mContainer.getForeground());
            } else if (((dragState & RecyclerViewDragDropManager.STATE_FLAG_DRAGGING) != 0) &&
                    ((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_IN_RANGE) != 0)) {
                bgResId = R.drawable.bg_group_item_dragging_state;
            } else if ((expandState & RecyclerViewExpandableItemManager.STATE_FLAG_IS_EXPANDED) != 0) {
                bgResId = R.drawable.bg_group_item_expanded_state;
            } else {
                bgResId = R.drawable.bg_group_item_normal_state;
            }

            if ((expandState & RecyclerViewExpandableItemManager.STATE_FLAG_IS_EXPANDED) != 0) {
                isExpanded = true;
            } else {
                isExpanded = false;
            }

            holder.mContainer.setBackgroundResource(bgResId);
            holder.mIndicator.setExpandedState(isExpanded, true);
        }
    }

    @Override
    public void onBindChildViewHolder(MyChildViewHolder holder, int groupPosition, int childPosition, int viewType) {
        // child item
        final AbstractExpandableDataProvider.ChildData item = mProvider.getChildItem(groupPosition, childPosition);

        // set text
        holder.mTextView.setText(item.getText());

        final int dragState = holder.getDragStateFlags();

        if (((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId;

            if ((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                DrawableUtils.clearState(holder.mContainer.getForeground());
            } else if (((dragState & RecyclerViewDragDropManager.STATE_FLAG_DRAGGING) != 0) &&
                    ((dragState & RecyclerViewDragDropManager.STATE_FLAG_IS_IN_RANGE) != 0)) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            holder.mContainer.setBackgroundResource(bgResId);
        }
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(MyGroupViewHolder holder, int groupPosition, int x, int y, boolean expand) {
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

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return !ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public boolean onCheckGroupCanStartDrag(MyGroupViewHolder holder, int groupPosition, int x, int y) {
        // check is normal item
        if (!isSectionHeader(holder)) {
            return false;
        }

        // x, y --- relative from the itemView's top-left
        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public boolean onCheckChildCanStartDrag(MyChildViewHolder holder, int groupPosition, int childPosition, int x, int y) {
        // x, y --- relative from the itemView's top-left
        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public ItemDraggableRange onGetGroupItemDraggableRange(MyGroupViewHolder holder, int groupPosition) {
        // sort within the same section
        final int start = findFirstSectionItem(groupPosition);
        final int end = findLastSectionItem(groupPosition);

        return new GroupPositionItemDraggableRange(start, end);
    }

    @Override
    public ItemDraggableRange onGetChildItemDraggableRange(MyChildViewHolder holder, int groupPosition, int childPosition) {
        // sort within the same group
        return new GroupPositionItemDraggableRange(groupPosition, groupPosition);

//        // sort within the same section
//        final int start = findFirstSectionItem(groupPosition);
//        final int end = findLastSectionItem(groupPosition);
//
//        return new GroupPositionItemDraggableRange(start, end);

//        // sort within the specified child range
//        final int start = 0;
//        final int end = 2;
//
//        return new ChildPositionItemDraggableRange(start, end);
    }

    @Override
    public void onMoveGroupItem(int fromGroupPosition, int toGroupPosition) {
        mProvider.moveGroupItem(fromGroupPosition, toGroupPosition);
    }

    @Override
    public void onMoveChildItem(int fromGroupPosition, int fromChildPosition, int toGroupPosition, int toChildPosition) {
        mProvider.moveChildItem(fromGroupPosition, fromChildPosition, toGroupPosition, toChildPosition);
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

    private static boolean isSectionHeader(MyGroupViewHolder holder) {
        final int groupViewType = RecyclerViewExpandableItemManager.getGroupViewType(holder.getItemViewType());
        return (groupViewType != GROUP_ITEM_VIEW_TYPE_SECTION_HEADER);
    }
}

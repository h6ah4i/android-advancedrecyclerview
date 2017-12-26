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

package com.h6ah4i.android.example.advrecyclerview.demo_e_add_remove;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.h6ah4i.android.example.advrecyclerview.R;
import com.h6ah4i.android.example.advrecyclerview.common.data.AbstractAddRemoveExpandableDataProvider;
import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;

class AddRemoveExpandableExampleAdapter
        extends AbstractExpandableItemAdapter<AddRemoveExpandableExampleAdapter.MyGroupViewHolder, AddRemoveExpandableExampleAdapter.MyChildViewHolder> {
    private static final String TAG = "MyExpandableItemAdapter";

    // NOTE: Make accessible with short name
    private interface Expandable extends ExpandableItemConstants {
    }

    private RecyclerViewExpandableItemManager mExpandableItemManager;
    private AbstractAddRemoveExpandableDataProvider mProvider;
    private View.OnClickListener mItemOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onClickItemView(v);
        }
    };

    public static abstract class MyBaseViewHolder extends AbstractExpandableItemViewHolder {
        public FrameLayout mContainer;
        public TextView mTextView;

        public MyBaseViewHolder(View v, View.OnClickListener clickListener) {
            super(v);
            mContainer = v.findViewById(R.id.container);
            mTextView = v.findViewById(android.R.id.text1);

            mContainer.setOnClickListener(clickListener);
        }
    }

    public static class MyGroupViewHolder extends MyBaseViewHolder {
        public Button mButtonAddChildTop;
        public Button mButtonAddChildBottom;
        public Button mButtonAddChild2Bottom;
        public Button mButtonRemoveChildTop;
        public Button mButtonRemoveChildBottom;
        public Button mButtonRemoveChild2Bottom;
        public Button mButtonAddGroupAbove;
        public Button mButtonAddGroupBelow;
        public Button mButtonRemoveGroup;
        public Button mButtonClearChildren;

        public MyGroupViewHolder(View v, View.OnClickListener clickListener) {
            super(v, clickListener);

            mButtonAddChildTop = v.findViewById(R.id.button_add_child_top);
            mButtonAddChildBottom = v.findViewById(R.id.button_add_child_bottom);
            mButtonAddChild2Bottom = v.findViewById(R.id.button_add_child_bottom_2);
            mButtonRemoveChildTop = v.findViewById(R.id.button_remove_child_top);
            mButtonRemoveChildBottom = v.findViewById(R.id.button_remove_child_bottom);
            mButtonRemoveChild2Bottom = v.findViewById(R.id.button_remove_child_bottom_2);
            mButtonAddGroupAbove = v.findViewById(R.id.button_add_group_above);
            mButtonAddGroupBelow = v.findViewById(R.id.button_add_group_below);
            mButtonRemoveGroup = v.findViewById(R.id.button_remove_group);
            mButtonClearChildren = v.findViewById(R.id.button_clear_children);

            mButtonAddChildTop.setOnClickListener(clickListener);
            mButtonAddChildBottom.setOnClickListener(clickListener);
            mButtonAddChild2Bottom.setOnClickListener(clickListener);
            mButtonRemoveChildTop.setOnClickListener(clickListener);
            mButtonRemoveChildBottom.setOnClickListener(clickListener);
            mButtonRemoveChild2Bottom.setOnClickListener(clickListener);
            mButtonAddGroupAbove.setOnClickListener(clickListener);
            mButtonAddGroupBelow.setOnClickListener(clickListener);
            mButtonRemoveGroup.setOnClickListener(clickListener);
            mButtonClearChildren.setOnClickListener(clickListener);
        }
    }

    public static class MyChildViewHolder extends MyBaseViewHolder {
        public Button mButtonAddChildAbove;
        public Button mButtonAddChildBelow;
        public Button mButtonRemoveChild;

        public MyChildViewHolder(View v, View.OnClickListener clickListener) {
            super(v, clickListener);
            mButtonAddChildAbove = v.findViewById(R.id.button_add_child_above);
            mButtonAddChildBelow = v.findViewById(R.id.button_add_child_below);
            mButtonRemoveChild = v.findViewById(R.id.button_remove_child);

            mButtonAddChildAbove.setOnClickListener(clickListener);
            mButtonAddChildBelow.setOnClickListener(clickListener);
            mButtonRemoveChild.setOnClickListener(clickListener);
        }
    }

    public AddRemoveExpandableExampleAdapter(
            RecyclerViewExpandableItemManager expandableItemManager,
            AbstractAddRemoveExpandableDataProvider dataProvider) {

        mExpandableItemManager = expandableItemManager;
        mProvider = dataProvider;

        // ExpandableItemAdapter requires stable ID, and also
        // have to implement the getGroupItemId()/getChildItemId() methods appropriately.
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
        return 0;
    }

    @Override
    public int getChildItemViewType(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public MyGroupViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.list_group_item_with_add_remove_buttons, parent, false);
        return new MyGroupViewHolder(v, mItemOnClickListener);
    }

    @Override
    public MyChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.list_item_with_add_remove_buttons, parent, false);
        return new MyChildViewHolder(v, mItemOnClickListener);
    }

    @Override
    public void onBindGroupViewHolder(MyGroupViewHolder holder, int groupPosition, int viewType) {
        // child item
        final AbstractAddRemoveExpandableDataProvider.BaseData item = mProvider.getGroupItem(groupPosition);

        // set text
        holder.mTextView.setText(item.getText());

        // mark as clickable
        holder.itemView.setClickable(true);

        // set background resource (target view ID: container)
        final int expandState = holder.getExpandStateFlags();

        if ((expandState & Expandable.STATE_FLAG_IS_UPDATED) != 0) {
            int bgResId;

            if ((expandState & Expandable.STATE_FLAG_IS_EXPANDED) != 0) {
                bgResId = R.drawable.bg_group_item_expanded_state;
            } else {
                bgResId = R.drawable.bg_group_item_normal_state;
            }

            holder.mContainer.setBackgroundResource(bgResId);
        }
    }

    @Override
    public void onBindChildViewHolder(MyChildViewHolder holder, int groupPosition, int childPosition, int viewType) {
        // group item
        final AbstractAddRemoveExpandableDataProvider.ChildData item = mProvider.getChildItem(groupPosition, childPosition);

        // set text
        holder.mTextView.setText(item.getText());

        // set background resource (target view ID: container)
        int bgResId;
        bgResId = R.drawable.bg_item_normal_state;
        holder.mContainer.setBackgroundResource(bgResId);
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(MyGroupViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        // NOTE: Handles all click events manually
        return false;
    }

    void onClickItemView(View v) {
        RecyclerView.ViewHolder vh = RecyclerViewAdapterUtils.getViewHolder(v);
        int flatPosition = vh.getAdapterPosition();

        if (flatPosition == RecyclerView.NO_POSITION) {
            return;
        }

        long expandablePosition = mExpandableItemManager.getExpandablePosition(flatPosition);
        int groupPosition = RecyclerViewExpandableItemManager.getPackedPositionGroup(expandablePosition);
        int childPosition = RecyclerViewExpandableItemManager.getPackedPositionChild(expandablePosition);

        switch (v.getId()) {
            // common events
            case R.id.container:
                if (childPosition == RecyclerView.NO_POSITION) {
                    handleOnClickGroupItemContainerView(groupPosition);
                } else {
                    handleOnClickChildItemContainerView(groupPosition, childPosition);
                }
                break;
            // group item events
            case R.id.button_add_child_top:
                handleOnClickGroupItemAddChildTopButton(groupPosition);
                break;
            case R.id.button_add_child_bottom:
                handleOnClickGroupItemAddChildBottomButton(groupPosition);
                break;
            case R.id.button_add_child_bottom_2:
                handleOnClickGroupItemAddChild2BottomButton(groupPosition);
                break;
            case R.id.button_remove_child_top:
                handleOnClickGroupItemRemoveChildTopButton(groupPosition);
                break;
            case R.id.button_remove_child_bottom:
                handleOnClickGroupItemRemoveChildBottomButton(groupPosition);
                break;
            case R.id.button_remove_child_bottom_2:
                handleOnClickGroupItemRemoveChild2BottomButton(groupPosition);
                break;
            case R.id.button_add_group_above:
                handleOnClickGroupItemAddAboveButton(groupPosition);
                break;
            case R.id.button_add_group_below:
                handleOnClickGroupItemAddBelowButton(groupPosition);
                break;
            case R.id.button_remove_group:
                handleOnClickGroupItemRemoveButton(groupPosition);
                break;
            case R.id.button_clear_children:
                handleOnClickGroupItemClearChildrenButton(groupPosition);
                break;
            // child item events
            case R.id.button_add_child_above:
                handleOnClickChildItemAddAboveButton(groupPosition, childPosition);
                break;
            case R.id.button_add_child_below:
                handleOnClickChildItemAddBelowButton(groupPosition, childPosition);
                break;
            case R.id.button_remove_child:
                handleOnClickChildItemRemoveButton(groupPosition, childPosition);
                break;
            default:
                throw new IllegalStateException("Unexpected click event");
        }
    }

    private void handleOnClickGroupItemAddChildTopButton(int groupPosition) {
        mProvider.addChildItem(groupPosition, 0);
        mExpandableItemManager.notifyChildItemInserted(groupPosition, 0);
    }

    private void handleOnClickGroupItemAddChildBottomButton(int groupPosition) {
        int childCount = mProvider.getChildCount(groupPosition);

        mProvider.addChildItem(groupPosition, childCount);
        mExpandableItemManager.notifyChildItemInserted(groupPosition, childCount);
    }

    private void handleOnClickGroupItemAddChild2BottomButton(int groupPosition) {
        int childCount = mProvider.getChildCount(groupPosition);

        mProvider.addChildItem(groupPosition, childCount);
        mProvider.addChildItem(groupPosition, childCount + 1);
        mExpandableItemManager.notifyChildItemRangeInserted(groupPosition, childCount, 2);
    }

    private void handleOnClickGroupItemRemoveChildTopButton(int groupPosition) {
        int count = mProvider.getChildCount(groupPosition);

        if (count > 0) {
            mProvider.removeChildItem(groupPosition, 0);
            mExpandableItemManager.notifyChildItemRemoved(groupPosition, 0);
        }
    }

    private void handleOnClickGroupItemRemoveChildBottomButton(int groupPosition) {
        int count = mProvider.getChildCount(groupPosition);

        if (count > 0) {
            mProvider.removeChildItem(groupPosition, (count - 1));

            mExpandableItemManager.notifyChildItemRemoved(groupPosition, (count - 1));
        }
    }

    private void handleOnClickGroupItemRemoveChild2BottomButton(int groupPosition) {
        int count = mProvider.getChildCount(groupPosition);
        int removeCount = Math.min(count, 2);

        if (removeCount > 0) {
            mProvider.removeChildItem(groupPosition, (count - removeCount));
            if (removeCount == 2) {
                mProvider.removeChildItem(groupPosition, (count - removeCount));
            }

            mExpandableItemManager.notifyChildItemRangeRemoved(groupPosition, count - removeCount, removeCount);
        }
    }


    private void handleOnClickGroupItemContainerView(int groupPosition) {
        // toggle expanded/collapsed
        if (isGroupExpanded(groupPosition)) {
            mExpandableItemManager.collapseGroup(groupPosition);
        } else {
            mExpandableItemManager.expandGroup(groupPosition);
        }
    }

    private void handleOnClickGroupItemAddAboveButton(int groupPosition) {
        mProvider.addGroupItem(groupPosition);
        mExpandableItemManager.notifyGroupItemInserted(groupPosition);
    }

    private void handleOnClickGroupItemAddBelowButton(int groupPosition) {
        mProvider.addGroupItem(groupPosition + 1);
        mExpandableItemManager.notifyGroupItemInserted(groupPosition + 1);
    }

    private void handleOnClickGroupItemRemoveButton(int groupPosition) {
        mProvider.removeGroupItem(groupPosition);
        mExpandableItemManager.notifyGroupItemRemoved(groupPosition);
    }

    private void handleOnClickGroupItemClearChildrenButton(int groupPosition) {
        int childCount = mProvider.getChildCount(groupPosition);
        mProvider.clearChildren(groupPosition);
        mExpandableItemManager.notifyChildItemRangeRemoved(groupPosition, 0, childCount);
    }

    private void handleOnClickChildItemContainerView(int groupPosition, int childPosition) {
    }

    private void handleOnClickChildItemAddAboveButton(int groupPosition, int childPosition) {
        mProvider.addChildItem(groupPosition, childPosition);
        mExpandableItemManager.notifyChildItemInserted(groupPosition, childPosition);
    }

    private void handleOnClickChildItemAddBelowButton(int groupPosition, int childPosition) {
        mProvider.addChildItem(groupPosition, childPosition + 1);
        mExpandableItemManager.notifyChildItemInserted(groupPosition, childPosition + 1);
    }

    private void handleOnClickChildItemRemoveButton(int groupPosition, int childPosition) {
        mProvider.removeChildItem(groupPosition, childPosition);
        mExpandableItemManager.notifyChildItemRemoved(groupPosition, childPosition);
    }

    // NOTE: This method is called from Fragment
    public void addGroupItemsBottom(int count) {
        int groupPosition = mProvider.getGroupCount();

        for (int i = 0; i < count; i++) {
            mProvider.addGroupItem(groupPosition + i);
        }

        mExpandableItemManager.notifyGroupItemRangeInserted(groupPosition, count);
    }

    // NOTE: This method is called from Fragment
    public void removeGroupItemsBottom(int count) {
        int groupCount = mProvider.getGroupCount();

        count = Math.min(count, groupCount);

        int groupPosition = groupCount - count;

        for (int i = 0; i < count; i++) {
            mProvider.removeGroupItem(groupPosition);
        }

        mExpandableItemManager.notifyGroupItemRangeRemoved(groupPosition, count);
    }

    // NOTE: This method is called from Fragment
    public void clearGroupItems() {
        int groupCount = mProvider.getGroupCount();

        mProvider.clear();

        mExpandableItemManager.notifyGroupItemRangeRemoved(0, groupCount);
    }

    //
    // Utilities
    //
    private static long getPackedPositionForGroup(int groupPosition) {
        return RecyclerViewExpandableItemManager.getPackedPositionForGroup(groupPosition);
    }

    private static long getPackedPositionForChild(int groupPosition, int childPosition) {
        return RecyclerViewExpandableItemManager.getPackedPositionForChild(groupPosition, childPosition);
    }

    private int getGroupItemFlatPosition(int groupPosition) {
        long packedPosition = getPackedPositionForGroup(groupPosition);
        return getFlatPosition(packedPosition);
    }

    private int getChildItemFlatPosition(int groupPosition, int childPosition) {
        long packedPosition = getPackedPositionForChild(groupPosition, childPosition);
        return getFlatPosition(packedPosition);
    }

    private int getFlatPosition(long packedPosition) {
        return mExpandableItemManager.getFlatPosition(packedPosition);
    }

    private boolean isGroupExpanded(int groupPosition) {
        return mExpandableItemManager.isGroupExpanded(groupPosition);
    }
}

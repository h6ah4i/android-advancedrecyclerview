/*
 *    Copyright (C) 2016 Haruki Hasegawa
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

package com.h6ah4i.android.example.advrecyclerview.demo_e_minimal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.h6ah4i.android.example.advrecyclerview.R;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;

import java.util.ArrayList;
import java.util.List;

/*
 * This example shows very very minimal implementation of expandable feature.
 * Please refer to other examples for more advanced usages. Thanks!
 */
public class MinimalExpandableExampleActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demo_minimal);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // Setup expandable feature and RecyclerView
        RecyclerViewExpandableItemManager expMgr = new RecyclerViewExpandableItemManager(null);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(expMgr.createWrappedAdapter(new MyAdapter(recyclerView, expMgr)));

        expMgr.attachRecyclerView(recyclerView);
    }

    static abstract class MyBaseItem {
        public final long id;
        public final String text;

        public MyBaseItem(long id, String text) {
            this.id = id;
            this.text = text;
        }
    }

    static class MyGroupItem extends MyBaseItem {
        public final List<MyChildItem> children;

        public MyGroupItem(long id, String text) {
            super(id, text);
            children = new ArrayList<>();
        }
    }

    static class MyChildItem extends MyBaseItem {
        public MyChildItem(long id, String text) {
            super(id, text);
        }
        public boolean checked;
    }

    static abstract class MyBaseViewHolder extends AbstractExpandableItemViewHolder {
        TextView textView;

        public MyBaseViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(android.R.id.text1);
        }
    }

    static class MyGroupViewHolder extends MyBaseViewHolder {
        public MyGroupViewHolder(View itemView) {
            super(itemView);
        }
    }

    static class MyChildViewHolder extends MyBaseViewHolder {
        CheckBox checkBox;
        public MyChildViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(android.R.id.checkbox);
        }
    }

    static class MyAdapter extends AbstractExpandableItemAdapter<MyGroupViewHolder, MyChildViewHolder> implements CompoundButton.OnCheckedChangeListener {

        private RecyclerView mRecyclerView;
        private RecyclerViewExpandableItemManager mExpandableItemManager;

        List<MyGroupItem> mItems;

        public MyAdapter(RecyclerView rv, RecyclerViewExpandableItemManager expandableItemManager) {
            setHasStableIds(true); // this is required for expandable feature.

            mRecyclerView = rv;
            mExpandableItemManager = expandableItemManager;

            mItems = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                MyGroupItem group = new MyGroupItem(i, "GROUP " + i);
                for (int j = 0; j < 5; j++) {
                    group.children.add(new MyChildItem(j, "child " + j));
                }
                mItems.add(group);
            }
        }

        @Override
        public int getGroupCount() {
            return mItems.size();
        }

        @Override
        public int getChildCount(int groupPosition) {
            return mItems.get(groupPosition).children.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            // This method need to return unique value within all group items.
            return mItems.get(groupPosition).id;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            // This method need to return unique value within the group.
            return mItems.get(groupPosition).children.get(childPosition).id;
        }

        @Override
        public MyGroupViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_group_item_for_expandable_minimal, parent, false);
            return new MyGroupViewHolder(v);
        }

        @Override
        public MyChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_child_item_for_expandable_minimal, parent, false);
            return new MyChildViewHolder(v);
        }

        @Override
        public void onBindGroupViewHolder(MyGroupViewHolder holder, int groupPosition, int viewType) {
            MyGroupItem group = mItems.get(groupPosition);
            holder.textView.setText(group.text);
        }

        @Override
        public void onBindChildViewHolder(MyChildViewHolder holder, int groupPosition, int childPosition, int viewType) {
            MyChildItem child = mItems.get(groupPosition).children.get(childPosition);
            holder.textView.setText(child.text);
            holder.checkBox.setOnCheckedChangeListener(null);
            holder.checkBox.setChecked(child.checked);
            holder.checkBox.setOnCheckedChangeListener(this);
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            super.onViewRecycled(holder);

            if (holder instanceof MyChildViewHolder) {
                ((MyChildViewHolder) holder).checkBox.setOnCheckedChangeListener(null);
            }
        }

        @Override
        public boolean onCheckCanExpandOrCollapseGroup(MyGroupViewHolder holder, int groupPosition, int x, int y, boolean expand) {
            return true;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            MyChildViewHolder vh = (MyChildViewHolder) mRecyclerView.findContainingViewHolder(buttonView);
            int flatPosition = vh.getLayoutPosition();
            long packedPosition = mExpandableItemManager.getExpandablePosition(flatPosition);
            int groupPosition = RecyclerViewExpandableItemManager.getPackedPositionGroup(packedPosition);
            int childPosition = RecyclerViewExpandableItemManager.getPackedPositionChild(packedPosition);

            List<MyChildItem> children = mItems.get(groupPosition).children;
            MyChildItem child = children.get(childPosition);
            child.checked = isChecked;

            if (isChecked) {
                int from = childPosition;
                int to = children.size() - 1;

                children.remove(from);
                children.add(to, child);

                // [note] These three methods are only available on the develop branch (commit: 1b2ec40920b444e1903e14b5467923aa83dfd7cf).
                // - notifyGroupItemMoved(from, to)
                // - notifyChildItemMoved(groupPosition, fromChildPosition, toChildPosition)
                // - notifyChildItemMoved(fromGroupPosition, fromChildPosition, toGroupPosition, toChildPosition)

                mExpandableItemManager.notifyChildItemMoved(groupPosition, from, to);
            }
        }

    }
}

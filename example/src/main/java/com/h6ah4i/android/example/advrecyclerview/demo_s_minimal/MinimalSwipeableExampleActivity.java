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

package com.h6ah4i.android.example.advrecyclerview.demo_s_minimal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.h6ah4i.android.example.advrecyclerview.R;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionRemoveItem;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.annotation.SwipeableItemDrawableTypes;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.annotation.SwipeableItemResults;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;

import java.util.ArrayList;
import java.util.List;

/*
 * This example shows very very minimal implementation of swiping feature.
 * Please refer to other examples for more advanced usages. Thanks!
 */
public class MinimalSwipeableExampleActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demo_minimal);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        // Setup swiping feature and RecyclerView
        RecyclerViewSwipeManager swipeMgr = new RecyclerViewSwipeManager();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(swipeMgr.createWrappedAdapter(new MyAdapter()));

        swipeMgr.attachRecyclerView(recyclerView);
    }

    static class MyItem {
        public final long id;
        public final String text;

        public MyItem(long id, String text) {
            this.id = id;
            this.text = text;
        }
    }

    static class MyViewHolder extends AbstractSwipeableItemViewHolder {
        FrameLayout containerView;
        TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            containerView = itemView.findViewById(R.id.container);
            textView = itemView.findViewById(android.R.id.text1);
        }

        @Override
        public View getSwipeableContainerView() {
            return containerView;
        }
    }

    static class MyAdapter extends RecyclerView.Adapter<MyViewHolder> implements SwipeableItemAdapter<MyViewHolder> {
        interface Swipeable extends SwipeableItemConstants {
        }

        List<MyItem> mItems;

        public MyAdapter() {
            setHasStableIds(true); // this is required for swiping feature.

            mItems = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                mItems.add(new MyItem(i, "Item " + i));
            }
        }

        @Override
        public long getItemId(int position) {
            return mItems.get(position).id; // need to return stable (= not change even after position changed) value
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_for_swipe_minimal, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            MyItem item = mItems.get(position);
            holder.textView.setText(item.text);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        @Override
        public void onSwipeItemStarted(MyViewHolder holder, int position) {
            notifyDataSetChanged();
        }

        @Override
        public SwipeResultAction onSwipeItem(MyViewHolder holder, int position, @SwipeableItemResults int result) {
            if (result == Swipeable.RESULT_CANCELED) {
                return new SwipeResultActionDefault();
            } else {
                return new MySwipeResultActionRemoveItem(this, position);
            }
        }

        @Override
        public int onGetSwipeReactionType(MyViewHolder holder, int position, int x, int y) {
            return Swipeable.REACTION_CAN_SWIPE_BOTH_H;
        }

        @Override
        public void onSetSwipeBackground(MyViewHolder holder, int position, @SwipeableItemDrawableTypes int type) {
        }

        static class MySwipeResultActionRemoveItem extends SwipeResultActionRemoveItem {
            private MyAdapter adapter;
            private int position;

            public MySwipeResultActionRemoveItem(MyAdapter adapter, int position) {
                this.adapter = adapter;
                this.position = position;
            }

            @Override
            protected void onPerformAction() {
                adapter.mItems.remove(position);
                adapter.notifyItemRemoved(position);
            }
        }
    }
}

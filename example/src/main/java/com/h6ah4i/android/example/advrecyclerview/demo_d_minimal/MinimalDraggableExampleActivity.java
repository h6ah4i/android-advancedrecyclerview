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

package com.h6ah4i.android.example.advrecyclerview.demo_d_minimal;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.h6ah4i.android.example.advrecyclerview.R;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * This example shows very very minimal implementation of draggable feature.
 * Please refer to other examples for more advanced usages. Thanks!
 */
public class MinimalDraggableExampleActivity extends AppCompatActivity {
Handler handler=new Handler();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.main);}
//        setContentView(R.layout.activity_demo_minimal);
//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
//
//        // Setup D&D feature and RecyclerView
//        RecyclerViewDragDropManager dragMgr = new RecyclerViewDragDropManager();
//
//        dragMgr.setInitiateOnMove(false);
//        dragMgr.setInitiateOnLongPress(true);
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        adapter=new MyAdapter(new ArrayList<MyItem>());
//        recyclerView.setAdapter(dragMgr.createWrappedAdapter(adapter));
//        handler=new Handler(){
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                adapter.updata((List<MyItem>) msg.obj);
//            }
//        };
//        dragMgr.attachRecyclerView(recyclerView);
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Message msg = handler.obtainMessage();
//                msg.obj=getData();
//                handler.sendMessageDelayed(msg,1000);
//            }
//        },1000);
//        Snackbar.make(findViewById(R.id.container), "TIP: Long press item to initiate Drag & Drop action!", Snackbar.LENGTH_LONG).show();
//    }
//private List<MyItem> getData(){
//    List<MyItem> mItems;
//    mItems = new ArrayList<>();
//    for (int i = 0; i < 50; i++) {
//        mItems.add(new MyItem(i, "Item " + i));
//    }
//    return mItems;
//}
//    static class MyItem {
//        public final long id;
//        public final String text;
//
//        public MyItem(long id, String text) {
//            this.id = id;
//            this.text = text;
//        }
//    }
//
//    static class MyViewHolder extends AbstractDraggableItemViewHolder {
//
//        TextView textView;
//        CheckBox checkBox;
//        public MyViewHolder(View itemView) {
//            super(itemView);
//            textView = (TextView) itemView.findViewById(android.R.id.text1);
//            checkBox= (CheckBox) itemView.findViewById(R.id.cb);
//        }
//    }
//
//    static class MyAdapter extends RecyclerView.Adapter<MyViewHolder> implements DraggableItemAdapter<MyViewHolder> {
//        List<MyItem> mItems;
//        private Map<Integer, Boolean> selectedFilterListIndex;
//        public MyAdapter(List<MyItem> itemstems) {
//            setHasStableIds(true); // this is required for D&D feature.
//             this.mItems=itemstems;
//            selectedFilterListIndex = new HashMap<>();
//
//        }
//        public void updata(List<MyItem> itemstems){
//            mItems.addAll(itemstems);
//            notifyDataSetChanged();
//        }
//        @Override
//        public long getItemId(int position) {
//            return mItems.get(position).id; // need to return stable (= not change even after reordered) value
//        }
//
//        @Override
//        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_for_drag_minimal, parent, false);
//            return new MyViewHolder(v);
//        }
//
//        @Override
//        public void onBindViewHolder(MyViewHolder holder, final int position) {
//            MyItem item = mItems.get(position);
//            holder.textView.setText(item.text);
//            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    Log.e("QX", "isChecked:" + isChecked);
//                    if (isChecked) {
//                        selectedFilterListIndex.put(position, isChecked);
//                    } else {
//                        selectedFilterListIndex.remove(position);
//                    }
//                    for (Integer i : selectedFilterListIndex.keySet()) {
//                        Log.e("QX", "CheckedIndex:" + i);
//                    }
//                }
//            });
//            holder.checkBox.setChecked(selectedFilterListIndex.get(position) == null ? false : true);
//        }
//
//        @Override
//        public int getItemCount() {
//            return mItems.size();
//        }
//
//        @Override
//        public void onMoveItem(int fromPosition, int toPosition) {
//            MyItem movedItem = mItems.remove(fromPosition);
//            mItems.add(toPosition, movedItem);
//            notifyItemMoved(fromPosition, toPosition);
//        }
//
//        @Override
//        public boolean onCheckCanStartDrag(MyViewHolder holder, int position, int x, int y) {
//            return true;
//        }
//
//        @Override
//        public ItemDraggableRange onGetItemDraggableRange(MyViewHolder holder, int position) {
//            return null;
//        }
//
//        @Override
//        public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
//            return true;
//        }
//    }
}

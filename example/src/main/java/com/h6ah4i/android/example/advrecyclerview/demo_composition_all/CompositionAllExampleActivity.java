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

package com.h6ah4i.android.example.advrecyclerview.demo_composition_all;

import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;

import com.h6ah4i.android.example.advrecyclerview.R;
import com.h6ah4i.android.example.advrecyclerview.common.adapter.DemoHeaderFooterAdapter;
import com.h6ah4i.android.example.advrecyclerview.common.adapter.OnListItemClickMessageListener;
import com.h6ah4i.android.widget.advrecyclerview.composedadapter.ComposedAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager;

public class CompositionAllExampleActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demo_minimal);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        OnListItemClickMessageListener clickListener = new OnListItemClickMessageListener() {
            @Override
            public void onItemClicked(String message) {
                View container = findViewById(R.id.container);
                Snackbar.make(container, message, Snackbar.LENGTH_SHORT).show();
            }
        };

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        RecyclerView.Adapter adapter;

        adapter = createComposedAdapter(recyclerView, clickListener);
        adapter = new DemoHeaderFooterAdapter(adapter, clickListener);

        recyclerView.setAdapter(adapter);
    }

    private ComposedAdapter createComposedAdapter(RecyclerView rv, OnListItemClickMessageListener clickListener) {
        RecyclerViewDragDropManager dragMgr = new RecyclerViewDragDropManager();
        RecyclerViewDragDropManager dragMgr2 = new RecyclerViewDragDropManager();
        RecyclerViewSwipeManager swipeMgr = new RecyclerViewSwipeManager();
        RecyclerViewSwipeManager swipeMgr2 = new RecyclerViewSwipeManager();
        RecyclerViewExpandableItemManager expMgr = new RecyclerViewExpandableItemManager(null);
        RecyclerViewExpandableItemManager expMgr2 = new RecyclerViewExpandableItemManager(null);

        dragMgr.setDraggingItemShadowDrawable((NinePatchDrawable) ContextCompat.getDrawable(this, R.drawable.material_shadow_z3));
        dragMgr2.setDraggingItemShadowDrawable((NinePatchDrawable) ContextCompat.getDrawable(this, R.drawable.material_shadow_z3));

        ComposedAdapter composedAdapter = new ComposedAdapter();

        composedAdapter.addAdapter(new MySectionHeaderAdapter("Draggable - 1"));
        composedAdapter.addAdapter(dragMgr.createWrappedAdapter(new MyDraggableAdapter(clickListener)));
        composedAdapter.addAdapter(new MySectionHeaderAdapter("Draggable - 2"));
        composedAdapter.addAdapter(dragMgr2.createWrappedAdapter(new MyDraggableAdapter(clickListener)));

        composedAdapter.addAdapter(new MySectionHeaderAdapter("Swipeable - 1"));
        composedAdapter.addAdapter(swipeMgr.createWrappedAdapter(new MySwipeableAdapter(clickListener)));
        composedAdapter.addAdapter(new MySectionHeaderAdapter("Swipeable - 2"));
        composedAdapter.addAdapter(swipeMgr2.createWrappedAdapter(new MySwipeableAdapter(clickListener)));

        composedAdapter.addAdapter(new MySectionHeaderAdapter("Expandable - 1"));
        composedAdapter.addAdapter(expMgr.createWrappedAdapter(new MyExpandableAdapter(expMgr, clickListener)));
        composedAdapter.addAdapter(new MySectionHeaderAdapter("Expandable - 2"));
        composedAdapter.addAdapter(expMgr2.createWrappedAdapter(new MyExpandableAdapter(expMgr2, clickListener)));

        dragMgr.attachRecyclerView(rv);
        dragMgr2.attachRecyclerView(rv);
        swipeMgr.attachRecyclerView(rv);
        swipeMgr2.attachRecyclerView(rv);
        expMgr.attachRecyclerView(rv);
        expMgr2.attachRecyclerView(rv);

        return composedAdapter;
    }
}

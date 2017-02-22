package com.kiven.demo;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.expandable.RecyclerViewExpandableItemManager;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentForSection extends Fragment implements RecyclerViewExpandableItemManager.OnGroupCollapseListener, RecyclerViewExpandableItemManager.OnGroupExpandListener {
    MyAdapter myAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView = new RecyclerView(getContext());

        RecyclerViewExpandableItemManager manager = new RecyclerViewExpandableItemManager(null);
        manager.setOnGroupCollapseListener(this);
        manager.setOnGroupExpandListener(this);

        myAdapter = new MyAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(manager.createWrappedAdapter(myAdapter));
        /*manager.expandAll();*/

        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        manager.attachRecyclerView(recyclerView);

        return recyclerView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myAdapter.gruopCount = 3;
        myAdapter.childCount = 2;
        myAdapter.notifyDataSetChanged();

        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                myAdapter.gruopCount = 4;
                myAdapter.childCount = 2;
                myAdapter.notifyDataSetChanged();
            }
        };

        handler.sendEmptyMessageDelayed(0, 7000);
    }
    @Override
    public void onGroupCollapse(int groupPosition, boolean fromUser) {}
    @Override
    public void onGroupExpand(int groupPosition, boolean fromUser) {}

    static class MyViewHolder extends AbstractExpandableItemViewHolder {
        TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(android.R.id.text1);
        }
    }

    /*static class MyItem {
        public final long id;
        public final String text;

        public MyItem(long id, String text) {
            this.id = id;
            this.text = text;
        }
    }*/

    class MyAdapter extends AbstractExpandableItemAdapter<MyViewHolder, MyViewHolder> {
        /*List<MyItem> mGroupItems;
        List<MyItem> mChildItems;*/

        int gruopCount = 0;
        int childCount = 0;

        public MyAdapter() {
            setHasStableIds(true);
            /*int size = 10;
            mGroupItems = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                mGroupItems.add(new MyItem(i, "group " + i));
            }

            size = 4;
            mChildItems = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                mChildItems.add(new MyItem(i, "child " + i));
            }*/
        }



        @Override
        public int getGroupCount() {
            return /*mGroupItems.size()*/gruopCount;
        }

        @Override
        public int getChildCount(int groupPosition) {
            return /*mChildItems.size()*/childCount;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return /*mGroupItems.get(groupPosition).id*/groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return /*mChildItems.get(childPosition).id*/childPosition;
        }

        @Override
        public MyViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_for_drag_minimal, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public MyViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_for_drag_minimal, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindGroupViewHolder(MyViewHolder holder, int groupPosition, int viewType) {
            /*MyItem item = mGroupItems.get(groupPosition);
            holder.textView.setText(item.text);*/
            holder.textView.setText("group " + groupPosition);
        }

        @Override
        public void onBindChildViewHolder(MyViewHolder holder, int groupPosition, int childPosition, int viewType) {
            /*MyItem item = mChildItems.get(childPosition);
            holder.textView.setText(item.text);*/
            holder.textView.setText("child " + childPosition);
        }
        @Override
        public boolean onCheckCanExpandOrCollapseGroup(MyViewHolder holder, int groupPosition, int x, int y, boolean expand) {
            return true;
        }

        /*@Override
        public boolean onHookGroupExpand(int groupPosition, boolean fromUser) {
            return super.onHookGroupExpand(groupPosition, fromUser);
        }

        @Override
        public boolean onHookGroupCollapse(int groupPosition, boolean fromUser) {
            return true;
        }*/
    }
}

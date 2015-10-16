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

package com.h6ah4i.android.example.advrecyclerview.launcher;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.h6ah4i.android.example.advrecyclerview.R;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;

import java.util.ArrayList;
import java.util.List;

public class LauncherButtonsAdapter
        extends RecyclerView.Adapter<LauncherButtonsAdapter.ViewHolder>
        implements View.OnClickListener {

    private static class LauncherItem {
        private final Class<? extends Activity> mActivityClass;
        @StringRes
        private final int mTextRes;

        public LauncherItem(Class<? extends Activity> activityClass, @StringRes int textRes) {
            mActivityClass = activityClass;
            mTextRes = textRes;
        }
    }

    private Fragment mFragment;
    private List<LauncherItem> mItems;

    public LauncherButtonsAdapter(Fragment fragment) {
        mFragment = fragment;
        mItems = new ArrayList<>();
    }

    public void put(Class<? extends Activity> activityClass, @StringRes int textRes) {
        mItems.add(new LauncherItem(activityClass, textRes));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_launcher_button, parent, false);
        ViewHolder holder = new ViewHolder(v);
        holder.mButton.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LauncherItem item = mItems.get(position);
        holder.mButton.setText(item.mTextRes);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onClick(View v) {
        ViewHolder viewHolder = (ViewHolder) RecyclerViewAdapterUtils.getViewHolder(v);
        int position = viewHolder.getAdapterPosition();

        Intent intent = new Intent(v.getContext(), mItems.get(position).mActivityClass);
        mFragment.startActivity(intent);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        Button mButton;

        public ViewHolder(View itemView) {
            super(itemView);
            mButton = (Button) itemView;
        }
    }
}

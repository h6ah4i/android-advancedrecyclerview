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

import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;

public class LegacyExpandableSwipeResultAction<GVH extends RecyclerView.ViewHolder, CVH extends RecyclerView.ViewHolder>
        extends SwipeResultAction {

    LegacyExpandableSwipeableItemAdapter<GVH, CVH> mAdapter;
    RecyclerView.ViewHolder mHolder;
    int mGroupPosition;
    int mChildPosition;
    int mResult;
    int mReaction;

    public LegacyExpandableSwipeResultAction(
            LegacyExpandableSwipeableItemAdapter<GVH, CVH> adapter,
            RecyclerView.ViewHolder holder, int groupPosition, int childPosition, int result, int reaction) {
        super(reaction);
        mAdapter = adapter;
        mHolder = holder;
        mGroupPosition = groupPosition;
        mChildPosition = childPosition;
        mResult = result;
        mReaction = reaction;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onPerformAction() {
        if (mChildPosition == RecyclerView.NO_POSITION) {
            mAdapter.onPerformAfterSwipeGroupReaction(
                    (GVH) mHolder, mGroupPosition, mResult, mReaction);
        } else {
            mAdapter.onPerformAfterSwipeChildReaction(
                    (CVH) mHolder, mGroupPosition, mChildPosition, mResult, mReaction);
        }
    }

    @Override
    protected void onCleanUp() {
        super.onCleanUp();

        mAdapter = null;
        mHolder = null;
    }
}

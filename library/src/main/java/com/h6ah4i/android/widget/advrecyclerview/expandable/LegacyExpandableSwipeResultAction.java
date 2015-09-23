package com.h6ah4i.android.widget.advrecyclerview.expandable;

import android.support.v7.widget.RecyclerView;

import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;

/**
 * Created by hasegawa on 9/16/15.
 */
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

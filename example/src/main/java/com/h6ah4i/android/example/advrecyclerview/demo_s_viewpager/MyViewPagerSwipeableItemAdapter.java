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

package com.h6ah4i.android.example.advrecyclerview.demo_s_viewpager;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.h6ah4i.android.example.advrecyclerview.R;
import com.h6ah4i.android.example.advrecyclerview.common.data.AbstractDataProvider;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionRemoveItem;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;

class MyViewPagerSwipeableItemAdapter
        extends RecyclerView.Adapter<MyViewPagerSwipeableItemAdapter.MyViewHolder>
        implements SwipeableItemAdapter<MyViewPagerSwipeableItemAdapter.MyViewHolder> {
    private static final String TAG = "MySwipeableItemAdapter";

    // NOTE: Make accessible with short name
    private interface Swipeable extends SwipeableItemConstants {
    }

    private AbstractDataProvider mProvider;
    private boolean mCanSwipeLeft;

    public static class MyViewHolder extends AbstractSwipeableItemViewHolder {
        public PagerSwipeItemFrameLayout mContainer;
        public TextView mTextView;

        public MyViewHolder(View v) {
            super(v);
            mContainer = (PagerSwipeItemFrameLayout) v.findViewById(R.id.container);
            mTextView = (TextView) v.findViewById(android.R.id.text1);
        }

        @Override
        public View getSwipeableContainerView() {
            return mContainer;
        }

        @Override
        public void onSlideAmountUpdated(float horizontalAmount, float verticalAmount, boolean isSwiping) {
            float alpha = 1.0f - Math.min(Math.max(Math.abs(horizontalAmount), 0.0f), 1.0f);
            ViewCompat.setAlpha(mContainer, alpha);
        }
    }

    public MyViewPagerSwipeableItemAdapter(AbstractDataProvider dataProvider, boolean canSwipeLeft) {
        mProvider = dataProvider;
        mCanSwipeLeft = canSwipeLeft;

        // SwipeableItemAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return mProvider.getItem(position).getId();
    }

    @Override
    public int getItemViewType(int position) {
        return mProvider.getItem(position).getViewType();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.list_item_view_pager, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final AbstractDataProvider.Data item = mProvider.getItem(position);

        holder.mContainer.setCanSwipeLeft(mCanSwipeLeft);
        holder.mContainer.setCanSwipeRight(!mCanSwipeLeft);

        holder.mTextView.setText(item.getText());
    }

    @Override
    public void onViewRecycled(MyViewHolder holder) {
        super.onViewRecycled(holder);
        ViewCompat.setAlpha(holder.mContainer, 1.0f);
    }

    @Override
    public int getItemCount() {
        return mProvider.getCount();
    }

    @Override
    public int onGetSwipeReactionType(MyViewHolder holder, int position, int x, int y) {
        // NOTE: Need to specify REACTION_FLAG_MASK_START_xxx flags to make ViewPager can handle touch event.
        if (mCanSwipeLeft) {
            return Swipeable.REACTION_CAN_SWIPE_LEFT |
                    Swipeable.REACTION_CAN_NOT_SWIPE_RIGHT_WITH_RUBBER_BAND_EFFECT |
                    Swipeable.REACTION_FLAG_MASK_START_SWIPE_RIGHT;
        } else {
            return Swipeable.REACTION_CAN_SWIPE_RIGHT |
                    Swipeable.REACTION_CAN_NOT_SWIPE_LEFT_WITH_RUBBER_BAND_EFFECT |
                    Swipeable.REACTION_FLAG_MASK_START_SWIPE_LEFT;
        }
    }

    @Override
    public void onSetSwipeBackground(MyViewHolder holder, int position, int type) {
    }

    @Override
    public SwipeResultAction onSwipeItem(MyViewHolder holder, final int position, int result) {
        Log.d(TAG, "onSwipeItem(position = " + position + ", result = " + result + ")");

        switch (result) {
            case Swipeable.RESULT_SWIPED_RIGHT:
            case Swipeable.RESULT_SWIPED_LEFT:
            case Swipeable.RESULT_CANCELED:
            default:
        }

        if ((mCanSwipeLeft && result == Swipeable.RESULT_SWIPED_LEFT) ||
                (!mCanSwipeLeft && result == Swipeable.RESULT_SWIPED_RIGHT)) {
            return new DismissResultAction(this, position);
        } else {
            return new DefaultResultAction(this, position);
        }
    }

    private static class DismissResultAction extends SwipeResultActionRemoveItem {
        private MyViewPagerSwipeableItemAdapter mAdapter;
        private final int mPosition;

        DismissResultAction(MyViewPagerSwipeableItemAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            mAdapter.mProvider.removeItem(mPosition);
            mAdapter.notifyItemRemoved(mPosition);
        }

        @Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

    private static class DefaultResultAction extends SwipeResultActionDefault {
        private MyViewPagerSwipeableItemAdapter mAdapter;
        private final int mPosition;

        DefaultResultAction(MyViewPagerSwipeableItemAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            AbstractDataProvider.Data item = mAdapter.mProvider.getItem(mPosition);
            if (item.isPinned()) {
                item.setPinned(false);
                mAdapter.notifyItemChanged(mPosition);
            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }
}

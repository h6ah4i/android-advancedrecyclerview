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

package com.h6ah4i.android.example.advrecyclerview.demo_s;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.h6ah4i.android.example.advrecyclerview.R;
import com.h6ah4i.android.example.advrecyclerview.common.data.AbstractDataProvider;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.MultiSwipeableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDefault;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection;
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionRemoveItem;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder;
import com.h6ah4i.android.widget.advrecyclerview.utils.RecyclerViewAdapterUtils;

class MySwipeableItemAdapter
        extends RecyclerView.Adapter<MySwipeableItemAdapter.MyViewHolder>
        implements SwipeableItemAdapter<MySwipeableItemAdapter.MyViewHolder> {
    private static final String TAG = "MySwipeableItemAdapter";

    // NOTE: Make accessible with short name
    private interface Swipeable extends SwipeableItemConstants {
    }

    private AbstractDataProvider mProvider;
    private EventListener mEventListener;
    private View.OnClickListener mItemViewOnClickListener;
    private View.OnClickListener mSwipeableViewContainerOnClickListener;

    public interface EventListener {
        void onItemRemoved(int position);

        void onItemPinned(int position);

        void onItemViewClicked(View v, boolean pinned);
    }

    public static class MyViewHolder extends AbstractSwipeableItemViewHolder implements MultiSwipeableItemViewHolder {
        public FrameLayout mContainerOuter;
        public FrameLayout mContainerInner;
        public TextView mTextView;
        public int mSwipeLevel;

        public MyViewHolder(View v) {
            super(v);
            mContainerOuter = (FrameLayout) v.findViewById(R.id.container_outer);
            mContainerInner = (FrameLayout) v.findViewById(R.id.container_inner);
            mTextView = (TextView) v.findViewById(android.R.id.text1);
        }

        @Override
        public View getSwipeableContainerView() {
            return getSwipeableContainerView(mSwipeLevel);
        }

        @Override
        public View getSwipeableContainerView(int level) {
            if (level == 0) {
                return mContainerInner;
            } else {
                return mContainerOuter;
            }
        }

        @Override
        public int getCurrentSwipeLevel() {
            return mSwipeLevel;
        }

        @Override
        public int getMaxSwipeLevel() {
            return 2;
        }
    }

    public MySwipeableItemAdapter(AbstractDataProvider dataProvider) {
        mProvider = dataProvider;
        mItemViewOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemViewClick(v);
            }
        };
        mSwipeableViewContainerOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSwipeableViewContainerClick(v);
            }
        };

        // SwipeableItemAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true);
    }

    private void onItemViewClick(View v) {
//        if (mEventListener != null) {
//            mEventListener.onItemViewClicked(v, true); // true --- pinned
//        }
    }

    private void onSwipeableViewContainerClick(View v) {
//        if (mEventListener != null) {
//            mEventListener.onItemViewClicked(RecyclerViewAdapterUtils.getParentViewHolderItemView(v), false);  // false --- not pinned
//        }
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
        final View v = inflater.inflate(R.layout.list_item_double_swipe, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final AbstractDataProvider.Data item = mProvider.getItem(position);

        // set listeners
        // (if the item is *pinned*, click event comes to the itemView)
        holder.itemView.setOnClickListener(mItemViewOnClickListener);
        // (if the item is *not pinned*, click event comes to the mContainer)
        holder.mContainerOuter.setOnClickListener(mSwipeableViewContainerOnClickListener);
        holder.mContainerInner.setOnClickListener(mSwipeableViewContainerOnClickListener);

        holder.mSwipeLevel = item.getSwipeLevel();

        // set text
        holder.mTextView.setText(item.getText());

        holder.mContainerInner.setBackgroundResource(R.drawable.bg_item_normal_state);

        // set swiping properties
        if (holder.mSwipeLevel == 1) {
            holder.setSwipeItemHorizontalSlideAmount(Swipeable.OUTSIDE_OF_THE_WINDOW_RIGHT);
        } else if (holder.mSwipeLevel == -1) {
            holder.setSwipeItemHorizontalSlideAmount(Swipeable.OUTSIDE_OF_THE_WINDOW_LEFT);
        }
    }

    @Override
    public int getItemCount() {
        return mProvider.getCount();
    }

    @Override
    public int onGetSwipeReactionType(MyViewHolder holder, int position, int x, int y) {
        return Swipeable.REACTION_CAN_SWIPE_BOTH_H;
    }

    @Override
    public void onSetSwipeBackground(MyViewHolder holder, int position, int type) {
        final int NEUTRAL = R.drawable.bg_swipe_item_neutral;
        final int SWIPE_ONCE = R.drawable.bg_swipe_item_left;
        final int SWIPE_TWICE = R.drawable.bg_swipe_item_right;

        if (holder.getCurrentSwipeLevel() == 0) {
            int bgRes = 0;
            switch (type) {
                case Swipeable.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND:
                    bgRes = NEUTRAL;
                    break;
                case Swipeable.DRAWABLE_SWIPE_LEFT_BACKGROUND:
                case Swipeable.DRAWABLE_SWIPE_RIGHT_BACKGROUND:
                    bgRes = SWIPE_ONCE;
                    break;
            }
            holder.mContainerOuter.setBackgroundResource(bgRes);
        } else {
            int bgRes = 0;
            switch (type) {
                case Swipeable.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND:
                    bgRes = SWIPE_ONCE;
                    break;
                case Swipeable.DRAWABLE_SWIPE_LEFT_BACKGROUND:
                case Swipeable.DRAWABLE_SWIPE_RIGHT_BACKGROUND:
                    bgRes = SWIPE_TWICE;
                    break;
            }
            holder.itemView.setBackgroundResource(bgRes);
        }
    }

    @Override
    public SwipeResultAction onSwipeItem(MyViewHolder holder, final int position, int result) {
        Log.d(TAG, "onSwipeItem(position = " + position + ", result = " + result + ")");

        if (holder.getCurrentSwipeLevel() == 0) {
            switch (result) {
                case Swipeable.RESULT_SWIPED_RIGHT:
                case Swipeable.RESULT_SWIPED_LEFT:
                    return new SwipeOnceResultAction(this, position, result);
                case Swipeable.RESULT_CANCELED:
                default:
                    if (position != RecyclerView.NO_POSITION) {
                        return new CancelSwipeResultAction(this, position);
                    } else {
                        return null;
                    }
            }
        } else {
            switch (result) {
                case Swipeable.RESULT_SWIPED_RIGHT:
                case Swipeable.RESULT_SWIPED_LEFT:
                    return new SwipeTwiceResultAction(this, position);
                case Swipeable.RESULT_CANCELED:
                default:
                    if (position != RecyclerView.NO_POSITION) {
                        return new CancelSwipeResultAction(this, position);
                    } else {
                        return null;
                    }
            }
        }
    }

    public EventListener getEventListener() {
        return mEventListener;
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }

    private static class SwipeOnceResultAction extends SwipeResultActionMoveToSwipedDirection {
        private MySwipeableItemAdapter mAdapter;
        private final int mPosition;
        private boolean mSetPinned;
        private int mDirection;

        SwipeOnceResultAction(MySwipeableItemAdapter adapter, int position, int result) {
            mAdapter = adapter;
            mPosition = position;
            mDirection = (result == Swipeable.RESULT_SWIPED_LEFT) ? -1 : 1;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            AbstractDataProvider.Data item = mAdapter.mProvider.getItem(mPosition);

            if (item.getSwipeLevel() == 0) {
                item.setSwipeLevel(mDirection);
                mAdapter.notifyItemChanged(mPosition);
                mSetPinned = true;
            }
        }

        @Override
        protected void onSlideAnimationEnd() {
            super.onSlideAnimationEnd();

//            if (mSetPinned && mAdapter.mEventListener != null) {
//                mAdapter.mEventListener.onItemPinned(mPosition);
//            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

    private static class SwipeTwiceResultAction extends SwipeResultActionRemoveItem {
        private MySwipeableItemAdapter mAdapter;
        private final int mPosition;

        SwipeTwiceResultAction(MySwipeableItemAdapter adapter, int position) {
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

//            if (mAdapter.mEventListener != null) {
//                mAdapter.mEventListener.onItemRemoved(mPosition);
//            }
        }

        @Override
        protected void onCleanUp() {
            super.onCleanUp();
            // clear the references
            mAdapter = null;
        }
    }

    private static class CancelSwipeResultAction extends SwipeResultActionDefault {
        private MySwipeableItemAdapter mAdapter;
        private final int mPosition;

        CancelSwipeResultAction(MySwipeableItemAdapter adapter, int position) {
            mAdapter = adapter;
            mPosition = position;
        }

        @Override
        protected void onPerformAction() {
            super.onPerformAction();

            AbstractDataProvider.Data item = mAdapter.mProvider.getItem(mPosition);
            if (item.getSwipeLevel() != 0) {
                item.setSwipeLevel(0);
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

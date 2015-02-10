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

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.h6ah4i.android.widget.advrecyclerview.utils.CustomRecyclerViewUtils;

public class RecyclerViewExpandableItemManager {
    private static final String TAG = "ARVExpandableItemMgr";

    public static final long NO_EXPANDABLE_POSITION = ExpandableAdapterHelper.NO_EXPANDABLE_POSITION;

    @SuppressWarnings("PointlessBitwiseExpression")
    public static final int STATE_FLAG_IS_GROUP = (1 << 0);
    public static final int STATE_FLAG_IS_CHILD = (1 << 1);
    public static final int STATE_FLAG_IS_EXPANDED = (1 << 2);
    public static final int STATE_FLAG_IS_UPDATED = (1 << 31);

    private SavedState mSavedState;

    private RecyclerView mRecyclerView;
    private ExpandableRecyclerViewWrapperAdapter mAdapter;
    private RecyclerView.OnItemTouchListener mInternalUseOnItemTouchListener;

    private long mTouchedItemId = RecyclerView.NO_ID;
    private int mTouchSlop;
    private int mInitialTouchX;
    private int mInitialTouchY;

    public RecyclerViewExpandableItemManager(Parcelable savedState) {
        mInternalUseOnItemTouchListener = new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return RecyclerViewExpandableItemManager.this.onInterceptTouchEvent(rv, e);
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }
        };

        if (savedState instanceof SavedState) {
            mSavedState = (SavedState) savedState;
        }
    }

    public boolean isReleased() {
        return (mInternalUseOnItemTouchListener == null);
    }

    public void attachRecyclerView(RecyclerView rv) {
        if (rv == null) {
            throw new IllegalArgumentException("RecyclerView cannot be null");
        }

        if (isReleased()) {
            throw new IllegalStateException("Accessing released object");
        }

        if (mRecyclerView != null) {
            throw new IllegalStateException("RecyclerView instance has already been set");
        }

        mRecyclerView = rv;
        mRecyclerView.addOnItemTouchListener(mInternalUseOnItemTouchListener);
        mTouchSlop = ViewConfiguration.get(mRecyclerView.getContext()).getScaledTouchSlop();
    }

    public void release() {
        if (mRecyclerView != null && mInternalUseOnItemTouchListener != null) {
            mRecyclerView.removeOnItemTouchListener(mInternalUseOnItemTouchListener);
        }
        mInternalUseOnItemTouchListener = null;
        mRecyclerView = null;
        mSavedState = null;
    }

    public ExpandableRecyclerViewWrapperAdapter createWrappedAdapter(RecyclerView.Adapter adapter) {
        if (mAdapter != null) {
            throw new IllegalStateException("already have a wrapped adapter");
        }

        int [] adapterSavedState = (mSavedState != null) ? mSavedState.adapterSavedState : null;
        mSavedState = null;

        mAdapter = new ExpandableRecyclerViewWrapperAdapter(this, adapter, adapterSavedState);

        return mAdapter;
    }

    public Parcelable getSavedState() {
        int [] adapterSavedState = null;

        if (mAdapter != null) {
            adapterSavedState = mAdapter.getExpandedItemsSavedStateArray();
        }

        return new SavedState(adapterSavedState);
    }

    /*package*/ boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        if (mAdapter == null) {
            return false;
        }

        final int action = MotionEventCompat.getActionMasked(e);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                handleActionDown(rv, e);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (handleActionUpOrCancel(rv, e)) {
                    return true;
                }
                break;
        }

        return false;
    }

    private void handleActionDown(RecyclerView rv, MotionEvent e) {
        final RecyclerView.ViewHolder holder = CustomRecyclerViewUtils.findChildViewHolderUnderWithTranslation(rv, e.getX(), e.getY());

        mInitialTouchX = (int) (e.getX() + 0.5f);
        mInitialTouchY = (int) (e.getY() + 0.5f);

        if (holder != null) {
            mTouchedItemId = holder.getItemId();
        } else {
            mTouchedItemId = RecyclerView.NO_ID;
        }
    }

    private boolean handleActionUpOrCancel(RecyclerView rv, MotionEvent e) {
        final long touchedItemId = mTouchedItemId;
        final int initialTouchX = mInitialTouchX;
        final int initialTouchY = mInitialTouchY;

        mTouchedItemId = RecyclerView.NO_ID;
        mInitialTouchX = 0;
        mInitialTouchY = 0;

        if (!((touchedItemId != RecyclerView.NO_ID) && (MotionEventCompat.getActionMasked(e) == MotionEvent.ACTION_UP))) {
            return false;
        }

        final int touchX = (int) (e.getX() + 0.5f);
        final int touchY = (int) (e.getY() + 0.5f);

        final int diffX = touchX - initialTouchX;
        final int diffY = touchY - initialTouchY;

        if (!((Math.abs(diffX) < mTouchSlop) && (Math.abs(diffY) < mTouchSlop))) {
            return false;
        }

        final RecyclerView.ViewHolder holder = CustomRecyclerViewUtils.findChildViewHolderUnderWithTranslation(rv, e.getX(), e.getY());

        if (!((holder != null) && (holder.getItemId() == touchedItemId))) {
            return false;
        }

        final View view = holder.itemView;
        final int translateX = (int) (ViewCompat.getTranslationX(view) + 0.5f);
        final int translateY = (int) (ViewCompat.getTranslationY(view) + 0.5f);
        final int viewX = touchX - (view.getLeft() + translateX);
        final int viewY = touchY - (view.getTop() + translateY);

        return mAdapter.onTapItem(holder, viewX, viewY);
    }

    public boolean expandGroup(int groupPosition) {
        return (mAdapter != null) && mAdapter.expandGroup(groupPosition);
    }

    public boolean collapseGroup(int groupPosition) {
        return (mAdapter != null) && mAdapter.collapseGroup(groupPosition);
    }

    public long getExpandablePosition(int flatPosition) {
        if (mAdapter == null) {
            return ExpandableAdapterHelper.NO_EXPANDABLE_POSITION;
        }
        return mAdapter.getExpandablePosition(flatPosition);
    }

    public int getFlatPosition(long packedPosition) {
        if (mAdapter == null) {
            return RecyclerView.NO_POSITION;
        }
        return mAdapter.getFlatPosition(packedPosition);
    }

    public static int getPackedPositionChild(long packedPosition) {
        return ExpandableAdapterHelper.getPackedPositionChild(packedPosition);
    }

    public static long getPackedPositionForChild(int groupPosition, int childPosition) {
        return ExpandableAdapterHelper.getPackedPositionForChild(groupPosition, childPosition);
    }

    public static long getPackedPositionForGroup(int groupPosition) {
        return ExpandableAdapterHelper.getPackedPositionForGroup(groupPosition);
    }

    public static int getPackedPositionGroup(long packedPosition) {
        return ExpandableAdapterHelper.getPackedPositionGroup(packedPosition);
    }

    public boolean isGroupExpanded(int groupPosition) {
        return (mAdapter != null) && mAdapter.isGroupExpanded(groupPosition);
    }

    public static long getCombinedChildId(long groupId, long childId) {
        return ExpandableAdapterHelper.getCombinedChildId(groupId, childId);
    }

    public static long getCombinedGroupId(long groupId) {
        return ExpandableAdapterHelper.getCombinedGroupId(groupId);
    }

    public static boolean isGroupViewType(int rawViewType) {
        return ExpandableAdapterHelper.isGroupViewType(rawViewType);
    }

    public static int getGroupViewType(int rawViewType) {
        return ExpandableAdapterHelper.getGroupViewType(rawViewType);
    }

    public static int getChildViewType(int rawViewType) {
        return ExpandableAdapterHelper.getChildViewType(rawViewType);
    }

    public static class SavedState implements Parcelable {
        final int [] adapterSavedState;

        public SavedState(int[] adapterSavedState) {
            this.adapterSavedState = adapterSavedState;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeIntArray(this.adapterSavedState);
        }

        private SavedState(Parcel in) {
            this.adapterSavedState = in.createIntArray();
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}

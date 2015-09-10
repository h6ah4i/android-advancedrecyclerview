/*
 *    Copyright (C) 2015 Gandulf Kohlweiss
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

package com.h6ah4i.android.widget.advrecyclerview.selectable;

import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.h6ah4i.android.widget.advrecyclerview.utils.CustomRecyclerViewUtils;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrapperAdapterUtils;

import java.util.List;

/**
 * Provides item selection operation for {@link android.support.v7.widget.RecyclerView}
 */
@SuppressWarnings("PointlessBitwiseExpression")
public class RecyclerViewSelectionManager {
    private static final String TAG = "ARVSelectionManager";

    private static final boolean LOCAL_LOGV = false;
    private static final boolean LOCAL_LOGD = false;

    private RecyclerView.OnItemTouchListener mInternalUseOnItemTouchListener;
    private RecyclerView mRecyclerView;

    private long mTouchedItemId = RecyclerView.NO_ID;
    private int mTouchSlop;
    private int mInitialTouchX;
    private int mInitialTouchY;

    private SelectableItemWrapperAdapter<RecyclerView.ViewHolder> mAdapter;

    /**
     * Constructor.
     */
    public RecyclerViewSelectionManager() {
        mInternalUseOnItemTouchListener = new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return RecyclerViewSelectionManager.this.onInterceptTouchEvent(rv, e);
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        };

    }

    /**
     * Create wrapped adapter.
     *
     * @param adapter The target adapter.
     *
     * @return Wrapped adapter which is associated to this {@link com.h6ah4i.android.widget.advrecyclerview.selectable.RecyclerViewSelectionManager} instance.
     */
    @SuppressWarnings("unchecked")
    public RecyclerView.Adapter createWrappedAdapter(RecyclerView.Adapter adapter) {
        if (mAdapter != null) {
            throw new IllegalStateException("already have a wrapped adapter");
        }

        mAdapter = new SelectableItemWrapperAdapter(this, adapter);

        return mAdapter;
    }

    /**
     * Indicates this manager instance has released or not.
     *
     * @return True if this manager instance has released
     */
    public boolean isReleased() {
        return (mInternalUseOnItemTouchListener == null);
    }

    /**
     * Attaches {@link android.support.v7.widget.RecyclerView} instance.
     *
     * Before calling this method, the target {@link android.support.v7.widget.RecyclerView} must set
     * the wrapped adapter instance which is returned by the
     * {@link #createWrappedAdapter(android.support.v7.widget.RecyclerView.Adapter)} method.
     *
     * @param rv The {@link android.support.v7.widget.RecyclerView} instance
     */
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

        if (mAdapter == null || getSelectableItemWrapperAdapter(rv) != mAdapter) {
            throw new IllegalStateException("adapter is not set properly");
        }

        mRecyclerView = rv;
        mRecyclerView.addOnItemTouchListener(mInternalUseOnItemTouchListener);

        final ViewConfiguration vc = ViewConfiguration.get(rv.getContext());

        mTouchSlop = vc.getScaledTouchSlop();
    }

    /**
     * Detach the {@link android.support.v7.widget.RecyclerView} instance and release internal field references.
     *
     * This method should be called in order to avoid memory leaks.
     */
    public void release() {
        if (mRecyclerView != null && mInternalUseOnItemTouchListener != null) {
            mRecyclerView.removeOnItemTouchListener(mInternalUseOnItemTouchListener);
        }
        mInternalUseOnItemTouchListener = null;

        mAdapter = null;
        mRecyclerView = null;
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

        mAdapter.toggleSelection(holder);

        return false;
    }

    private static SelectableItemWrapperAdapter getSelectableItemWrapperAdapter(RecyclerView rv) {
        return WrapperAdapterUtils.findWrappedAdapter(rv.getAdapter(), SelectableItemWrapperAdapter.class);
    }

    private static boolean supportsViewPropertyAnimator() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }


    public int getChoiceMode() {
        return getSelectableItemWrapperAdapter(mRecyclerView).getChoiceMode();
    }

    /**
     * <p>Toggle whether this MultiSelector is in selection mode or not.
     * {@link com.h6ah4i.android.widget.advrecyclerview.selectable.SelectableItemViewHolder#setSelectable(boolean)}
     * will be called on any attached holders as well.</p>
     *
     * @param choiceMode
     */
    public void setChoiceMode(int choiceMode) {
        getSelectableItemWrapperAdapter(mRecyclerView).setChoiceMode(choiceMode);
    }

    /**
     *
     * @param holder     Holder to set selection value for.
     * @param isSelected Whether the item should be selected.
     */
    public void setSelected(RecyclerView.ViewHolder holder, boolean isSelected) {
        getSelectableItemWrapperAdapter(mRecyclerView).setSelected(holder, isSelected);
    }

    public void setSelected(int position, boolean isSelected) {
        getSelectableItemWrapperAdapter(mRecyclerView).setSelected(position, isSelected);
    }

    public boolean isSelected(RecyclerView.ViewHolder holder) {
        return getSelectableItemWrapperAdapter(mRecyclerView).isSelected(holder);
    }
    public boolean isSelected(int position) {
        return getSelectableItemWrapperAdapter(mRecyclerView).isSelected(position);
    }

    public void toggleSelection(int position) {
        getSelectableItemWrapperAdapter(mRecyclerView).toggleSelection(position);
    }

    public void toggleSelection(RecyclerView.ViewHolder holder) {
        getSelectableItemWrapperAdapter(mRecyclerView).toggleSelection(holder);
    }

    /**
     * <p>Sets selected to false for all positions. Will refresh
     * all bound holders.</p>
     */
    public void clearSelections() {
        getSelectableItemWrapperAdapter(mRecyclerView).clearSelections();
    }

    /**
     * <p>Return a list of selected positions.</p>
     *
     * @return A list of the currently selected positions.
     */
    public List<Integer> getSelectedPositions() {
        return getSelectableItemWrapperAdapter(mRecyclerView).getSelectedPositions();
    }
    /**
     * <p>Return a list of selected positions.</p>
     *
     * @return A list of the currently selected positions.
     */
    public List<Object> getSelectedItems() {
        return getSelectableItemWrapperAdapter(mRecyclerView).getSelectedItems();
    }
}

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
package com.h6ah4i.android.widget.advrecyclerview.adapter;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.utils.WrappedAdapterUtils;

import java.util.Collections;
import java.util.List;

/**
 * A simple wrapper class. It just bypasses all methods and events to the wrapped adapter.
 * Use this class as a default implementation of {@link WrapperAdapter}, so extend it
 * and override each methods to build your own specialized adapter!
 */
public class SimpleWrapperAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> implements WrapperAdapter<VH>, BridgeAdapterDataObserver.Subscriber {

    private static final String TAG = "ARVSimpleWAdapter";
    private static final boolean LOCAL_LOGD = false;

    private RecyclerView.Adapter<VH> mWrappedAdapter;
    private BridgeAdapterDataObserver mBridgeObserver;

    protected static final List<Object> FULL_UPDATE_PAYLOADS = Collections.emptyList();

    /**
     * Constructor
     * @param adapter The adapter which to be wrapped
     */
    public SimpleWrapperAdapter(@NonNull RecyclerView.Adapter<VH> adapter) {
        mWrappedAdapter = adapter;
        mBridgeObserver = new BridgeAdapterDataObserver(this, mWrappedAdapter, null);
        mWrappedAdapter.registerAdapterDataObserver(mBridgeObserver);

        super.setHasStableIds(mWrappedAdapter.hasStableIds());
    }

    /**
     * Returns whether the wrapped adapter exists.
     * @return True if underlying adapter is present. Otherwise, false.
     */
    public boolean isWrappedAdapterAlive() {
        return mWrappedAdapter != null;
    }

    /**
     * Returns underlying adapter.
     * @return The underlying adapter instance
     */
    public RecyclerView.Adapter<VH> getWrappedAdapter() {
        return mWrappedAdapter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getWrappedAdapters(@NonNull List<RecyclerView.Adapter> adapters) {
        if (mWrappedAdapter != null) {
            adapters.add(mWrappedAdapter);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void release() {
        onRelease();

        if (mWrappedAdapter != null && mBridgeObserver != null) {
            mWrappedAdapter.unregisterAdapterDataObserver(mBridgeObserver);
        }

        mWrappedAdapter = null;
        mBridgeObserver = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        if (isWrappedAdapterAlive())
            mWrappedAdapter.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        if (isWrappedAdapterAlive())
            mWrappedAdapter.onDetachedFromRecyclerView(recyclerView);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewAttachedToWindow(VH holder) {
        onViewAttachedToWindow(holder, holder.getItemViewType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewAttachedToWindow(VH holder, int viewType) {
        if (isWrappedAdapterAlive()) {
            WrappedAdapterUtils.invokeOnViewAttachedToWindow(mWrappedAdapter, holder, viewType);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewDetachedFromWindow(VH holder) {
        onViewDetachedFromWindow(holder, holder.getItemViewType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewDetachedFromWindow(VH holder, int viewType) {
        if (isWrappedAdapterAlive()) {
            WrappedAdapterUtils.invokeOnViewDetachedFromWindow(mWrappedAdapter, holder, viewType);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewRecycled(VH holder) {
        onViewRecycled(holder, holder.getItemViewType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewRecycled(VH holder, int viewType) {
        if (isWrappedAdapterAlive()) {
            WrappedAdapterUtils.invokeOnViewRecycled(mWrappedAdapter, holder, viewType);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onFailedToRecycleView(VH holder) {
        return onFailedToRecycleView(holder, holder.getItemViewType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onFailedToRecycleView(VH holder, int viewType) {
        boolean shouldBeRecycled = false;

        if (isWrappedAdapterAlive()) {
            shouldBeRecycled = WrappedAdapterUtils.invokeOnFailedToRecycleView(mWrappedAdapter, holder, viewType);
        }

        if (shouldBeRecycled) {
            return true;
        }

        return super.onFailedToRecycleView(holder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);

        if (isWrappedAdapterAlive())
            mWrappedAdapter.setHasStableIds(hasStableIds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return mWrappedAdapter.onCreateViewHolder(parent, viewType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBindViewHolder(VH holder, int position) {
        onBindViewHolder(holder, position, FULL_UPDATE_PAYLOADS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBindViewHolder(VH holder, int position, List<Object> payloads) {
        if (isWrappedAdapterAlive())
            mWrappedAdapter.onBindViewHolder(holder, position, payloads);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemCount() {
        return isWrappedAdapterAlive() ? mWrappedAdapter.getItemCount() : 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getItemId(int position) {
        return mWrappedAdapter.getItemId(position);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemViewType(int position) {
        return mWrappedAdapter.getItemViewType(position);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unwrapPosition(@NonNull UnwrapPositionResult dest, int position) {
        dest.adapter = getWrappedAdapter();
        dest.position = position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int wrapPosition(@NonNull AdapterPathSegment pathSegment, int position) {
        if (pathSegment.adapter == getWrappedAdapter()) {
            return position;
        } else {
            return RecyclerView.NO_POSITION;
        }
    }

    @CallSuper
    protected void onRelease() {
        // override this method if needed
    }

    protected void onHandleWrappedAdapterChanged() {
        notifyDataSetChanged();
    }

    protected void onHandleWrappedAdapterItemRangeChanged(int positionStart, int itemCount) {
        notifyItemRangeChanged(positionStart, itemCount);
    }

    protected void onHandleWrappedAdapterItemRangeChanged(int positionStart, int itemCount, Object payload) {
        notifyItemRangeChanged(positionStart, itemCount, payload);
    }

    protected void onHandleWrappedAdapterItemRangeInserted(int positionStart, int itemCount) {
        notifyItemRangeInserted(positionStart, itemCount);
    }

    protected void onHandleWrappedAdapterItemRangeRemoved(int positionStart, int itemCount) {
        notifyItemRangeRemoved(positionStart, itemCount);
    }

    protected void onHandleWrappedAdapterRangeMoved(int fromPosition, int toPosition, int itemCount) {
        if (itemCount != 1) {
            throw new IllegalStateException("itemCount should be always 1  (actual: " + itemCount + ")");
        }

        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public final void onBridgedAdapterChanged(RecyclerView.Adapter source, Object tag) {
        if (LOCAL_LOGD) {
            Log.d(TAG, "onBridgedAdapterChanged");
        }

        onHandleWrappedAdapterChanged();
    }

    @Override
    public final void onBridgedAdapterItemRangeChanged(RecyclerView.Adapter source, Object tag, int positionStart, int itemCount) {
        if (LOCAL_LOGD) {
            Log.d(TAG, "onBridgedAdapterItemRangeChanged(positionStart = " + positionStart + ", itemCount = " + itemCount + ")");
        }

        onHandleWrappedAdapterItemRangeChanged(positionStart, itemCount);
    }

    @Override
    public final void onBridgedAdapterItemRangeChanged(RecyclerView.Adapter sourceAdapter, Object tag, int positionStart, int itemCount, Object payload) {
        if (LOCAL_LOGD) {
            Log.d(TAG, "onBridgedAdapterItemRangeChanged(positionStart = " + positionStart + ", itemCount = " + itemCount + ", payload = " + payload + ")");
        }

        onHandleWrappedAdapterItemRangeChanged(positionStart, itemCount, payload);
    }

    @Override
    public final void onBridgedAdapterItemRangeInserted(RecyclerView.Adapter sourceAdapter, Object tag, int positionStart, int itemCount) {
        if (LOCAL_LOGD) {
            Log.d(TAG, "onBridgedAdapterItemRangeInserted(positionStart = " + positionStart + ", itemCount = " + itemCount + ")");
        }

        onHandleWrappedAdapterItemRangeInserted(positionStart, itemCount);
    }

    @Override
    public final void onBridgedAdapterItemRangeRemoved(RecyclerView.Adapter sourceAdapter, Object tag, int positionStart, int itemCount) {
        if (LOCAL_LOGD) {
            Log.d(TAG, "onBridgedAdapterItemRangeRemoved(positionStart = " + positionStart + ", itemCount = " + itemCount + ")");
        }

        onHandleWrappedAdapterItemRangeRemoved(positionStart, itemCount);
    }

    @Override
    public final void onBridgedAdapterRangeMoved(RecyclerView.Adapter sourceAdapter, Object tag, int fromPosition, int toPosition, int itemCount) {
        if (LOCAL_LOGD) {
            Log.d(TAG, "onBridgedAdapterRangeMoved(fromPosition = " + fromPosition + ", toPosition = " + toPosition + ", itemCount = " + itemCount + ")");
        }

        onHandleWrappedAdapterRangeMoved(fromPosition, toPosition, itemCount);
    }
}
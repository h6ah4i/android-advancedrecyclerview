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
package com.h6ah4i.android.widget.advrecyclerview.composedadapter;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.adapter.AdapterPathSegment;
import com.h6ah4i.android.widget.advrecyclerview.adapter.BridgeAdapterDataObserver;
import com.h6ah4i.android.widget.advrecyclerview.adapter.UnwrapPositionResult;
import com.h6ah4i.android.widget.advrecyclerview.adapter.WrapperAdapter;
import com.h6ah4i.android.widget.advrecyclerview.adapter.ItemIdComposer;
import com.h6ah4i.android.widget.advrecyclerview.adapter.ItemViewTypeComposer;
import com.h6ah4i.android.widget.advrecyclerview.utils.WrappedAdapterUtils;

import java.util.List;

/**
 * A wrapper adapter which can compose and manage several children adapters.
 */
public class ComposedAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements WrapperAdapter<RecyclerView.ViewHolder>, BridgeAdapterDataObserver.Subscriber {

    /**
     * Corresponding segmented position value of {@link RecyclerView#NO_POSITION}.
     */
    public static long NO_SEGMENTED_POSITION = AdaptersSet.NO_SEGMENTED_POSITION;

    private AdaptersSet mAdaptersSet;
    private SegmentedPositionTranslator mSegmentedPositionTranslator;
    private SegmentedViewTypeTranslator mViewTypeTranslator;

    /**
     * Constructor.
     */
    public ComposedAdapter() {
        mAdaptersSet = new AdaptersSet(this);
        mSegmentedPositionTranslator = new SegmentedPositionTranslator(mAdaptersSet);
        mViewTypeTranslator = new SegmentedViewTypeTranslator();

        setHasStableIds(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getWrappedAdapters(@NonNull List<RecyclerView.Adapter> adapters) {
        if (mAdaptersSet != null) {
            adapters.addAll(mAdaptersSet.getUniqueAdaptersList());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void release() {
        onRelease();
    }


    /**
     * Implementation of the {@link #release()} method.
     */
    @CallSuper
    protected void onRelease() {
        if (mAdaptersSet != null) {
            mAdaptersSet.release();
            mAdaptersSet = null;
        }
        if (mSegmentedPositionTranslator != null) {
            mSegmentedPositionTranslator.release();
            mSegmentedPositionTranslator = null;
        }
        mViewTypeTranslator = null;
    }

    /**
     * Returns the total number of children adapters.
     *
     * @return The total number of children adapters
     */
    public int getChildAdapterCount() {
        return mAdaptersSet.getSegmentCount();
    }

    /**
     * Add a child adapter to the tail.
     * <p>This method can be invoked before attaching (= call {@link RecyclerView#setAdapter(RecyclerView.Adapter)}) to RecyclerView.
     * Also it may throws an {@link IllegalStateException} if the ComposedAdapter has been configured to support stable IDs, </p>
     *
     * @param adapter The adapter which to be managed by the ComposedAdapter
     * @return An instance of {@link ComposedChildAdapterTag}
     * @see {@link #addAdapter(RecyclerView.Adapter, int)}
     */
    public ComposedChildAdapterTag addAdapter(@NonNull RecyclerView.Adapter adapter) {
        return addAdapter(adapter, getChildAdapterCount());
    }

    /**
     * Adda child adapter to the specified position.
     *
     * @param adapter  The adapter which to be managed by the ComposedAdapter
     * @param position The position inserting a child adapter
     * @return An instance of {@link ComposedChildAdapterTag}
     * @see {@link #addAdapter(RecyclerView.Adapter)}
     */
    public ComposedChildAdapterTag addAdapter(@NonNull RecyclerView.Adapter adapter, int position) {
        if (hasObservers() && hasStableIds()) {
            if (!adapter.hasStableIds()) {
                throw new IllegalStateException("Wrapped child adapter must has stable IDs");
            }
        }

        final ComposedChildAdapterTag tag = mAdaptersSet.addAdapter(adapter, position);
        final int segment = mAdaptersSet.getAdapterSegment(tag);

        mSegmentedPositionTranslator.invalidateSegment(segment);

        // NOTE: Need to assume as data set change here because view types and item IDs are completely changed!
        notifyDataSetChanged();

        return tag;
    }

    /**
     * Remove a child adapter.
     *
     * @param tag The tag object linked to a child adapter to be removed
     * @return True if the child adapter is removed. Otherwise false.
     */
    public boolean removeAdapter(@NonNull ComposedChildAdapterTag tag) {
        final int segment = mAdaptersSet.getAdapterSegment(tag);

        if (segment < 0) {
            return false;
        }

        mAdaptersSet.removeAdapter(tag);
        mSegmentedPositionTranslator.invalidateSegment(segment);

        // NOTE: Need to assume as data set change here because view types and item IDs are completely changed!
        notifyDataSetChanged();

        return true;
    }

    /**
     * Returns the assigned segment number.
     *
     * @param tag The tag object linked to a child adapter
     * @return Number of the assigned segment
     */
    public int getSegment(@NonNull ComposedChildAdapterTag tag) {
        return mAdaptersSet.getAdapterSegment(tag);
    }

    /**
     * Gets a "segmented position".
     * <p>The segmented position is a packed long value which contains "segment" and
     * "offset inside of the segment" information.</p>
     *
     * @param flatPosition The normal flat position to be converted
     * @return Segmented Position
     * @see {@link #extractSegmentPart(long)}
     * @see {@link #extractSegmentOffsetPart(long)}
     */
    public long getSegmentedPosition(int flatPosition) {
        return mSegmentedPositionTranslator.getSegmentedPosition(flatPosition);
    }

    /**
     * Returns the segment value extracted from a segmented position.
     *
     * @param segmentedPosition The segment position value to be converted
     * @return segment part
     */
    public static int extractSegmentPart(long segmentedPosition) {
        return AdaptersSet.extractSegment(segmentedPosition);
    }

    /**
     * Returns the offset value extracted from a segmented position.
     *
     * @param segmentedPosition The segment position value to be converted
     * @return offset part
     */
    public static int extractSegmentOffsetPart(long segmentedPosition) {
        return AdaptersSet.extractSegmentOffset(segmentedPosition);
    }

    /**
     * {@liheritDoc}
     */
    @Override
    public void setHasStableIds(boolean hasStableIds) {

        // checks all children adapters support stable IDs
        if (hasStableIds && !hasStableIds()) {
            final int numSegments = mAdaptersSet.getSegmentCount();
            for (int i = 0; i < numSegments; i++) {
                RecyclerView.Adapter adapter = mAdaptersSet.getAdapter(i);

                if (!adapter.hasStableIds()) {
                    throw new IllegalStateException("All child adapters must support stable IDs");
                }
            }
        }

        super.setHasStableIds(hasStableIds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getItemId(int position) {
        final long segmentedPosition = getSegmentedPosition(position);
        final int segment = AdaptersSet.extractSegment(segmentedPosition);
        final int offset = AdaptersSet.extractSegmentOffset(segmentedPosition);
        final RecyclerView.Adapter adapter = mAdaptersSet.getAdapter(segment);

        final int rawViewType = adapter.getItemViewType(offset);
        final long rawId = adapter.getItemId(offset);

        final int wrappedViewType = mViewTypeTranslator.wrapItemViewType(segment, rawViewType);
        final int wrappedSegment = ItemViewTypeComposer.extractSegmentPart(wrappedViewType);

        return ItemIdComposer.composeSegment(wrappedSegment, rawId);
    }

    /**
     * {@lineritDoc}
     */
    @Override
    public int getItemViewType(int position) {
        final long segmentedPosition = getSegmentedPosition(position);
        final int segment = AdaptersSet.extractSegment(segmentedPosition);
        final int offset = AdaptersSet.extractSegmentOffset(segmentedPosition);
        final RecyclerView.Adapter adapter = mAdaptersSet.getAdapter(segment);

        final int rawViewType = adapter.getItemViewType(offset);

        return mViewTypeTranslator.wrapItemViewType(segment, rawViewType);
    }

    /**
     * {@lineritDoc}
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final long packedViewType = mViewTypeTranslator.unwrapViewType(viewType);
        final int segment = SegmentedViewTypeTranslator.extractWrapperSegment(packedViewType);
        final int origViewType = SegmentedViewTypeTranslator.extractWrappedViewType(packedViewType);
        final RecyclerView.Adapter adapter = mAdaptersSet.getAdapter(segment);

        return adapter.onCreateViewHolder(parent, origViewType);
    }

    /**
     * {@lineritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final long segmentedPosition = getSegmentedPosition(position);
        final int segment = AdaptersSet.extractSegment(segmentedPosition);
        final int offset = AdaptersSet.extractSegmentOffset(segmentedPosition);
        final RecyclerView.Adapter adapter = mAdaptersSet.getAdapter(segment);

        adapter.onBindViewHolder(holder, offset);
    }

    /**
     * {@lineritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        final long segmentedPosition = getSegmentedPosition(position);
        final int segment = AdaptersSet.extractSegment(segmentedPosition);
        final int offset = AdaptersSet.extractSegmentOffset(segmentedPosition);
        final RecyclerView.Adapter adapter = mAdaptersSet.getAdapter(segment);

        adapter.onBindViewHolder(holder, offset, payloads);
    }

    /**
     * {@lineritDoc}
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        final List<RecyclerView.Adapter> adapters = mAdaptersSet.getUniqueAdaptersList();
        for (int i = 0; i < adapters.size(); i++) {
            adapters.get(i).onAttachedToRecyclerView(recyclerView);
        }
    }

    /**
     * {@lineritDoc}
     */
    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        final List<RecyclerView.Adapter> adapters = mAdaptersSet.getUniqueAdaptersList();
        for (int i = 0; i < adapters.size(); i++) {
            adapters.get(i).onDetachedFromRecyclerView(recyclerView);
        }
    }

    /**
     * {@lineritDoc}
     */
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        onViewAttachedToWindow(holder, holder.getItemViewType());
    }

    /**
     * {@lineritDoc}
     */
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder, int viewType) {
        final long packedViewType = mViewTypeTranslator.unwrapViewType(viewType);
        final int segment = SegmentedViewTypeTranslator.extractWrapperSegment(packedViewType);
        final int wrappedViewType = SegmentedViewTypeTranslator.extractWrappedViewType(packedViewType);
        final RecyclerView.Adapter adapter = mAdaptersSet.getAdapter(segment);

        WrappedAdapterUtils.invokeOnViewAttachedToWindow(adapter, holder, wrappedViewType);
    }

    /**
     * {@lineritDoc}
     */
    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        onViewDetachedFromWindow(holder, holder.getItemViewType());
    }

    /**
     * {@lineritDoc}
     */
    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder, int viewType) {
        final long packedViewType = mViewTypeTranslator.unwrapViewType(viewType);
        final int segment = SegmentedViewTypeTranslator.extractWrapperSegment(packedViewType);
        final int wrappedViewType = SegmentedViewTypeTranslator.extractWrappedViewType(packedViewType);
        final RecyclerView.Adapter adapter = mAdaptersSet.getAdapter(segment);

        WrappedAdapterUtils.invokeOnViewDetachedFromWindow(adapter, holder, wrappedViewType);
    }

    /**
     * {@lineritDoc}
     */
    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        onViewRecycled(holder, holder.getItemViewType());
    }

    /**
     * {@lineritDoc}
     */
    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder, int viewType) {
        final long packedViewType = mViewTypeTranslator.unwrapViewType(viewType);
        final int segment = SegmentedViewTypeTranslator.extractWrapperSegment(packedViewType);
        final int wrappedViewType = SegmentedViewTypeTranslator.extractWrappedViewType(packedViewType);
        final RecyclerView.Adapter adapter = mAdaptersSet.getAdapter(segment);

        WrappedAdapterUtils.invokeOnViewRecycled(adapter, holder, wrappedViewType);
    }

    /**
     * {@lineritDoc}
     */
    @Override
    public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder) {
        return onFailedToRecycleView(holder, holder.getItemViewType());
    }

    /**
     * {@lineritDoc}
     */
    @Override
    public boolean onFailedToRecycleView(RecyclerView.ViewHolder holder, int viewType) {
        final long packedViewType = mViewTypeTranslator.unwrapViewType(viewType);
        final int segment = SegmentedViewTypeTranslator.extractWrapperSegment(packedViewType);
        final int wrappedViewType = SegmentedViewTypeTranslator.extractWrappedViewType(packedViewType);
        final RecyclerView.Adapter adapter = mAdaptersSet.getAdapter(segment);

        return WrappedAdapterUtils.invokeOnFailedToRecycleView(adapter, holder, wrappedViewType);
    }

    /**
     * {@lineritDoc}
     */
    @Override
    public int getItemCount() {
        return mSegmentedPositionTranslator.getTotalItemCount();
    }

    /**
     * {@lineritDoc}
     */
    @Override
    public void unwrapPosition(@NonNull UnwrapPositionResult dest, int position) {
        final long segmentedPosition = mSegmentedPositionTranslator.getSegmentedPosition(position);

        if (segmentedPosition != AdaptersSet.NO_SEGMENTED_POSITION) {
            final int segment = AdaptersSet.extractSegment(segmentedPosition);
            final int offset = AdaptersSet.extractSegmentOffset(segmentedPosition);

            dest.adapter = mAdaptersSet.getAdapter(segment);
            dest.position = offset;
            dest.tag = mAdaptersSet.getTag(segment);
        }
    }

    /**
     * {@lineritDoc}
     */
    @Override
    public int wrapPosition(@NonNull AdapterPathSegment pathSegment, int position) {
        if (pathSegment.tag != null) {
            final ComposedChildAdapterTag tag = (ComposedChildAdapterTag) pathSegment.tag;
            final int segment = mAdaptersSet.getAdapterSegment(tag);

            return mSegmentedPositionTranslator.getFlatPosition(segment, position);
        } else {
            return RecyclerView.NO_POSITION;
        }
    }

    /**
     * {@lineritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void onBridgedAdapterChanged(RecyclerView.Adapter source, Object tag) {
        onHandleWrappedAdapterChanged(source, (List<ComposedChildAdapterTag>) tag);
    }

    /**
     * {@lineritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void onBridgedAdapterItemRangeChanged(RecyclerView.Adapter source, Object tag, int positionStart, int itemCount) {
        onHandleWrappedAdapterItemRangeChanged(source, (List<ComposedChildAdapterTag>) tag, positionStart, itemCount);
    }

    /**
     * {@lineritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void onBridgedAdapterItemRangeChanged(RecyclerView.Adapter source, Object tag, int positionStart, int itemCount, Object payload) {
        onHandleWrappedAdapterItemRangeChanged(source, (List<ComposedChildAdapterTag>) tag, positionStart, itemCount, payload);
    }

    /**
     * {@lineritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void onBridgedAdapterItemRangeInserted(RecyclerView.Adapter source, Object tag, int positionStart, int itemCount) {
        onHandleWrappedAdapterItemRangeInserted(source, (List<ComposedChildAdapterTag>) tag, positionStart, itemCount);
    }

    /**
     * {@lineritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void onBridgedAdapterItemRangeRemoved(RecyclerView.Adapter source, Object tag, int positionStart, int itemCount) {
        onHandleWrappedAdapterItemRangeRemoved(source, (List<ComposedChildAdapterTag>) tag, positionStart, itemCount);
    }

    /**
     * {@lineritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void onBridgedAdapterRangeMoved(RecyclerView.Adapter source, Object tag, int fromPosition, int toPosition, int itemCount) {
        onHandleWrappedAdapterRangeMoved(source, (List<ComposedChildAdapterTag>) tag, fromPosition, toPosition, itemCount);
    }

    protected void onHandleWrappedAdapterChanged(RecyclerView.Adapter sourceAdapter, List<ComposedChildAdapterTag> sourceTags) {
        mSegmentedPositionTranslator.invalidateAll();
        notifyDataSetChanged();
    }

    protected void onHandleWrappedAdapterItemRangeChanged(RecyclerView.Adapter sourceAdapter, List<ComposedChildAdapterTag> sourceTags, int localPositionStart, int itemCount) {
        final int nTags = sourceTags.size();
        for (int i = 0; i < nTags; i++) {
            final int adapterSegment = mAdaptersSet.getAdapterSegment(sourceTags.get(i));
            final int positionStart = mSegmentedPositionTranslator.getFlatPosition(adapterSegment, localPositionStart);

            notifyItemRangeChanged(positionStart, itemCount);
        }
    }

    protected void onHandleWrappedAdapterItemRangeChanged(RecyclerView.Adapter sourceAdapter, List<ComposedChildAdapterTag> sourceTags, int localPositionStart, int itemCount, Object payload) {
        final int nTags = sourceTags.size();
        for (int i = 0; i < nTags; i++) {
            final int adapterSegment = mAdaptersSet.getAdapterSegment(sourceTags.get(i));
            final int positionStart = mSegmentedPositionTranslator.getFlatPosition(adapterSegment, localPositionStart);

            notifyItemRangeChanged(positionStart, itemCount, payload);
        }
    }

    protected void onHandleWrappedAdapterItemRangeInserted(RecyclerView.Adapter sourceAdapter, List<ComposedChildAdapterTag> sourceTags, int localPositionStart, int itemCount) {
        if (itemCount <= 0) {
            return;
        }

        final int nTags = sourceTags.size();

        if (nTags == 1) {
            final int adapterSegment = mAdaptersSet.getAdapterSegment(sourceTags.get(0));

            mSegmentedPositionTranslator.invalidateSegment(adapterSegment);

            final int positionStart = mSegmentedPositionTranslator.getFlatPosition(adapterSegment, localPositionStart);

            notifyItemRangeInserted(positionStart, itemCount);
        } else {
            for (int i = 0; i < nTags; i++) {
                final int adapterSegment = mAdaptersSet.getAdapterSegment(sourceTags.get(i));
                mSegmentedPositionTranslator.invalidateSegment(adapterSegment);
            }

            notifyDataSetChanged();
        }
    }

    protected void onHandleWrappedAdapterItemRangeRemoved(RecyclerView.Adapter sourceAdapter, List<ComposedChildAdapterTag> sourceTags, int localPositionStart, int itemCount) {
        if (itemCount <= 0) {
            return;
        }

        final int nTags = sourceTags.size();

        if (nTags == 1) {
            final int adapterSegment = mAdaptersSet.getAdapterSegment(sourceTags.get(0));

            mSegmentedPositionTranslator.invalidateSegment(adapterSegment);

            final int positionStart = mSegmentedPositionTranslator.getFlatPosition(adapterSegment, localPositionStart);

            notifyItemRangeRemoved(positionStart, itemCount);
        } else {
            for (int i = 0; i < nTags; i++) {
                final int adapterSegment = mAdaptersSet.getAdapterSegment(sourceTags.get(i));
                mSegmentedPositionTranslator.invalidateSegment(adapterSegment);
            }

            notifyDataSetChanged();
        }
    }

    protected void onHandleWrappedAdapterRangeMoved(RecyclerView.Adapter sourceAdapter, List<ComposedChildAdapterTag> sourceTags, int localFromPosition, int localToPosition, int itemCount) {
        if (itemCount != 1) {
            throw new IllegalStateException("itemCount should be always 1  (actual: " + itemCount + ")");
        }

        final int nTags = sourceTags.size();

        if (nTags == 1) {
            final int adapterSegment = mAdaptersSet.getAdapterSegment(sourceTags.get(0));
            final int fromPosition = mSegmentedPositionTranslator.getFlatPosition(adapterSegment, localFromPosition);
            final int toPosition = mSegmentedPositionTranslator.getFlatPosition(adapterSegment, localToPosition);

            notifyItemMoved(fromPosition, toPosition);
        } else {
            notifyDataSetChanged();
        }
    }
}

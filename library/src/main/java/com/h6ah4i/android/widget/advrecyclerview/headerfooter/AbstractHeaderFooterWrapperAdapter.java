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
package com.h6ah4i.android.widget.advrecyclerview.headerfooter;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.h6ah4i.android.widget.advrecyclerview.adapter.AdapterPathSegment;
import com.h6ah4i.android.widget.advrecyclerview.adapter.ItemIdComposer;
import com.h6ah4i.android.widget.advrecyclerview.adapter.ItemViewTypeComposer;
import com.h6ah4i.android.widget.advrecyclerview.composedadapter.ComposedAdapter;
import com.h6ah4i.android.widget.advrecyclerview.composedadapter.ComposedChildAdapterTag;

import java.util.List;

/**
 * A simplified version of ComposedAdapter for creating headers and footers.
 */
public abstract class AbstractHeaderFooterWrapperAdapter<HeaderVH extends RecyclerView.ViewHolder, FooterVH extends RecyclerView.ViewHolder> extends ComposedAdapter {
    /**
     * Segment type: header items
     */
    public static final int SEGMENT_TYPE_HEADER = 0;

    /**
     * Segment type: normal items
     */
    public static final int SEGMENT_TYPE_NORMAL = 1;

    /**
     * Segment type: footer items
     */
    public static final int SEGMENT_TYPE_FOOTER = 2;

    private RecyclerView.Adapter mHeaderAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    private RecyclerView.Adapter mFooterAdapter;

    private ComposedChildAdapterTag mHeaderAdapterTag;
    private ComposedChildAdapterTag mWrappedAdapterTag;
    private ComposedChildAdapterTag mFooterAdapterTag;

    /**
     * Constructor
     *
     */
    public AbstractHeaderFooterWrapperAdapter() {
    }

    /**
     *
     * @param adapter Wrapped contents adapter.
     */
    @SuppressWarnings("unchecked")
    public AbstractHeaderFooterWrapperAdapter setAdapter(@NonNull RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter) {
        if (mWrappedAdapter != null) {
            throw new IllegalStateException("setAdapter() can call only once");
        }

        mWrappedAdapter = adapter;
        mHeaderAdapter = onCreateHeaderAdapter();
        mFooterAdapter = onCreateFooterAdapter();

        final boolean hasStableIds = adapter.hasStableIds();
        mHeaderAdapter.setHasStableIds(hasStableIds);
        mFooterAdapter.setHasStableIds(hasStableIds);
        setHasStableIds(hasStableIds);

        mHeaderAdapterTag = addAdapter(mHeaderAdapter);
        mWrappedAdapterTag = addAdapter(mWrappedAdapter);
        mFooterAdapterTag = addAdapter(mFooterAdapter);

        return this;
    }

    @Override
    protected void onRelease() {
        super.onRelease();

        mHeaderAdapterTag = null;
        mWrappedAdapterTag = null;
        mFooterAdapterTag = null;
        mHeaderAdapter = null;
        mWrappedAdapter = null;
        mFooterAdapter = null;
    }

    /**
     * Returns a newly created adapter for header items.
     *
     * @return Adapter for header items
     */
    @NonNull
    protected RecyclerView.Adapter onCreateHeaderAdapter() {
        return new BaseHeaderAdapter(this);
    }

    /**
     * Returns a newly created adapter for the footer items.
     *
     * @return Adapter for the footer items
     */
    @NonNull
    protected RecyclerView.Adapter onCreateFooterAdapter() {
        return new BaseFooterAdapter(this);
    }

    /**
     * Returns the adapter for the header items.
     *
     * @return Adapter for the header items
     */
    public RecyclerView.Adapter getHeaderAdapter() {
        return mHeaderAdapter;
    }

    /**
     * Returns the adapter for the footer items.
     *
     * @return Adapter for the footer items
     */
    public RecyclerView.Adapter getFooterAdapter() {
        return mFooterAdapter;
    }

    /**
     * Returns underlying adapter.
     * @return The underlying adapter instance
     */
    public RecyclerView.Adapter getWrappedAdapter() {
        return mWrappedAdapter;
    }


    /**
     * Returns the path segment for the underlying adapter.
     *
     * @return AdapterPathSegment for the wrapped adapter
     */
    public AdapterPathSegment getWrappedAdapterSegment() {
      return new AdapterPathSegment(mWrappedAdapter, mWrappedAdapterTag);
    }

    /**
     * Returns the path segment for the header adapter.
     *
     * @return AdapterPathSegment for the header adapter
     */
    public AdapterPathSegment getHeaderSegment() {
      return new AdapterPathSegment(mHeaderAdapter, mHeaderAdapterTag);
    }

    /**
     * Returns the path segment for the footer adapter.
     *
     * @return AdapterPathSegment for the footer adapter
     */
    public AdapterPathSegment getFooterSegment() {
      return new AdapterPathSegment(mFooterAdapter, mFooterAdapterTag);
    }

    /**
     * Called when RecyclerView needs a new {@link RecyclerView.ViewHolder} of the
     * given type to represent a header item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 a header adapter position.
     * @param viewType The view type of the new header View.
     * @return A new ViewHolder for the header that holds a View of the given view type.
     * @see {@link RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)}
     * @see {@link #getHeaderItemViewType(int)}
     * @see {@link #onBindHeaderItemViewHolder(RecyclerView.ViewHolder, int)}
     */
    public abstract HeaderVH onCreateHeaderItemViewHolder(ViewGroup parent, int viewType);

    /**
     * Called when RecyclerView needs a new {@link RecyclerView.ViewHolder} of the
     * given type to represent a footer item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 a footer adapter position.
     * @param viewType The view type of the new footer View.
     * @return A new ViewHolder for the footer that holds a View of the given view type.
     * @see {@link RecyclerView.Adapter#onCreateViewHolder(ViewGroup, int)}
     * @see {@link #getFooterItemViewType(int)}
     * @see {@link #onBindFooterItemViewHolder(RecyclerView.ViewHolder, int)}
     */
    public abstract FooterVH onCreateFooterItemViewHolder(ViewGroup parent, int viewType);

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link RecyclerView.ViewHolder#itemView} to reflect the header item
     * at the given position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param localPosition The position of the item within the header adapter's data set.
     * @see {@link RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)}
     */
    public abstract void onBindHeaderItemViewHolder(HeaderVH holder, int localPosition);

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link RecyclerView.ViewHolder#itemView} to reflect the footer item
     * at the given position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param localPosition The position of the item within the footer adapter's data set.
     * @see {@link RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int)}
     */
    public abstract void onBindFooterItemViewHolder(FooterVH holder, int localPosition);

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link RecyclerView.ViewHolder#itemView} to reflect the header item
     * at the given position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param localPosition The position of the item within the header adapter's data set.
     * @param payloads A non-null list of merged payloads. Can be empty list if requires full
     *                 update.
     * @see {@link RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int, List)}
     */
    public void onBindHeaderItemViewHolder(HeaderVH holder, int localPosition, List<Object> payloads) {
        onBindHeaderItemViewHolder(holder, localPosition);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link RecyclerView.ViewHolder#itemView} to reflect the footer item
     * at the given position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param localPosition The position of the item within the footer adapter's data set.
     * @param payloads A non-null list of merged payloads. Can be empty list if requires full
     *                 update.
     * @see {@link RecyclerView.Adapter#onBindViewHolder(RecyclerView.ViewHolder, int, List)}
     */
    public void onBindFooterItemViewHolder(FooterVH holder, int localPosition, List<Object> payloads) {
        onBindFooterItemViewHolder(holder, localPosition);
    }

     /**
     * Returns the total number of items in the data set hold by the header adapter.
     *
     * @return The total number of items in the header adapter.
     * @see {@link RecyclerView.Adapter#getItemCount()}
     */
    public abstract int getHeaderItemCount();

    /**
     * Returns the total number of items in the data set hold by the footer adapter.
     *
     * @return The total number of items in the footer adapter.
     * @see {@link RecyclerView.Adapter#getItemCount()}
     */
    public abstract int getFooterItemCount();

    /**
     * Return the stable ID for the item at <code>localPosition</code>. If {@link #hasStableIds()}
     * would return false this method should return {@link RecyclerView#NO_ID}.
     *
     * @param localPosition Header adapter position to query
     * @return the stable ID of the item at position
     * @see {@link RecyclerView.Adapter#getItemId(int)}
     */
    @IntRange(from = ItemIdComposer.MIN_WRAPPED_ID, to = ItemIdComposer.MAX_WRAPPED_ID)
    public long getHeaderItemId(int localPosition) {
        if (hasStableIds()) {
            return RecyclerView.NO_ID;
        }
        return localPosition; // This works for simple header items without structural changes
    }

    /**
     * Return the stable ID for the item at <code>localPosition</code>. If {@link #hasStableIds()}
     * would return false this method should return {@link RecyclerView#NO_ID}.
     *
     * @param localPosition Foote adapter position to query
     * @return the stable ID of the item at position
     * @see {@link RecyclerView.Adapter#getItemId(int)}
     */
    @IntRange(from = ItemIdComposer.MIN_WRAPPED_ID, to = ItemIdComposer.MAX_WRAPPED_ID)
    public long getFooterItemId(int localPosition) {
        if (hasStableIds()) {
            return RecyclerView.NO_ID;
        }
        return localPosition; // This works for simple footer items without structural changes
    }

    /**
     * Return the view type of the header item at <code>localPosition</code> for the purposes
     * of view recycling.
     *
     * <p>The default implementation of this method returns 0, making the assumption of
     * a single view type for the adapter. Unlike ListView adapters, types need not
     * be contiguous.</p>
     *
     * @param localPosition The header adapter local position to query
     * @return integer value identifying the type of the view needed to represent the item at
     *                 <code>localPosition</code>. Type codes need not be contiguous.
     */
    @IntRange(from = ItemViewTypeComposer.MIN_WRAPPED_VIEW_TYPE, to = ItemViewTypeComposer.MAX_WRAPPED_VIEW_TYPE)
    public int getHeaderItemViewType(int localPosition) {
        return 0;
    }

    /**
     * Return the view type of the footer item at <code>localPosition</code> for the purposes
     * of view recycling.
     *
     * <p>The default implementation of this method returns 0, making the assumption of
     * a single view type for the adapter. Unlike ListView adapters, types need not
     * be contiguous.</p>
     *
     * @param localPosition The footer adapter local position to query
     * @return integer value identifying the type of the view needed to represent the item at
     *                 <code>localPosition</code>. Type codes need not be contiguous.
     */
    @IntRange(from = ItemViewTypeComposer.MIN_WRAPPED_VIEW_TYPE, to = ItemViewTypeComposer.MAX_WRAPPED_VIEW_TYPE)
    public int getFooterItemViewType(int localPosition) {
        return 0;
    }

    public static class BaseHeaderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        protected AbstractHeaderFooterWrapperAdapter mHolder;

        public BaseHeaderAdapter(AbstractHeaderFooterWrapperAdapter holder) {
            mHolder = holder;
        }

        @Override
        public int getItemCount() {
            return mHolder.getHeaderItemCount();
        }

        @Override
        public long getItemId(int position) {
            return mHolder.getHeaderItemId(position);
        }

        @Override
        public int getItemViewType(int position) {
            return mHolder.getHeaderItemViewType(position);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return mHolder.onCreateHeaderItemViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            throw new IllegalStateException();
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
            mHolder.onBindHeaderItemViewHolder(holder, position, payloads);
        }
    }

    public static class BaseFooterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        protected AbstractHeaderFooterWrapperAdapter mHolder;

        public BaseFooterAdapter(AbstractHeaderFooterWrapperAdapter holder) {
            mHolder = holder;
        }

        @Override
        public int getItemCount() {
            return mHolder.getFooterItemCount();
        }

        @Override
        public long getItemId(int position) {
            return mHolder.getFooterItemId(position);
        }

        @Override
        public int getItemViewType(int position) {
            return mHolder.getFooterItemViewType(position);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return mHolder.onCreateFooterItemViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            throw new IllegalStateException();
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
            mHolder.onBindFooterItemViewHolder(holder, position, payloads);
        }
    }
}

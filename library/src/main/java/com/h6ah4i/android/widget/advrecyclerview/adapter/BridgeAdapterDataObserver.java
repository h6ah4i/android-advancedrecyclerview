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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import java.lang.ref.WeakReference;

/**
 * This class behaves like a "proxy" which bridges
 * {@link RecyclerView.AdapterDataObserver} events to another subscriber object.
 */
public class BridgeAdapterDataObserver extends RecyclerView.AdapterDataObserver {

    /**
     * The subscriber interface.
     */
    public interface Subscriber {
        /**
         * Routed {@link RecyclerView.AdapterDataObserver#onChanged()} event.
         *
         * @param source The source adapter
         * @param tag    The tag object
         * @see {@link RecyclerView.AdapterDataObserver#onChanged()}
         */
        void onBridgedAdapterChanged(RecyclerView.Adapter source, Object tag);

        /**
         * Routed {@link RecyclerView.AdapterDataObserver#onItemRangeChanged(int, int)} event.
         *
         * @param source        The source adapter
         * @param tag           The tag object
         * @param positionStart Position of the first item that has changed
         * @param itemCount     Number of items that have changed
         * @see {@link RecyclerView.AdapterDataObserver#onItemRangeChanged(int, int)}
         */
        void onBridgedAdapterItemRangeChanged(RecyclerView.Adapter source, Object tag, int positionStart, int itemCount);

        /**
         * Routed {@link RecyclerView.AdapterDataObserver#onItemRangeChanged(int, int, Object)} event.
         *
         * @param source        The source adapter
         * @param tag           The tag object
         * @param positionStart Position of the first item that has changed
         * @param itemCount     Number of items that have changed
         * @param payload       Optional parameter, use null to identify a "full" update
         * @see {@link RecyclerView.AdapterDataObserver#onItemRangeChanged(int, int, Object)}
         */
        void onBridgedAdapterItemRangeChanged(RecyclerView.Adapter source, Object tag, int positionStart, int itemCount, Object payload);

        /**
         * Routed {@link RecyclerView.AdapterDataObserver#onItemRangeInserted(int, int)} event.
         *
         * @param source        The source adapter
         * @param tag           The tag object
         * @param positionStart Position of the first item that was inserted
         * @param itemCount     Number of items inserted
         * @see {@link RecyclerView.AdapterDataObserver#onItemRangeInserted(int, int)}
         */
        void onBridgedAdapterItemRangeInserted(RecyclerView.Adapter source, Object tag, int positionStart, int itemCount);

        /**
         * Routed {@link RecyclerView.AdapterDataObserver#onItemRangeRemoved(int, int)} event.
         *
         * @param source        The source adapter
         * @param tag           The tag object
         * @param positionStart Previous position of the first item that was removed
         * @param itemCount     Number of items removed from the data set
         * @see {@link RecyclerView.AdapterDataObserver#onItemRangeRemoved(int, int)}}
         */
        void onBridgedAdapterItemRangeRemoved(RecyclerView.Adapter source, Object tag, int positionStart, int itemCount);

        /**
         * Routed {@link RecyclerView.AdapterDataObserver#onItemRangeChanged(int, int, Object)} event.
         *
         * @param source       The source adapter
         * @param tag          The tag object
         * @param fromPosition Previous position of the item.
         * @param toPosition   New position of the item.
         * @param itemCount    Number of items moved (NOTE: this parameter is not actually used, always 1.)
         * @see {@link RecyclerView.AdapterDataObserver#onItemRangeChanged(int, int, Object)}
         */
        void onBridgedAdapterRangeMoved(RecyclerView.Adapter source, Object tag, int fromPosition, int toPosition, int itemCount);
    }

    private final WeakReference<Subscriber> mRefSubscriber;
    private final WeakReference<RecyclerView.Adapter> mRefSourceHolder;
    private final Object mTag;

    /**
     * Constructor.
     *
     * @param subscriber    The subscriber object
     * @param sourceAdapter The event source adapter
     * @param tag           The tag object which users can use in any purpose.
     */
    public BridgeAdapterDataObserver(@NonNull Subscriber subscriber, @NonNull RecyclerView.Adapter sourceAdapter, @Nullable Object tag) {
        mRefSubscriber = new WeakReference<>(subscriber);
        mRefSourceHolder = new WeakReference<>(sourceAdapter);
        mTag = tag;
    }

    /**
     * Returns tag object.
     *
     * @return The tag object
     */
    public Object getTag() {
        return mTag;
    }

    /**
     * This method dispatches {@link RecyclerView.AdapterDataObserver#onChanged()} event to underlying subscriber.
     * {@inheritDoc}
     */
    @Override
    public void onChanged() {
        final Subscriber subscriber = mRefSubscriber.get();
        final RecyclerView.Adapter source = mRefSourceHolder.get();
        if (subscriber != null && source != null) {
            subscriber.onBridgedAdapterChanged(source, mTag);
        }
    }

    /**
     * This method dispatches {@link RecyclerView.AdapterDataObserver#onItemRangeChanged(int, int)} event to underlying subscriber.
     * {@inheritDoc}
     */
    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
        final Subscriber subscriber = mRefSubscriber.get();
        final RecyclerView.Adapter source = mRefSourceHolder.get();
        if (subscriber != null && source != null) {
            subscriber.onBridgedAdapterItemRangeChanged(source, mTag, positionStart, itemCount);
        }
    }

    /**
     * This method dispatches {@link RecyclerView.AdapterDataObserver#onItemRangeChanged(int, int, Object)} event to underlying subscriber.
     * {@inheritDoc}
     */
    @Override
    public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
        final Subscriber subscriber = mRefSubscriber.get();
        final RecyclerView.Adapter source = mRefSourceHolder.get();
        if (subscriber != null && source != null) {
            subscriber.onBridgedAdapterItemRangeChanged(source, mTag, positionStart, itemCount, payload);
        }
    }

    /**
     * This method dispatches {@link RecyclerView.AdapterDataObserver#onItemRangeInserted(int, int)} event to underlying subscriber.
     * {@inheritDoc}
     */
    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        final Subscriber subscriber = mRefSubscriber.get();
        final RecyclerView.Adapter source = mRefSourceHolder.get();
        if (subscriber != null && source != null) {
            subscriber.onBridgedAdapterItemRangeInserted(source, mTag, positionStart, itemCount);
        }
    }

    /**
     * This method dispatches {@link RecyclerView.AdapterDataObserver#onItemRangeRemoved(int, int)} event to underlying subscriber.
     * {@inheritDoc}
     */
    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        final Subscriber subscriber = mRefSubscriber.get();
        final RecyclerView.Adapter source = mRefSourceHolder.get();
        if (subscriber != null && source != null) {
            subscriber.onBridgedAdapterItemRangeRemoved(source, mTag, positionStart, itemCount);
        }
    }

    /**
     * This method dispatches {@link RecyclerView.AdapterDataObserver#onItemRangeMoved(int, int, int)} event to underlying subscriber.
     * {@inheritDoc}
     */
    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
        final Subscriber subscriber = mRefSubscriber.get();
        final RecyclerView.Adapter source = mRefSourceHolder.get();
        if (subscriber != null && source != null) {
            subscriber.onBridgedAdapterRangeMoved(source, mTag, fromPosition, toPosition, itemCount);
        }
    }
}

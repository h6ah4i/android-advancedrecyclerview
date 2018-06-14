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

import androidx.recyclerview.widget.RecyclerView;

import com.h6ah4i.android.widget.advrecyclerview.adapter.ItemViewTypeComposer;

import java.util.Arrays;

class SegmentedPositionTranslator {
    private static final int NO_CACHED_SEGMENT = 0;
    private static final int NO_CACHED_ITEM_COUNT = -1;

    private AdaptersSet mAdaptersSet;
    private int mLastOffsetCachedSegment;
    private int[] mSegmentItemCountCache;
    private int[] mSegmentOffsetCache;
    private int mCachedTotalItemCount;

    public SegmentedPositionTranslator(AdaptersSet adaptersSet) {
        mAdaptersSet = adaptersSet;
        mLastOffsetCachedSegment = NO_CACHED_SEGMENT;
        mCachedTotalItemCount = NO_CACHED_ITEM_COUNT;
        mSegmentItemCountCache = new int[ItemViewTypeComposer.MAX_SEGMENT + 1]; // NOTE: +1 room
        mSegmentOffsetCache = new int[ItemViewTypeComposer.MAX_SEGMENT + 1]; // NOTE: +1 room
        Arrays.fill(mSegmentItemCountCache, NO_CACHED_ITEM_COUNT);
    }

    public int getTotalItemCount() {
        if (mCachedTotalItemCount == NO_CACHED_ITEM_COUNT) {
            mCachedTotalItemCount = countTotalItems();
        }
        return mCachedTotalItemCount;
    }

    public int getFlatPosition(int segment, int offset) {
        return getSegmentOffset(segment) + offset;
    }

    public long getSegmentedPosition(int flatPosition) {
        if (flatPosition == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION;
        }

        final int binSearchResult = Arrays.binarySearch(mSegmentOffsetCache, 0, mLastOffsetCachedSegment, flatPosition);

        final int loopStartIndex;
        int segment;
        int localOffset;

        if (binSearchResult >= 0) {
            loopStartIndex = binSearchResult;
            segment = loopStartIndex;
            localOffset = 0;
        } else {
            loopStartIndex = Math.max(0, (~binSearchResult) - 1);
            segment = -1;
            localOffset = -1;
        }

        final int nSegments = mAdaptersSet.getSegmentCount();
        int segmentOffset = mSegmentOffsetCache[loopStartIndex];
        for (int i = loopStartIndex; i < nSegments; i++) {
            final int count = getSegmentItemCount(i);

            if ((segmentOffset + count) > flatPosition) {
                localOffset = flatPosition - segmentOffset;
                segment = i;
                break;
            }

            segmentOffset += count;
        }

        if (segment >= 0) {
            return AdaptersSet.composeSegmentedPosition(segment, localOffset);
        } else {
            return AdaptersSet.NO_SEGMENTED_POSITION;
        }
    }

    private int countTotalItems() {
        int segmentCount = mAdaptersSet.getSegmentCount();

        if (segmentCount == 0) {
            return 0;
        }

        final int lastSegment = segmentCount - 1;
        final int lastSegmentOffset = getSegmentOffset(lastSegment);
        final int lastSegmentCount = getSegmentItemCount(lastSegment);

        return (lastSegmentOffset + lastSegmentCount);
    }

    public int getSegmentOffset(int segment) {
        if (segment <= mLastOffsetCachedSegment) {
            // cache hit
            return mSegmentOffsetCache[segment];
        } else {
            // cache miss
            final int nSegments = mAdaptersSet.getSegmentCount();
            final int loopStartIndex = mLastOffsetCachedSegment;
            int offset = mSegmentOffsetCache[loopStartIndex];
            for (int i = loopStartIndex; i < segment; i++) {
                offset += getSegmentItemCount(i);
            }
            return offset;
        }
    }

    public int getSegmentItemCount(int segment) {
        if (mSegmentItemCountCache[segment] != NO_CACHED_ITEM_COUNT) {
            // cache hit
            return mSegmentItemCountCache[segment];
        } else {
            // cache miss
            final int count = mAdaptersSet.getAdapter(segment).getItemCount();
            mSegmentItemCountCache[segment] = count;
            if (segment == mLastOffsetCachedSegment) {
                mSegmentOffsetCache[segment + 1] = mSegmentOffsetCache[segment] + count;
                mLastOffsetCachedSegment = segment + 1;
            }
            return count;
        }
    }

    public void invalidateSegment(int segment) {
        mCachedTotalItemCount = NO_CACHED_ITEM_COUNT;
        mLastOffsetCachedSegment = Math.min(mLastOffsetCachedSegment, segment);
        mSegmentItemCountCache[segment] = NO_CACHED_ITEM_COUNT;
    }

    public void invalidateAll() {
        mCachedTotalItemCount = NO_CACHED_ITEM_COUNT;
        mLastOffsetCachedSegment = NO_CACHED_SEGMENT;
        Arrays.fill(mSegmentItemCountCache, NO_CACHED_ITEM_COUNT);
    }

    public void release() {
        mAdaptersSet = null;
        mSegmentItemCountCache = null;
        mSegmentOffsetCache = null;
    }
}

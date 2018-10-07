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


import androidx.annotation.IntRange;

/**
 * Utility class providing "Composed item view type" related definitions and methods.
 * <p>
 * Spec:
 * <table summary="Bit usages of composed item view type">
 * <tr><th>bit 31</th><td>Expandable group flag  (1: expandable group / 0: normal item)</td></tr>
 * <tr><th>bit 30-24</th><td>View type segment</td></tr>
 * <tr><th>bit 23-0</th><td>Wrapped view type code</td></tr>
 * </table>
 * </p>
 */
public class ItemViewTypeComposer {

    /**
     * Bit offset of the expandable flag part.
     */
    public static final int BIT_OFFSET_EXPANDABLE_FLAG = 31;

    /**
     * Bit offset of the segment part.
     */
    public static final int BIT_OFFSET_SEGMENT = 24;

    /**
     * Bit offset of the wrapped view type part.
     */
    public static final int BIT_OFFSET_WRAPPED_VIEW_TYPE = 0;

    // ---

    /**
     * Bit width of the expandable flag part.
     */
    public static final int BIT_WIDTH_EXPANDABLE_FLAG = 1;

    /**
     * Bit width of the segment part.
     */
    public static final int BIT_WIDTH_SEGMENT = 7;

    /**
     * Bit width of the wrapped view type part.
     */
    public static final int BIT_WIDTH_WRAPPED_VIEW_TYPE = 24;

    /**
     * Bit mask of the expandable flag part.
     */
    @SuppressWarnings("NumericOverflow")
    public static final int BIT_MASK_EXPANDABLE_FLAG = (1 << (BIT_WIDTH_EXPANDABLE_FLAG - 1)) << BIT_OFFSET_EXPANDABLE_FLAG;

    // ---

    /**
     * Bit mask of the segment part.
     */
    public static final int BIT_MASK_SEGMENT = ((1 << BIT_WIDTH_SEGMENT) - 1) << BIT_OFFSET_SEGMENT;

    /**
     * Bit mask of the wrapped view type part.
     */
    public static final int BIT_MASK_WRAPPED_VIEW_TYPE = ((1 << BIT_WIDTH_WRAPPED_VIEW_TYPE) - 1) << BIT_OFFSET_WRAPPED_VIEW_TYPE;

    // ---

    /**
     * Minimum value of segment.
     */
    public static final int MIN_SEGMENT = 0;

    /**
     * Maximum value of segment.
     */
    public static final int MAX_SEGMENT = (1 << BIT_WIDTH_SEGMENT) - 1;

    /**
     * Minimum value of wrapped view type.
     */
    public static final int MIN_WRAPPED_VIEW_TYPE = -(1 << (BIT_WIDTH_WRAPPED_VIEW_TYPE - 1));

    /**
     * Maximum value of wrapped view type.
     */
    public static final int MAX_WRAPPED_VIEW_TYPE = (1 << (BIT_WIDTH_WRAPPED_VIEW_TYPE - 1)) - 1;


    private ItemViewTypeComposer() {
    }

    /**
     * Extracts "Segment" part from composed view type.
     *
     * @param composedViewType Composed view type
     * @return Segment part
     */
    @IntRange(from = MIN_SEGMENT, to = MAX_SEGMENT)
    public static int extractSegmentPart(int composedViewType) {
        return (composedViewType & BIT_MASK_SEGMENT) >>> BIT_OFFSET_SEGMENT;
    }

    /**
     * Extracts "Wrapped view type" part from composed view type.
     *
     * @param composedViewType Composed view type
     * @return Wrapped view type part
     */
    @IntRange(from = MIN_WRAPPED_VIEW_TYPE, to = MAX_WRAPPED_VIEW_TYPE)
    public static int extractWrappedViewTypePart(int composedViewType) {
        return (composedViewType << (32 - BIT_WIDTH_WRAPPED_VIEW_TYPE - BIT_OFFSET_WRAPPED_VIEW_TYPE)) >> (32 - BIT_WIDTH_WRAPPED_VIEW_TYPE);
    }

    /**
     * Checks the composed view type is a expandable group or not.
     *
     * @param composedViewType Composed view type
     * @return True if the specified composed composed view type is an expandable group item view type. Otherwise, false.
     */
    public static boolean isExpandableGroup(int composedViewType) {
        return (composedViewType & BIT_MASK_EXPANDABLE_FLAG) != 0;
    }

    /**
     * Makes a composed ID with specified segment and wrapped ID.
     *
     * @param segment         Segment
     * @param wrappedViewType Wrapped view type
     * @return Composed View type.
     */
    public static int composeSegment(@IntRange(from = MIN_SEGMENT, to = MAX_SEGMENT) int segment, int wrappedViewType) {
        if (segment < MIN_SEGMENT || segment > MAX_SEGMENT) {
            throw new IllegalArgumentException("Segment value is out of range. (segment = " + segment + ")");
        }

        return (segment << BIT_OFFSET_SEGMENT) | (wrappedViewType & (BIT_MASK_EXPANDABLE_FLAG | BIT_MASK_WRAPPED_VIEW_TYPE));
    }
}
